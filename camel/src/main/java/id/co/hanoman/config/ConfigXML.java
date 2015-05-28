package id.co.hanoman.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigXML extends Config {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigXML.class);
	final Element base;
	XPathFactory xPathFactory;
	
	public static ConfigXML load(File f) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return new ConfigXML(f.getCanonicalPath(), "", builder.parse(f).getDocumentElement());
	}
	
	public ConfigXML(String url, String path, Element base) {
		super(url, path);
		this.base = base;
		xPathFactory = XPathFactory.newInstance();
	}

	@Override
	public Object getContext() {
		return null;
	}

	@Override
	public Config getConfig(String path) {
		try {
			Element ne = (Element) xPathFactory.newXPath().compile(path).evaluate(base, XPathConstants.NODE);
			return new ConfigXML(url, this.path+"/"+path, ne);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public List<Config> getList(String path) {
		try {
			NodeList nl = (NodeList) xPathFactory.newXPath().compile(path).evaluate(base, XPathConstants.NODESET);
			List<Config> lst = new LinkedList<Config>();
			for (int i=0, il=nl.getLength(); i<il; i++) {
				lst.add(new ConfigXML(url, this.path+"/"+path+"["+(i+1)+"]", (Element) nl.item(i)));
			}
			return Collections.unmodifiableList(lst);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public String getStringValue(String path, String nvl) {
		try {
			Node value = (Node) xPathFactory.newXPath().compile(path).evaluate(base, XPathConstants.NODE);
			if (value == null) {
				return nvl;
			}
			if (value instanceof Attr) {
				return ((Attr) value).getTextContent();
			} else if (value instanceof Element) {
				return ((Element) value).getTextContent();
			}
			LOG.info("Unknown node "+value.getClass().getName()+" ["+value+"]");
			return value != null ? value.toString() : nvl;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
