package part_xml.xmlEntities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.sharko.main.Customer;

@XmlRootElement(name = "Customers")
@XmlSeeAlso(Customer.class)
public class Customers implements Iterable<Customer> {

	@XmlElement
	private List<Customer> customers;

	@XmlTransient
	public void setCustomers(List<Customer> cust) {
		this.customers = cust;
	}

	public List<Customer> getCustomers() {
		return this.customers;
	}

	public void addCustomer(Customer cust) {
		if (this.customers == null) {
			this.customers = new ArrayList<Customer>();
		}
		this.customers.add(cust);
	}
	
	public void removeCustomer(Customer customer) {
		Iterator<Customer> iterator = customers.iterator();
		while (iterator.hasNext()) {
			Customer c = iterator.next();
			if (customer != null && c != null && (customer.getId() == c.getId())) {
				iterator.remove();
			}
		}
	}

	@Override
	public Iterator<Customer> iterator() {
		return new CustIterator();
	}

	private final class CustIterator implements Iterator<Customer> {

		private int pos;

		public CustIterator() {
			if (!customers.isEmpty()) {
				pos = 0;
			}
		}

		@Override
		public boolean hasNext() {
			return !(customers.size() == pos);
		}

		@Override
		public Customer next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return customers.get(pos++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
