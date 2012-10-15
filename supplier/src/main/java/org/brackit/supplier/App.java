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
package org.brackit.supplier;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.supplier.compiler.MongoDBQueryCompiler;
import org.brackit.supplier.io.helper.IOHelper;
import org.brackit.supplier.store.DataStore;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.compiler.CompileChain;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

/**
 * Hello world!
 *
 */
public class App 
{
	private static IOHelper ioHelper = new IOHelper();
	private static final Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args ) throws IOException, QueryException
    {
    	logger.debug("Start");
    	String content = ioHelper.getContent(new File("c:/test.xq"));
    	logger.debug("Query \n"+content);
    	XQuery xq = new XQuery(new MongoDBQueryCompiler(), content);
    	xq.setPrettyPrint(true);
		QueryContext ctx = new QueryContext(new DataStore());
		
    	xq.serialize(ctx, System.out);
    	
    }
}
