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
package org.brackit.supplier;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.supplier.compiler.RelationalCompilerChain;
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
	
    public static void main( String[] args ) throws IOException, QueryException, InterruptedException{
    	logger.debug("Start");
    	String content = ioHelper.getContent(new File("c:/test1.xq"));
    	logger.debug("Query \n"+content);
    	XQuery xq = new XQuery(new RelationalCompilerChain(), content);
    	xq.setPrettyPrint(true);
		QueryContext ctx = new QueryContext(new DataStore());
    	xq.serialize(ctx, System.out);
    	
    }
}
