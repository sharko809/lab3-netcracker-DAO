package part_xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.sharko.main.Customer;
import com.sharko.main.Employee;
import com.sharko.main.Project;
import com.sharko.main.ProjectDAO;

import part_xml.parser.JaxbParser;
import part_xml.parser.Parser;
import part_xml.xmlEntities.Customers;
import part_xml.xmlEntities.Employees;
import part_xml.xmlEntities.Projects;
import service.IDGenerator;
import service.IDGenerator.IdType;

public class XmlProjectDAO implements ProjectDAO {

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
	public int addProject(String project_name, int lead_id, int customer_id) {
		int id = IDGenerator.generateXmlId(IdType.PROJECT);
		Parser parser = new JaxbParser();
		Project projToAdd = new Project(id, project_name, lead_id, customer_id, new ArrayList<Employee>());
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			projs.addProject(projToAdd);
			parser.saveObject(new File(PROJECT), projs);
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : cust) {
				if(customer.getId()==customer_id){
					customer.addProject(projToAdd);
				}
			}
			parser.saveObject(new File(CUSTOMER), cust);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public boolean deleteProjectById(int project_id) {
		if (project_id <= 0) {
			System.out.println("No such id");
			return false;
		}
		Parser parser = new JaxbParser();
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			for (Employee employee : new ArrayList<Employee>(emps.getEmployees())) {
				employee.getProjects().forEach(p -> {
					if (p.getId() == project_id) {
						employee.removeProject(p);
					}
				});
			}
			parser.saveObject(new File(EMPLOYEE), emps);
			for (Project project : new ArrayList<Project>(projs.getProjects())) {
				if (project.getId() == project_id) {
					projs.removeProject(project);
					
				}
			}
			parser.saveObject(new File(PROJECT), projs);
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : new ArrayList<>(cust.getCustomers())) {
				customer.getProjects().forEach(p -> {
					if(p.getId()==project_id){
						customer.removeProject(p);
					}
				});
			}
			parser.saveObject(new File(CUSTOMER), cust);
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteProjectByName(String project_name) {
		if (project_name == null || "".equals(project_name) || " ".equals(project_name)) {
			System.out.println("No such name");
			return false;
		}
		Parser parser = new JaxbParser();
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			for (Employee employee : new ArrayList<Employee>(emps.getEmployees())) {
				employee.getProjects().forEach(p -> {
					if (p.getName().equals(project_name)) {
						employee.removeProject(p);
					}
				});
			}
			parser.saveObject(new File(EMPLOYEE), emps);
			for (Project project : new ArrayList<Project>(projs.getProjects())) {
				if (project.getName().equals(project_name)) {
					projs.removeProject(project);
				}
			}
			parser.saveObject(new File(PROJECT), projs);
			Customers cust = (Customers) parser.getObject(new File(CUSTOMER), Customers.class);
			for (Customer customer : new ArrayList<>(cust.getCustomers())) {
				customer.getProjects().forEach(p -> {
					if(p.getName().equals(project_name)){
						customer.removeProject(p);
					}
				});
			}
			parser.saveObject(new File(CUSTOMER), cust);
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Project getProjectById(int project_id) {
		if (project_id <= 0) {
			System.out.println("No proj id");
			return null;
		}
		Parser parser = new JaxbParser();
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getId() == project_id) {
					return project;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Project getProjectByName(String project_name) {
		if (project_name == null || "".equals(project_name) || " ".equals(project_name)) {
			System.out.println("No such name");
			return null;
		}
		Parser parser = new JaxbParser();
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getName() == project_name) {
					return project;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Project> getProjects() {
		Parser parser = new JaxbParser();
		try {
			Projects projs = (Projects) parser.getObject(new File("project.xml"), Projects.class);
			if (projs.getProjects() != null) {
				return projs.getProjects();
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean updateProject(int id, String name, int lead_id, int customer_id) {// TODO
																						// rewrite
																						// properly
		if (id <= 0) {
			System.out.println("Wrong id");
			return false;
		}
		Parser parser = new JaxbParser();
		updateName(id, name, parser);
		updateLead(id, lead_id, parser);
		updateCustomer(id, customer_id, parser);
		return true;
	}

	private boolean updateName(int id, String name, Parser parser) {
		if (name == null || "".equals(name) || " ".equals(name)) {
			return false;
		}
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getId() == id) {
					project.setName(name);
				}
			}
			parser.saveObject(new File(PROJECT), projs);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean updateLead(int id, int lead, Parser parser) {
		if (lead <= 0) {
			return false;
		}
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getId() == id) {
					project.setLead_id(lead);
				}
			}
			parser.saveObject(new File(PROJECT), projs);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean updateCustomer(int id, int customer, Parser parser) {
		if (customer <= 0) {
			return false;
		}
		try {
			Projects proj = (Projects) parser.getObject(new File(PROJECT), Project.class);
			for (Project project : proj) {
				if (project.getId() == id) {
					project.setCustomer_id(customer);
				}
			}
			parser.saveObject(new File(PROJECT), proj);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return true;
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

}
