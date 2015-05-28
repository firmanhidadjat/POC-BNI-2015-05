package id.co.hanoman.h2hpajak;

import id.co.hanoman.h2hpajak.BaseHelper.KeyValueKeySelector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestVerify2 {
	static Logger log = Logger.getLogger(TestVerify2.class);
	
	public static void main(String[] args) {
		try {
			DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
//			Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("messages/sample/msg-0311133311-signed.xml"));
			Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("messages/sample/test-root.xml"));

			NodeList signatureNL = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (signatureNL.getLength() == 0) {
				throw new Exception("Cannot find Signature element");
			}
			
			NodeList bodyNL = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			if (bodyNL.getLength() == 0) {
				throw new Exception("Cannot find Body element");
			}
			
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

	        // Create a DOMValidateContext and specify a KeyValue KeySelector
	        // and document context
	        DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), signatureNL.item(0));
//	        valContext.setIdAttributeNS((Element) bodyNL.item(0), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			valContext.setIdAttributeNS((Element) bodyNL.item(0), "http://schemas.xmlsoap.org/soap/security/2000-12","id");

	        // Unmarshal the XMLSignature.
		    XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
		    Reference ref = sigFactory.newReference("#Body", sigFactory.newDigestMethod(DigestMethod.SHA1,
		        null));
		    SignedInfo signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(
		        CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), sigFactory
		        .newSignatureMethod(SignatureMethod.DSA_SHA1, null), Collections.singletonList(ref));
		    KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
		    KeyValue kv = kif.newKeyValue(getRootCertificate().getPublicKey());
		    KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(kv));

		    XMLSignature sig = sigFactory.newXMLSignature(signedInfo, keyInfo);
		    
	        // Validate the XMLSignature.
	        boolean coreValidity = sig.validate(valContext);
	        
	        log.info("CORE VALIDITY "+coreValidity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static PrivateKey getClientKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableEntryException {
		KeyStore clientStore = KeyStore.getInstance("JKS");
		clientStore.load(new FileInputStream("../keys/client.jks"), "dodolduren123".toCharArray());
		return ((PrivateKeyEntry) clientStore.getEntry("cl"
				+ "ient", new KeyStore.PasswordProtection("dodolduren123".toCharArray()))).getPrivateKey();
	}
	
	public static X509Certificate getClientCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		KeyStore clientStore = KeyStore.getInstance("JKS");
		clientStore.load(new FileInputStream("../keys/client-trust.jks"), "dodolduren123".toCharArray());
		return (X509Certificate) clientStore.getCertificate("client");
	}
	
	public static X509Certificate getRootCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		KeyStore clientStore = KeyStore.getInstance("JKS");
		clientStore.load(new FileInputStream("../keys/root.jks"), "dodolduren123".toCharArray());
		return (X509Certificate) clientStore.getCertificate("rootca");
	}

}
