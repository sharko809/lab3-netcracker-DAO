package com.sharko.main;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
 *
 *         <p>
 *         Project transfer object. Used to communicate with client.
 *
 */
@XmlRootElement(name = "PROJECT")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "name", "lead_id", "customer_id", "employees" })
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7485721074016882627L;
	@XmlElement
	private int id;
	@XmlElement
	private String name = "";
	@XmlElement
	private int lead_id = 0;
	@XmlElement
	private int customer_id = 0;
	@XmlElement
	@XmlElementWrapper
	private Set<Employee> employees = null;

	public Project() {
	}

	public Project(int id, String name, int lead_id, int customer_id) {
		this.id = id;
		this.name = name;
		this.lead_id = lead_id;
		this.customer_id = customer_id;
	}

	public Project(int id, String name, int lead_id, int customer_id, Employee employee) {
		this.id = id;
		this.name = name;
		this.lead_id = lead_id;
		this.customer_id = customer_id;
		this.employees.add(employee);
	}

	public Project(int id, String name, int lead_id, int customer_id, Collection<Employee> employees) {
		this.id = id;
		this.name = name;
		this.lead_id = lead_id;
		this.customer_id = customer_id;
		addEmployee(employees);
	}

	private void addEmployee(Collection<Employee> employees) {
		if (this.employees != null) {
			this.employees.addAll(employees);
		} else if (this.employees == null) {
			this.employees = new HashSet<Employee>();
			this.employees.addAll(employees);
		}
	}

	public Set<Employee> getEmployees() {
		return employees;
	}

	@XmlTransient
	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	public void setEmployee(Employee emp) {
		if (employees == null || employees.isEmpty()) {
			employees = new HashSet<Employee>();
		}
		employees.add(emp);
	}

	public void removeEmployee(Employee emp) {
		Iterator<Employee> iterator = employees.iterator();
		while (iterator.hasNext()) {
			Employee e = iterator.next();
			if (emp != null && (emp.getId() == e.getId())) {
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

	public int getLead_id() {
		return lead_id;
	}

	@XmlTransient
	public void setLead_id(int lead_id) {
		this.lead_id = lead_id;
	}

	public int getCustomer_id() {
		return customer_id;
	}

	@XmlTransient
	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String toStringShort() {
		return "Project [id=" + id + ", name=" + name + ", lead_id=" + lead_id + ", customer_id=" + customer_id + "]";
	}

	@Override
	public String toString() {
		return "Project [id: " + id + ", Name: " + name + ", Project lead: " + lead_id + ", Customer: " + customer_id
				+ ", Employees: " + parseEmployees(this.employees) + "]";
	}

	private String parseEmployees(Collection<Employee> employees) {
		if (employees == null || employees.isEmpty()) {
			return "No employees yet :(";
		}
		// used StringBuffer instead of StringBuilder due to speed
		StringBuffer temp = new StringBuffer();
		employees.forEach(e -> {
			temp.append(e.getName() + ", ");
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
