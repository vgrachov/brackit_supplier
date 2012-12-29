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
package org.brackit.supplier.compiler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.brackit.supplier.compiler.optimizer.RelationalOptimizer;
import org.brackit.supplier.compiler.translator.RelationalTranslator;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.compiler.optimizer.DefaultOptimizer;
import org.brackit.xquery.compiler.optimizer.Optimizer;
import org.brackit.xquery.compiler.optimizer.TopDownOptimizer;
import org.brackit.xquery.compiler.translator.TopDownTranslator;
import org.brackit.xquery.compiler.translator.Translator;

public class RelationalCompilerChain extends CompileChain{

	private static final Logger logger = Logger.getLogger(RelationalCompilerChain.class);
	
	@Override
	protected Optimizer getOptimizer(Map<QNm, Str> options) {
		logger.debug("Start optimizing");
		return new RelationalOptimizer(options);
	}

	@Override
	protected Translator getTranslator(Map<QNm, Str> options) {
		logger.debug("Start translation");
		return new RelationalTranslator(options);
	}

}
