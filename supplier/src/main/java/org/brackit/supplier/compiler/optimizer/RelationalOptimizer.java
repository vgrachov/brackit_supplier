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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.optimizer.Stage;
import org.brackit.xquery.compiler.optimizer.TopDownOptimizer;
import org.brackit.xquery.module.StaticContext;

public class RelationalOptimizer extends TopDownOptimizer {
	
	private static final Logger logger = Logger.getLogger(RelationalOptimizer.class);
	
	private final TableAccessIdentifier tableAccessIdentifier;
	private final ProjectionWalker walker = new ProjectionWalker();

	private final ComparisonExpressionIdentifier comparisonExpressionIdentifier;
	private final ComparisonExprWalker comparisonWalker = new ComparisonExprWalker();

	
	public RelationalOptimizer(Map<QNm, Str> options){
		super(options);
		tableAccessIdentifier = new TableAccessIdentifier();
		comparisonExpressionIdentifier = new ComparisonExpressionIdentifier();
		stages.add(tableAccessIdentifier);
		stages.add(comparisonExpressionIdentifier);
	}

	private class TableAccessIdentifier implements Stage {
		
		public AST rewrite(StaticContext sctx, AST ast) throws QueryException {
			walker.walk(ast);
			return ast;
		}
	}

	private class ComparisonExpressionIdentifier implements Stage {
		
		public AST rewrite(StaticContext sctx, AST ast) throws QueryException {
			comparisonWalker.walk(ast);
			return ast;
		}
	}

	
	public Map<String,Set<String>> getProjectionMap() {
		return walker.getProjectionMap();
	}
	
	public Map<String,List<AST>> getComparisonExprMap() {
		return comparisonWalker.getComparisonExprMap();
	}
}
