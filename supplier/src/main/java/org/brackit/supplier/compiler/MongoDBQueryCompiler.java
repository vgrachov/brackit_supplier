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
package org.brackit.supplier.compiler;

import java.util.Map;

import org.brackit.supplier.compiler.optimizer.MongoDBlOptimizer;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.compiler.optimizer.Optimizer;
import org.brackit.xquery.compiler.optimizer.TopDownOptimizer;
import org.brackit.xquery.compiler.translator.TopDownTranslator;
import org.brackit.xquery.compiler.translator.Translator;

public class MongoDBQueryCompiler extends CompileChain{

	@Override
	protected Optimizer getOptimizer(Map<QNm, Str> options) {
		return new MongoDBlOptimizer(options);
	}

	@Override
	protected Translator getTranslator(Map<QNm, Str> options) {
		System.out.println("Start translation");
		return new RelationalTranslator(options);
	}

}
