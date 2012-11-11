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
package org.brackit.supplier.xquery.node;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.Schema;
import org.brackit.berkeleydb.catalog.Catalog;
import org.brackit.berkeleydb.cursor.ITupleCursor;
import org.brackit.berkeleydb.cursor.TupleCursor;
import org.brackit.berkeleydb.cursor.TupleCursor.CursorType;
import org.brackit.berkeleydb.tuple.Tuple;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.node.AbstractCollection;
import org.brackit.xquery.node.parser.SubtreeParser;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Kind;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class TableCollection extends AbstractCollection<AbstractRDBMSNode>{

	private static final Logger logger = Logger.getLogger(TableCollection.class);
	private final Schema schema;
	
	public TableCollection(String tableName){
		super(tableName);
		this.schema = Catalog.getInstance().getSchemaByDatabaseName("LINEITEM");
	}
	
	public void delete() throws DocumentException {
		// TODO Auto-generated method stub
		
	}

	public void remove(long documentID) throws OperationNotSupportedException,
			DocumentException {
		// TODO Auto-generated method stub
		
	}

	public AbstractRDBMSNode getDocument() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Stream<? extends AbstractRDBMSNode> getDocuments() throws DocumentException {
		System.out.println("Get Docs");
		final ITupleCursor tupleCursor = new TupleCursor("LINEITEM",CursorType.FullScan);
		tupleCursor.open();
		return new Stream<RowNode>() {
			private int counter = 0;
			public RowNode next() throws DocumentException {
				Tuple tuple =  tupleCursor.next();
				if (tuple!=null)
					return new RowNode(tuple,schema);
				else
					return null;
			}

			public void close() {
				tupleCursor.close();
			}
			
		}; 
		

	}

	public AbstractRDBMSNode add(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
