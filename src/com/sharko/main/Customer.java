package com.sharko.main;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
 *         <p>
 * 
 *         <b>Notice: </b>I assume customers to be absolutely unique, so I won't
 *         add any other identification method accept name check.
 *         </p>
 */
@XmlRootElement(name = "CUSTOMER")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "name", "projects" })
public class Customer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 363973210926437853L;

	@XmlElement
	private int id;
	@XmlElement
	private String name = "";
	@XmlElement
	@XmlElementWrapper
	private Collection<Project> projects = null;

	public Customer() {
	}

	/**
	 * This constructor is used to create Customer object and store information.
	 * 
	 * @param name
	 *            Customer name
	 * @param projects
	 *            List of projects (IDs) orders made by customer
	 */
	public Customer(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Customer(int id, String name, Collection<Project> collection) {
		this.id = id;
		this.name = name;
		this.projects = collection;
	}

	public Customer(int id, String name, Project project) {
		this.id = id;
		this.name = name;
		addProject(project);
	}

	public void addProject(Project p) {
		if (p != null) {
			if (projects.isEmpty() || projects == null) {
				projects = new HashSet<Project>();
				projects.add(p);
			} else {
				projects.add(p);
			}
		}
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

	public Collection<Project> getProjects() {
		return projects;
	}

	@XmlTransient
	public void setProjects(Collection<Project> projects) {
		this.projects = projects;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String toStringShort() {
		return "Customer id=" + id + ", Name=" + name;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", projects=" + projects + "]";
	}

}
