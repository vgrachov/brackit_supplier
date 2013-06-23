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
package org.brackit.supplier.compiler.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.XQ;
import org.brackit.xquery.compiler.optimizer.walker.Walker;

/**
 * Build map of bind variable and comparison expressions associated with it.
 * Should be used for assigning access paths to ForBind operator.
 * 
 */
public class ComparisonExprWalker extends Walker {
	
	private static final Logger logger = Logger.getLogger(ComparisonExprWalker.class);
	
	// map binded variable to list of ComparisonExpr
	private Map<String,List<AST>> comparisonExprMap = new HashMap<String,List<AST>>();
	
	public ComparisonExprWalker() {

	}
	
	protected AST visit(AST node) {
		if (node.getType() == XQ.ComparisonExpr && node.getChild(1).getType() == XQ.PathExpr && (node.getChild(2).getType() != XQ.PathExpr && node.getChild(2).getType() != XQ.ArithmeticExpr)) {
			String bindedVariable = node.getChild(1).getChild(0).getStringValue();
			logger.info("Bind variable "+bindedVariable);
			List<AST> comparisonExprList = null;
			if (comparisonExprMap.containsKey(bindedVariable)) {
				comparisonExprList = comparisonExprMap.get(bindedVariable);
			} else {
				comparisonExprList = new ArrayList<AST>();
			}
			comparisonExprList.add(node);
			comparisonExprMap.put(bindedVariable, comparisonExprList);
		}
		return node;
	}
	
	/**
	 * return comparison expr map
	 */
	public Map<String,List<AST>> getComparisonExprMap() {
		return comparisonExprMap;
	}
}
