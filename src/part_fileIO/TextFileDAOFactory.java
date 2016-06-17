package part_fileIO;

import com.sharko.main.CustomerDAO;
import com.sharko.main.DAOFactory;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.ProjectDAO;

public class TextFileDAOFactory extends DAOFactory {

	@Override
	public EmployeeDAO getEmployeeDAO() {
		return new TextEmployeeDAO();
	}

	@Override
	public ProjectDAO getProjectDAO() {
		return new TextProjectDAO();
	}

	@Override
	public CustomerDAO getCustomerDAO() {
		return new TextCustomerDAO();
	}

}
