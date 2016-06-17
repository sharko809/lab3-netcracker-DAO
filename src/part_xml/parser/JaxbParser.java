package part_xml.parser;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbParser implements Parser {

	@Override
	public Object getObject(File file, Class<?> c) throws JAXBException {
		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(c.newInstance().getClass());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Object object = unmarshaller.unmarshal(file);

		return object;
	}

	@Override
	public void saveObject(File file, Object o) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(o.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(o, file);
	}
}