package id.co.hanoman.h2hpajak;

import id.co.hanoman.U;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.component.xmlsecurity.api.DefaultKeyAccessor;
import org.apache.camel.spi.Registry;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.commons.codec.binary.Base64;
import org.apache.xmlbeans.XmlCalendar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WebServiceHelper extends BaseHelper {
	
	@Override
	public void log(Exchange exchange) throws Exception {
		InputStream in = exchange.getIn().getBody(InputStream.class);
		byte bb[] = U.read(in);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(bb));
		for (Map.Entry<String, Object> me : exchange.getIn().getHeaders().entrySet()) {
			log.info("HEADER ["+me.getKey()+"]  =  ["+me.getValue()+"]");
		}
		String auth = (String) exchange.getIn().getHeader("Authorization");
		if (auth != null && auth.startsWith("Basic ")) {
			auth = new String(Base64.decodeBase64(auth.substring(6)));
			log.info("AUTH ["+auth+"]");
		}
		log.info("BODY  "+U.dump(doc));
		exchange.getIn().setBody(new ByteArrayInputStream(bb));
	}
	
	public void sign(Registry registry, Exchange exchange) throws Exception {
		Document doc = exchange.getIn().getBody(Document.class);
		// ALTER DOC ADD ID
		{
		    Element envelope = getFirstChildElement(doc);
		    Element header = getFirstChildElement(envelope);
		    Element body = getNextSiblingElement(header);
		    body.setAttributeNS("http://schemas.xmlsoap.org/soap/security/2000-12", "soap-sec:id", "Body");
		    ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.transform(new DOMSource(doc), new StreamResult(bout));
			bout.close();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			doc = dbf.newDocumentBuilder().parse(bin);
			bin.close();
		}
		
		KeyStoreParameters signatureParams = (KeyStoreParameters) registry.lookupByName("signatureParams");
		DefaultKeyAccessor signatureAccessor = (DefaultKeyAccessor) registry.lookupByName("signatureAccessor");
		KeyStore keyStore = signatureParams.createKeyStore();
		
		PrivateKeyEntry privateEntry = (PrivateKeyEntry) keyStore.getEntry("client", new KeyStore.PasswordProtection(signatureParams.getPassword().toCharArray()));

		XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
		Reference ref = sigFactory.newReference("#Body", sigFactory.newDigestMethod(DigestMethod.SHA1, null));
		SignedInfo signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(
				CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), 
				sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref)
		);
		KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
	    KeyInfo keyInfo = signatureAccessor.getKeyInfo(exchange.getIn(), null, kif);
	    XMLSignature sig = sigFactory.newXMLSignature(signedInfo, keyInfo);
	    
	    Element envelope = getFirstChildElement(doc);
	    Element header = getFirstChildElement(envelope);
	    Element body = getNextSiblingElement(header);
	    DOMSignContext sigContext = new DOMSignContext(privateEntry.getPrivateKey(), header);
	    sigContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");
	    sigContext.setIdAttributeNS(body, "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
	    sig.sign(sigContext);
	    
		exchange.getIn().setBody(doc);
	}
	
	public void verify(Registry registry, Exchange exchange) throws Exception {
		Document doc = exchange.getIn().getBody(Document.class);
		
	    Element envelope = getFirstChildElement(doc);
	    Element header = getFirstChildElement(envelope);
	    Element body = getNextSiblingElement(header);
		Element sigElement = (Element) header.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0);
	    NodeList certs = sigElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
	    if (certs.getLength() == 0) {
		    certs = sigElement.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "KeyIdentifier");
//	    	ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3"
	    }
		log.info("CERT ELEMENT "+U.dump(certs));
		Element certElement = (Element) certs.item(0);
		X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(certElement.getTextContent())));
		log.info("CERT "+cert);
		log.info("SIG ELEMENT "+U.dump(sigElement));
		log.info("BODY "+U.dump(body));
		
		DOMValidateContext valContext = new DOMValidateContext(cert.getPublicKey(), sigElement);
		if (body.getAttributeNS("http://schemas.xmlsoap.org/soap/security/2000-12", "id").length() > 0) {
			valContext.setIdAttributeNS(body, "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
		} else {
			log.info("USE WSS UTILITY");
			valContext.setIdAttributeNS(body, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
		}
	    
	    XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
	    XMLSignature sig = sigFactory.unmarshalXMLSignature(valContext);
		
	    // remove UsernameToken
		NodeList nlist = header.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken");
		for (int i=0, il=nlist.getLength(); i<il; i++) {
			Element n = (Element) nlist.item(i);
			n.getParentNode().removeChild(n);
		}
	    
	    boolean isValid = sig.validate(valContext);
	    log.info("IS VALID "+isValid);
	    if (isValid) {
			NodeList securities = header.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
	    	if (securities.getLength() > 0) {
	    		String user = cert.getSubjectX500Principal().getName();
	    		int ix = user.indexOf("CN=");
	    		if (ix >= 0) {
	    			int iy = user.indexOf(',', ix+3);
	    			if (iy > 0) {
	    				user = user.substring(ix+3, iy);
	    			} else {
	    				user = user.substring(ix+3);
	    			}
	    		}
	    		Element securityElement = (Element) securities.item(0);
	    		Element usernameToken = securityElement.getOwnerDocument().createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:UsernameToken");
	    		securityElement.appendChild(usernameToken);
	    		usernameToken.setAttributeNS("http://schemas.xmlsoap.org/ws/2003/06/utility", "wsu:Id", user);
	    		Element te = securityElement.getOwnerDocument().createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Username");
	    		te.setTextContent(user);
	    		usernameToken.appendChild(te);
	    		te = securityElement.getOwnerDocument().createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Password");
	    		te.setAttribute("Type", "wsse:PasswordText");
	    		te.setTextContent("*****");
	    		usernameToken.appendChild(te);
	    		te = securityElement.getOwnerDocument().createElementNS("http://schemas.xmlsoap.org/ws/2003/06/utility", "wsu:Created");
	    		te.setTextContent(new XmlCalendar(new Date()).toString());
	    		usernameToken.appendChild(te);
	    		
	    		exchange.getIn().setHeader("Authorization", "Basic "+Base64.encodeBase64String((user+"|*****").getBytes()));
	    	}
	    } else {
	    	throw new Exception("Not authorized");
	    }
	    
		exchange.getIn().setBody(doc);
	}
	
	private static Element getFirstChildElement(Node node) {
		Node child = node.getFirstChild();
		while ((child != null) && (child.getNodeType() != Node.ELEMENT_NODE)) {
			child = child.getNextSibling();
		}
		return (Element) child;
	}

	public static Element getNextSiblingElement(Node node) {
		Node sibling = node.getNextSibling();
		while ((sibling != null) && (sibling.getNodeType() != Node.ELEMENT_NODE)) {
			sibling = sibling.getNextSibling();
		}
		return (Element) sibling;
	}
}
