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
package org.brackit.supplier.collection;

import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.catalog.Catalog;
import org.brackit.berkeleydb.cursor.FullTableScanCursor;
import org.brackit.relational.api.cursor.ITupleCursor;
import org.brackit.relational.api.impl.DatabaseAccessFactory;
import org.brackit.relational.api.transaction.ITransaction;
import org.brackit.relational.metadata.Schema;
import org.brackit.relational.metadata.tuple.Tuple;
import org.brackit.supplier.xquery.node.AbstractRDBMSNode;
import org.brackit.supplier.xquery.node.RowNode;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.node.AbstractCollection;
import org.brackit.xquery.node.parser.SubtreeParser;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Kind;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class FullScanCollection extends AbstractCollection<AbstractRDBMSNode>{

	private static final Logger logger = Logger.getLogger(FullScanCollection.class);
	private final Schema schema;
	private final Set<String> projectionFields;
	
	private ITransaction transaction;
	
	public FullScanCollection(String tableName, ITransaction transaction, Set<String> projectionFields){
		super(tableName);
		this.schema = Catalog.getInstance().getSchemaByDatabaseName(tableName);
		this.transaction = transaction;
		this.projectionFields = projectionFields;
	}
	
	public void delete() throws DocumentException {
		logger.debug("FullScanCollection delete");
		
	}

	public void remove(long documentID) throws OperationNotSupportedException,
			DocumentException {
		logger.debug("FullScanCollection remove");
		
	}

	public AbstractRDBMSNode getDocument() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Stream<? extends AbstractRDBMSNode> getDocuments() throws DocumentException {
		
		final ITupleCursor tupleCursor = DatabaseAccessFactory.getInstance().create(schema.getDatabaseName()).getFullScanCursor(transaction, projectionFields);
		//final ITupleCursor tupleCursor = new FullTableScanCursor(schema.getDatabaseName(), null);
		tupleCursor.open();
		
		return new Stream<RowNode>() {
			private int counter = 0;
			private long timer = 0;
			public RowNode next() throws DocumentException {
				long start = System.currentTimeMillis();
				Tuple tuple =  tupleCursor.next();
				counter++;
				if (counter % 100000 == 0)
					logger.debug(counter);
				if (tuple!=null){
					RowNode rowNode = new RowNode(tuple,schema,transaction, projectionFields);
					timer +=System.currentTimeMillis()-start;
					return rowNode;
				}else
					return null;
			}

			public void close() {
				tupleCursor.close();
				if (logger.isInfoEnabled())
					logger.info("Time for scan table "+schema.getDatabaseName()+" - "+timer);
			}
			
		}; 
		

	}

	public AbstractRDBMSNode add(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
