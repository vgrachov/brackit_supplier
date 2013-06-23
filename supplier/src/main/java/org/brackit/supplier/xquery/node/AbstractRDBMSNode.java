/*******************************************************************************
 * [New BSD License]
 *   Copyright (c) 2012-2013, Volodymyr Grachov <vladimir.grachov@gmail.com>  
 *   All rights reserved.
 *   
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *       * Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *       * Neither the name of the Brackit Project Team nor the
 *         names of its contributors may be used to endorse or promote products
 *         derived from this software without specific prior written permission.
 *   
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *   ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.brackit.supplier.xquery.node;

import org.apache.log4j.Logger;
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

public abstract class AbstractRDBMSNode extends AbstractNode<AbstractRDBMSNode> {

	private static final Logger logger = Logger.getLogger(AbstractRDBMSNode.class); 
	
	protected final NodeType nodeType;
	protected final QNm nodeName;
	protected final AbstractRDBMSNode parentNode;
	
	
	public AbstractRDBMSNode(NodeType nodeType, QNm nodeName, AbstractRDBMSNode parentNode){
		this.nodeName = nodeName;
		this.nodeType = nodeType;
		this.parentNode = parentNode;
	}
	
	public boolean isSelfOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isParentOf(Node<?> node) {
		System.out.println("isParentOf");
		return false;
	}

	public boolean isChildOf(Node<?> node) {
		System.out.println("isChildOf");
		return false;
	}

	public boolean isDescendantOf(Node<?> node) {
		System.out.println("isDescendantOf");
		return false;
	}

	public boolean isDescendantOrSelfOf(Node<?> node) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAncestorOf(Node<?> node) {
		System.out.println("isAncestorOf");
		return false;
	}

	public boolean isAncestorOrSelfOf(Node<?> peek) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSiblingOf(Node<?> node) {
		System.out.println("isSiblingOf");
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
		System.out.println("isRoot");
		return false;
	}

	public int getNodeClassID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection<AbstractRDBMSNode> getCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	public Scope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public Kind getKind() {
		if (nodeType==NodeType.Table)
			return Kind.DOCUMENT; else
		if (nodeType==NodeType.Row)
			return Kind.ELEMENT; else
		if (nodeType==NodeType.Field)
			return Kind.ELEMENT;
		return null;
	}

	public QNm getName() throws DocumentException {
		//logger.debug("get Name call " + nodeName);
		return nodeName;
	}

	public void setName(QNm name) throws OperationNotSupportedException,
			DocumentException {
		throw new IllegalAccessError();
	}

	public abstract Atomic getValue() throws DocumentException;

	public abstract void setValue(Atomic value) throws OperationNotSupportedException, DocumentException;
			
	public AbstractRDBMSNode getParent() throws DocumentException {
		return parentNode;
	}

	public AbstractRDBMSNode getFirstChild() throws DocumentException {
		System.out.println("getFirstChild");
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode getLastChild() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract Stream<? extends AbstractRDBMSNode> getChildren()	throws DocumentException;

	public Stream<? extends AbstractRDBMSNode> getSubtree()
			throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren() throws DocumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public AbstractRDBMSNode getNextSibling() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode getPreviousSibling() throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode append(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode append(Node<?> child)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode append(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode prepend(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode prepend(Node<?> child)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode prepend(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode insertBefore(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode insertBefore(Node<?> node)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode insertBefore(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode insertAfter(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode insertAfter(Node<?> node)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode insertAfter(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode setAttribute(Node<?> attribute)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode setAttribute(QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteAttribute(QNm name)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public Stream<? extends AbstractRDBMSNode> getAttributes()
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode getAttribute(QNm name) throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode replaceWith(Node<?> node)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode replaceWith(SubtreeParser parser)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractRDBMSNode replaceWith(Kind kind, QNm name, Atomic value)
			throws OperationNotSupportedException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAttributes() throws DocumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public void delete() throws DocumentException {
		logger.debug("delete node "+nodeName);

	}

	public abstract void parse(SubtreeHandler handler) throws DocumentException;

	@Override
	protected int cmpInternal(AbstractRDBMSNode other) {
		// TODO Auto-generated method stub
		return 0;
	}

}
