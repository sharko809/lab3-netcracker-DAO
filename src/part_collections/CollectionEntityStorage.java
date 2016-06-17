package part_collections;

import java.util.ArrayList;

import com.sharko.main.Customer;
import com.sharko.main.Employee;
import com.sharko.main.Project;

/**
 * 
 * @author Neltarion
 *         <p>
 *         Class containing collections storages for <b>Customer</b>s,
 *         <b>Project</b>s and <b>Employee</b>s
 *         </p>
 */
public class CollectionEntityStorage {

	/**
	 * ArrayList that stores customers each as an example of <b>Customer</b>
	 * class
	 */
	public static ArrayList<Customer> customerList = new ArrayList<Customer>();

	/**
	 * ArrayList that stores projects each as an example of <b>Project</b> class
	 */
	public static ArrayList<Project> projectList = new ArrayList<Project>();

	/**
	 * ArrayList that stores employees each as an example of <b>Employee</b>
	 * class
	 */
	public static ArrayList<Employee> empList = new ArrayList<Employee>();

}
