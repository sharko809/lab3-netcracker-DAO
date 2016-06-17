package part_collections;

import com.sharko.main.CustomerDAO;
import com.sharko.main.DAOFactory;
import com.sharko.main.EmployeeDAO;
import com.sharko.main.ProjectDAO;

public class CollectionDAOFactory extends DAOFactory {

	@Override
	public EmployeeDAO getEmployeeDAO() {
		return new CollectionEmployeeDAO();
	}

	@Override
	public ProjectDAO getProjectDAO() {
		return new CollectionProjectDAO();
	}

	@Override
	public CustomerDAO getCustomerDAO() {
		return new CollectionCustomerDAO();
	}

}
