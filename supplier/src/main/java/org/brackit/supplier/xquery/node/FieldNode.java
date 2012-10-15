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
		logger.debug("Call parser for table node");
		handler.begin();
		handler.beginFragment();
		handler.text(value);
		handler.endFragment();
		handler.end();		

	}

}
