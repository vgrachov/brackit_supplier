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

import java.util.ArrayList;
import java.util.List;

public final class DataSourceDescriptor {

	private final List<ColumnDescriptor> tableColums;
	
	private final String tableName;
	
	public DataSourceDescriptor(String tableName, List<ColumnDescriptor> tableColums){
		this.tableColums = new ArrayList<ColumnDescriptor>(tableColums.size());
		this.tableColums.addAll(tableColums);
		this.tableName = tableName;
	}

	public List<ColumnDescriptor> getTableColums() {
		return tableColums;
	}

	public String getTableName() {
		return tableName;
	}
	
}
