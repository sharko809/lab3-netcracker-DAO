package part_xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.sharko.main.Customer;
import com.sharko.main.CustomerDAO;
import com.sharko.main.Employee;
import com.sharko.main.Project;

import part_xml.parser.JaxbParser;
import part_xml.parser.Parser;
import part_xml.xmlEntities.Customers;
import part_xml.xmlEntities.Employees;
import part_xml.xmlEntities.Projects;
import service.IDGenerator;
import service.IDGenerator.IdType;

public class XmlCustomerDAO implements CustomerDAO {

	private static final String CUSTOMER = "customer.xml";
	private static final String EMPLOYEE = "employee.xml";
	private static final String PROJECT = "project.xml";

	@Override
	public Collection<Employee> getEmployeesByProject(final int project_id) {
		if (project_id <= 0) {
			System.out.println("Nu such project id");
			return new ArrayList<Employee>();
		}
		Parser parser = new JaxbParser();
		try {
			Projects p1 = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : p1) {
				if (project.getId() == project_id) {
					return project.getEmployees();
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return new ArrayList<Employee>();
	}

	@Override
	public Collection<Project> getProjectsByEmployee(int employee_id) {
		if (employee_id <= 0) {
			System.out.println("Nu such employee id");
			return null;
		}
		return getEmployeeById(employee_id).getProjects();
	}

	@Override
	public Collection<Project> getProjectsByEmployee(String employee_name) {
		return getEmployeeByName(employee_name).getProjects();
	}

	@Override
	public Collection<Employee> getIdleEmpByDepartment(String department) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		getEmployees().forEach(e -> {
			if (department.equals(e.getDepartment())) {
				if (e.getProjects().isEmpty() || e.getProjects() == null) {
					emp.add(e);
				}
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getIdleEmployee() {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		getEmployees().forEach(e -> {
			if (e.getProjects().isEmpty() || e.getProjects() == null) {
				emp.add(e);
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getEmployeeByLead(int employee_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		getProjectsByEmployee(employee_id).forEach(p -> {
			if (p.getLead_id() == employee_id) {
				emp.addAll(getEmployeesByProject(p.getId()));
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getLeadsByEmp(int employee_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		getProjectsByEmployee(employee_id).forEach(p -> {
			// prevent choosing himself by second statement in if
			if (!(p.getLead_id() <= 0) && p.getLead_id() != employee_id) {
				emp.add(getEmployeeById(p.getLead_id()));
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getEmpBySameProjects(int employee_id) {
		HashSet<Project> proj = (HashSet<Project>) getProjectsByEmployee(employee_id);
		int size = proj.size();
		if (proj.isEmpty() || proj == null) {
			System.out.println("This employee has no projects.");
			return new HashSet<Employee>();
		}
		ArrayList<Employee> emp = new ArrayList<Employee>();
		getEmployees().forEach(e -> {
			List<Project> set = e.getProjects();
			if (!(set == null) && !set.isEmpty()) {
				for (Project project : proj) {
					int counter = 0;
					for (Project project_ : set) {
						if (project_.getId() == project.getId()) {
							counter++;
						}
					}
					if (counter == size) {
						emp.add(e);
					}
				}
			}
		});
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByCustomer(int customer_id) {
		Collection<Project> proj = new ArrayList<Project>();
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			cust.getCustomers().forEach(c -> {
				if (c.getId() == customer_id && !c.getProjects().isEmpty() && c.getProjects() != null) {
					proj.addAll(c.getProjects());
				}
			});
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return proj;
	}

	@Override
	public Collection<Employee> getEmpByCustomer(int customer_id) {
		Collection<Employee> emp = new ArrayList<Employee>();
		getProjectsByCustomer(customer_id).forEach(p -> {
			Set<Employee> emps = p.getEmployees();
			emp.addAll((!emps.isEmpty() && emps != null) ? emps : new ArrayList<Employee>());
		});
		return emp;
	}

	@Override
	public int addCustomer(String name) {
		int id = IDGenerator.generateXmlId(IdType.PROJECT);
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			cust.addCustomer(new Customer(id, name, new ArrayList<Project>()));
			parser.saveObject(new File(CUSTOMER), cust);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public int addProjectToCustomer(int customer_id, String project_name, int lead_id) {
		int id = 0;
		if (checkProjectCopies(customer_id, project_name) && customer_id > 0 && lead_id >= 0
				&& !ifExists(project_name)) {
			Parser parser = new JaxbParser();
			id = IDGenerator.generateXmlId(IdType.PROJECT);
			try {
				Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
				Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
				Project newProj = new Project(id, project_name, lead_id, customer_id, new ArrayList<Employee>());
				projs.addProject(newProj);
				parser.saveObject(new File(PROJECT), projs);
				cust.forEach(c -> {
					if (c.getId() == customer_id) {
						c.addProject(newProj);
					}
				});
				parser.saveObject(new File(CUSTOMER), cust);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

		}
		return id;
	}

	@Override
	public boolean deleteCustomerById(int id) {
		if (id <= 0) {
			System.out.println("Nu such customer id");
			return false;
		}
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : new ArrayList<Customer>(cust.getCustomers())) {
				if (customer.getId() == id) {
					cust.removeCustomer(customer);
					parser.saveObject(new File(CUSTOMER), cust);
					return true;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteCustomerByName(String name) {
		if (name == null || name.equals("") || name.equals(" ") || name.trim().equals("")) {
			System.out.println("Nu such customer name");
			return false;
		}
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : new ArrayList<Customer>(cust.getCustomers())) {
				if (name.equals(customer.getName())) {
					cust.removeCustomer(customer);
					parser.saveObject(new File(CUSTOMER), cust);
					return true;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Customer getCustomerById(int customer_id) {
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : cust) {
				if (customer.getId() == customer_id) {
					return customer;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Customer getCustomerByName(String name) {
		if (name == null || name.equals("") || name.equals(" ") || name.trim().equals("")) {
			System.out.println("Nu such customer name");
			return null;
		}
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : cust) {
				if (name != null && customer.getName().equals(name)) {
					return customer;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Customer> getCustomers() {
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			if (cust.getCustomers() != null) {
				return cust.getCustomers();
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean updateCustomer(int id, String name) {
		if (id <= 0) {
			System.out.println("Nu such customer id");
			return false;
		}
		return updateName(id, name);
	}

	private boolean updateName(int id, String name) {
		if (name == null || name.equals("") || name.equals(" ") || name.trim().equals("")) {
			System.out.println("Nu such customer name");
			return false;
		}
		Parser parser = new JaxbParser();
		try {
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : cust.getCustomers()) {
				if (customer.getId() == id) {
					customer.setName(name);
					parser.saveObject(new File(CUSTOMER), cust);
					return true;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Employee getEmployeeById(int id) {
		if (id <= 0) {
			System.out.println("No such id");
			return null;
		} else {
			Parser parser = new JaxbParser();
			Employee emp = new Employee();
			try {
				Employees e1 = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
				for (Employee employee : e1) {
					if (employee.getId() == id) {
						emp = employee;
						break;
					}
				}
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			return emp;
		}
	}

	private Employee getEmployeeByName(String emp_name) {
		if (emp_name.trim().equals("") || emp_name == null) {
			System.out.println("That's not a name. Please, try something else.");
			return null;
		}
		Parser parser = new JaxbParser();
		Employees emps;
		try {
			emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			for (Employee employee : emps) {
				if (emp_name.equals(employee.getName())) {
					return employee;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Collection<Employee> getEmployees() {
		Parser parser = new JaxbParser();
		try {
			Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			return emps.getEmployees();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkProjectCopies(final int customer_id, final String proj_name) {
		Collection<Project> projs = getProjectsByCustomer(customer_id);
		if (projs == null || projs.isEmpty()) {
			return true;
		}
		for (Project project : projs) {
			if (project.getName() == proj_name) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Show whether project with such name already exists in project list.
	 * 
	 * @param project
	 *            Project to check
	 * @return <b>true</b> if project with such name already exists
	 */
	private boolean ifExists(String project) {
		Parser parser = new JaxbParser();
		boolean flag = false;
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project proj : projs) {
				if (proj != null && proj.getName() != null && project != null && (project.equals(proj.getName()))) {
					flag = true;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return flag;
	}

}
