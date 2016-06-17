package part_collections;

import java.util.ArrayList;
import java.util.Collection;
import com.sharko.main.Employee;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.Project;

import service.IDGenerator;
import service.IDGenerator.IdType;

public class CollectionEmployeeDAO implements EmployeeDAO {

	@Override
	public Collection<Employee> getEmployeesByProject(int project_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		for (Employee employee : CollectionEntityStorage.empList) {
			employee.getProjects().forEach(p -> {
				if (p.getId() == project_id) {
					emp.add(employee);
				}
			});
		}
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByEmployee(int employee_id) {
		if (employee_id <= 0) {
			System.out.println("wrong id");
			return null;
		}
		return getEmployeeById(employee_id).getProjects();
	}

	@Override
	public Collection<Project> getProjectsByEmployee(String employee_name) {
		if (employee_name == null) {
			System.out.println("wrong name");
			return null;
		}
		return getEmployeeByName(employee_name).getProjects();
	}

	@Override
	public Collection<Employee> getIdleEmpByDepartment(String department) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		CollectionEntityStorage.empList.forEach(e -> {
			if (e.getDepartment().equals(department)) {
				if (e.getProjects().isEmpty()) {
					emp.add(e);
				}
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getIdleEmployee() {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		CollectionEntityStorage.empList.forEach(e -> {
			if (e.getProjects().isEmpty()) {
				emp.add(e);
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getEmployeeByLead(int employee_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		CollectionEntityStorage.projectList.forEach(p -> {
			if (p.getLead_id() == employee_id) {
				Collection<Employee> emp1 = getEmployeesByProject(p.getId());
				emp1.remove(getEmployeeById(p.getLead_id()));
				emp.addAll(emp1);
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getLeadsByEmp(int employee_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		getProjectsByEmployee(employee_id).forEach(p -> {
			if (p.getLead_id() != 0 && p.getLead_id() > 0 && p.getLead_id() != employee_id) {
				emp.add(getEmployeeById(p.getLead_id()));
			}
		});
		return emp;
	}

	@Override
	public Collection<Employee> getEmpBySameProjects(int employee_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();// TODO
		CollectionEntityStorage.empList.forEach(e -> {
			if (getEmployeeById(employee_id).getProjects().containsAll(getProjectsByEmployee(e.getId()))
					&& e.getId() != employee_id) {
				emp.add(e);
			}
		});
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByCustomer(int customer_id) {
		ArrayList<Project> proj = new ArrayList<Project>();
		CollectionEntityStorage.projectList.forEach(p -> {
			if (p.getCustomer_id() == customer_id) {
				proj.add(p);
			}
		});
		return proj;
	}

	@Override
	public Collection<Employee> getEmpByCustomer(int customer_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		CollectionEntityStorage.projectList.forEach(p -> {
			if (p.getCustomer_id() == customer_id) {
				emp.addAll(getEmployeesByProject(p.getId()));
			}
		});
		return emp;
	}

	@Override
	public int addEmployee(String emp_name, String department) {
		int id = IDGenerator.generateCollectionId(IdType.EMPLOYEE);
		CollectionEntityStorage.empList.add(new Employee(id, emp_name, department));
		return id;
	}

	@Override
	public boolean addProjectToEmployee(int employee_id, int project_id) {
		if (checkProjectCopies(employee_id, project_id)) {
			getEmployeeById(employee_id).setProject(getProjectById(project_id));
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteEmployeeById(int id) {
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getId() == id) {
				CollectionEntityStorage.empList.remove(employee);
				return true;
			}
		}
		return false;
	}

	@Override
	public Employee getEmployeeById(int id) {
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getId() == id) {
				return employee;
			}
		}
		return null;
	}

	@Override
	public Collection<Employee> getEmployeesByName(String name) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getName().equals(name)) {
				emp.add(employee);
			}
		}
		return emp;
	}

	@Override
	public Collection<Employee> getEmployees() {
		return CollectionEntityStorage.empList;
	}

	@Override
	public boolean updateEmployee(int id, String name, String department) {
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getId() == id) {
				updateName(id, name);
				updateDepartment(id, department);
				return true;
			}
		}
		return false;
	}

	private boolean updateName(int id, String name) {
		if (name == null) {
			return false;
		} else {
			getEmployeeById(id).setName(name);
			System.out.println("name updated");
			return true;
		}
	}

	private boolean updateDepartment(int id, String department) {
		if (department == null) {
			return false;
		} else {
			getEmployeeById(id).setDepartment(department);
			System.out.println("department updated");
			return true;
		}
	}

	private Project getProjectById(int project_id) {
		for (Project project : CollectionEntityStorage.projectList) {
			if (project.getId() == project_id) {
				return project;
			}
		}
		return null;
	}

	/**
	 * It's more a helper method. Because it returns only one single employee.
	 * But IRL you don't meet complete namesakes to often so I let myself use
	 * this method
	 * 
	 * @param name
	 * @return
	 */
	private Employee getEmployeeByName(String name) {
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getName().equals(name)) {
				return employee;
			}
		}
		return null;
	}

	private boolean checkProjectCopies(int emp_id, int proj_id) {
		for (Employee emp : CollectionEntityStorage.empList) {
			if (emp.getProjects().isEmpty()) {
				return true;
			}
			for (Project proj : emp.getProjects()) {
				if (proj.getId() == proj_id) {
					return false;
				}
			}
		}
		return false;
	}
}
