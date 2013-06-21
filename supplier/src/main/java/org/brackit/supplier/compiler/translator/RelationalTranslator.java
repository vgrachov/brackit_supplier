/*******************************************************************************
 * Copyright 2012 Volodymyr Grachov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.brackit.supplier.compiler.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.relational.metadata.tuple.Column;
import org.brackit.relational.metadata.tuple.ColumnType;
import org.brackit.supplier.access.AccessColumn;
import org.brackit.supplier.access.EqualAccessColumn;
import org.brackit.supplier.access.FullRangeAccessColumn;
import org.brackit.supplier.access.LeftRangeAccessColumn;
import org.brackit.supplier.access.RangeAccessColumn;
import org.brackit.supplier.access.RightRangeAccessColumn;
import org.brackit.supplier.collection.RangeAccessCollection;
import org.brackit.supplier.expr.DeleteExpr;
import org.brackit.supplier.expr.InsertExpr;
import org.brackit.supplier.function.FullScanFunction;
import org.brackit.supplier.function.RangeAccessFunction;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.AbstractAtomic;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Dec;
import org.brackit.xquery.atomic.Int;
import org.brackit.xquery.atomic.Int32;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.XQ;
import org.brackit.xquery.compiler.translator.TopDownTranslator;
import org.brackit.xquery.function.FunctionExpr;
import org.brackit.xquery.operator.Operator;
import org.brackit.xquery.xdm.Expr;
import org.brackit.xquery.xdm.Function;

import com.google.common.collect.ImmutableSet;


public class RelationalTranslator extends TopDownTranslator {

	private static final Logger logger = Logger.getLogger(RelationalTranslator.class);
	private Map<String,Set<String>> projectionMap;
	
	public RelationalTranslator(Map<QNm, Str> options) {
		super(options);
	}
	
	public void setProjectionMap(Map<String,Set<String>> projectionMap) {
		this.projectionMap = projectionMap; 
	}
	
	protected List<AST> foundPredicates(AST node) {
		if (node==null) 
			return new ArrayList<AST>();
		List<AST> comparisonPredicates = new ArrayList<AST>();
		for (int i=0;i<node.getChildCount();i++)
			if (node.getChild(i).getType() == XQ.ComparisonExpr && node.getChild(i).getChildCount()==3 && node.getChild(i).getChild(1).getType()==XQ.PathExpr && node.getChild(i).getChild(2).getType()!=XQ.PathExpr)
				comparisonPredicates.add(node.getChild(i));
		for (int i=0;i<node.getChildCount();i++)
			if (node.getChild(i).getType() == XQ.AndExpr || node.getChild(i).getType() == XQ.OrExpr){
				comparisonPredicates.addAll(foundPredicates(node.getChild(i)));
			}
		return comparisonPredicates;
	}
	
	private org.brackit.xquery.atomic.Atomic getValueFromNode(AST valueNode){
		org.brackit.xquery.atomic.Atomic value = null;
		if (valueNode.getType() == XQ.Str){
			value = (Str)valueNode.getValue();
			logger.debug("Equal match value : "+value );
		}else
		if (valueNode.getType() == XQ.Int){
			value = (Int32)valueNode.getValue();
			logger.debug("Equal match value : "+value );
		}else
		if (valueNode.getType() == XQ.Dec){
			value = (Dec)valueNode.getValue();
			logger.debug("Equal match value : "+value );
		}else {
			logger.fatal("Type can't be process "+value);
			throw new IllegalArgumentException("Type can't be process "+value);
		}
		return value;
	}
	
	private EqualAccessColumn findEqualAccessPredicate(List<AST> comparisonExpresions, Str tableName){
		for (int i=0;i<comparisonExpresions.size();i++){
			AST accessNode = comparisonExpresions.get(i);
			if (accessNode.getChild(0).getType() == XQ.GeneralCompEQ){
				QNm accessField = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				logger.info("Aceess to field : "+accessField);
				if (accessNode.getChild(2).getType() != XQ.ParenthesizedExpr) {
					AST valueNode = accessNode.getChild(2);
					org.brackit.xquery.atomic.Atomic value = getValueFromNode(valueNode);
					return new EqualAccessColumn(null, tableName.stringValue(), accessField.localName, value);
				}
			}
		}
		return null;
	}
	
	private FullRangeAccessColumn findFullRangeAccessColumn(List<AST> comparisonExpresions, Str tableName){
		Set<String> allAccessedFields = new HashSet<String>();
		for (int i=0;i<comparisonExpresions.size();i++){
			AST accessNode = comparisonExpresions.get(i);
			QNm accessField = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
			allAccessedFields.add(accessField.getLocalName());
		}
		Iterator<String> i = allAccessedFields.iterator();
		while (i.hasNext()){
			String accessField = i.next();
			AST leftAccessNode = null;
			AST rightAccessNode = null;
			boolean isRightRangeFound = false;
			boolean isLeftRangeFound = false;
			Atomic rightKey = null;
			Atomic leftKey = null;
			for (int j=0;j<comparisonExpresions.size();j++){
				AST accessNode = comparisonExpresions.get(j);
				QNm accessFieldQNm = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				if (accessFieldQNm.getLocalName().equals(accessField)){
					if (accessNode.getChild(0).getType()==XQ.GeneralCompLE || accessNode.getChild(0).getType()==XQ.GeneralCompLT ||
						accessNode.getChild(0).getType()==XQ.ValueCompLE || accessNode.getChild(0).getType()==XQ.ValueCompLT){
						isRightRangeFound = true;
						rightKey = getValueFromNode(accessNode.getChild(2));
					}else
					if (accessNode.getChild(0).getType()==XQ.GeneralCompGE || accessNode.getChild(0).getType()==XQ.GeneralCompGT ||
						accessNode.getChild(0).getType()==XQ.ValueCompGE || accessNode.getChild(0).getType()==XQ.ValueCompGT){
						isLeftRangeFound = true;
						leftKey = getValueFromNode(accessNode.getChild(2));
					}
				}
			}
			if (isRightRangeFound && isLeftRangeFound){
				return new FullRangeAccessColumn(null, tableName.stringValue(), accessField, leftKey, rightKey);
			}
		}
		return null;
	}
	
	private RightRangeAccessColumn findFirstRightRangeAccessPredicate(List<AST> comparisonExpresions, Str tableName){
		for (int i=0;i<comparisonExpresions.size();i++){
			AST accessNode = comparisonExpresions.get(i);
			if (accessNode.getChild(0).getType()==XQ.GeneralCompLE || accessNode.getChild(0).getType()==XQ.GeneralCompLT ||
				accessNode.getChild(0).getType()==XQ.ValueCompLE || accessNode.getChild(0).getType()==XQ.ValueCompLT){
				QNm accessField = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				Atomic rightKey = getValueFromNode(accessNode.getChild(2));
				return new RightRangeAccessColumn(null, tableName.stringValue(), accessField.getLocalName(), rightKey);
			}
		}
		return null;
	}

	private LeftRangeAccessColumn findFirstLeftRangeAccessPredicate(List<AST> comparisonExpresions, Str tableName){
		for (int i=0;i<comparisonExpresions.size();i++){
			AST accessNode = comparisonExpresions.get(i);
			if (accessNode.getChild(0).getType()==XQ.GeneralCompGE || accessNode.getChild(0).getType()==XQ.GeneralCompGT ||
				accessNode.getChild(0).getType()==XQ.ValueCompGE || accessNode.getChild(0).getType()==XQ.ValueCompGT){
				QNm accessField = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				Atomic rightKey = getValueFromNode(accessNode.getChild(2));
				return new LeftRangeAccessColumn(null, tableName.stringValue(), accessField.getLocalName(), rightKey);
			}
		}
		return null;
	}
	
	private AccessColumn selectAccessColumn(List<AST> comparisonExpresions, Str tableName){
		logger.debug("Access table : "+tableName);
		EqualAccessColumn equalAccessColumn = findEqualAccessPredicate(comparisonExpresions, tableName);
		if (equalAccessColumn!=null)
			return equalAccessColumn;
		else{
			FullRangeAccessColumn fullRangeAccessColumn = findFullRangeAccessColumn(comparisonExpresions, tableName);
			if (fullRangeAccessColumn!=null)
				return fullRangeAccessColumn;
			else{
				RightRangeAccessColumn rightRangeAccessColumn = findFirstRightRangeAccessPredicate(comparisonExpresions, tableName);
				if (rightRangeAccessColumn!=null)
					return rightRangeAccessColumn;
				else{
					LeftRangeAccessColumn leftRangeAccessColumn = findFirstLeftRangeAccessPredicate(comparisonExpresions, tableName);
					if (leftRangeAccessColumn!=null)
						return leftRangeAccessColumn;
					else return null;
				}
					
			}
		}
	}
	
	protected Operator anyOp(Operator in, AST node) throws QueryException {
		logger.debug("Found operation : "+node.getStringValue()+" "+node.getType());
		return super.anyOp(in, node);
	}
	
	private Set<String> getFields(String tableName) {
		Set<String> projectionFields = null;
		if ("lineitem".equals(tableName))
			projectionFields = ImmutableSet.of("l_orderkey", "l_partkey", "l_suppkey", "l_linenumber", "l_quantity", "l_extendedprice", "l_discount", "l_tax", "l_returnflag", "l_linestatus", "l_shipdate", "l_commitdate", "l_receiptdate", "l_shipinstruct", "l_shipmode", "l_comment");
		else
		if ("orders".equals(tableName))
			projectionFields = ImmutableSet.of("o_orderkey", "o_custkey", "o_orderstatus", "o_totalprice", "o_orderdate", "o_orderpriority", "o_clerk", "o_shippriority", "o_comment");
		else
		if ("customer".equals(tableName))
			projectionFields = ImmutableSet.of("c_custkey", "c_name", "c_address", "c_nationkey", "c_phone", "c_acctbal", "c_mktsegment", "c_comment");
		else
		if ("supplier".equals(tableName))
			projectionFields = ImmutableSet.of("s_suppkey", "s_name", "s_address", "s_nationkey", "s_phone", "s_acctbal", "s_comment");
		else
		if ("nation".equals(tableName))
			projectionFields = ImmutableSet.of("n_nationkey", "n_name", "n_regionkey", "n_comment");
		else
		if ("region".equals(tableName))
			projectionFields = ImmutableSet.of("r_regionkey", "r_name", "r_comment");
		else
		if ("part".equals(tableName))
			projectionFields = ImmutableSet.of("p_partkey", "p_name", "p_mfgr", "p_brand", "p_type", "p_size", "p_container", "p_retailprice", "p_comment");
		return projectionFields;
	}

	protected Expr anyExpr(AST node) throws QueryException {
		logger.debug("Found expression :"+node.getStringValue()+" "+node.getType());
		if (node.getType()==XQ.FunctionCall && ((QNm)node.getValue()).getLocalName().equals("collection")){
			AST parent = node.getParent();
			if (parent!=null && parent.getType()==XQ.ForBind && parent.getChildCount()==3 && parent.getChild(2).getType()==XQ.Selection){
				logger.debug("Found selection");
				List<AST> comparisonExpresions = foundPredicates(parent.getChild(2));
				Str tableName = (Str)parent.getChild(1).getChild(0).getValue();
				String bindedVariable = parent.getChild(0).getChild(0).getStringValue();
				Set<String> projectionFields = projectionMap.get(bindedVariable);
				//logger.info("Projection test "+bindedVariable+" "+projectionFields);
				
				//Set<String> projectionFields = getFields(tableName.stringValue());

				AccessColumn accessColumn = selectAccessColumn(comparisonExpresions,tableName);
				if (accessColumn!=null){
					if (accessColumn instanceof RangeAccessColumn){
						System.out.println("Found range acess "+((RangeAccessColumn)accessColumn).getAccessColumn());
						Function fn = new RangeAccessFunction((RangeAccessColumn)accessColumn, projectionFields);
						return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
					}else{
						if (accessColumn instanceof EqualAccessColumn){
							System.out.println("Found equal match "+((EqualAccessColumn)accessColumn).getAccessColumn());
							System.out.println("Found equal value "+((EqualAccessColumn)accessColumn).getKey());
							Function fn = new RangeAccessFunction((EqualAccessColumn)accessColumn, projectionFields);
							return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
						}else{
							Function fn = new FullScanFunction(tableName.stringValue(), projectionFields);
							return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
						}
					}
				}else{
					Function fn = new FullScanFunction(tableName.stringValue(), projectionFields);
					return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
				}
			} else
			if (parent!=null && parent.getType()==XQ.ForBind && parent.getChildCount()==3 && (parent.getChild(2).getType()==XQ.End || parent.getChild(2).getType()==XQ.ForBind) ) {
				logger.debug("Found Forbind without selection");
				Str tableName = (Str)parent.getChild(1).getChild(0).getValue();
				String bindedVariable = parent.getChild(0).getChild(0).getStringValue();
				Set<String> projectionFields = projectionMap.get(bindedVariable);
				logger.info("Projection test "+bindedVariable+" "+projectionFields);

				//Set<String> projectionFields = getFields(tableName.stringValue());
				Function fn = new FullScanFunction(tableName.stringValue(), projectionFields);
				return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
			} else
				if (parent!=null && parent.getType()==XQ.ForBind && parent.getChildCount()==3){
					Str tableName = (Str)parent.getChild(1).getChild(0).getValue();
					//TODO(vgrachov) : set map of fields from Schema
					Function fn = new FullScanFunction(tableName.stringValue(), null);
					return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
				}else
				if (parent!=null && parent.getType() == XQ.FilterExpr){
					Str tableName = (Str)parent.getChild(0).getChild(0).getValue();
					//TODO(vgrachov) : set map of fields from Schema
					Function fn = new FullScanFunction(tableName.stringValue(), null);
					return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
				
				}
		}else
		if (node.getType() == XQ.InsertExpr){
			Expr sourceExpr = expr(node.getChild(1), true);
			QNm tableName = (QNm)node.getChild(1).getChild(0).getValue();
			return new InsertExpr(sourceExpr, tableName);
		}else
		if (node.getType() == XQ.DeleteExpr){
			Expr targetExpr = expr(node.getChild(0), true);
			return new DeleteExpr(targetExpr);
		}
		return super.anyExpr(node);
	}
}
