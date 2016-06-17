package part_collections;

import java.util.ArrayList;
import java.util.Collection;
import com.sharko.main.Customer;
import com.sharko.main.CustomerDAO;
import com.sharko.main.Employee;
import com.sharko.main.Project;

import service.IDGenerator;
import service.IDGenerator.IdType;

public class CollectionCustomerDAO implements CustomerDAO {
	
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
	public int addCustomer(String name) {
		int id = IDGenerator.generateCollectionId(IdType.CUSTOMER);
		CollectionEntityStorage.customerList.add(new Customer(id, name));
		return id;
	}

	@Override
	public int addProjectToCustomer(int customer_id, String project_name, int lead_id) {
		int id = IDGenerator.generateCollectionId(IdType.PROJECT);
		CollectionEntityStorage.projectList.add(new Project(id, project_name, lead_id, customer_id));
		return id;
	}

	@Override
	public boolean deleteCustomerById(int id) {
		for (Customer customer : CollectionEntityStorage.customerList) {
			if (customer.getId() == id) {
				return CollectionEntityStorage.customerList.remove(customer);
			}
		}
		return false;
	}

	@Override
	public boolean deleteCustomerByName(String name) {
		for (Customer customer : CollectionEntityStorage.customerList) {
			if (customer.getName().equals(name)) {
				return CollectionEntityStorage.customerList.remove(customer);
			}
		}
		return false;
	}

	@Override
	public Customer getCustomerById(int customer_id) {
		for (Customer customer : CollectionEntityStorage.customerList) {
			if (customer.getId() == customer_id) {
				return customer;
			}
		}
		return null;
	}

	@Override
	public Customer getCustomerByName(String name) {
		for (Customer customer2 : CollectionEntityStorage.customerList) {
			if (customer2.getName().equals(name)) {
				return customer2;
			}
		}
		return null;
	}

	@Override
	public Collection<Customer> getCustomers() {
		return CollectionEntityStorage.customerList;
	}

	@Override
	public boolean updateCustomer(int id, String name) {
		for (Customer customer : CollectionEntityStorage.customerList) {
			if (customer.getId() == id) {
				return updateName(id, name);
			}
		}
		return false;
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

	private Employee getEmployeeById(int id) {
		for (Employee employee : CollectionEntityStorage.empList) {
			if (employee.getId() == id) {
				return employee;
			}
		}
		return null;
	}
	
	private boolean updateName(int id, String name) {
		if(name==null){
			return false;
		} else {
			getCustomerById(id).setName(name);
			return true;
		}
	}

}
