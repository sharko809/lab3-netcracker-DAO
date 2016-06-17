package part_fileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.sharko.main.Customer;
import com.sharko.main.Employee;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.Project;

import part_xml.xmlEntities.Customers;
import part_xml.xmlEntities.Employees;
import part_xml.xmlEntities.Projects;
import service.IDGenerator;
import service.IDGenerator.IdType;

public class TextEmployeeDAO implements EmployeeDAO {

	private static final String EMPLOYEE = "employee.txt";
	private static final String PROJECT = "project.txt";
	private static final String CUSTOMER = "customer.txt";

	@Override
	public Collection<Employee> getEmployeesByProject(int project_id) {
		if (project_id <= 0) {
			System.out.println("Nu such project id");
			return new ArrayList<Employee>();
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			Projects projs = (Projects) mapper.readValue(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getId() == project_id) {
					Collection<Employee> emps = project.getEmployees();
					if (emps == null || emps.isEmpty()) {
						System.out.println("No employees on this project yet");
					}
					return emps;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<Employee>();
	}

	@Override
	public Collection<Project> getProjectsByEmployee(int employee_id) {
		if (employee_id <= 0) {
			System.out.println("Nu such employee id");
			return new ArrayList<Project>();
		}
		Collection<Project> projs = getEmployeeById(employee_id).getProjects();
		if (projs == null || projs.isEmpty()) {
			System.out.println("This employee has no projects yet");
			return new ArrayList<Project>();
		}
		return projs;
	}

	@Override
	public Collection<Project> getProjectsByEmployee(String employee_name) {
		if (checkString(employee_name)) {
			System.out.println("Nu such employee name");
			return new ArrayList<Project>();
		}
		Collection<Project> projs = getEmployeeByName(employee_name).getProjects();
		if (projs == null || projs.isEmpty()) {
			System.out.println("This employee has no projects yet");
			return new ArrayList<Project>();
		}
		return projs;
	}

	@Override
	public Collection<Employee> getIdleEmpByDepartment(String department) {
		Collection<Employee> emps = getEmployees();
		Collection<Employee> match = new ArrayList<Employee>();
		if (emps == null || emps.isEmpty()) {
			System.out.println("No employees");
			return new ArrayList<Employee>();
		}
		if (checkString(department)) {
			System.out.println("No such department");
			return new ArrayList<Employee>();
		}
		for (Employee employee : emps) {
			if (department.equals(employee.getDepartment())
					&& (employee.getProjects() == null || employee.getProjects().isEmpty())) {
				match.add(employee);
			}
		}
		return match;
	}

	@Override
	public Collection<Employee> getIdleEmployee() {
		Collection<Employee> emps = getEmployees();
		Collection<Employee> match = new ArrayList<Employee>();
		if (emps.isEmpty()) {
			System.out.println("No employees");
			return new ArrayList<Employee>();
		}
		for (Employee employee : emps) {
			if (employee.getProjects() == null || employee.getProjects().isEmpty()) {
				match.add(employee);
			}
		}
		return match;
	}

	@Override
	public Collection<Employee> getEmployeeByLead(int employee_id) {
		if (employee_id <= 0) {
			System.out.println("Nu such employee id");
			return new ArrayList<Employee>();
		}
		Collection<Project> projs = getProjectsByEmployee(employee_id);
		if (projs == null || projs.isEmpty()) {
			System.out.println("This employee has no projects yet");
			new ArrayList<Employee>();
		}
		Collection<Employee> match = new HashSet<Employee>();
		projs.forEach(p -> {
			if (p.getLead_id() != employee_id) {
				match.addAll(getEmployeesByProject(p.getId()));
			}
		});
		// remove Lead himself from list
		for (Employee employee : new ArrayList<Employee>(match)) {
			if (employee.getId() == employee_id) {
				match.remove(employee);
			}
		}
		return match;
	}

	@Override
	public Collection<Employee> getLeadsByEmp(int employee_id) {
		if (employee_id <= 0) {
			System.out.println("No such employee id");
			return new ArrayList<Employee>();
		}
		Collection<Employee> match = new ArrayList<Employee>();
		getProjectsByEmployee(employee_id).forEach(p -> {
			if (!(p.getLead_id() <= 0) && p.getLead_id() != employee_id) {
				match.add(getEmployeeById(p.getLead_id()));
			}
		});
		return match;
	}

	@Override
	public Collection<Employee> getEmpBySameProjects(int employee_id) {
		Collection<Project> proj = getProjectsByEmployee(employee_id);
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
		for (Employee employee : new ArrayList<Employee>(emp)) {
			if (employee.getId() == employee_id) {
				emp.remove(employee);
			}
		}
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByCustomer(int customer_id) {
		if (customer_id <= 0) {
			System.out.println("Nu such customer id");
			return new ArrayList<Project>();
		}
		ObjectMapper mapper = new ObjectMapper();
		Collection<Project> match = new ArrayList<Project>();
		try {
			Customers cust = (Customers) mapper.readValue(new File(CUSTOMER), Customers.class);
			for (Customer customer : cust) {
				if (customer.getId() == customer_id) {
					match.addAll(customer.getProjects());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return match;
	}

	@Override
	public Collection<Employee> getEmpByCustomer(int customer_id) {
		if (customer_id <= 0) {
			System.out.println("Nu such customer id");
			return new ArrayList<Employee>();
		}
		Collection<Employee> match = new ArrayList<Employee>();
		getProjectsByCustomer(customer_id).forEach(p -> {
			Collection<Employee> emps = p.getEmployees();
			match.addAll((emps != null && !emps.isEmpty()) ? emps : new ArrayList<Employee>());
		});
		return match;
	}

	@Override
	public int addEmployee(String emp_name, String department) {
		int id = IDGenerator.generateTextId(IdType.EMPLOYEE);
		ObjectMapper mapper = new ObjectMapper();
		try {
			Employees emps = (Employees) mapper.readValue(new File(EMPLOYEE), Employees.class);
			emps.addEmployee(new Employee(id, emp_name, department, new ArrayList<Project>()));
			mapper.writeValue(new File(EMPLOYEE), emps);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public boolean addProjectToEmployee(int employee_id, int project_id) {
		if (checkProjectCopies(employee_id, project_id) && employee_id > 0 && project_id > 0) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				Employees emps = (Employees) mapper.readValue(new File(EMPLOYEE), Employees.class);
				Projects projs = (Projects) mapper.readValue(new File(PROJECT), Projects.class);
				emps.forEach(e -> {
					if (e.getId() == employee_id) {
						projs.forEach(p -> {
							if (p.getId() == project_id) {
								p.setEmployee(e);
							}
						});
						try {
							mapper.writeValue(new File(PROJECT), projs);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						e.setProject(getProjById(project_id));
					}
				});
				mapper.writeValue(new File(EMPLOYEE), emps);
				return true;
			} catch (IOException e) {
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
		ObjectMapper mapper = new ObjectMapper();
		try {
			Employees emps = (Employees) mapper.readValue(new File(EMPLOYEE), Employees.class);
			Projects projs = (Projects) mapper.readValue(new File(PROJECT), Projects.class);
			for (Project project : new ArrayList<Project>(projs.getProjects())) {
				new ArrayList<Employee>(project.getEmployees()).forEach(e -> {
					if (e.getId() == id) {
						project.removeEmployee(e);
					}
				});
			}
			mapper.writeValue(new File(PROJECT), projs);
			for (Employee employee : new ArrayList<Employee>(emps.getEmployees())) {
				if (employee.getId() == id) {
					emps.removeEmployee(employee);
					mapper.writeValue(new File(EMPLOYEE), emps);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Employee getEmployeeById(int id) {
		Collection<Employee> emps = getEmployees();
		if (id <= 0) {
			System.out.println("Wrong id");
			return new Employee();
		}
		if (emps.isEmpty()) {
			System.out.println("No employees");
			return new Employee();
		}
		for (Employee employee : emps) {
			if (employee.getId() == id) {
				return employee;
			}
		}
		return new Employee();
	}

	@Override
	public Collection<Employee> getEmployeesByName(String name) {
		Collection<Employee> emps = getEmployees();
		Collection<Employee> match = new ArrayList<Employee>();
		if (emps.isEmpty()) {
			System.out.println("No employees");
			return emps;
		}
		if (checkString(name)) {
			System.out.println("Wrong name");
			return emps;
		}
		for (Employee employee : emps) {
			if (name.equals(employee.getName())) {
				match.add(employee);
			}
		}
		return match;
	}

	@Override
	public Collection<Employee> getEmployees() {
		ObjectMapper mapper = new ObjectMapper();
		Collection<Employee> emp = new ArrayList<Employee>();
		try {
			Employees emps = (Employees) mapper.readValue(new File(EMPLOYEE), Employees.class);
			for (Employee employee : emps) {
				emp.add(employee);
			}
		} catch (IOException e) {
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
		ObjectMapper mapper = new ObjectMapper();
		updateName(id, name, mapper);
		updateDepartment(id, department, mapper);
		return true;
	}

	private boolean updateName(int id, String name, ObjectMapper mapper) {
		if (checkString(name)) {
			return false;
		}
		try {
			Employees emps = (Employees) mapper.readValue(new File(EMPLOYEE), Employees.class);
			for (Employee employee : emps) {
				if (employee.getId() == id) {
					employee.setName(name);
					return true;
				}
			}
			mapper.writeValue(new File(EMPLOYEE), emps);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean updateDepartment(int id, String department, ObjectMapper mapper) {
		if (checkString(department)) {
			return false;
		} else {
			try {
				Employees emps = (Employees) mapper.readValue(new File(EMPLOYEE), Employees.class);
				for (Employee employee : emps) {
					if (employee.getId() == id) {
						employee.setDepartment(department);
						return true;
					}
				}
				mapper.writeValue(new File(EMPLOYEE), emps);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private Employee getEmployeeByName(String name) {
		Collection<Employee> emps = getEmployees();
		if (emps.isEmpty()) {
			System.out.println("No employees");
			return new Employee();
		}
		if (checkString(name)) {
			System.out.println("Wrong name");
			return new Employee();
		}
		for (Employee employee : emps) {
			if (name.equals(employee.getName())) {
				return employee;
			}
		}
		System.out.println("No name found");
		return new Employee();
	}

	private boolean checkString(String str) {
		if (str == null || str.trim().equals("") || str.equals(" ")) {
			return true;
		}
		return false;
	}

	private Project getProjById(int id) {
		if (id <= 0) {
			System.out.println("No proj id");
			return new Project();
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			Projects projs = (Projects) mapper.readValue(new File(PROJECT), Projects.class);
			for (Project project : projs) {
				if (project.getId() == id) {
					return project;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Project();
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


