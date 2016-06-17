package com.sharko.main;

import java.util.Collection;

/**
 * 
 * @author Neltarion
 *
 *         <p>
 *         Base DAO interface for Employee. Each CustomerDAO should implement
 *         this interface.
 */
public interface CustomerDAO extends WorkWithData {

	/**
	 * Creates new customer record
	 * 
	 * @param name
	 *            Customer name to be added
	 * @return Row number
	 */
	public abstract int addCustomer(String name);

	/**
	 * Adds new project for specific customer
	 * 
	 * @param customer_id
	 *            customer ID
	 * @param project_id
	 *            ID of project to be added
	 * @return project id or 0
	 */
	public abstract int addProjectToCustomer(int customer_id, String project_name, int lead_id);

	/**
	 * Deletes customer record. Customer's projects WILL NOT be deleted.
	 * 
	 * @param id
	 *            Customer's id
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean deleteCustomerById(int id);

	/**
	 * Deletes customer record. Customer's projects WILL NOT be deleted.
	 * 
	 * @param name
	 *            Customer's name
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean deleteCustomerByName(String name);

	/**
	 * 
	 * @param customer_id
	 *            Customer ID
	 * @return customer as an example of <b>Customer</b> class
	 */
	public abstract Customer getCustomerById(int customer_id);

	/**
	 * 
	 * @param name
	 *            Customer name
	 * @return List of customers, each as an example of <b>Customer</b> class
	 */
	public abstract Customer getCustomerByName(String name);

	/**
	 * 
	 * @return List of all customers, each as an example of <b>Customer</b>
	 *         class
	 */
	public abstract Collection<Customer> getCustomers();

	/**
	 * Updates customer data
	 * 
	 * @param id
	 *            ID of customer to be updated
	 * @param name
	 *            New customer name. Set <b>null</b> if you don't want to change
	 *            name
	 * @param project
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean updateCustomer(int id, String name);

}
