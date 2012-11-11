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
