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
package org.brackit.supplier.xquery.node;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.Schema;
import org.brackit.berkeleydb.tuple.AtomicChar;
import org.brackit.berkeleydb.tuple.AtomicDate;
import org.brackit.berkeleydb.tuple.AtomicDouble;
import org.brackit.berkeleydb.tuple.AtomicInteger;
import org.brackit.berkeleydb.tuple.AtomicString;
import org.brackit.berkeleydb.tuple.ColumnType;
import org.brackit.berkeleydb.tuple.Tuple;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Date;
import org.brackit.xquery.atomic.Dbl;
import org.brackit.xquery.atomic.Int;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.node.parser.SubtreeHandler;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class RowNode extends AbstractRDBMSNode {

	private static final Logger logger = Logger.getLogger(RowNode.class);
	
	private final Tuple tuple;
	private final Schema schema;
	
	
	public RowNode(Tuple tuple, Schema schema) throws DocumentException{
		super(NodeType.Row, new QNm("row"), null);
		this.tuple = tuple;
		this.schema = schema;
	}
	
	@Override
	public Atomic getValue() throws DocumentException {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<tuple.getFields().length;i++)
			sb.append(tuple.getFields()[i]);
		return new Str(sb.toString());
	}

	@Override
	public void setValue(Atomic value) throws OperationNotSupportedException,
			DocumentException {
		logger.fatal("Should not be visited");
	}

	@Override
	public Stream<? extends AbstractRDBMSNode> getChildren()
			throws DocumentException {
		return new Stream<FieldNode>() {
			private int counter = 0;
			public FieldNode next() throws DocumentException {
				if (counter<schema.getColumns().length){
					//logger.debug(schema.getColumns()[counter].getColumnName().toLowerCase());
					//logger.debug(tuple);
					logger.debug(tuple.getFields()[counter].toString());
					FieldNode fieldNode = null;
					if (schema.getColumns()[counter].getType() == ColumnType.String)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName().toLowerCase()), RowNode.this, new Str(tuple.getFields()[counter].toString()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Integer)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName().toLowerCase()), RowNode.this, new Int(((AtomicInteger)tuple.getFields()[counter]).getData()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Double)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName().toLowerCase()), RowNode.this, new Dbl(((AtomicDouble)tuple.getFields()[counter]).getData()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Char)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName().toLowerCase()), RowNode.this, new Str(tuple.getFields()[counter].toString()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Date){
						AtomicDate atomicDate = (AtomicDate)tuple.getFields()[counter];
						//String date = String.valueOf(atomicDate.getData().getYear()+1900)+"-"+String.valueOf(atomicDate.getData().getMonth())+"-"+String.valueOf(atomicDate.getData().getDay());
						//logger.info(date);
						Date date = new Date((short)(atomicDate.getData().getYear()+1900),(byte)atomicDate.getData().getMonth(),(byte)atomicDate.getData().getDay(),null);
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName().toLowerCase()), RowNode.this, new Date((short)(atomicDate.getData().getYear()+1900),(byte)atomicDate.getData().getMonth(),(byte)atomicDate.getData().getDay(),null));
					}else
						throw new IllegalArgumentException();
					counter++;
					return fieldNode;
				}else
					return null;
			}

			public void close() {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public QNm getName() throws DocumentException {
		return new QNm("row");
	}

	@Override
	public void parse(SubtreeHandler handler) throws DocumentException {
		logger.debug("Call parser for row node");
		handler.begin();
		handler.beginFragment();
		handler.startElement(new QNm("values"));
		StringBuilder valuesSerializeBuffer = new StringBuilder();
		for (int i=0;i<tuple.getFields().length;i++){
			valuesSerializeBuffer.append(tuple.getFields()[i]);
			if (i!=tuple.getFields().length-1)
				valuesSerializeBuffer.append(",");
		}
		handler.text(new QNm(valuesSerializeBuffer.toString()));
		handler.endElement(new QNm("values"));
		handler.endFragment();
		handler.end();		
	}
	
}
