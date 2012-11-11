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
package org.brackit.supplier.datasource;

public final class ColumnDescriptor {
	
	private final String columnName;
	
	private final DataType columnType;

	public ColumnDescriptor(String columnName, DataType columnType) {
		if (columnName==null || columnName.isEmpty())
			throw new IllegalArgumentException("Column name can't be empty or null");
		if (columnType==null)
			throw new IllegalArgumentException("Column type can't be null");
		this.columnName = columnName;
		this.columnType = columnType;
	}

	public String getColumnName() {
		return columnName;
	}

	public DataType getColumnType() {
		return columnType;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof ColumnDescriptor){
			ColumnDescriptor column = (ColumnDescriptor)o;
			if (this.columnName.equals(column.getColumnName()) && this.columnType==column.getColumnType()) return true;
			else return false;
		}
		return false;
	}
}
