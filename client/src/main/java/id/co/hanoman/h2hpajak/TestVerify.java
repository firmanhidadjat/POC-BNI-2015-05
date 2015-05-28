package id.co.hanoman.h2hpajak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TestVerify {
	static Logger log = Logger.getLogger(TestVerify.class);

	public static void main(String[] args) {
		try {
//			MessageFactory msgFac = MessageFactory.newInstance();
//			SOAPMessage msg = msgFac.createMessage(null, new FileInputStream("messages/sample/test.xml"));
//
//			KeyStore rootStore = KeyStore.getInstance("JKS");
//			rootStore.load(new FileInputStream("../keys/client.jks"), "dodolduren123".toCharArray());
//
//			Node signature = (Node) msg.getSOAPHeader().getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0);
//			
//			log.info("VALIDATE "+validateSignature(signature, msg.getSOAPBody(), rootStore));
			
			Document doc = loadDocument(new File("messages/sample/msg-signed.xml"));
			
			KeyStore rootStore = KeyStore.getInstance("JKS");
			rootStore.load(new FileInputStream("../keys/client.jks"), "dodolduren123".toCharArray());

			Node signature = (Node) doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0);
			
			log.info("VALIDATE "+validateSignature(signature, doc.getDocumentElement(), rootStore));
		
			log.info("Done verify file");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static boolean validateSignature(Node signatureNode, Node bodyTag, KeyStore trustStore) {
	    boolean signatureIsValid = false;
	    try {
	        // Create a DOM XMLSignatureFactory that will be used to unmarshal the
	        // document containing the XMLSignature
	        String providerName = System.getProperty
	                ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
	        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
	                (Provider) Class.forName(providerName).newInstance());

	        // Create a DOMValidateContext and specify a KeyValue KeySelector
	        // and document context
	        DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(trustStore), signatureNode);
//	        valContext.setIdAttributeNS((Element) bodyTag, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");

	        // Unmarshal the XMLSignature.
	        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
	        // Validate the XMLSignature.
	        signatureIsValid = signature.validate(valContext);

	    } catch (Exception ex) {
	        log.error("An Error Raised while Signature Validation");
	        log.error("Cause: " + ex.getCause());
	        log.error("Message: " + ex.getMessage());
	    }

	    return signatureIsValid;
	}
	
	public static PrivateKey getRootPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableEntryException {
		KeyStore clientStore = KeyStore.getInstance("JKS");
		clientStore.load(new FileInputStream("../keys/root.jks"), "dodolduren123".toCharArray());
		return ((PrivateKeyEntry) clientStore.getEntry("rootca", new KeyStore.PasswordProtection("dodolduren123".toCharArray()))).getPrivateKey();
	}
	
	public static X509Certificate getRootCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		KeyStore clientStore = KeyStore.getInstance("JKS");
		clientStore.load(new FileInputStream("../keys/client.jks"), "dodolduren123".toCharArray());
		return (X509Certificate) clientStore.getCertificate("rootca");
	}
	
	public static Document loadDocument(File file) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(file);
	}

}
