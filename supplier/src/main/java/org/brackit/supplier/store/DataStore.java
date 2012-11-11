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
package org.brackit.supplier.store;

import org.brackit.supplier.xquery.node.TableCollection;
import org.brackit.xquery.node.parser.SubtreeParser;
import org.brackit.xquery.xdm.Collection;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Store;
import org.brackit.xquery.xdm.Stream;

public class DataStore implements Store {

	public Collection<?> lookup(String name) throws DocumentException {
		System.out.println("Try look up "+name);
		return new TableCollection("books");
	}

	public Collection<?> create(String name) throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<?> create(String name, SubtreeParser parser)
			throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<?> create(String name, Stream<SubtreeParser> parsers)
			throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void drop(String name) throws DocumentException {
		// TODO Auto-generated method stub
		
	}

	public void makeDir(String path) throws DocumentException {
		// TODO Auto-generated method stub
		
	}
	

}
