package com.sharko.main;

import java.util.Collection;

/**
 * 
 * @author Neltarion
 *         <p>
 *         Base DAO interface for Employee. Each CustomerDAO should implement
 *         this interface.
 *         </p>
 */
public interface EmployeeDAO extends WorkWithData {

	/**
	 * 
	 * @param emp_name
	 *            Employee name that will be added
	 * @param department
	 *            Employee department that will be added
	 * @return Employee ID
	 */
	public abstract int addEmployee(String emp_name, String department);

	/**
	 * Adds <b>EXISTING</b> project to employee
	 * @param employee_id
	 *            Employee (his ID) for which you want to add project.
	 * @param project_id
	 *            ID of project to add.
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean addProjectToEmployee(int employee_id, int project_id);

	/**
	 * 
	 * @param id
	 *            ID of employee to delete
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean deleteEmployeeById(int id);

	/**
	 * 
	 * @param id
	 *            ID of employee to retrieve
	 * @return employee as an example of <b>Employee</b> class
	 */
	public abstract Employee getEmployeeById(int id);

	public Collection<Employee> getEmployeesByName(String name);
	
	/**
	 * 
	 * @return List of all employees from database as an example of
	 *         <b>Employee</b> class
	 */
	public abstract Collection<Employee> getEmployees();

	/**
	 * Updates data for specific employee.
	 * 
	 * @param id
	 *            ID of employee you want to update
	 * @param name
	 *            Name you want to set to employee. If you don't want to change
	 *            name set this parameter to <b>null</b>
	 * @param department
	 *            Department you want to set to employee. If you don't want to
	 *            change it set this parameter to <b>null</b>
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean updateEmployee(int id, String name, String department);

}
