package part_jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.sharko.main.Employee;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.Project;

public class JdbcEmployeeDAO implements EmployeeDAO {

	@Override
	public int addEmployee(String emp_name, String department) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			stmt.executeUpdate(
					"insert into employee (name, department) values ('" + emp_name + "','" + department + "')",
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	@Override
	public boolean deleteEmployeeById(int id) {
		final String deleteStatement = "delete employee where id = " + id;
		final String deleteFromProj = "delete emp_proj where emp_id = " + id;
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			stmt.execute(deleteStatement);
			stmt.execute(deleteFromProj);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Employee getEmployeeById(int employee_id) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM employee WHERE id = " + employee_id);
			while (rs.next()) {
				return new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByEmployee(employee_id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Employee> getEmployeesByName(String name) {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM employee WHERE name = '" + name + "'");
			while (rs.next()) {
				emp.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("department"),
						getProjectsByEmployee(name)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Employee> getEmployees() {
		ArrayList<Employee> emp = new ArrayList<Employee>();
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM employee");
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
	public boolean updateEmployee(int id, String name, String department) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			updateName(id, name, conn);
			updateDepartment(id, department, conn);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean addProjectToEmployee(int employee_id, int project_id) {
		try (Connection conn = JdbcDAOFactory.createConnection(); Statement stmt = (Statement) conn.createStatement()) {
			if (checkCopies(employee_id, project_id, conn)) {
				return stmt.execute(
						"INSERT INTO emp_proj (emp_id, proj_id) VALUES (" + employee_id + ", " + project_id + ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("already exists");
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
						getEmployeeById(rs.getInt("id")).getProjects()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emp;
	}

	@Override
	public Collection<Project> getProjectsByEmployee(int employee_id) {
		ArrayList<Project> proj = new ArrayList<Project>();
		try (Connection conn = JdbcDAOFactory.createConnection();
				Statement stmt = (Statement) conn.createStatement();
				Statement stmt_temp = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM project WHERE id IN "
					+ "(SELECT proj_id FROM emp_proj WHERE emp_id = " + employee_id + ")");
			while (rs.next()) {
				String query = "SELECT emp_id FROM emp_proj WHERE proj_id = " + rs.getInt("id");
				String q = "SELECT * FROM employee WHERE id IN (" + query + ")";
				ResultSet temp = stmt_temp.executeQuery(q);
				Collection<Employee> emps = new ArrayList<Employee>();
				while (temp.next()) {
					emps.add(new Employee(temp.getInt("id"), temp.getString("name"), temp.getString("department")));
				}
				proj.add(new Project(rs.getInt("id"), rs.getString("name"), rs.getInt("lead"), rs.getInt("customer"),
						emps));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proj;
	}

	@Override
	public Collection<Project> getProjectsByEmployee(String employee_name) {
		ArrayList<Project> proj = new ArrayList<Project>();
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
				return stmt.execute("UPDATE employee SET name = '" + name + "' WHERE id = " + id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updateDepartment(int id, String depertment, Connection conn) {
		if (depertment == null) {
			return false;
		} else {
			try (Statement stmt = (Statement) conn.createStatement()) {
				return stmt.execute("UPDATE employee SET department = '" + depertment + "' WHERE id = " + id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean checkCopies(int employee_id, int project_id, Connection conn) {
		String query = "SELECT * FROM emp_proj WHERE emp_id = " + employee_id + " AND proj_id = " + project_id;
		try (Statement stmt = (Statement) conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
