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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestVerify3 {
	static Logger log = Logger.getLogger(TestVerify3.class);
	
	public static void main(String[] args) {
		try {
			DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
//			Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("messages/sample/msg-0311133311-signed.xml"));
			Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("messages/sample/test-sec.xml"));

			NodeList signatureNL = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (signatureNL.getLength() == 0) {
				throw new Exception("Cannot find Signature element");
			}
			
			NodeList bodyNL = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			if (bodyNL.getLength() == 0) {
				throw new Exception("Cannot find Body element");
			}
			
			XMLSignatureFactory signFac = XMLSignatureFactory.getInstance("DOM");

			DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), signatureNL.item(0));
			valContext.setIdAttributeNS((Element) bodyNL.item(0), "http://schemas.xmlsoap.org/soap/security/2000-12","Id");
			
//			DOMValidateContext valContext = new DOMValidateContext();
//	        valContext.setIdAttributeNS((Element) bodyNL.item(0), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
//	        valContext.setIdAttributeNS((Element) bodyNL.item(0), "http://schemas.xmlsoap.org/soap/security/2000-12", "Id");

//	        XMLSignature signature = signFac.unmarshalXMLSignature(valContext);

			
			Reference ref = signFac.newReference(
					"", 
					signFac.newDigestMethod(DigestMethod.SHA1, null),
					Collections.singletonList(
							signFac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
					),
					null, 
					null
			);
			SignedInfo si = signFac.newSignedInfo(signFac.newCanonicalizationMethod(
					CanonicalizationMethod.INCLUSIVE,
					(C14NMethodParameterSpec) null),
					signFac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
					Collections.singletonList(ref)
			);
			
			KeyInfoFactory kif = signFac.getKeyInfoFactory();
			List<Object> x509Content = new ArrayList<Object>();
			x509Content.add(getRootCertificate());
			X509Data xd = kif.newX509Data(x509Content);
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

			XMLSignature signature = signFac.newXMLSignature(si, ki);
			
			boolean coreValidity = signature.validate(valContext); 
	        
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
