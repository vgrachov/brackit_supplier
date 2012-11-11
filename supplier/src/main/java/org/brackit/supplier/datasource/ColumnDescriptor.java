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
