package part_xml.xmlEntities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sharko.main.Project;

@XmlRootElement(name = "Projects")
public class Projects implements Iterable<Project> {

	@XmlElement
	private List<Project> projects;

	@XmlTransient
	public void setProjects(List<Project> proj) {
		this.projects = proj;
	}

	public List<Project> getProjects() {
		return this.projects;
	}

	public void addProject(Project proj) {
		if (this.projects == null) {
			this.projects = new ArrayList<Project>();
		}
		this.projects.add(proj);
	}

	public void removeProject(Project proj) {
		Iterator<Project> iterator = projects.iterator();
		while (iterator.hasNext()) {
			Project p = iterator.next();
			if (proj != null && (p.getId() == proj.getId())) {
				iterator.remove();
			}
		}
	}

	@Override
	public Iterator<Project> iterator() {
		return new ProjIterator();
	}

	private final class ProjIterator implements Iterator<Project> {

		private int pos;

		public ProjIterator() {
			if (!Projects.this.projects.isEmpty()) {
				pos = 0;
			}
		}

		@Override
		public boolean hasNext() {
			return !(projects.size() == pos);
		}

		@Override
		public Project next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return projects.get(pos++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
