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

import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.node.AbstractNode;
import org.brackit.xquery.node.parser.SubtreeHandler;
import org.brackit.xquery.node.parser.SubtreeParser;
import org.brackit.xquery.xdm.Collection;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Kind;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.OperationNotSupportedException;
import org.brackit.xquery.xdm.Scope;
import org.brackit.xquery.xdm.Stream;

public class TupleNode extends AbstractNode<TupleNode> {

	private Kind nodeKind;
	
	public TupleNode(Kind kind){
		this.nodeKind = kind;
	}
	
	public boolean isSelfOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isParentOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDescendantOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDescendantOrSelfOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAncestorOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAncestorOrSelfOf(Node<?> peek) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSiblingOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPrecedingSiblingOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFollowingSiblingOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPrecedingOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFollowingOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAttributeOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDocumentOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRoot() {
		return false;
	}

	public int getNodeClassID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection<TupleNode> getCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	public Scope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public Kind getKind() {
		// TODO Auto-generated method stub
		return null;
	}

	public QNm getName() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(QNm name) throws OperationNotSupportedException,
			DocumentException {
		// TODO Auto-generated method stub
		
	}

	public Atomic getValue() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(Atomic value) throws OperationNotSupportedException,
			DocumentException {
		// TODO Auto-generated method stub
		
	}

	public TupleNode getParent() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode getFirstChild() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode getLastChild() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Stream<? extends TupleNode> getChildren() throws DocumentException {
		return null;
	}

	public Stream<? extends TupleNode> getSubtree() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren() throws DocumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public TupleNode getNextSibling() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode getPreviousSibling() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode append(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode append(Node<?> child)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode append(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode prepend(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode prepend(Node<?> child)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode prepend(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode insertBefore(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode insertBefore(Node<?> node)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode insertBefore(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode insertAfter(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode insertAfter(Node<?> node)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode insertAfter(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode setAttribute(Node<?> attribute)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode setAttribute(QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteAttribute(QNm name)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public Stream<? extends TupleNode> getAttributes()
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode getAttribute(QNm name) throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode replaceWith(Node<?> node)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode replaceWith(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public TupleNode replaceWith(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAttributes() throws DocumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public void delete() throws DocumentException {
		// TODO Auto-generated method stub
		
	}

	public void parse(SubtreeHandler handler) throws DocumentException {
		System.out.println("Parse");
		handler.begin();
		handler.beginFragment();
		handler.startElement(new QNm("testelement"));
		handler.endElement(new QNm("testelement"));
		
		handler.endFragment();
		handler.end();		
	}

	@Override
	protected int cmpInternal(TupleNode other) {
		// TODO Auto-generated method stub
		return 0;
	}

}
