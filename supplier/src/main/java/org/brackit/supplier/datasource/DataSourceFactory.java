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
package org.brackit.supplier.datasource;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DataSourceFactory {

	private static final Logger logger = Logger.getLogger(DataSourceFactory.class);
	
	private static class DataSourceFactoryLoader{
		private static DataSourceFactory instance = new DataSourceFactory();
	}
	
	private String dataBaseDriver;
	
	private String connectStr;
	
	private ComboPooledDataSource connectionPool;
	
	private DataSourceFactory(){
		logger.debug("Init data source factory");
		Properties properties = new Properties();
		try {
			
		    properties.load(new FileInputStream(this.getClass().getClassLoader().getResource("connection.properties").getFile()));
		    dataBaseDriver = properties.getProperty("database.driver");
		    connectStr = properties.getProperty("database.url");
		    logger.debug("Database driver ::"+dataBaseDriver);
		    logger.debug("Database connect string ::"+connectStr);
		    connectionPool = new ComboPooledDataSource(); 
		    connectionPool.setDriverClass( dataBaseDriver ); //loads the jdbc driver 
		    connectionPool.setJdbcUrl( connectStr );
		    logger.debug("Connection is build successfully");
		} catch (IOException e) {
			logger.fatal(e.getMessage());
		} catch (PropertyVetoException e) {
			logger.fatal(e.getMessage());
		}		
	}

	public static DataSourceFactory getInstance(){
		return DataSourceFactoryLoader.instance;
	}
	
	public DataSource getDataSource(String tableName){
		try {
			ResultSet resultSet = connectionPool.getConnection().getMetaData().getCatalogs();
			while (resultSet.next()){
				resultSet.toString();
				logger.debug(resultSet.toString());
			}
			resultSet.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args){
		DataSourceFactory sourceFactory = DataSourceFactory.getInstance();
		sourceFactory.getDataSource("test");
	}
}
