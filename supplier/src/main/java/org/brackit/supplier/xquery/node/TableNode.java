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
package org.brackit.supplier.xquery.node;

import java.util.List;

import org.apache.log4j.Logger;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.node.ArrayCollection;
import org.brackit.xquery.node.parser.SubtreeHandler;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class TableNode extends AbstractRDBMSNode {

	private static final Logger logger = Logger.getLogger(TableNode.class);
	
	public TableNode(QNm tableName){
		super(NodeType.Table,tableName,null);
	}
	
	@Override
	public Atomic getValue() throws DocumentException {
		throw new IllegalAccessError();
	}

	@Override
	public void setValue(Atomic value) throws OperationNotSupportedException,
			DocumentException {
		throw new IllegalAccessError();
	}

	@Override
	public Stream<? extends AbstractRDBMSNode> getChildren()
			throws DocumentException {
		return new Stream<RowNode>() {
			private int counter = 0;
			public RowNode next() throws DocumentException {
				counter++;
				if (counter==1)
					return new RowNode(TableNode.this);
				else
				if (counter == 2)
					return new RowNode(TableNode.this);
				else
				if (counter == 3)
					return new RowNode(TableNode.this);
				
				return null;
			}

			public void close() {
				// TODO Auto-generated method stub
				
			}
			
		}; 
	}
	
	@Override
	public void parse(SubtreeHandler handler) throws DocumentException {
		logger.error("Call parser for table node");
		handler.begin();
		handler.beginFragment();
		handler.startElement(new QNm("rows"));
		handler.endElement(new QNm("rows"));
		handler.endFragment();
		handler.end();		
	}


}
