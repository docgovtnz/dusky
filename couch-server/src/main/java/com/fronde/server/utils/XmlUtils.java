package com.fronde.server.utils;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 */
public class XmlUtils implements Serializable {

  public static <T> T cloneViaJAXB(T object) {
    StringWriter writer = new StringWriter();
    writeJAXB(writer, object);

    StringReader reader = new StringReader(writer.toString());
    T result = (T) readJAXB(object.getClass(), reader, object.getClass());
    return result;
  }

  public static String convertToString(Object object) {
    StringWriter writer = new StringWriter();
    writeJAXB(writer, object);
    String xml = writer.toString();
    return xml;
  }

  public static <T> T convertFromString(Class<T> objectClass, String xml) {
    T t = readJAXB(objectClass, new StringReader(xml), objectClass);
    return t;
  }


  public static void writeJAXB(Writer writer, Object object) {
    try {
      JAXBContext jc = JAXBContext.newInstance(object.getClass());

      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(object, writer);
    } catch (PropertyException e) {
      throw new RuntimeException(e);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }

  }

  public static <T> T readJAXB(Class<T> declaredType, Reader reader, Class... classesToBeBound) {
    try {
      JAXBContext jc = JAXBContext.newInstance(classesToBeBound);

      Unmarshaller u = jc.createUnmarshaller();

      JAXBElement root = u.unmarshal(new StreamSource(reader), declaredType);
      return (T) root.getValue();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}

