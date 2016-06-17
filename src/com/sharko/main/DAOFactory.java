package com.sharko.main;

import part_collections.CollectionDAOFactory;
import part_fileIO.BinaryDAOFactory;
import part_fileIO.TextFileDAOFactory;
import part_jdbc.JdbcDAOFactory;
import part_xml.XmlDAOFactory;

/**
 * 
 * @author Neltarion
 *         <p>
 *         Basically, this is our class to create DAO for each data storage type
 *         </p>
 */
public abstract class DAOFactory {

	/**
	 * 
	 * @author Neltarion
	 *         <p>
	 *         Supported DAO types </>
	 * 
	 */
	public enum DAOType {
		COLLECTION, XML, TEXTFILE, BINARYFILE, JDBC
	}

	// each DAO method we'll need;
	// realization is to be written in each particular generator
	public abstract EmployeeDAO getEmployeeDAO();

	public abstract ProjectDAO getProjectDAO();

	public abstract CustomerDAO getCustomerDAO();

	/**
	 * Creates a new instance of specified DAOFactory type that extends
	 * DAOFactory.
	 * 
	 * @see DAOType
	 * @param whichFactory
	 *            Type of DAOFactory to create
	 * @return new instance of specified in parameters DAOFactory object
	 */
	public static DAOFactory getDAOFactory(DAOType whichFactory) {
		switch (whichFactory) {
		case COLLECTION:
			return new CollectionDAOFactory();
		case XML:
			return new XmlDAOFactory();
		case TEXTFILE:
			return new TextFileDAOFactory();
		case BINARYFILE:
			return new BinaryDAOFactory();
		case JDBC:
			return new JdbcDAOFactory();
		default:
			return null;
		}
	}

}
