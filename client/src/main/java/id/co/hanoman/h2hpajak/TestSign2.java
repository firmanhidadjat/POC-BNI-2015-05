package id.co.hanoman.h2hpajak;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class TestSign2 {
	static Logger log = Logger.getLogger(TestSign2.class);
	
	public static void main(String[] args) {
		try {
			XMLSignatureFactory signFac = XMLSignatureFactory.getInstance("DOM");

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
			KeyValue kv = kif.newKeyValue(getClientCertificate().getPublicKey());

		       KeyInfo ki = 
		           kif.newKeyInfo(Collections.singletonList(kv));

		       DocumentBuilderFactory dbf =
		           DocumentBuilderFactory.newInstance();
		       dbf.setNamespaceAware(true);
		       Document doc = 
		           dbf.newDocumentBuilder().
		           parse(new FileInputStream("messages/sample/msg-0311133311.xml"));

		       DOMSignContext dsc = new DOMSignContext(getClientKey(), doc.getDocumentElement());

		       XMLSignature signature = signFac.newXMLSignature(si, ki);
		       
		       signature.sign(dsc);

		       TransformerFactory tf = TransformerFactory.newInstance();
		       Transformer trans = tf.newTransformer();
		       trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("messages/sample/msg-0311133311-signed.xml")));
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

}
