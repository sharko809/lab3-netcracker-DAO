package com.sharko.main;

import com.sharko.main.DAOFactory.DAOType;

import service.TestData;

public class Main {

	public static void main(String[] args) {
		
		TestData.synchronizeStorages();
		
		// JDBC
		// create DAO generator
		DAOFactory jDaoFactory = DAOFactory.getDAOFactory(DAOType.JDBC);
		// create DAO
		EmployeeDAO jEmployeeDAO = jDaoFactory.getEmployeeDAO();
		ProjectDAO jProjectDAO = jDaoFactory.getProjectDAO();
		CustomerDAO jCustomerDAO = jDaoFactory.getCustomerDAO();

		// Collections
		DAOFactory collectionFactory = DAOFactory.getDAOFactory(DAOType.COLLECTION);
		CustomerDAO customerDAO = collectionFactory.getCustomerDAO();
		ProjectDAO projectDAO = collectionFactory.getProjectDAO();
		EmployeeDAO employeeDAO = collectionFactory.getEmployeeDAO();

		// XML
		DAOFactory xmlFactory = DAOFactory.getDAOFactory(DAOType.XML);
		CustomerDAO xmlCustomerDAO = xmlFactory.getCustomerDAO();
		ProjectDAO xmlProjectDAO = xmlFactory.getProjectDAO();
		EmployeeDAO xmlEmployeeDAO = xmlFactory.getEmployeeDAO();

		
		// Text files
		DAOFactory textFactory = DAOFactory.getDAOFactory(DAOType.TEXTFILE);
		EmployeeDAO textEmployeeDAO = textFactory.getEmployeeDAO();
		ProjectDAO textProjectDAO = textFactory.getProjectDAO();
		CustomerDAO textCustomerDAO = textFactory.getCustomerDAO();
	

	}


}
