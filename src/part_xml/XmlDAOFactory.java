package part_xml;

import com.sharko.main.CustomerDAO;
import com.sharko.main.DAOFactory;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.ProjectDAO;

public class XmlDAOFactory extends DAOFactory {

	@Override
	public EmployeeDAO getEmployeeDAO() {
		return new XmlEmployeeDAO();
	}

	@Override
	public ProjectDAO getProjectDAO() {
		return new XmlProjectDAO();
	}

	@Override
	public CustomerDAO getCustomerDAO() {
		return new XmlCustomerDAO();
	}

}
