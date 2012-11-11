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
package org.brackit.supplier.collection;



import org.apache.log4j.Logger;
import org.brackit.berkeleydb.Schema;
import org.brackit.berkeleydb.catalog.Catalog;
import org.brackit.berkeleydb.cursor.ITupleCursor;
import org.brackit.berkeleydb.cursor.RangeIndexSearchCursor;
import org.brackit.berkeleydb.cursor.TupleCursor;
import org.brackit.berkeleydb.cursor.TupleCursor.CursorType;
import org.brackit.berkeleydb.tuple.Atomic;
import org.brackit.berkeleydb.tuple.AtomicChar;
import org.brackit.berkeleydb.tuple.AtomicDouble;
import org.brackit.berkeleydb.tuple.AtomicInteger;
import org.brackit.berkeleydb.tuple.AtomicString;
import org.brackit.berkeleydb.tuple.Column;
import org.brackit.berkeleydb.tuple.ColumnType;
import org.brackit.berkeleydb.tuple.Tuple;
import org.brackit.supplier.access.FullRangeAccessColumn;
import org.brackit.supplier.access.LeftRangeAccessColumn;
import org.brackit.supplier.access.RangeAccessColumn;
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

	private final RangeAccessColumn accessColumn;
	private static final Logger logger = Logger.getLogger(RangeAccessCollection.class);
	
	
	public RangeAccessCollection(RangeAccessColumn accessColumn) {
		super(accessColumn.getTableName());
		this.accessColumn = accessColumn;
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
	
	private Atomic getAtomicBerkeleyDBValue(Column column, org.brackit.xquery.atomic.Atomic accessColumnKey){
		if (column.getType() == ColumnType.String){
			return new AtomicString(column.getColumnName(), ((Str)accessColumnKey).stringValue());
		}else
		if (column.getType() == ColumnType.Integer){
			return new AtomicInteger(column.getColumnName(), ((Int32)accessColumnKey).intValue());
		}else
		if (column.getType() == ColumnType.Double){
			return new AtomicDouble(column.getColumnName(), ((Dec)accessColumnKey).doubleValue());
		}else
			throw new IllegalArgumentException("Not supported type for index scan");
	}

	public Stream<? extends AbstractRDBMSNode> getDocuments()
			throws DocumentException {
		logger.debug("Start scan");
		final Schema schema = Catalog.getInstance().getSchemaByDatabaseName(accessColumn.getTableName().toUpperCase());
		Column column = null;
		for (int i=0;i<schema.getColumns().length;i++)
			if (schema.getColumns()[i].getColumnName().equalsIgnoreCase(accessColumn.getAccessColumn())){
				column = schema.getColumns()[i];
			}
		if (column == null)
			throw new IllegalArgumentException("Column with name "+accessColumn.getAccessColumn()+" is not found");
		if (!column.isDirectIndexExist())
			throw new IllegalArgumentException("Column "+accessColumn.getAccessColumn()+" don't have index");
		Atomic rightKey = null;
		Atomic leftKey = null;
		if (accessColumn instanceof FullRangeAccessColumn){
			leftKey = getAtomicBerkeleyDBValue(column,((FullRangeAccessColumn)accessColumn).getLeftKey());
			rightKey = getAtomicBerkeleyDBValue(column,((FullRangeAccessColumn)accessColumn).getRightKey());
		}else
		if (accessColumn instanceof RightRangeAccessColumn){
			rightKey = getAtomicBerkeleyDBValue(column,((RightRangeAccessColumn)accessColumn).getRightKey());
		}else
		if (accessColumn instanceof LeftRangeAccessColumn){
			leftKey = getAtomicBerkeleyDBValue(column,((LeftRangeAccessColumn)accessColumn).getLeftKey());
		}else
			throw new IllegalArgumentException("Such range access method is not supported");
			
		//Column column, Atomic leftKey, Atomic rightKey
		final ITupleCursor tupleCursor = new RangeIndexSearchCursor(column,leftKey,rightKey);
		tupleCursor.open();
		return new Stream<AbstractRDBMSNode>() {

			public AbstractRDBMSNode next() throws DocumentException {
				Tuple tuple = tupleCursor.next();
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
