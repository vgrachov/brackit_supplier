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
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.XQ;
import org.brackit.xquery.compiler.optimizer.walker.Walker;

public final class TableIdentifierWalker extends Walker{
	
	private static final Logger logger = Logger.getLogger(TableIdentifierWalker.class);
	
	public TableIdentifierWalker(){

	}
	
	protected AST visit(AST node){
		return node;
	}
	
	
}