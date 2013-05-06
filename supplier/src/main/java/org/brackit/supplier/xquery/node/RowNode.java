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
package org.brackit.supplier.xquery.node;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.brackit.relational.api.IDatabaseAccess;
import org.brackit.relational.api.impl.DatabaseAccessFactory;
import org.brackit.relational.api.transaction.ITransaction;
import org.brackit.relational.metadata.Schema;
import org.brackit.relational.metadata.tuple.AtomicDate;
import org.brackit.relational.metadata.tuple.AtomicDouble;
import org.brackit.relational.metadata.tuple.AtomicInteger;
import org.brackit.relational.metadata.tuple.ColumnType;
import org.brackit.relational.metadata.tuple.Tuple;
import org.brackit.relational.properties.RelationalStorageProperties;
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
	private final ITransaction transaction;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(RelationalStorageProperties.getDatePattern());
	
	
	public RowNode(Tuple tuple, Schema schema, ITransaction transaction) throws DocumentException{
		super(NodeType.Row, new QNm("row"), null);
		this.tuple = tuple;
		this.schema = schema;
		this.transaction = transaction;
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
					//logger.debug(tuple.getFields()[counter].toString());
					FieldNode fieldNode = null;
					if (schema.getColumns()[counter].getType() == ColumnType.String)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName()), RowNode.this, new Str(tuple.getFields()[counter].toString()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Integer)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName()), RowNode.this, new Int(((AtomicInteger)tuple.getFields()[counter]).getData()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Double)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName()), RowNode.this, new Dbl(((AtomicDouble)tuple.getFields()[counter]).getData()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Char)
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName()), RowNode.this, new Str(tuple.getFields()[counter].toString()));
					else
					if (schema.getColumns()[counter].getType() == ColumnType.Date){
						AtomicDate atomicDate = (AtomicDate)tuple.getFields()[counter];
						fieldNode = new FieldNode(new QNm(schema.getColumns()[counter].getColumnName()), RowNode.this, new Str(DATE_FORMAT.format(new java.util.Date(atomicDate.getData()))));
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
	public void delete() throws DocumentException {
		if (logger.isDebugEnabled())
			logger.debug("delete node from "+schema.getDatabaseName());
		IDatabaseAccess databaseAccess = DatabaseAccessFactory.getInstance().create(schema.getDatabaseName());
		boolean result = false;
		if (transaction != null)
			result = databaseAccess.delete(tuple,transaction);
		else
			result = databaseAccess.delete(tuple,null);
		if (logger.isDebugEnabled())
			logger.debug("Result "+result);
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
