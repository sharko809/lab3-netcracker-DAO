package com.sharko.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Neltarion
 *         <p>
 *         Customer transfer object. Used to communicate with client.
 *         </p>
 */
@XmlRootElement(name = "EMPLOYEE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "name", "department", "projects" })
public class Employee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1427080968870287923L;
	@XmlElement
	private int id;
	@XmlElement
	private String name = "";
	@XmlElement
	private String department = "";
	@XmlElement
	@XmlElementWrapper
	private List<Project> projects = null;

	public Employee() {
	}

	public Employee(int emp_id, String emp_name, String emp_department) {
		this.id = emp_id;
		this.name = emp_name;
		this.department = emp_department;
	}

	public Employee(int emp_id, String emp_name, String emp_department, Project project) {
		this.id = emp_id;
		this.name = emp_name;
		this.department = emp_department;
		this.projects.add(project);
	}

	public Employee(int emp_id, String emp_name, String emp_department, Collection<Project> projects) {
		this.id = emp_id;
		this.name = emp_name;
		this.department = emp_department;
		addProject(projects);
	}

	private void addProject(Collection<Project> projects) {
		if (this.projects != null) {
			this.projects.addAll(projects);
		} else if (this.projects == null) {
			this.projects = new ArrayList<Project>();
			this.projects.addAll(projects);
		}
	}

	public int getId() {
		return id;
	}

	@XmlTransient
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@XmlTransient
	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	@XmlTransient
	public void setDepartment(String department) {
		this.department = department;
	}

	public List<Project> getProjects() {
		return this.projects;
	}

	@XmlTransient
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public void setProject(Project project) {
		if (this.projects == null) {
			this.projects = new ArrayList<Project>();
		}
		this.projects.add(project);
	}
	
	public void removeProject(Project proj) {
		Iterator<Project> iterator = projects.iterator();
		while (iterator.hasNext()) {
			Project p = iterator.next();
			if (proj != null && (proj.getId() == p.getId())) {
				iterator.remove();
			}
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String toStringShort() {
		return "Employee [id=" + id + ", name=" + name + ", department=" + department + "]";
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", Name: " + name + ", Department: " + department + ", Projects: "
				+ parseProjects(projects) + "]";
	}

	private String parseProjects(List<Project> projects) {
		if (projects == null || projects.isEmpty()) {
			return "No projects yet :(";
		}
		// used StringBuffer instead of StringBuilder due to speed
		StringBuffer temp = new StringBuffer();
		projects.forEach(p -> {
			temp.append(p.getName() + ", ");
		});
		replaceComa(temp); // just to make it look more pretty
		return temp.toString();
	}

	private void replaceComa(StringBuffer str) {
		for (int i = str.length() - 1; i >= 0; i--) {
			if (str.charAt(i - 1) == ',' && str.charAt(i) == ' ') {
				str.setCharAt(i - 1, '.');
				str.deleteCharAt(i);
				break;
			}
		}
	}


}
