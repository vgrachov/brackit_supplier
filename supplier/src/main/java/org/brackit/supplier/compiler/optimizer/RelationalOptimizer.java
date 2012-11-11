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
