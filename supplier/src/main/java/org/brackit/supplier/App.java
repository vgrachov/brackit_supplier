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
package org.brackit.supplier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.environment.BerkeleyDBEnvironment;
import org.brackit.supplier.api.transaction.ITransaction;
import org.brackit.supplier.api.transaction.ITransactionManager;
import org.brackit.supplier.api.transaction.TransactionException;
import org.brackit.supplier.api.transaction.impl.BerkeleyDBTransactionManager;
import org.brackit.supplier.compiler.RelationalCompilerChain;
import org.brackit.supplier.io.helper.IOHelper;
import org.brackit.supplier.store.RelationalDataStore;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.compiler.optimizer.DefaultOptimizer;

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
	
    public static void main( String[] args ) throws IOException, QueryException, InterruptedException,TransactionException{
		DefaultOptimizer.JOIN_DETECTION = true;
		DefaultOptimizer.UNNEST = true;

    	logger.debug("Start");
    	String content = ioHelper.getContent(new File("G:\\Projects\\.git\\brackit_supplier\\supplier\\src\\test\\resources\\queries\\q01.xq"));
    	logger.debug("Query \n"+content);
    	XQuery xq = new XQuery(new RelationalCompilerChain(), content);
    	xq.setPrettyPrint(true);
    	long start = System.currentTimeMillis();
    	ITransactionManager transactionManager = new BerkeleyDBTransactionManager();
    	ITransaction transaction = null;
    	try {
			transaction = transactionManager.begin();
		} catch (TransactionException e) {
			logger.fatal(e.getMessage());
			throw e;
		}
    	RelationalQueryContext ctx = new RelationalQueryContext(new RelationalDataStore(), transaction);
		PrintStream printStream = new PrintStream(new File("G:\\Projects\\.git\\brackit_supplier\\supplier\\src\\test\\resources\\queries\\10mb\\q011.res"));
    	xq.serialize(ctx, printStream);
    	logger.info("Full query time "+(System.currentTimeMillis()-start));
    	printStream.close();
    	if (transaction!=null){
    		try{
    			transaction.commit();
    		}catch (Exception e) {
    			logger.error(e.getMessage());
				transaction.abort();
			}
    	}

    }
}
