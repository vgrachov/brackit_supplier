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
package org.brackit.supplier.xquery.node;

import org.apache.log4j.Logger;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.node.parser.SubtreeHandler;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class FieldNode extends AbstractRDBMSNode {

	private static final Logger logger = Logger.getLogger(FieldNode.class);
	
	private Atomic value;
	
	public FieldNode(QNm columnName, RowNode row, Atomic value){
		super(NodeType.Field,columnName,row);
		this.value = value;
	}

	@Override
	public Atomic getValue() throws DocumentException {
		return value;
	}

	@Override
	public void setValue(Atomic value) throws OperationNotSupportedException,
			DocumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public Stream<? extends AbstractRDBMSNode> getChildren()
			throws DocumentException {
		logger.error("getChildren in "+this.getClass().getName());
		return null;
	}

	@Override
	public void parse(SubtreeHandler handler) throws DocumentException {
		//logger.debug("Call parser for table node");
		handler.begin();
		handler.beginFragment();
		handler.text(value);
		handler.endFragment();
		handler.end();		

	}

}
