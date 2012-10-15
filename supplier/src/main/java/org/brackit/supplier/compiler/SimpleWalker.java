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
package org.brackit.supplier.compiler;

import java.util.HashMap;
import java.util.Map;

import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.XQ;
import org.brackit.xquery.compiler.optimizer.walker.Walker;

public class SimpleWalker extends Walker{
	
	private final Map<String, AST> tableAccess;
	
	public SimpleWalker(){
		tableAccess = new HashMap<String, AST>();
	}
	
	protected AST visit(AST node)
	{
		System.out.println(node.getType()+" "+node.getStringValue());
		if (node.getType() != XQ.FunctionCall) {
			return node;
		}
		
		// TODO: compared based on predefined QName
		if (!node.getStringValue().equalsIgnoreCase("collection")) {
			return node;
		}
		
		AST arg = node.getChild(0);
		if (arg.getType() != XQ.Str) {
			return node;
		}
		
		String tableName = arg.getStringValue();
		System.out.println("Table name "+arg.getStringValue());

		//TableInfo tableInfo = schema.getTableInfo(tableName);
		
		/*if (tableInfo == null) {
			return node;
		}*/
		
		AST forBind = node.getParent();
		if (forBind.getType() != XQ.ForBind) {
			return node;
		}
		
		AST variable = forBind.getChild(0).getChild(0);
		if (variable.getType() != XQ.Variable) {
			return node;
		}
		
		tableAccess.put(variable.getStringValue(), node);
		
		return node;
	}

	
}
