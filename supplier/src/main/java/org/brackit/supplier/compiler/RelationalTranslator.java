package org.brackit.supplier.compiler;

import java.util.Map;

import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.compiler.AST;
import org.brackit.xquery.compiler.XQ;
import org.brackit.xquery.compiler.translator.TopDownTranslator;
import org.brackit.xquery.xdm.Expr;

public class RelationalTranslator extends TopDownTranslator {

	public RelationalTranslator(Map<QNm, Str> options) {
		super(options);
	}
	

	protected Expr anyExpr(AST node) throws QueryException {
		System.out.println(node.getStringValue());
		try{
			Expr recordExpr = anyExpr(node.getChild(0));
			System.out.println("---"+recordExpr);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return super.anyExpr(node);
	}
}
