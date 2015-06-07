package id.co.hanoman.bni.message;

import java.io.File;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CreatePojo {
	private static final Logger LOG = LoggerFactory.getLogger(CreatePojo.class);
	
	private static String getType(Element n) {
		String type = n.getAttribute("type");
		if ("string".equals(type)) {
			return "String";
		} else {
			return "["+type+"]";
		}
	}
	
	private static String fixName(String name) {
		StringBuilder sb = new StringBuilder();
		for (int i=0, il=name.length(), state=1; i<il; i++) {
			char ch = name.charAt(i);
			if (state == 0) {
				if (Character.isAlphabetic(ch)) {
					sb.append(Character.toUpperCase(ch));
					state = 1;
				} else {
					sb.append(ch);
				}
			} else if (state == 1) {
				if (Character.isJavaIdentifierPart(ch)) {
					sb.append(ch);
				} else if (Character.isWhitespace(ch)) {
					state = 2;
				} else {
					sb.append("_");
				}
			} else if (state == 2) {
				if (Character.isAlphabetic(ch)) {
					sb.append(Character.toUpperCase(ch));
					state = 1;
				} else if (Character.isWhitespace(ch)) {
					// skip
				} else {
					sb.append(ch);
					state = 1;
				}
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("codex/bancs-resp-ok.xml"));
			
			PrintWriter out = new PrintWriter(System.out);
			
			XPathExpression xpexpr = XPathFactory.newInstance().newXPath().compile("//field");
			NodeList nl = (NodeList) xpexpr.evaluate(doc, XPathConstants.NODESET);
			for (int i=0, il=nl.getLength(); i<il; i++) {
				Element n = (Element) nl.item(i);
				out.println(getType(n)+" "+fixName(n.getAttribute("id"))+";");
			}
			out.close();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
