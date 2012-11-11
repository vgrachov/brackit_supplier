/*******************************************************************************
 * Copyright (c) 2012 Volodymyr Grachov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Volodymyr Grachov - initial API and implementation
 ******************************************************************************/
package org.brackit.supplier.compiler.translator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.relational.xquery.function.fn.RowCollectionFunction;
import org.brackit.supplier.access.AccessColumn;
import org.brackit.supplier.access.EqualAccessColumn;
import org.brackit.supplier.access.FullRangeAccessColumn;
import org.brackit.supplier.function.RangeAccessFunction;
import org.brackit.supplier.xquery.node.RangeAccessRowCollection;
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


public class RelationalTranslator extends TopDownTranslator {

	private static final Logger logger = Logger.getLogger(RelationalTranslator.class);
	
	public RelationalTranslator(Map<QNm, Str> options) {
		super(options);
	}
	
	
	protected List<AST> foundPredicates(AST node) {
		if (node==null) 
			return new ArrayList<AST>();
		List<AST> comparisonPredicates = new ArrayList<AST>();
		for (int i=0;i<node.getChildCount();i++)
			if (node.getChild(i).getType() == XQ.ComparisonExpr)
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
		}else
			throw new IllegalArgumentException("Type can't be process");
		return value;
	}
	
	private EqualAccessColumn findEqualAccessPredicate(List<AST> comparisonExpresions, Str tableName){
		for (int i=0;i<comparisonExpresions.size();i++){
			AST accessNode = comparisonExpresions.get(i);
			if (accessNode.getChild(0).getType() == XQ.GeneralCompEQ){
				QNm accessField = (QNm)accessNode.getChild(1).getChild(1).getChild(1).getChild(0).getValue();
				logger.debug("Aceess to field : "+accessField);
				AST valueNode = accessNode.getChild(2);
				org.brackit.xquery.atomic.Atomic value = getValueFromNode(valueNode);
				return new EqualAccessColumn(null, tableName.stringValue(), accessField.localName, value);
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
					if (accessNode.getChild(0).getType()==XQ.GeneralCompLE || accessNode.getChild(0).getType()==XQ.GeneralCompLT){
						isRightRangeFound = true;
						rightKey = getValueFromNode(accessNode.getChild(2));
					}else
					if (accessNode.getChild(0).getType()==XQ.GeneralCompGE || accessNode.getChild(0).getType()==XQ.GeneralCompGT){
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
	
	private AccessColumn selectAccessColumn(List<AST> comparisonExpresions, Str tableName){
		logger.debug("Access table : "+tableName);
		/*EqualAccessColumn equalAccessColumn = findEqualAccessPredicate(comparisonExpresions, tableName);
		if (equalAccessColumn==null)
			return equalAccessColumn;
		else{*/
			FullRangeAccessColumn fullRangeAccessColumn = findFullRangeAccessColumn(comparisonExpresions, tableName);
			return fullRangeAccessColumn;
		//}
	}
	
	protected Operator anyOp(Operator in, AST node) throws QueryException {
		/*logger.debug("Found operation : "+node.getStringValue()+" "+node.getType());
		if (node.getType() == XQ.ForBind && node.getChildCount()==3 && node.getChild(2).getType()==XQ.Selection){
			logger.debug("Found selection");
			List<AST> comparisonExpresions = foundPredicates(node.getChild(2));
			Str tableName = (Str)node.getChild(1).getChild(0).getValue();
			AccessColumn accessColumn = selectAccessColumn(comparisonExpresions,tableName);
			if (accessColumn!=null){
				Function fn = new RangeAccessFunction((FullRangeAccessColumn)accessColumn);
				return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
				
			}
			return super.anyOp(in, node);
		}*/
		return super.anyOp(in, node);
	}
	

	protected Expr anyExpr(AST node) throws QueryException {
		logger.debug("Found expression :"+node.getStringValue()+" "+node.getType());
		if (node.getType()==XQ.FunctionCall && ((QNm)node.getValue()).getLocalName().equals("collection")){
			AST parent = node.getParent();
			if (parent!=null && parent.getType()==XQ.ForBind && parent.getChildCount()==3 && parent.getChild(2).getType()==XQ.Selection){
				logger.debug("Found selection");
				List<AST> comparisonExpresions = foundPredicates(parent.getChild(2));
				Str tableName = (Str)parent.getChild(1).getChild(0).getValue();
				AccessColumn accessColumn = selectAccessColumn(comparisonExpresions,tableName);
				if (accessColumn!=null){
					Function fn = new RangeAccessFunction((FullRangeAccessColumn)accessColumn);
					return new FunctionExpr(node.getStaticContext(), fn, super.anyExpr(node.getLastChild()));
				}
			}
		}
		return super.anyExpr(node);
	}
}
