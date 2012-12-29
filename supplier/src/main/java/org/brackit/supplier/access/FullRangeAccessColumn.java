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
package org.brackit.supplier.access;

public final class FullRangeAccessColumn extends RangeAccessColumn {
	
	private final org.brackit.xquery.atomic.Atomic leftKey,rightKey;
	

	public FullRangeAccessColumn(String bindVariable, String tableName,
			String accessColumn, org.brackit.xquery.atomic.Atomic leftKey, org.brackit.xquery.atomic.Atomic rightKey) {
		super(bindVariable,tableName,accessColumn);
		this.leftKey = leftKey;
		this.rightKey = rightKey;
	}

	public org.brackit.xquery.atomic.Atomic getLeftKey() {
		return leftKey;
	}

	public org.brackit.xquery.atomic.Atomic getRightKey() {
		return rightKey;
	}
	
}
