package part_collections;

import java.util.ArrayList;
import java.util.Collection;
import com.sharko.main.Employee;
import com.sharko.main.Project;
import com.sharko.main.ProjectDAO;

import service.IDGenerator;
import service.IDGenerator.IdType;

public class CollectionProjectDAO implements ProjectDAO {
	
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
	public int addProject(String project_name, int lead_id, int customer_id) {
		int id = IDGenerator.generateCollectionId(IdType.PROJECT);
		CollectionEntityStorage.projectList.add(new Project(id, project_name, lead_id, customer_id));
		return id;
	}

	@Override
	public boolean deleteProjectById(int project_id) {
		return CollectionEntityStorage.projectList.remove(getProjectById(project_id));
	}

	@Override
	public boolean deleteProjectByName(String project_name) {
		return CollectionEntityStorage.projectList.remove(getProjectByName(project_name));
	}

	@Override
	public Project getProjectById(int project_id) {
		for (Project project : CollectionEntityStorage.projectList) {
			if (project.getId() == project_id) {
				return project;
			}
		}
		return null;
	}

	@Override
	public Project getProjectByName(String project_name) {
		for (Project project : CollectionEntityStorage.projectList) {
			if (project.getName().equals(project_name)) {
				return project;
			}
		}
		return null;
	}

	@Override
	public Collection<Project> getProjects() {
		return CollectionEntityStorage.projectList;
	}

	@Override
	public boolean updateProject(int id, String name, int lead_id, int customer_id) {
		for (Project project : CollectionEntityStorage.projectList) {
			if (project.getId() == id) {
				updateName(id, name);
				updateLead(id, lead_id);
				updateCustomer(id, customer_id);
				return true;
			}
		}
		return false;
	}

	private boolean updateName(int id, String name) {
		if (name == null) {
			return false;
		} else {
			getProjectById(id).setName(name);
			return true;
		}
	}

	private boolean updateLead(int id, int lead_id) {
		if(lead_id==0){
			return false;
		} else {
			getProjectById(id).setLead_id(lead_id);
			return true;
		}
	}

	private boolean updateCustomer(int id, int customer_id) {
		if(customer_id==0){
			return false;
		} else {
			getProjectById(id).setCustomer_id(customer_id);
			return true;
		}
	}

	private Employee getEmployeeById(int id) {
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getId() == id) {
				return employee;
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
	
}