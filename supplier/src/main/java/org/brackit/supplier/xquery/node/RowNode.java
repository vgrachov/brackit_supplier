package org.brackit.supplier.xquery.node;

import org.apache.log4j.Logger;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.node.parser.SubtreeHandler;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Stream;

public class RowNode extends AbstractRDBMSNode {

	private static final Logger logger = Logger.getLogger(RowNode.class);
	
	private Atomic[] values;

	private static int rowCounter = 0;
	
	public RowNode(TableNode tableNode) throws DocumentException{
		super(NodeType.Row, new QNm("row"), tableNode);
		values = new Atomic[5];
		for (int i=0;i<5;i++)
			values[i] = new Str(String.valueOf(i));
		rowCounter++;
	}
	
	@Override
	public Atomic getValue() throws DocumentException {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<values.length;i++)
			sb.append(values[i]);
		return new Str(sb.toString());
	}

	@Override
	public void setValue(Atomic value) throws OperationNotSupportedException,
			DocumentException {
		logger.fatal("Should not be visited");
	}

	@Override
	public Stream<? extends AbstractRDBMSNode> getChildren()
			throws DocumentException {
		return new Stream<FieldNode>() {
			private int counter = 0;
			public FieldNode next() throws DocumentException {
				counter++;
				if (counter == 1)
					return new FieldNode(new QNm("name"), RowNode.this, new Str("name"+rowCounter));
				else
				if (counter == 2)
					return new FieldNode(new QNm("login"), RowNode.this, new Str("login"+rowCounter));
				
				return null;
			}

			public void close() {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public QNm getName() throws DocumentException {
		return new QNm("row");
	}

	@Override
	public void parse(SubtreeHandler handler) throws DocumentException {
		logger.debug("Call parser for row node");
		handler.begin();
		handler.beginFragment();
		handler.startElement(new QNm("values"));
		StringBuilder valuesSerializeBuffer = new StringBuilder();
		for (int i=0;i<values.length;i++){
			valuesSerializeBuffer.append(values[i]);
			if (i!=values.length-1)
				valuesSerializeBuffer.append(",");
		}
		handler.text(new QNm(valuesSerializeBuffer.toString()));
		handler.endElement(new QNm("values"));
		handler.endFragment();
		handler.end();		
	}
	
}
