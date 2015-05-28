package id.co.hanoman.h2hpajak;

import id.co.hanoman.U;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPElement;

import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class BaseHelper {
	Logger log = Logger.getLogger(getClass());
	File tempDir = new File("messages/tmp");

	protected KeyStore clientStore;
	protected PrivateKey clientPrivateKey;
	protected CertificateFactory certFac;
	protected X509Certificate clientCert, rootCert;
	
	public PrivateKey getClientPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableEntryException {
		if (clientPrivateKey == null) {
			log.debug("load client.jks");
			if (clientStore == null) {
				clientStore = KeyStore.getInstance("JKS");
				clientStore.load(new FileInputStream("file:///Users/seno/workspace/h2hpajak/keys/client.jks"), "dodolduren123".toCharArray());
			}
			
			clientPrivateKey = ((PrivateKeyEntry) clientStore.getEntry("client", new KeyStore.PasswordProtection("dodolduren123".toCharArray()))).getPrivateKey();
		}
		return clientPrivateKey;
	}
	
	public X509Certificate getClientCertificate() throws CertificateException, FileNotFoundException {
		if (clientCert == null) {
			log.debug("load client.cer");
			if (certFac == null) {
				certFac = CertificateFactory.getInstance("X.509");
			}
			clientCert = (X509Certificate) certFac.generateCertificate(new FileInputStream("file:///Users/seno/workspace/h2hpajak/keys/client.cer"));
		}
		return clientCert;
	}

	public PrivateKey getRootPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableEntryException {
		if (clientPrivateKey == null) {
			log.debug("load root.jks");
			if (clientStore == null) {
				clientStore = KeyStore.getInstance("JKS");
				clientStore.load(new FileInputStream("file:///Users/seno/workspace/h2hpajak/keys/root.jks"), "dodolduren123".toCharArray());
			}
			
			clientPrivateKey = ((PrivateKeyEntry) clientStore.getEntry("rootca", new KeyStore.PasswordProtection("dodolduren123".toCharArray()))).getPrivateKey();
		}
		return clientPrivateKey;
	}
	
	public X509Certificate getRootCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		if (rootCert == null) {
			log.debug("load client.jks");
			if (clientStore == null) {
				clientStore = KeyStore.getInstance("JKS");
				clientStore.load(new FileInputStream("file:///Users/seno/workspace/h2hpajak/keys/root.jks"), "dodolduren123".toCharArray());
			}
			rootCert = (X509Certificate) clientStore.getCertificate("rootca");
		}
		return rootCert;
	}
	
	public Document loadDocument(File file) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(file);
	}

	protected static class KeyValueKeySelector extends KeySelector {
        KeySelector.Purpose purpose;
        XMLCryptoContext context;
        
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
            throws KeySelectorException {
        	this.context = context;
        	this.purpose = purpose;
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            List<?> list = keyInfo.getContent();

            for (Iterator<?> itr = list.iterator(); itr.hasNext(); ) {
                XMLStructure xmlStructure = (XMLStructure) itr.next();
                KeySelectorResult result = process(sm, xmlStructure);
                if (result != null) return result;
            }
            throw new KeySelectorException("No KeyValue element found!");
        }
        
        protected KeySelectorResult process(SignatureMethod sm, XMLStructure xmlStructure) throws KeySelectorException {
        	if (xmlStructure instanceof KeyValue) {
        		PublicKey pk = null;
                try {
                    pk = ((KeyValue)xmlStructure).getPublicKey();
                } catch (KeyException ke) {
                    throw new KeySelectorException(ke);
                }
                // make sure algorithm is compatible with method
                if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                    return new SimpleKeySelectorResult(pk);
                }
        	} else if (xmlStructure instanceof X509Data) {
            	List<?> list = ((X509Data) xmlStructure).getContent();
                for (Iterator<?> itr = list.iterator(); itr.hasNext(); ) {
                	Object obj = itr.next();
                	if (obj instanceof XMLStructure) {
	                    KeySelectorResult result = process(sm, (XMLStructure) obj);
	                    if (result != null) return result;
                    } else if (obj instanceof X509Certificate) {
                        PublicKey pk = ((X509Certificate) obj).getPublicKey();
                        // make sure algorithm is compatible with method
                        if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                            return new X509CertificateKeySelectorResult((X509Certificate) obj);
                        } else {
                        	Logger.getLogger(getClass()).info("Unsupported aldorithm "+sm.getAlgorithm()+"  "+pk.getAlgorithm());
                        }
                	} else {
                    	Logger.getLogger(getClass()).info("Unsupported object "+obj.getClass().getName());
                	}
                }
        	} else if (xmlStructure instanceof DOMStructure) {
        		Element el = (Element) ((DOMStructure) xmlStructure).getNode();
    			NodeList nl = el.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "KeyIdentifier");
    			if (nl.getLength() > 0) {
    				try {
	    				byte b[] = Base64.decodeBase64(nl.item(0).getTextContent());
	    				CertificateFactory certFac = CertificateFactory.getInstance("X.509");
	    				X509Certificate cert = (X509Certificate) certFac.generateCertificate(new ByteArrayInputStream(b));
	    				Logger.getLogger(getClass()).info("CERT "+U.dump(cert));
	    				return new X509CertificateKeySelectorResult(cert);
    				} catch (Exception e) {
                    	Logger.getLogger(getClass()).info("Unsupported object "+e.getMessage()+" "+U.dump(el), e);
    				}
    			}
        		Logger.getLogger(getClass()).info("Unsupported object "+U.dump(nl.item(0)));
            } else {
            	Logger.getLogger(getClass()).info("Unknown xml structure "+U.dump(xmlStructure));
            }
            return null;
        }

        //@@@FIXME: this should also work for key types other than DSA/RSA
        static boolean algEquals(String algURI, String algName) {
            if (algName.equalsIgnoreCase("DSA") &&
                algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
                return true;
            } else if (algName.equalsIgnoreCase("RSA") &&
                       algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
                return true;
            } else {
                return false;
            }
        }
    }
	
	public void log(Exchange exchange) {
		log.info("HEADERS "+U.dump(exchange.getIn().getHeaders())+"BODY\n"+U.dump(exchange.getIn().getBody()));
	}

	protected static class X509CertificateKeySelectorResult implements KeySelectorResult {
        private final X509Certificate certificate;
        
        X509CertificateKeySelectorResult(X509Certificate certificate) {
            this.certificate = certificate;
            Logger.getLogger(getClass()).info("CERT "+U.dump(certificate));
        }

//        public X509Certificate getCertificate() {
//			return certificate;
//		}
//
		@Override
		public Key getKey() {
			return certificate.getPublicKey();
		}
    }
	
	protected static class SimpleKeySelectorResult implements KeySelectorResult {
        private final Key key;
        
        SimpleKeySelectorResult(Key key) {
            this.key = key;
            Logger.getLogger(getClass()).info("KEY "+U.dump(key));
        }

		@Override
		public Key getKey() {
			return key;
		}
    }
	
	public Document getBodyAsDocument(Exchange exchange) throws ParserConfigurationException, SAXException, IOException {
		InputStream in = exchange.getIn().getBody(InputStream.class);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(in);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
	}
	
	public static class CleanUp implements Synchronization {
		final File file;
		
		public CleanUp(File file) {
			this.file = file;
		}

		@Override
		public void onComplete(Exchange arg0) {
			file.delete();
		}

		@Override
		public void onFailure(Exchange arg0) {
			file.delete();
		}
		
		
	}

}
