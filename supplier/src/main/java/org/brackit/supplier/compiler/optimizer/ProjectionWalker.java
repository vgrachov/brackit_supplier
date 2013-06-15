/*******************************************************************************
 * [New BSD License]
 *  Copyright (c) 2012, Volodymyr Grachov <vladimir.grachov@gmail.com>  
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Brackit Project Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.brackit.supplier.compiler.optimizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.XQ;
import org.brackit.xquery.compiler.optimizer.walker.Walker;

/**
 * Build Map between XQuery binded variables and StepExprs.
 * 
 * @author Volodymyr Grachov
 */
public final class ProjectionWalker extends Walker {
	
	private static final Logger logger = Logger.getLogger(ProjectionWalker.class);
	
	// map binded variable to list of StepExprs
	private Map<String,Set<String>> projectionMap = new HashMap<String,Set<String>>();
	
	public ProjectionWalker() {

	}
	
	protected AST visit(AST node) {
		if (node.getType() == XQ.PathExpr) {
			String bindedVariable = node.getChild(0).getStringValue();
			String accessedPath = ((QNm)node.getChild(1).getChild(1).getChild(0).getValue()).stringValue();
			logger.info(bindedVariable+" - "+accessedPath);
			Set<String> accessedPathsExpr = null;
			if (projectionMap.containsKey(bindedVariable)) {
				accessedPathsExpr = projectionMap.get(bindedVariable);
			} else {
				accessedPathsExpr = new HashSet<String>();
			}
			accessedPathsExpr.add(accessedPath);
			projectionMap.put(bindedVariable, accessedPathsExpr);
		}
		return node;
	}
	
	/**
	 * return copy of projection map
	 */
	public Map<String,Set<String>> getProjectionMap() {
		return new HashMap<String, Set<String>>(projectionMap);
	}
}
