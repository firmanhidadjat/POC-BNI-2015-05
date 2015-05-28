package id.co.hanoman.h2hpajak;

import id.co.hanoman.U;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TestSoapSign {

	public static void main(String[] args) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			Document doc = dbf.newDocumentBuilder().parse(new File("request.xml"));
			{
			    Element envelope = getFirstChildElement(doc);
			    Element header = getFirstChildElement(envelope);
			    Element body = getNextSiblingElement(header);
			    body.setAttributeNS("http://schemas.xmlsoap.org/soap/security/2000-12", "soap-sec:id", "Body");
			    FileOutputStream fout = new FileOutputStream("request2.xml");
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
				transformer.transform(new DOMSource(doc), new StreamResult(fout));
				fout.close();
			}
			
			doc = dbf.newDocumentBuilder().parse(new File("request2.xml"));
		    System.out.println("DOC");
		    dumpDOMDocument(doc);
			{
				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(new FileInputStream("../keys/client.jks"), "dodolduren123".toCharArray());
				
				PrivateKeyEntry privateEntry = (PrivateKeyEntry) keyStore.getEntry("client", new KeyStore.PasswordProtection("dodolduren123".toCharArray()));
				X509Certificate cert = (X509Certificate) keyStore.getCertificate("client");
				
				XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
				Reference ref = sigFactory.newReference("#Body", sigFactory.newDigestMethod(DigestMethod.SHA1, null));
				SignedInfo signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(
						CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), 
						sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref)
				);
				KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
				List<Object> x509Contents = new ArrayList<Object>();
				x509Contents.add(cert.getSubjectX500Principal().getName());
				x509Contents.add(cert);
				KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(kif.newX509Data(x509Contents)));
			    XMLSignature sig = sigFactory.newXMLSignature(signedInfo, keyInfo);
			    
			    Element envelope = getFirstChildElement(doc);
			    Element header = getFirstChildElement(envelope);
			    Element body = getNextSiblingElement(header);
			    DOMSignContext sigContext = new DOMSignContext(privateEntry.getPrivateKey(), header);
			    sigContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");
			    sigContext.setIdAttributeNS(body, "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
			    sig.sign(sigContext);
			    
			    FileOutputStream fout = new FileOutputStream("request-signed.xml");
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
				transformer.transform(new DOMSource(doc), new StreamResult(fout));
				fout.close();
			}
			
			
		    System.out.println("PRE-DOC");
		    dumpDOMDocument(doc);
			doc = dbf.newDocumentBuilder().parse(new File("request-signed.xml"));
		    System.out.println("SIGNED-DOC");
		    dumpDOMDocument(doc);
			{
			    Element envelope = getFirstChildElement(doc);
			    Element header = getFirstChildElement(envelope);
				Element sigElement = getFirstChildElement(header);
			    Element body = getNextSiblingElement(header);
				Element certElement = (Element) sigElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0);
				X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(certElement.getTextContent())));
				System.out.println("CERT "+cert);
				System.out.println("SIG ELEMENT "+U.dump(sigElement));
				System.out.println("BODY "+U.dump(body));
				
				DOMValidateContext valContext = new DOMValidateContext(cert.getPublicKey(), sigElement);
			    valContext.setIdAttributeNS(body, "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
			    
			    XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
			    XMLSignature sig = sigFactory.unmarshalXMLSignature(valContext);
			    System.out.println("SignedInfo REFS "+U.dump(sig.getSignedInfo().getReferences()));
				Reference ref = (Reference) sig.getSignedInfo().getReferences().get(0);
				System.out.println("REF 0 "+U.dump(ref.getCalculatedDigestValue()));
			    System.out.println("IS VALID "+sig.validate(valContext));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void dumpDOMDocument(org.w3c.dom.Node root) throws TransformerException, TransformerConfigurationException {
		System.out.println("\n");
		// Create a new transformer object
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		// Dump the DOM representation to standard output
		transformer.transform(new DOMSource(root), new StreamResult(System.out));
		System.out.println("\n");
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
