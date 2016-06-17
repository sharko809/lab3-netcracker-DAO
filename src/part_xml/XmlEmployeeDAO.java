package part_xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.sharko.main.Employee;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.Project;

import part_xml.parser.JaxbParser;
import part_xml.parser.Parser;
import part_xml.xmlEntities.Customers;
import part_xml.xmlEntities.Employees;
import part_xml.xmlEntities.Projects;
import service.IDGenerator;
import service.IDGenerator.IdType;

public class XmlEmployeeDAO implements EmployeeDAO {

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
		for (Employee employee : new ArrayList<Employee>(emp)) {
			if(employee_id==employee.getId()){
				emp.remove(employee);
			}
		}
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
	public int addEmployee(String emp_name, String department) {
		int id = IDGenerator.generateXmlId(IdType.EMPLOYEE);
		Parser parser = new JaxbParser();
		try {
			Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			emps.addEmployee(new Employee(id, emp_name, department, new ArrayList<Project>()));
			parser.saveObject(new File(EMPLOYEE), emps);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public boolean addProjectToEmployee(int employee_id, int project_id) {
		if (checkProjectCopies(employee_id, project_id) && employee_id > 0 && project_id > 0) {
			Parser parser = new JaxbParser();
			try {
				Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
				Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
				emps.forEach(e -> {
					if (e.getId() == employee_id) {
						projs.forEach(p -> {
							if (p.getId() == project_id) {
								p.setEmployee(e);
							}
						});
						try {
							parser.saveObject(new File(PROJECT), projs);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						e.setProject(getProjById(project_id));
					}
				});
				parser.saveObject(new File(EMPLOYEE), emps);
				return true;
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean deleteEmployeeById(int id) {
		if (id <= 0) {
			System.out.println("No such id");
			return false;
		}
		Parser parser = new JaxbParser();
		try {
			Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : new ArrayList<Project>(projs.getProjects())) {
				new ArrayList<Employee>(project.getEmployees()).forEach(e -> {
					if (e.getId() == id) {
						project.removeEmployee(e);
					}
				});
			}
			parser.saveObject(new File(PROJECT), projs);
			for (Employee employee : new ArrayList<Employee>(emps.getEmployees())) {
				if (employee.getId() == id) {
					emps.removeEmployee(employee);
					parser.saveObject(new File(EMPLOYEE), emps);
					return true;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Employee getEmployeeById(int id) {
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

	@Override
	public Collection<Employee> getEmployeesByName(String name) {
		Collection<Employee> emp = new ArrayList<Employee>();
		getEmployees().forEach(e -> {
			if (name.equals(e.getName())) {
				emp.add(e);
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getEmployees() {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		Parser parser = new JaxbParser();
		try {
			Employees e = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			emp.addAll(e.getEmployees());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public boolean updateEmployee(int id, String name, String department) {
		if (id <= 0) {
			System.out.println("Wrong id");
			return false;
		}
		Parser parser = new JaxbParser();
		updateName(id, name, parser);
		updateDepartment(id, department, parser);
		return true;
	}

	private boolean updateName(int id, String name, Parser parser) {
		if (name == null || "".equals(name) || " ".equals(name)) {
			return false;
		}
		try {
			Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
			for (Employee employee : emps) {
				if (employee.getId() == id) {
					employee.setName(name);
					return true;
				}
			}
			parser.saveObject(new File(EMPLOYEE), emps);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean updateDepartment(int id, String department, Parser parser) {
		if (department == null || "".equals(department) || " ".equals(department)) {
			return false;
		} else {
			try {
				Employees emps = (Employees) parser.getObject(new File(EMPLOYEE), Employees.class);
				for (Employee employee : emps) {
					if (employee.getId() == id) {
						employee.setDepartment(department);
						return true;
					}
				}
				parser.saveObject(new File(EMPLOYEE), emps);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private Employee getEmployeeByName(String emp_name) {
		if (emp_name.trim().equals("") || emp_name == null) {
			System.out.println("That's not a name. Please, try something else.");
			return null;
		}
		for (Employee employee : getEmployees()) {
			if (emp_name.equals(employee.getName())) {
				return employee;
			}
		}
		return null;
	}

	private Project getProjById(int id) {
		if (id <= 0) {
			System.out.println("No proj id");
			return null;
		}
		Parser parser = new JaxbParser();
		try {
			Projects projs = (Projects) parser.getObject(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getId() == id) {
					return project;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkProjectCopies(final int emp_id, final int proj_id) {
		Collection<Project> projs = getProjectsByEmployee(emp_id);
		if (projs == null || projs.isEmpty()) {
			return true;
		}
		for (Project project : projs) {
			if (project.getId() == proj_id) {
				return false;
			}
		}
		return true;
	}

}
