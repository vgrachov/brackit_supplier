package org.brackit.supplier.store;

import org.brackit.supplier.xquery.node.TableCollection;
import org.brackit.xquery.node.parser.SubtreeParser;
import org.brackit.xquery.xdm.Collection;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Store;
import org.brackit.xquery.xdm.Stream;

public class DataStore implements Store {

	public Collection<?> lookup(String name) throws DocumentException {
		System.out.println("Try look up "+name);
		return new TableCollection("books");
	}

	public Collection<?> create(String name) throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<?> create(String name, SubtreeParser parser)
			throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<?> create(String name, Stream<SubtreeParser> parsers)
			throws DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void drop(String name) throws DocumentException {
		// TODO Auto-generated method stub
		
	}

	public void makeDir(String path) throws DocumentException {
		// TODO Auto-generated method stub
		
	}
	

}
