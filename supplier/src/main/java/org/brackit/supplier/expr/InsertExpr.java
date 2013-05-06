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

package org.brackit.supplier.expr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.catalog.Catalog;
import org.brackit.berkeleydb.cursor.BerkeleydbDatabaseAccess;
import org.brackit.relational.api.IDatabaseAccess;
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
import org.brackit.relational.properties.RelationalStorageProperties;
import org.brackit.supplier.RelationalQueryContext;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.Tuple;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.expr.ConstructedNodeBuilder;
import org.brackit.xquery.xdm.Expr;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.Sequence;

public class InsertExpr extends ConstructedNodeBuilder implements Expr {

	private static final Logger logger = Logger.getLogger(InsertExpr.class);

	private final Expr sourceExpr;

	private final QNm tableName;
	
	private ITransaction transaction;

	public InsertExpr(Expr sourceExpr, QNm tableName) {
		this.sourceExpr = sourceExpr;
		this.tableName = tableName;
	}

	public Sequence evaluate(QueryContext ctx, Tuple tuple)
			throws QueryException {
		RelationalQueryContext queryContext = (RelationalQueryContext)ctx;
		this.transaction = queryContext.getTransaction();
		return evaluateToItem(ctx, tuple);
	}

	public Item evaluateToItem(QueryContext ctx, Tuple tuple)
			throws QueryException {
		insertInto(ctx, tuple);
		return null;
	}

	public boolean isUpdating() {
		return true;
	}

	public boolean isVacuous() {
		return false;
	}
	

	private AtomicValue getAtomicBerkeleyDBValue(Column column, Atomic value){
		if (column.getType() == ColumnType.String){
			return new AtomicString(column.getColumnName(), value.stringValue());
		}else
		if (column.getType() == ColumnType.Integer){
			return new AtomicInteger(column.getColumnName(), Integer.valueOf(value.stringValue()));
		}else
		if (column.getType() == ColumnType.Double){
			return new AtomicDouble(column.getColumnName(), Double.valueOf(value.stringValue()));
		}else
		if (column.getType() == ColumnType.Char){
			return new AtomicChar(column.getColumnName(), value.stringValue().charAt(0));
		}else
		if (column.getType() == ColumnType.Date){
			SimpleDateFormat dateFormat = new SimpleDateFormat(RelationalStorageProperties.getDatePattern());
			try{
				return new AtomicDate(column.getColumnName(), dateFormat.parse(value.stringValue()).getTime());
			}catch (ParseException e){
				//TODO: add exception
				logger.error(e.getMessage());
				return null;
			}
		}else
			
			throw new IllegalArgumentException("Not supported type for index scan");
	}
	
	
	private void insertInto(QueryContext ctx, Tuple tuple)
			throws QueryException {

		Sequence source = sourceExpr.evaluate(ctx, tuple);
		ContentList cList = new ContentList();
		buildContentSequence(ctx, cList, source);

		if (cList.size()==1){
			Node<?> insertNode = cList.get(0);
			Schema schema = Catalog.getInstance().getSchemaByDatabaseName(tableName.stringValue());
			Node<?> nextField = insertNode.getFirstChild();
			HashMap<QNm,Atomic> fieldsMap = new HashMap<QNm, Atomic>();
			while (nextField != null){
				QNm fieldName = nextField.getName();
				Atomic value = nextField.getFirstChild().atomize();
				if (!fieldsMap.containsKey(fieldName)){
					fieldsMap.put(fieldName, value);
					logger.debug("insert "+fieldName+" value "+value);
				}else{
					throw new QueryException(new QNm("Field "+fieldName+" is referenced several times"));
				}
				nextField = nextField.getNextSibling();
			}
			if (fieldsMap.size()==schema.getColumns().length){
				IDatabaseAccess databaseAccess = DatabaseAccessFactory.getInstance().create(tableName.stringValue());
				AtomicValue[] record = new AtomicValue[schema.getColumns().length]; 
				for (int i=0;i<schema.getColumns().length;i++){
					Column column = schema.getColumns()[i];
					if (fieldsMap.containsKey(new QNm(column.getColumnName()))){
						record[i] = getAtomicBerkeleyDBValue(column, fieldsMap.get(new QNm(column.getColumnName())));
					}else
						throw new QueryException(new QNm("Field "+column.getColumnName()+ " is not setted"));
				}
				databaseAccess.insert(new org.brackit.relational.metadata.tuple.Tuple(record),transaction);
			}else
				throw new QueryException(new QNm("Not all fields are setted"));
		}else{
			throw new QueryException(new QNm("Incorrect insert format"));
		}
	}



}
