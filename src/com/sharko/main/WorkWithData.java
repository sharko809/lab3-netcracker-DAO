package com.sharko.main;

import java.util.Collection;
import java.util.List;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import part_jdbc.JdbcDAOFactory;
import service.GenericSort;

/**
 * 
 * @author Neltarion
 *         <p>
 *         Interface containing method for work with projects, employees and
 *         customers. For example sort them or get particular information about
 *         them.
 *         </p>
 * 
 *         <p>
 *         <b>Notice:</b> also contains default genericSort method sorting any
 *         object by it's desired field.
 *         </p>
 */
public interface WorkWithData {

	/**
	 * Get all employees participating in <i><b>this</b></i> project
	 * 
	 * @param project_id
	 *            Project id where you get employees from.
	 * @return List of employees from specified project.
	 */
	public abstract Collection<Employee> getEmployeesByProject(int project_id);

	/**
	 * Get all projects in which <i><b>this</b></i> employee participates
	 * 
	 * @param employee_id
	 *            Id of employee which projects you want to retrieve.
	 * @return List of projects which include specific employee.
	 */
	public abstract Collection<Project> getProjectsByEmployee(int employee_id);

	/**
	 * Get all projects in which <i><b>this</b></i> employee participates
	 * 
	 * @param employee_name
	 *            Name of employee which projects you want to retrieve.
	 * @return List of projects which include specific employee.
	 */
	public abstract Collection<Project> getProjectsByEmployee(String employee_name);

	/**
	 * Get all employees from <i><b>this</b></i> department that don't
	 * participate in any project
	 * 
	 * @param department
	 * @return List of employees from specific department that are not involved
	 *         in any project.
	 */
	public abstract Collection<Employee> getIdleEmpByDepartment(String department);

	/**
	 * Get all employees that don't participate in any project
	 * 
	 * @return List of employees that are not involved in ANY project.
	 */
	public abstract Collection<Employee> getIdleEmployee();

	/**
	 * Get all employees from projects where <i><b>this</b></i> employee is lead
	 * 
	 * @param lead_id
	 *            Employee id
	 * @return List of employees which have specified employee as a lead.
	 */
	public abstract Collection<Employee> getEmployeeByLead(int employee_id);

	/**
	 * Get all leads for <i><b>this</b></i> employee
	 * 
	 * @param employee_id
	 *            Employee id
	 * @return List of project leads which specified employee has.
	 */
	public abstract Collection<Employee> getLeadsByEmp(int employee_id);

	/**
	 * Get all employees having same projects as <i><b>this</b></i> employee
	 * 
	 * @param employee_id
	 *            Employee id
	 * @return List of employees involved in same projects as specified
	 *         employee.
	 */
	public abstract Collection<Employee> getEmpBySameProjects(int employee_id);

	/**
	 * Get all projects ordered by <i><b>this</b></i> customer
	 * 
	 * @param customer_id
	 *            Customer id
	 * @return List of projects for specified customer.
	 */
	public abstract Collection<Project> getProjectsByCustomer(int customer_id);

	/**
	 * Get all employees participating in projects ordered by <i><b>this</b></i>
	 * customer
	 * 
	 * @param customer_id
	 *            Customer id
	 * @return List of employees involved in projects for specified customer.
	 */
	public abstract Collection<Employee> getEmpByCustomer(int customer_id);

	/**
	 * Generic sort method. Can sort any type of entities by every it's field.
	 *
	 * @param toSort
	 *            Collection to be sorted
	 * @param fieldName
	 *            Entity field according to which should be performed sorting
	 * @param isDesc
	 *            By default sorting is ascending. Set this parameter to
	 *            <b>true</b> if you want sorting to be descending
	 */
	default <Type> void genericSortByField(Collection<Type> toSort, String fieldName, boolean isDesc) {
		try {
			GenericSort.sortElements((List<Type>) toSort, fieldName, isDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
