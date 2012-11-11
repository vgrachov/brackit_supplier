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
package org.brackit.supplier.compiler.optimizer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.optimizer.DefaultOptimizer;
import org.brackit.xquery.compiler.optimizer.Stage;
import org.brackit.xquery.compiler.optimizer.TopDownOptimizer;
import org.brackit.xquery.compiler.optimizer.walker.topdown.TopDownPipeline;
import org.brackit.xquery.module.StaticContext;

public class RelationalOptimizer extends TopDownOptimizer {
	
	private static final Logger logger = Logger.getLogger(RelationalOptimizer.class);
	private final TableAccessIdentifier tableAccessIdentifier;
	
	public RelationalOptimizer(Map<QNm, Str> options){
		super(options);
		tableAccessIdentifier = new TableAccessIdentifier(); 
		stages.add(tableAccessIdentifier);
	}

	private class TableAccessIdentifier implements Stage {
		
		public AST rewrite(StaticContext sctx, AST ast) throws QueryException {
			TableIdentifierWalker walker = new TableIdentifierWalker();
			walker.walk(ast);
			return ast;
		}

	}
	
}
