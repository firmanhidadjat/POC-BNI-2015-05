package id.co.hanoman.h2hpajak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Collections;

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
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Test5 {
	public static void main(String[] args) throws Exception {
	    SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
	    SOAPPart soapPart = soapMessage.getSOAPPart();
	    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

	    SOAPHeader soapHeader = soapEnvelope.getHeader();
	    SOAPHeaderElement headerElement = soapHeader.addHeaderElement(soapEnvelope.createName(
	        "Signature", "SOAP-SEC", "http://schemas.xmlsoap.org/soap/security/2000-12"));

	    SOAPBody soapBody = soapEnvelope.getBody();
	    soapBody.addAttribute(soapEnvelope.createName("id", "SOAP-SEC",
	        "http://schemas.xmlsoap.org/soap/security/2000-12"), "Body");
	    Name bodyName = soapEnvelope.createName("FooBar", "z", "http://example.com");
	    SOAPBodyElement gltp = soapBody.addBodyElement(bodyName);

	    Source source = soapPart.getContent();
	    Node root = null;
	    if (source instanceof DOMSource) {
	      root = ((DOMSource) source).getNode();
	    } else if (source instanceof SAXSource) {
	      InputSource inSource = ((SAXSource) source).getInputSource();
	      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	      dbf.setNamespaceAware(true);
	      DocumentBuilder db = null;

	      db = dbf.newDocumentBuilder();

	      Document doc = db.parse(inSource);
	      root = (Node) doc.getDocumentElement();
	    }

	    dumpDocument(root);

	    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
	    kpg.initialize(1024, new SecureRandom());
	    KeyPair keypair = kpg.generateKeyPair();

	    XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
	    Reference ref = sigFactory.newReference("#Body", sigFactory.newDigestMethod(DigestMethod.SHA1,
	        null));
	    SignedInfo signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(
	        CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), sigFactory
	        .newSignatureMethod(SignatureMethod.DSA_SHA1, null), Collections.singletonList(ref));
	    KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
	    KeyValue kv = kif.newKeyValue(keypair.getPublic());
	    KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(kv));

	    XMLSignature sig = sigFactory.newXMLSignature(signedInfo, keyInfo);

	    System.out.println("Signing the message...");
	    PrivateKey privateKey = keypair.getPrivate();
	    Element envelope = getFirstChildElement(root);
	    Element header = getFirstChildElement(envelope);
	    DOMSignContext sigContext = new DOMSignContext(privateKey, header);
	    sigContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");
	    sigContext.setIdAttributeNS(getNextSiblingElement(header),
	        "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
	    sig.sign(sigContext);

	    dumpDocument(root);
	    
	    saveDocument(root, new File("test-xx.xml"));
	    
	    System.out.println("Validate the signature...");
	    
	    sigFactory = XMLSignatureFactory.getInstance();
	    ref = sigFactory.newReference("#Body", sigFactory.newDigestMethod(DigestMethod.SHA1,
	        null));
	    signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(
	        CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), sigFactory
	        .newSignatureMethod(SignatureMethod.DSA_SHA1, null), Collections.singletonList(ref));
	    kif = sigFactory.getKeyInfoFactory();
	    kv = kif.newKeyValue(keypair.getPublic());
	    keyInfo = kif.newKeyInfo(Collections.singletonList(kv));

	    sig = sigFactory.newXMLSignature(signedInfo, keyInfo);
	    sigContext = new DOMSignContext(privateKey, header);
	    sigContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");
	    sigContext.setIdAttributeNS(getNextSiblingElement(header), "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
	    sig.sign(sigContext);
	    
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    root = dbf.newDocumentBuilder().parse(new File("test-xx.xml"));
	    envelope = getFirstChildElement(root);
	    header = getFirstChildElement(envelope);
	    
	    System.out.println("\n\nHEADERRRRR");
	    dumpDocument(header);

	    System.out.println("\n\nsigElement");
	    Element sigElement = getFirstChildElement(header);
	    dumpDocument(sigElement);
	    
	    System.out.println("\n\nBODY");
	    Element body = getNextSiblingElement(header);
	    dumpDocument(body);

	    DOMValidateContext valContext = new DOMValidateContext(keypair.getPublic(), sigElement);
	    valContext.setIdAttributeNS(getNextSiblingElement(header), "http://schemas.xmlsoap.org/soap/security/2000-12", "id");
	    boolean valid = sig.validate(valContext);

	    System.out.println("Signature valid? " + valid);
	  }

	  private static void saveDocument(Node root, File file) throws TransformerException, IOException {
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    FileOutputStream fos = new FileOutputStream(file);
	    transformer.transform(new DOMSource(root), new StreamResult(fos));
	    fos.close();
	  }

	  private static void dumpDocument(Node root) throws TransformerException {
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.transform(new DOMSource(root), new StreamResult(System.out));
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
