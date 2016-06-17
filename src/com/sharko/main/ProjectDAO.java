package com.sharko.main;

import java.util.Collection;

/**
 * 
 * @author Neltarion
 *
 *         <p>
 *         Base DAO interface for Project. Each ProjectDAO should implement
 *         this interface.
 */
public interface ProjectDAO extends WorkWithData {

	/**
	 * Adds project record
	 * 
	 * @param project_name
	 *            Project name to add
	 * @param lead_id
	 *            ID of employee which leads this project. If project has no
	 *            leads set this parameter to 0.
	 * @param customer
	 *            Customer ID
	 * @return project ID
	 */
	public abstract int addProject(String project_name, int lead_id, int customer_id);

	/**
	 * Deletes project record
	 * 
	 * @param project_id
	 *            ID of project to be deleted
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean deleteProjectById(int project_id);

	/**
	 * Deletes project record
	 * 
	 * @param project_name
	 *            name of project to be deleted
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean deleteProjectByName(String project_name);

	/**
	 * 
	 * @param project_id
	 *            Project ID
	 * @return project as an example of <b>Project</b> class
	 */
	public abstract Project getProjectById(int project_id);

	/**
	 * 
	 * @param project_name
	 *            Project name
	 * @return List of projects, each as an example of <b>Project</b> class
	 */
	public abstract Project getProjectByName(String project_name);

	/**
	 * 
	 * @return List of all projects, each as an example of <b>Project</> class
	 */
	public abstract Collection<Project> getProjects();

	/**
	 * Updates project data
	 * 
	 * @param id
	 *            ID of project to update
	 * @param name
	 *            New project name. Set to <b>null</b> if you don't want to
	 *            change project name
	 * @param lead_id
	 *            ID of employee which leads this project. Set to 0 if project
	 *            doesn't have any lead
	 * @param customer_id
	 *            Customer ID
	 * @return <b>true</b> if operation successful
	 */
	public abstract boolean updateProject(int id, String name, int lead_id, int customer_id);

}
