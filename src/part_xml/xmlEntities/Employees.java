package part_xml.xmlEntities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.sharko.main.Employee;

/**
 * 
 * @author Neltarion
 * 
 *         <p>
 *         This class is some type of wrapper class. It wraps <b>Employee</b>
 *         list. This class also implements Iterable interface so I can iterate
 *         through each <b>Employee</b>
 */
@XmlRootElement(name = "employees")
@XmlSeeAlso(Employee.class)
public class Employees implements Iterable<Employee> {

	@XmlElement
	private List<Employee> employees;

	@XmlTransient
	public void setEmployees(List<Employee> emp) {
		this.employees = emp;
	}
	
	public List<Employee> getEmployees() {
		return this.employees;
	}

	public void addEmployee(Employee emp) {
		if (this.employees == null) {
			this.employees = new ArrayList<Employee>();
		}
		this.employees.add(emp);
	}

	public void removeEmployee(Employee emp) {
		Iterator<Employee> iterator = employees.iterator();
		while (iterator.hasNext()) {
			Employee e = iterator.next();
			if (emp!=null && (e.getId()==emp.getId())) {
				iterator.remove();
			}
		}
	}

	@Override
	public Iterator<Employee> iterator() {
		return new EmpIterator();
	}

	private final class EmpIterator implements Iterator<Employee> {

		// private Employee cursor;
		private int pos;

		public EmpIterator() {
			if (!Employees.this.employees.isEmpty()) {
				// this.cursor = Employees.this.employees.get(pos);
				pos = 0;
			}
		}

		@Override
		public boolean hasNext() {
			return !(employees.size() == pos);
		}

		@Override
		public Employee next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			return employees.get(pos++);
		}

		@Override
		public void remove() {
			employees.remove(pos);
		}

	}

}
