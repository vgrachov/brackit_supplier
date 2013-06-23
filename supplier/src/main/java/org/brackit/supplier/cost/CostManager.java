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
package org.brackit.supplier.cost;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.brackit.berkeleydb.catalog.Catalog;
import org.brackit.berkeleydb.cursor.BerkeleydbDatabaseAccess;
import org.brackit.relational.api.ICatalog;
import org.brackit.relational.api.IDatabaseAccess;
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
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Dec;
import org.brackit.xquery.atomic.Int32;
import org.brackit.xquery.atomic.Str;

import com.google.common.base.Preconditions;

public class CostManager implements ICostManager{

	private static Logger logger = Logger.getLogger(CostManager.class);
	
	private final ICatalog catalog = Catalog.getInstance();
	
	public CostManager() {
		
	}
	
	public double getCostEstimation(String tableName, String columnName, Atomic lowerBound, Atomic upperBound) {
		Preconditions.checkNotNull(tableName);
		Preconditions.checkNotNull(columnName);
		if (lowerBound == null && upperBound == null) return 1;
		
		Schema schema = catalog.getSchemaByDatabaseName(tableName);
		Column column = null;
		for (int i=0; i<schema.getColumns().length; i++) {
			if (schema.getColumns()[i].getColumnName().equals(columnName)) {
				column = schema.getColumns()[i];
			}
		}
		if (column == null)
			throw new IllegalArgumentException("Column "+columnName+" is not exist in schema");
		// BTree is not supported. 
		if (!column.isDirectIndexExist()) return 1;
		
		AtomicValue lowerBoundValue = getAtomicBerkeleyDBValue(column, lowerBound);
		AtomicValue upperBoundValue = getAtomicBerkeleyDBValue(column, upperBound);
		IDatabaseAccess databaseAccess = new BerkeleydbDatabaseAccess(tableName);
		AtomicValue minFieldValue = databaseAccess.getMinFieldValue(column);
		AtomicValue maxFieldValue = databaseAccess.getMaxFieldValue(column);
		
		double cost = 0;
		if (lowerBound != null && upperBound != null) {
			cost = getCostComputing(column, lowerBoundValue, upperBoundValue, minFieldValue, maxFieldValue);
		} else
			if (upperBound == null) {
				cost = getCostComputing(column, lowerBoundValue, maxFieldValue, minFieldValue, maxFieldValue);
			} else
				cost = getCostComputing(column, minFieldValue, upperBoundValue, minFieldValue, maxFieldValue);
		logger.info("Cost for accessing table "+tableName+" through index "+columnName+" with lowerbound "+lowerBoundValue +" and upperbound "+upperBoundValue+" is "+cost);
		return cost;
	}
	
	private AtomicValue getAtomicBerkeleyDBValue(Column column, Atomic fieldValue){
		if (fieldValue == null) return null;
		if (column.getType() == ColumnType.String) {
			return new AtomicString(column.getColumnName(), ((Str)fieldValue).stringValue());
		} else
		if (column.getType() == ColumnType.Integer) {
			return new AtomicInteger(column.getColumnName(), ((Int32)fieldValue).intValue());
		} else
		if (column.getType() == ColumnType.Double) {
			return new AtomicDouble(column.getColumnName(), ((Dec)fieldValue).doubleValue());
		} else
		if (column.getType() == ColumnType.Char) {
			return new AtomicChar(column.getColumnName(), ((Str)fieldValue).stringValue().charAt(0));
		} else
		if (column.getType() == ColumnType.Date) {
			try{
				SimpleDateFormat dateFormat = new SimpleDateFormat(RelationalStorageProperties.getDatePattern());
				return new AtomicDate(column.getColumnName(), dateFormat.parse(((Str)fieldValue).stringValue()).getTime() );
			} catch(ParseException e){
				logger.error(e.getMessage());
				//TODO: checked exception should serves.
				return null;
			}
		} else
			throw new IllegalArgumentException("Not supported type for index scan");
	}

	private double getCostComputing(Column column, AtomicValue lowerBoundValue, AtomicValue upperBoundValue, AtomicValue minFieldValue, AtomicValue maxFieldValue) {
		double distance = 0;
		double minMaxDistance = 0;
		if (column.getType() == ColumnType.Date) {
			distance = Math.abs(((AtomicDate)upperBoundValue).getData() - ((AtomicDate)lowerBoundValue).getData());
			minMaxDistance = Math.abs(((AtomicDate)minFieldValue).getData() - ((AtomicDate)maxFieldValue).getData());
		} else
			if (column.getType() == ColumnType.Integer) {
				distance = Math.abs(((AtomicInteger)upperBoundValue).getData() - ((AtomicInteger)lowerBoundValue).getData());
				minMaxDistance = Math.abs(((AtomicInteger)minFieldValue).getData() - ((AtomicInteger)maxFieldValue).getData());
			} else
				if (column.getType() == ColumnType.Double) {
					distance = Math.abs( ((AtomicDouble)upperBoundValue).getData() - ((AtomicDouble)lowerBoundValue).getData() );
					minMaxDistance = Math.abs(((AtomicDouble)minFieldValue).getData() - ((AtomicDouble)maxFieldValue).getData());
				} else
					if (column.getType() == ColumnType.Char) {
						distance = Math.abs(((AtomicChar)upperBoundValue).getData() - ((AtomicChar)lowerBoundValue).getData());
						minMaxDistance = Math.abs(((AtomicChar)minFieldValue).getData() - ((AtomicChar)maxFieldValue).getData());
					} else
						if (column.getType() == ColumnType.String) {
							distance = 1;
							minMaxDistance = 1;
						} else
							throw new IllegalArgumentException("Not supported type for index scan"); 
		if (Math.abs(minMaxDistance) < 0.0001) {
			return 1;
		} else {
			return (distance+0.0) / minMaxDistance;
		}
	}
}