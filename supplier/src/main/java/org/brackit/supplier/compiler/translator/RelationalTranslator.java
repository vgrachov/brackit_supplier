/*******************************************************************************
 * [New BSD License]
 *   Copyright (c) 2012-2013, Volodymyr Grachov <vladimir.grachov@gmail.com>  
 *   All rights reserved.
 *   
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *       * Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *       * Neither the name of the Brackit Project Team nor the
 *         names of its contributors may be used to endorse or promote products
 *         derived from this software without specific prior written permission.
 *   
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *   ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.brackit.supplier.compiler.translator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.supplier.access.AccessColumn;
import org.brackit.supplier.access.BothRangeAccessColumn;
import org.brackit.supplier.access.EqualAccessColumn;
import org.brackit.supplier.access.LeftRangeAccessColumn;
import org.brackit.supplier.access.RangeAccessColumn;
import org.brackit.supplier.access.RightRangeAccessColumn;
import org.brackit.supplier.cost.CostManager;
import org.brackit.supplier.cost.ICostManager;
import org.brackit.supplier.expr.DeleteExpr;
import org.brackit.supplier.expr.InsertExpr;
import org.brackit.supplier.function.FullScanFunction;
import org.brackit.supplier.function.RangeAccessFunction;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Dec;
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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;


public class RelationalTranslator extends TopDownTranslator {

	private static final Logger logger = Logger.getLogger(RelationalTranslator.class);
	
	private Map<String,Set<String>> projectionMap;
	private Map<String,List<AST>> comparisonMap;
	
	private final ICostManager costManager; 
	
	public RelationalTranslator(Map<QNm, Str> options) {
		super(options);
		costManager = new CostManager();
	}
	
	public void setProjectionMap(Map<String,Set<String>> projectionMap) {
		this.projectionMap = projectionMap; 
	}
	
	public void setComparisonExprMap(Map<String,List<AST>> comparisonMap) {
		this.comparisonMap = comparisonMap; 
	}
	
	private Atomic getValueFromNode(AST valueNode){
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
	
	private EqualAccessColumn findEqualAccessPredicate(String bindVariable, List<AST> comparisonExpresions, Str tableName){
		Preconditions.checkNotNull(tableName);
		if (comparisonExpresions == null) return null;

		logger.info("Finding equal mathcing checking for variable "+bindVariable);
		for (int i=0; i<comparisonExpresions.size(); i++){
			AST comparisonExpr = comparisonExpresions.get(i);
			if (comparisonExpr.getChild(0).getType() == XQ.GeneralCompEQ){
				QNm accessField = (QNm)comparisonExpr.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				logger.info("Aceess to field : "+accessField);
				if (comparisonExpr.getChild(2).getType() != XQ.ParenthesizedExpr) {
					AST valueNode = comparisonExpr.getChild(2);
					Atomic value = getValueFromNode(valueNode);
					logger.info("Equal mathcing is found "+accessField.getLocalName());
					return new EqualAccessColumn(bindVariable, tableName.stringValue(), accessField.getLocalName(), value);
				}
			}
		}
		return null;
	}
	
	private BothRangeAccessColumn findFullRangeAccessColumn(String bindVariable, List<AST> comparisonExpresions, Str tableName) {
		Preconditions.checkNotNull(tableName);
		if (comparisonExpresions == null) return null;

		Set<String> accessedFields = new HashSet<String>();
		for (int i=0; i<comparisonExpresions.size(); i++) {
			AST comparisonNode = comparisonExpresions.get(i);
			QNm accessField = (QNm)comparisonNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
			accessedFields.add(accessField.getLocalName());
		}
		Iterator<String> fieldIterator = accessedFields.iterator();
		while (fieldIterator.hasNext()) {
			String comparedFieldName = fieldIterator.next();
			boolean isRightRangeFound = false;
			boolean isLeftRangeFound = false;
			Atomic rightKey = null;
			Atomic leftKey = null;
			
			AST leftBoundComparisonExpr = null;
			AST rightBoundComparisonExpr = null;
			
			for (int j=0; j<comparisonExpresions.size(); j++) {
				AST accessNode = comparisonExpresions.get(j);
				QNm accessFieldQNm = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				if (accessFieldQNm.getLocalName().equals(comparedFieldName)) {
					if (accessNode.getChild(0).getType()==XQ.GeneralCompLE || accessNode.getChild(0).getType()==XQ.GeneralCompLT ||
						accessNode.getChild(0).getType()==XQ.ValueCompLE || accessNode.getChild(0).getType()==XQ.ValueCompLT){
						isRightRangeFound = true;
						rightBoundComparisonExpr = accessNode;
						rightKey = getValueFromNode(accessNode.getChild(2));
					} else
					if (accessNode.getChild(0).getType()==XQ.GeneralCompGE || accessNode.getChild(0).getType()==XQ.GeneralCompGT ||
						accessNode.getChild(0).getType()==XQ.ValueCompGE || accessNode.getChild(0).getType()==XQ.ValueCompGT) {
						isLeftRangeFound = true;
						leftBoundComparisonExpr = accessNode;
						leftKey = getValueFromNode(accessNode.getChild(2));
					}
				}
			}
			if (isRightRangeFound && isLeftRangeFound){
				double cost = costManager.getCostEstimation(tableName.stringValue(), comparedFieldName, leftKey, rightKey);
				return new BothRangeAccessColumn(bindVariable, tableName.stringValue(), comparedFieldName, leftKey, rightKey, cost, 
						leftBoundComparisonExpr, rightBoundComparisonExpr);
			}
		}
		return null;
	}
	
	private RightRangeAccessColumn findFirstRightRangeAccessPredicate(String bindVariable, List<AST> comparisonExpresions, Str tableName){
		Preconditions.checkNotNull(tableName);
		if (comparisonExpresions == null) return null;

		for (int i=0; i<comparisonExpresions.size(); i++) {
			AST comparisonNode = comparisonExpresions.get(i);
			if (comparisonNode.getChild(0).getType()==XQ.GeneralCompLE || comparisonNode.getChild(0).getType()==XQ.GeneralCompLT ||
				comparisonNode.getChild(0).getType()==XQ.ValueCompLE || comparisonNode.getChild(0).getType()==XQ.ValueCompLT) {
				QNm accessField = (QNm)comparisonNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				Atomic rightKey = getValueFromNode(comparisonNode.getChild(2));
				double cost = costManager.getCostEstimation(tableName.stringValue(), accessField.getLocalName(), null, rightKey);
				return new RightRangeAccessColumn(bindVariable, tableName.stringValue(), accessField.getLocalName(), rightKey, cost, comparisonNode);
			}
		}
		return null;
	}

	private LeftRangeAccessColumn findFirstLeftRangeAccessPredicate(String bindVariable, List<AST> comparisonExpresions, Str tableName){
		Preconditions.checkNotNull(tableName);
		if (comparisonExpresions == null) return null;

		for (int i=0; i<comparisonExpresions.size(); i++){
			AST comparisonNode = comparisonExpresions.get(i);
			if (comparisonNode.getChild(0).getType()==XQ.GeneralCompGE || comparisonNode.getChild(0).getType()==XQ.GeneralCompGT ||
				comparisonNode.getChild(0).getType()==XQ.ValueCompGE || comparisonNode.getChild(0).getType()==XQ.ValueCompGT){
				QNm accessField = (QNm)comparisonNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				Atomic leftKey = getValueFromNode(comparisonNode.getChild(2));
				double cost = costManager.getCostEstimation(tableName.stringValue(), accessField.getLocalName(), leftKey, null);
				return new LeftRangeAccessColumn(bindVariable, tableName.stringValue(), accessField.getLocalName(), leftKey, cost, comparisonNode);
			}
		}
		return null;
	}
	
	private AccessColumn selectAccessColumn(String bindVariable, List<AST> comparisonExpresions, Str tableName){
		logger.debug("Access table : "+tableName);
		EqualAccessColumn equalAccessColumn = findEqualAccessPredicate(bindVariable, comparisonExpresions, tableName);
		if (equalAccessColumn!=null) {
			return equalAccessColumn;
		} else {
			BothRangeAccessColumn fullRangeAccessColumn = findFullRangeAccessColumn(bindVariable, comparisonExpresions, tableName);
			if (fullRangeAccessColumn != null) {
				return fullRangeAccessColumn;
			} else {
				RightRangeAccessColumn rightRangeAccessColumn = findFirstRightRangeAccessPredicate(bindVariable, comparisonExpresions, tableName);
				if (rightRangeAccessColumn != null) {
					return rightRangeAccessColumn;
				} else {
					LeftRangeAccessColumn leftRangeAccessColumn = findFirstLeftRangeAccessPredicate(bindVariable, comparisonExpresions, tableName);
					if (leftRangeAccessColumn != null)
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

	private void removeCondition(AST removingNode) {
		AST selectionNode = removingNode.getParent();
		AST forbindNode = selectionNode.getParent();
		forbindNode.replaceChild(2, selectionNode.getLastChild());
	}
	
	protected Expr anyExpr(AST node) throws QueryException {
		logger.debug("Found expression :"+node.getStringValue()+" "+node.getType());
		if (node.getType()==XQ.FunctionCall && ((QNm)node.getValue()).getLocalName().equals("collection")){
			AST parent = node.getParent();
			if (parent!=null && parent.getType()==XQ.ForBind && parent.getChildCount()==3 && parent.getChild(2).getType()==XQ.Selection){
				logger.debug("Found selection");
				
				String bindedVariable = parent.getChild(0).getChild(0).getStringValue();
				List<AST> comparisonExpresions = comparisonMap.get(bindedVariable);
				
				Str tableName = (Str)parent.getChild(1).getChild(0).getValue();
				Set<String> projectionFields = projectionMap.get(bindedVariable);
				logger.info("Projection test "+bindedVariable+" "+projectionFields);
				
				//Set<String> projectionFields = getFields(tableName.stringValue());

				AccessColumn accessColumn = selectAccessColumn(bindedVariable, comparisonExpresions, tableName);
				if (accessColumn!=null){
					if (accessColumn instanceof EqualAccessColumn) {
						EqualAccessColumn equalMathingColumn = ((EqualAccessColumn)accessColumn);
						logger.info("Found equal match "+equalMathingColumn.getAccessColumn());
						logger.info("Found equal value "+equalMathingColumn.getKey());
						Function fn = new RangeAccessFunction(equalMathingColumn, projectionFields);
						return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
					} else {
						if (accessColumn instanceof RangeAccessColumn && ((RangeAccessColumn)accessColumn).getCost() < 0.4) {
							RangeAccessColumn rangeAccessColumn = ((RangeAccessColumn)accessColumn);
							
							if (rangeAccessColumn instanceof BothRangeAccessColumn) {
								removeCondition(((BothRangeAccessColumn)rangeAccessColumn).getLeftBoundComparisonExpr());
								removeCondition(((BothRangeAccessColumn)rangeAccessColumn).getRighBoundComparisonExpr());
							} else
								if (rangeAccessColumn instanceof LeftRangeAccessColumn) 
									removeCondition(((LeftRangeAccessColumn)rangeAccessColumn).getLeftBoundComparisonExpr());
								else
									if (rangeAccessColumn instanceof RightRangeAccessColumn) 
										removeCondition(((RightRangeAccessColumn)rangeAccessColumn).getRightBoundComparisonExpr());
							
							logger.info("Found range access "+rangeAccessColumn.getAccessColumn()+" with cost "+rangeAccessColumn.getCost());
							Function fn = new RangeAccessFunction((RangeAccessColumn)accessColumn, projectionFields);
							return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
						} else {
							Function fn = new FullScanFunction(tableName.stringValue(), projectionFields);
							return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
						}
					}
				}else{
					Function fn = new FullScanFunction(tableName.stringValue(), projectionFields);
					return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
				}
			} else
			if (parent!=null && parent.getType()==XQ.ForBind && parent.getChildCount()==3 && (parent.getChild(2).getType()==XQ.End || parent.getChild(2).getType()==XQ.ForBind 
			|| parent.getChild(2).getType()==XQ.LetBind) ) {
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
