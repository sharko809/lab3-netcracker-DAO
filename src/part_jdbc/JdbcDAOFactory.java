package part_jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Statement;
import com.sharko.main.CustomerDAO;
import com.sharko.main.DAOFactory;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.ProjectDAO;

import snaq.db.ConnectionPool;

public class JdbcDAOFactory extends DAOFactory {

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost/lab03";
	private static final String DB_LOGIN = "root";
	private static final String DB_PASSWORD = "YOUR_PASS";// TODO pass

	/**
	 * Registers JDBC driver and creates connection with database using specific
	 * login and password.
	 * 
	 * @return mySql Connection
	 */
	public static Connection createConnection() {
		Connection conn = null;
		try {
			Class.forName(DRIVER);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			conn = (Connection) DriverManager.getConnection(URL, DB_LOGIN, DB_PASSWORD);
			return conn;
			// TODO connection pool
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public static ConnectionPool createConnectionWithPool() {

		try {
			Driver driver = (Driver) Class.forName(DRIVER).newInstance();
			DriverManager.registerDriver(driver);
			ConnectionPool pool = new ConnectionPool("pool", 5, 200, 250, 200, URL, DB_LOGIN, DB_PASSWORD);
			return pool;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Closes connection if it's active
	 * 
	 * @param conn
	 *            connection to close
	 */
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * Closes statement if it's active
	 * 
	 * @param stmt
	 *            statement to close
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public EmployeeDAO getEmployeeDAO() {
		return new JdbcEmployeeDAO();
	}

	@Override
	public ProjectDAO getProjectDAO() {
		return new JdbcProjectDAO();
	}

	@Override
	public CustomerDAO getCustomerDAO() {
		return new JdbcCustomerDAO();
	}

}
