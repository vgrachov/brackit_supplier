/*******************************************************************************
\ * Copyright (c) 2012 Volodymyr Grachov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Volodymyr Grachov - initial API and implementation
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
				/*counter++;
				if (counter==1)
					return new RowNode(null);
				else
				if (counter == 2)
					return new RowNode(null);
				else
				if (counter == 3)
					return new RowNode(null);*/
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
