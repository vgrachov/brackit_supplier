/*******************************************************************************
 * [New BSD License]
 *  Copyright (c) 2012, Volodymyr Grachov <vladimir.grachov@gmail.com>  
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Brackit Project Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.brackit.supplier.function;

import java.util.Set;

import org.brackit.relational.api.transaction.ITransaction;
import org.brackit.supplier.RelationalQueryContext;
import org.brackit.supplier.access.AccessColumn;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.fn.Collection;
import org.brackit.xquery.module.Namespaces;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.type.AtomicType;
import org.brackit.xquery.xdm.type.Cardinality;
import org.brackit.xquery.xdm.type.DocumentType;
import org.brackit.xquery.xdm.type.SequenceType;

import com.google.common.base.Preconditions;

public class RangeAccessFunction extends Collection{
	public static final QNm FN_COLLECTION = new QNm(Namespaces.FN_NSURI, Namespaces.FN_PREFIX, "collection");
	public static final Signature SIGNATURE = new Signature(
			new SequenceType(DocumentType.DOC, Cardinality.ZeroOrMany),
			new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne));
	
	private final AccessColumn accessColumn;
	private final Set<String> projectionFields;
	
	public RangeAccessFunction(AccessColumn accessColumn, Set<String> projectionFields) {
		super(FN_COLLECTION, SIGNATURE);
		this.accessColumn = accessColumn;
		this.projectionFields = projectionFields;
	}
	
	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		Preconditions.checkNotNull(projectionFields);
		RelationalQueryContext context = (RelationalQueryContext)ctx;
		ITransaction transaction = context.getTransaction();
		return context.getStore().rangeAccess(accessColumn,transaction, projectionFields);
	}
}
