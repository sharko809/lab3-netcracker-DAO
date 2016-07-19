package service;

import java.io.File;
import javax.xml.bind.JAXBException;

import org.codehaus.jackson.map.ObjectMapper;

import com.sharko.main.CustomerDAO;
import com.sharko.main.DAOFactory;
import com.sharko.main.DAOFactory.DAOType;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.ProjectDAO;

import part_collections.CollectionEntityStorage;
import part_xml.parser.JaxbParser;
import part_xml.parser.Parser;
import part_xml.xmlEntities.Customers;
import part_xml.xmlEntities.Employees;
import part_xml.xmlEntities.Projects;

public class TestData {

	private static Parser parser = null;
	private static final String CUSTOMER = "customer.txt";
	private static final String EMPLOYEE = "employee.txt";
	private static final String PROJECT = "project.txt";

	/**
	 * Synchronizes DB content with Collection, XML, Text and binary file
	 * storages
	 */
	public static void synchronizeStorages() {

		DAOFactory jDaoFactory = DAOFactory.getDAOFactory(DAOType.JDBC);
		EmployeeDAO jEmployeeDAO = jDaoFactory.getEmployeeDAO();
		ProjectDAO jProjectDAO = jDaoFactory.getProjectDAO();
		CustomerDAO jCustomerDAO = jDaoFactory.getCustomerDAO();

		putDataToCollection(jEmployeeDAO, jProjectDAO, jCustomerDAO);
		putDataToXml(jEmployeeDAO, jProjectDAO, jCustomerDAO);
		putDataToTextFile(jEmployeeDAO, jProjectDAO, jCustomerDAO);
	}

	private static void putDataToCollection(EmployeeDAO jEmployeeDAO, ProjectDAO jProjectDAO,
			CustomerDAO jCustomerDAO) {
		CollectionEntityStorage.empList.addAll(jEmployeeDAO.getEmployees());
		IDGenerator.setGlobalEmploeeId(jEmployeeDAO.getEmployees().size());
		CollectionEntityStorage.customerList.addAll(jCustomerDAO.getCustomers());
		IDGenerator.setGlobalCustomerId(jCustomerDAO.getCustomers().size());
		CollectionEntityStorage.projectList.addAll(jProjectDAO.getProjects());
		IDGenerator.setGlobalProjectId(jProjectDAO.getProjects().size());
		System.out.println("Collections storage synchronized");
	}

	private static void putDataToXml(EmployeeDAO jEmployeeDAO, ProjectDAO jProjectDAO, CustomerDAO jCustomerDAO) {
		parser = new JaxbParser();
		Employees employee = new Employees();
		Projects project = new Projects();
		Customers customer = new Customers();

		jEmployeeDAO.getEmployees().forEach(e -> {
			employee.addEmployee(e);
		});
		try {
			parser.saveObject(new File("employee.xml"), employee);
			IDGenerator.setGlobalXmlEmploeeId(employee.getEmployees().size());
		} catch (JAXBException e2) {
			e2.printStackTrace();
		}

		jProjectDAO.getProjects().forEach(p -> {
			project.addProject(p);
		});
		try {
			parser.saveObject(new File("project.xml"), project);
			IDGenerator.setGlobalXmlProjectId(project.getProjects().size());
		} catch (JAXBException e2) {
			e2.printStackTrace();
		}

		jCustomerDAO.getCustomers().forEach(c -> {
			customer.addCustomer(c);
		});
		try {
			parser.saveObject(new File("customer.xml"), customer);
			IDGenerator.setGlobalXmlCustomerId(customer.getCustomers().size());
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		System.out.println("XML storage synchronized");
	}

	private static void putDataToTextFile(EmployeeDAO jEmployeeDAO, ProjectDAO jProjectDAO, CustomerDAO jCustomerDAO) {

		ObjectMapper mapper = new ObjectMapper();
		Employees employee = new Employees();
		Projects project = new Projects();
		Customers customer = new Customers();

		jEmployeeDAO.getEmployees().forEach(e -> {
			employee.addEmployee(e);
		});
		try {
			mapper.writeValue(new File(EMPLOYEE), employee);
			IDGenerator.setGlobalTextEmploeeId(employee.getEmployees().size());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		jProjectDAO.getProjects().forEach(p -> {
			project.addProject(p);
		});
		try {
			mapper.writeValue(new File(PROJECT), project);
			IDGenerator.setGlobalTextProjectId(project.getProjects().size());
		} catch (Exception e) {
			e.printStackTrace();
		}

		jCustomerDAO.getCustomers().forEach(c -> {
			customer.addCustomer(c);
		});
		try {
			mapper.writeValue(new File(CUSTOMER), customer);
			IDGenerator.setGlobalTextCustomerId(customer.getCustomers().size());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Text file storage synchronized");
	}


}
