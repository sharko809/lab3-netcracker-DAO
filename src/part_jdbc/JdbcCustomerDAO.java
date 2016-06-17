package part_jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.sharko.main.Customer;
import com.sharko.main.CustomerDAO;
import com.sharko.main.Employee;
import com.sharko.main.Project;

public class JdbcCustomerDAO implements CustomerDAO {

	@Override
	public int addCustomer(String name) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO customer (name) VALUES ('" + name + "')", Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int addProjectToCustomer(int customer_id, String project_name, int lead_id) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO project (name, lead, customer) VALUES ('" + project_name + "', " + lead_id
					+ ", " + customer_id + ")", Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean deleteCustomerById(int id) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			return stmt.execute("DELETE customer WHERE id = " + id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteCustomerByName(String name) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			return stmt.execute("DELETE customer WHERE name = '" + name + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Customer getCustomerById(int id) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM customer WHERE id = " + id);
			if (rs.next()) {
				return new Customer(rs.getInt("id"), rs.getString("name"), getProjectsByCustomer(id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Customer getCustomerByName(String name) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM customer WHERE name = '" + name + "'");
			if (rs.next()) {
				return new Customer(rs.getInt("id"), rs.getString("name"), getProjectsByCustomer(rs.getInt("id")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Customer> getCustomers() {
		ArrayList<Customer> customer = new ArrayList<Customer>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM customer");
			while (rs.next()) {
				customer.add(new Customer(rs.getInt("id"), rs.getString("name"), getCustomerById(rs.getInt("id")).getProjects()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return customer;
	}

	@Override
	public boolean updateCustomer(int id, String name) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			updateName(id, name, conn);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Collection<Employee> getEmpByCustomer(int customer) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		String query = "SELECT * FROM employee WHERE id IN " + "(SELECT emp_id FROM emp_proj WHERE proj_id IN "
				+ "(SELECT id FROM project WHERE customer = " + customer + "))";
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByCustomer(customer)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Employee> getEmployeesByProject(int project_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		String query = "SELECT * FROM employee WHERE id IN " + "(SELECT emp_id FROM emp_proj WHERE proj_id = "
				+ project_id + ")";
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByEmployee(rs.getInt("id"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByEmployee(int employee_id) {
		ArrayList<Project> proj = new ArrayList<Project>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM project WHERE id IN "
					+ "(SELECT proj_id FROM emp_proj WHERE emp_id = " + employee_id + ")");
			while (rs.next()) {
				proj.add(new Project(rs.getInt("id"), rs.getString("name"), rs.getInt("lead"), rs.getInt("customer")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proj;
	}

	@Override
	public Collection<Project> getProjectsByEmployee(String employee_name) {
		ArrayList<Project> proj = new ArrayList<>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM project WHERE id IN " + "(SELECT proj_id FROM emp_proj WHERE emp_id = "
							+ "(SELECT id FROM employee WHERE name = '" + employee_name + "'))");
			while (rs.next()) {
				proj.add(new Project(rs.getInt("id"), rs.getString("name"), rs.getInt("lead"), rs.getInt("customer")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return proj;
	}

	@Override
	public Collection<Employee> getIdleEmpByDepartment(String department) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("select * from employee where department = '" + department + "'");
			while (rs.next()) {
				if (getProjectsByEmployee(rs.getInt("id")).isEmpty()) {
					emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Employee> getIdleEmployee() {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("select * from employee");
			while (rs.next()) {
				if (getProjectsByEmployee(rs.getInt("id")).isEmpty()) {
					emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Employee> getEmployeeByLead(int lead_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM employee WHERE id IN (SELECT emp_id FROM emp_proj WHERE proj_id IN (SELECT id FROM project WHERE lead = "
							+ lead_id + ")) AND id != " + lead_id);
			while (rs.next()) {
				emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByEmployee(rs.getInt("id"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Employee> getLeadsByEmp(int employee_id) {
		ArrayList<Employee> leads = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM employee WHERE id IN (SELECT lead FROM project WHERE id IN (SELECT proj_id FROM emp_proj WHERE emp_id = "
							+ employee_id + ")) AND id != " + employee_id);
			while (rs.next()) {
				leads.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByEmployee(rs.getInt("id"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return leads;
	}

	@Override
	public Collection<Employee> getEmpBySameProjects(int employee_id) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM employee WHERE id IN (SELECT emp_id FROM emp_proj WHERE proj_id IN (SELECT proj_id FROM emp_proj WHERE emp_id = "
							+ employee_id + ")) AND id != " + employee_id);
			while (rs.next()) {
				emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByEmployee(rs.getInt("id"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByCustomer(int customer) {
		ArrayList<Project> proj = new ArrayList<Project>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM project WHERE customer = " + customer);
			while (rs.next()) {
				proj.add(new Project(rs.getInt("id"), rs.getString("name"), rs.getInt("lead"), rs.getInt("customer"),
						getEmployeesByProject(rs.getInt("id"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return proj;
	}

	private boolean updateName(int id, String name, Connection conn) {
		if (name == null) {
			return false;
		} else {
			try (Statement stmt = (Statement) conn.createStatement()) {
				return stmt.execute("UPDATE customer SET name = '" + name + "' WHERE id = " + id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Deprecated
	private boolean checkCustomerCopies(int id, String name, Connection conn, Statement stmt) {
		try {
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM customer WHERE id = " + id + " AND name = '" + name + "'");
			if (!rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


}
