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
package org.brackit.supplier.function;

import org.brackit.relational.xquery.xdm.RowCollection;
import org.brackit.supplier.access.FullRangeAccessColumn;
import org.brackit.supplier.access.RangeAccessColumn;
import org.brackit.supplier.collection.RangeAccessCollection;
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

public class RangeAccessFunction extends Collection{
	public static final QNm FN_COLLECTION = new QNm(Namespaces.FN_NSURI, Namespaces.FN_PREFIX, "collection");
	public static final Signature SIGNATURE = new Signature(
			new SequenceType(DocumentType.DOC, Cardinality.ZeroOrMany),
			new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne));
	
	private final RangeAccessColumn accessColumn;
	
	public RangeAccessFunction(RangeAccessColumn accessColumn){
		super(FN_COLLECTION, SIGNATURE);
		this.accessColumn = accessColumn;
	}
	
	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException
	{
		RangeAccessCollection rangeAccessRowCollection = new RangeAccessCollection(accessColumn);
		return rangeAccessRowCollection;
	}

}
