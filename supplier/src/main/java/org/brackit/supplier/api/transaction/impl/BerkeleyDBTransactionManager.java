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

package org.brackit.supplier.api.transaction.impl;

import org.brackit.berkeleydb.environment.BerkeleyDBEnvironment;
import org.brackit.supplier.api.transaction.ITransaction;
import org.brackit.supplier.api.transaction.ITransactionManager;
import org.brackit.supplier.api.transaction.TransactionException;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.Transaction;
import com.sleepycat.db.TransactionConfig;

public class BerkeleyDBTransactionManager implements ITransactionManager {

	private Environment environment;
	private ThreadLocal<ITransaction> transactions;
	
	public BerkeleyDBTransactionManager(){
		environment = BerkeleyDBEnvironment.getInstance().getEnv();
		transactions = new ThreadLocal<ITransaction>();
	}
	
	public ITransaction begin() throws TransactionException {
		TransactionConfig transactionConfig = TransactionConfig.DEFAULT;
		try {
			Transaction transaction = environment.beginTransaction(null, transactionConfig);
			transactions.set(new BerkeleyDBTransaction(transaction));
			return transactions.get();
		} catch (DatabaseException e) {
			throw new TransactionException(e.getMessage());
		}
	}
	
	public ITransaction getCurrentTransaction() throws TransactionException{
		return transactions.get();
	}

}
