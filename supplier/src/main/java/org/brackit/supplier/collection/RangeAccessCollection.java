/*******************************************************************************
 * [New BSD License]
 *   Copyright (c) 2012-2013, Volodymyr Grachov <vladimir.grachov@gmail.com>  
 *   All rights reserved.
 *   
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *       * Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *       * Neither the name of the Brackit Project Team nor the
 *         names of its contributors may be used to endorse or promote products
 *         derived from this software without specific prior written permission.
 *   
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *   ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.brackit.supplier.collection;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.catalog.Catalog;
import org.brackit.relational.api.cursor.ITupleCursor;
import org.brackit.relational.api.impl.DatabaseAccessFactory;
import org.brackit.relational.api.transaction.ITransaction;
import org.brackit.relational.metadata.Schema;
import org.brackit.relational.metadata.tuple.AtomicChar;
import org.brackit.relational.metadata.tuple.AtomicDate;
import org.brackit.relational.metadata.tuple.AtomicDouble;
import org.brackit.relational.metadata.tuple.AtomicInteger;
import org.brackit.relational.metadata.tuple.AtomicString;
import org.brackit.relational.metadata.tuple.AtomicValue;
import org.brackit.relational.metadata.tuple.Column;
import org.brackit.relational.metadata.tuple.ColumnType;
import org.brackit.relational.metadata.tuple.Tuple;
import org.brackit.relational.properties.RelationalStorageProperties;
import org.brackit.supplier.access.AccessColumn;
import org.brackit.supplier.access.BothRangeAccessColumn;
import org.brackit.supplier.access.EqualAccessColumn;
import org.brackit.supplier.access.LeftRangeAccessColumn;
import org.brackit.supplier.access.RightRangeAccessColumn;
import org.brackit.supplier.xquery.node.AbstractRDBMSNode;
import org.brackit.supplier.xquery.node.RowNode;
import org.brackit.xquery.atomic.Dec;
import org.brackit.xquery.atomic.Int32;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.node.AbstractCollection;
import org.brackit.xquery.node.parser.SubtreeParser;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class RangeAccessCollection extends AbstractCollection<AbstractRDBMSNode> {

	private final AccessColumn accessColumn;
	private static final Logger logger = Logger.getLogger(RangeAccessCollection.class);
	private final ITransaction transaction;
	private final Set<String> projectionFields;
	
	
	public RangeAccessCollection(AccessColumn accessColumn,ITransaction transaction, Set<String> projectionFields) {
		super(accessColumn.getTableName());
		this.accessColumn = accessColumn;
		this.transaction = transaction;
		this.projectionFields = projectionFields;
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
	
	private AtomicValue getAtomicBerkeleyDBValue(Column column, org.brackit.xquery.atomic.Atomic accessColumnKey){
		SimpleDateFormat dateFormat = new SimpleDateFormat(RelationalStorageProperties.getDatePattern());
		if (column.getType() == ColumnType.String){
			return new AtomicString(column.getColumnName(), ((Str)accessColumnKey).stringValue());
		}else
		if (column.getType() == ColumnType.Integer){
			return new AtomicInteger(column.getColumnName(), ((Int32)accessColumnKey).intValue());
		}else
		if (column.getType() == ColumnType.Double){
			return new AtomicDouble(column.getColumnName(), ((Dec)accessColumnKey).doubleValue());
		}else
		if (column.getType() == ColumnType.Char){
			return new AtomicChar(column.getColumnName(), ((Str)accessColumnKey).stringValue().charAt(0));
		}else
		if (column.getType() == ColumnType.Date){
			try{
				return new AtomicDate(column.getColumnName(), dateFormat.parse(((Str)accessColumnKey).stringValue()).getTime() );
			} catch(ParseException e){
				logger.error(e.getMessage());
				return null;
			}
		}else
			throw new IllegalArgumentException("Not supported type for index scan");
	}

	public Stream<? extends AbstractRDBMSNode> getDocuments()
			throws DocumentException {
		logger.debug("Start scan");
		final Schema schema = Catalog.getInstance().getSchemaByDatabaseName(accessColumn.getTableName());
		Column column = null;
		for (int i=0;i<schema.getColumns().length;i++)
			if (schema.getColumns()[i].getColumnName().equalsIgnoreCase(accessColumn.getAccessColumn())){
				column = schema.getColumns()[i];
			}
		if (column == null)
			throw new IllegalArgumentException("Column with name "+accessColumn.getAccessColumn()+" is not found");
		if (!column.isDirectIndexExist())
			throw new IllegalArgumentException("Column "+accessColumn.getAccessColumn()+" don't have index");
		AtomicValue rightKey = null;
		AtomicValue leftKey = null;
		if (accessColumn instanceof EqualAccessColumn){
			leftKey = getAtomicBerkeleyDBValue(column,((EqualAccessColumn)accessColumn).getKey());
			rightKey = getAtomicBerkeleyDBValue(column,((EqualAccessColumn)accessColumn).getKey());
		}else
		if (accessColumn instanceof BothRangeAccessColumn){
			leftKey = getAtomicBerkeleyDBValue(column,((BothRangeAccessColumn)accessColumn).getLeftKey());
			rightKey = getAtomicBerkeleyDBValue(column,((BothRangeAccessColumn)accessColumn).getRightKey());
		}else
		if (accessColumn instanceof RightRangeAccessColumn){
			rightKey = getAtomicBerkeleyDBValue(column,((RightRangeAccessColumn)accessColumn).getRightKey());
		}else
		if (accessColumn instanceof LeftRangeAccessColumn){
			leftKey = getAtomicBerkeleyDBValue(column,((LeftRangeAccessColumn)accessColumn).getLeftKey());
		}else
			throw new IllegalArgumentException("Such range access method is not supported");
			
		//Column column, Atomic leftKey, Atomic rightKey
		final ITupleCursor tupleCursor = DatabaseAccessFactory.getInstance().create(schema.getDatabaseName()).getRangeIndexScanCursor(column,leftKey,rightKey,transaction, projectionFields);
		tupleCursor.open();
		return new Stream<AbstractRDBMSNode>() {
			
			private long timer = 0;
			
			public AbstractRDBMSNode next() throws DocumentException {
				long start = System.currentTimeMillis();
				Tuple tuple = tupleCursor.next();
				if (tuple!=null){
					// logger.debug("Extracted tuple : "+tuple);
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
