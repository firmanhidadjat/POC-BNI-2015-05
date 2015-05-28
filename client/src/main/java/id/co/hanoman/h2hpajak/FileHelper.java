package id.co.hanoman.h2hpajak;

import id.co.hanoman.U;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.XMLStructure;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class FileHelper extends BaseHelper {

	public void sign(Exchange exchange) throws Exception {
		File file = exchange.getIn().getBody(File.class);
		log.info("Sign file "+file);
		
		PrivateKey clientPrivateKey = getClientPrivateKey();
		X509Certificate cert = getClientCertificate();
		
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
		List<Object> x509Content = new ArrayList<Object>();
		x509Content.add(cert.getSubjectX500Principal().getName());
		x509Content.add(cert);
		X509Data xd = kif.newX509Data(x509Content);
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

		log.debug("load document");

		// Create a DOMSignContext and specify the RSA PrivateKey and
		// location of the resulting XMLSignature's parent element.
		Document doc = loadDocument(file);
		
		// Where to insert signature
		DOMSignContext dsc = new DOMSignContext(clientPrivateKey, doc.getDocumentElement());

		// Create the XMLSignature, but don't sign it yet.
		XMLSignature signature = signFac.newXMLSignature(si, ki);

		log.debug("sign document");
		// Marshal, generate, and sign the enveloped signature.
		signature.sign(dsc);
		
		final File tmpFile = File.createTempFile("sign-", ".tmp", tempDir);
		tmpFile.deleteOnExit();
		exchange.addOnCompletion(new CleanUp(tmpFile));
		
		FileOutputStream fo = new FileOutputStream(tmpFile);
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(fo));
			fo.close();
		} finally {
			try {
				fo.close();
			} catch (Exception e) {}
		}
		
		exchange.getIn().setBody(tmpFile);
		log.info("Done sign file "+file);
	}

	public void verify(Exchange exchange) throws Exception {
		File file = exchange.getIn().getBody(File.class);
		log.info("Verify file "+file);
		
		Document doc = loadDocument(file);
		
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));
		
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature (generated above)
        boolean coreValidity = signature.validate(valContext);

        // Check core validation status
        if (coreValidity == false) {
        	log.warn("Signature failed core validation");
            boolean sv = signature.getSignatureValue().validate(valContext);
            log.warn("signature validation status: " + sv);
            // check the validation status of each Reference
            Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
            for (int j=0; i.hasNext(); j++) {
                boolean refValid = ((Reference) i.next()).validate(valContext);
                log.warn("ref["+j+"] validity status: " + refValid);
            }
            throw new Exception("Invalid certificate");
        } else {
        	log.info("Signature passed core validation");
        	KeyInfo keyInfo = signature.getKeyInfo();
        	Iterator<?> iter = keyInfo.getContent().iterator();
        	List<X509Certificate> certs = new LinkedList<X509Certificate>();
        	while (iter.hasNext()) {
        	    XMLStructure kiType = (XMLStructure) iter.next();
            	List<?> list = ((X509Data) kiType).getContent();
                for (Iterator<?> itr = list.iterator(); itr.hasNext(); ) {
                	Object obj = itr.next();
                    if (obj instanceof X509Certificate) {
                    	certs.add((X509Certificate) obj);
                	}
                }
        	}
        	
        	// instantiate a CertificateFactory for X.509
            CertificateFactory certFac = CertificateFactory.getInstance("X.509");
            CertPath cp = certFac.generateCertPath(certs);
        	
            PKIXParameters params = new PKIXParameters(Collections.singleton(new TrustAnchor(getRootCertificate(), null)));
            params.setRevocationEnabled(false);
        	
        	CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
        	cpv.validate(cp, params);
        	
        	for (X509Certificate c : certs) {
        		log.info("VALID CERT "+c.getSubjectX500Principal().getName());
        		String cn = c.getSubjectX500Principal().getName();
        		int ix = cn.indexOf("CN=");
        		int iy = cn.indexOf(",", ix+3);
        		if (iy > 0) {
        			cn = cn.substring(ix+3, iy);
        		} else {
        			cn = cn.substring(ix+3);
        		}
        		exchange.getIn().setHeader("X509SubjectName", cn);
        	}
        }
		
		log.info("Done verify file "+file);
	}

	public void encrypt(Exchange exchange) throws Exception {
		final File file = exchange.getIn().getBody(File.class);
		log.info("Encrypt file "+file);
		
		byte buf[] = new byte[1024];
		
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey skey = keyGen.generateKey();
		
		File zipFile = File.createTempFile("zip-", ".tmp", tempDir);
		zipFile.deleteOnExit();
		exchange.addOnCompletion(new CleanUp(zipFile));
		
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new GZIPOutputStream(new FileOutputStream(zipFile)) {
				{
					def.setLevel(Deflater.BEST_COMPRESSION);
				}
			};
			int len;
			while ((len = in.read(buf)) >= 0) {
				out.write(buf, 0, len);
			}
		} finally {
			try {
				out.close();
			} catch (Exception e) {}
			try {
				in.close();
			} catch (Exception e) {}
		}
		
		File tmpFile = File.createTempFile("encrypt-", ".tmp", tempDir);
		tmpFile.deleteOnExit();
		exchange.addOnCompletion(new CleanUp(tmpFile));
		
		in = null;
		out = null;
		try {
			in = new FileInputStream(zipFile);
			out = new FileOutputStream(tmpFile);
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getRootCertificate().getPublicKey());
			out.write(cipher.doFinal(skey.getEncoded()));
			out.flush();
			
			log.info("SKEY "+U.dump(skey.getEncoded()));
			
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			
			CipherOutputStream cout = new CipherOutputStream(out, cipher);
			int len;
			while ((len = in.read(buf)) >= 0) {
				if (len > 0) {
					cout.write(buf, 0, len);
				}
			}
			cout.close();
		} finally {
			try {
				out.close();
			} catch (Exception e) {}
			try {
				in.close();
			} catch (Exception e) {}
		}
		
		exchange.getIn().setBody(tmpFile);
		log.info("Done encrypt file "+file);
	}

	public void decrypt(Exchange exchange) throws Exception {
		File file = exchange.getIn().getBody(File.class);
		log.info("Decrypt file "+file);
		
		byte buf[] = new byte[1024];
		
		File zipFile = File.createTempFile("zip-", ".tmp", tempDir);
		zipFile.deleteOnExit();
		exchange.addOnCompletion(new CleanUp(zipFile));
		
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(file);
			out =  new FileOutputStream(zipFile);
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, getRootPrivateKey());

			byte[] bsec = new byte[cipher.getOutputSize(1)];
			in.read(bsec);
			
			byte bplain[] = cipher.doFinal(bsec);
			
			SecretKey skey = new SecretKeySpec(bplain, "AES");
			
			log.info("SKEY "+U.dump(skey.getEncoded()));
			
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			
			CipherInputStream cin = new CipherInputStream(in, cipher);
			int len;
			while ((len = cin.read(buf)) >= 0) {
				if (len > 0) {
					out.write(buf, 0, len);
				}
			}
			cin.close();
		} finally {
			try {
				out.close();
			} catch (Exception e) {}
			try {
				in.close();
			} catch (Exception e) {}
		}
		
		File tmpFile = File.createTempFile("decrypt-", ".tmp", tempDir);
		tmpFile.deleteOnExit();
		exchange.addOnCompletion(new CleanUp(tmpFile));
		
		in = null;
		out = null;
		try {
			in = new GZIPInputStream(new FileInputStream(zipFile));
			out =  new FileOutputStream(tmpFile);
			int len;
			while ((len = in.read(buf)) >= 0) {
				out.write(buf, 0, len);
			}
		} finally {
			try {
				out.close();
			} catch (Exception e) {}
			try {
				in.close();
			} catch (Exception e) {}
		}
		
		exchange.getIn().setBody(tmpFile);
		log.info("Done decrypt file "+file);
	}
	
	
	String dest;
	FileSystem hdfs;

	public void hdfsWrite(Exchange exchange, String dest) throws Exception {
		String fn = (String) exchange.getIn().getHeader("CamelFileName");

		if (hdfs == null || !dest.equals(this.dest)) {
			log.info("Connecting to "+dest);
			Configuration configuration = new Configuration();
			hdfs = FileSystem.get(new URI(dest), configuration);
			this.dest = dest;
			log.info("Connected to "+dest);
		}
		
		String pathStr = dest + "/" + fn;
		Path pt = new Path(pathStr+".tmp");
		
		log.info("Writing to "+pathStr);
		OutputStream out = hdfs.create(pt, true);
		try {
			out.write(exchange.getIn().getBody(byte[].class));
		} finally {
			out.close();
		}
		Path pto = new Path(pathStr);
		hdfs.delete(pto, true);
		hdfs.rename(pt, pto);
		log.info("Done write to "+pathStr);
	}
	
	public Iterator<Message> split(final Exchange exchange, int length) throws Exception {
		final String fn = (String) exchange.getIn().getHeader("CamelFileName");
		log.info("RAW-SPLITTER "+fn);
		final InputStream in = exchange.getIn().getBody(InputStream.class);
		final byte bb[] = new byte[length];
		return new Iterator<Message>() {
			int index = 0;
			int len = 0;
			byte res[] = null;

			@Override
			public boolean hasNext() {
				if (len == 0) {
					try {
						len = in.read(bb);
					} catch (IOException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
					if (len > 0) {
						res = new byte[len];
						System.arraycopy(bb, 0, res, 0, len);
						return true;
					} else {
						try {
							in.close();
						} catch (IOException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					}
				}
				return len >= 0;
			}

			@Override
			public Message next() {
				if (len == 0) hasNext();
				len = 0;
				DefaultMessage message = new DefaultMessage();
	            message.setBody(res);
	            if (hasNext()) {
		            message.setHeader("CamelFileName", fn + "."  + index + ".part");
		            message.setHeader("CamelSplitIndex", index++);
		            message.setHeader("CamelSplitComplete", false);
	            } else {
		            message.setHeader("CamelFileName", fn + "."  + index + ".end");
		            message.setHeader("CamelSplitIndex", index);
		            message.setHeader("CamelSplitComplete", true);
	            }
				return message;
			}

			@Override
			public void remove() {
			}
		};
	}
	
	public void merge(Exchange exchange) throws IOException {
		File file = exchange.getIn().getBody(File.class);
		String fn = (String) exchange.getIn().getHeader("CamelFileName");
		if (!fn.endsWith(".end")) throw new RuntimeException("Invalid file '"+file+"'");
		fn = fn.substring(0, fn.length() - 4);
		int ix = fn.lastIndexOf('.');
		int last = Integer.parseInt(fn.substring(ix+1));
		fn = fn.substring(0, ix);
		for (int i=0; i<last; i++) {
			File f = new File(file.getParentFile(), fn + "." + i + ".part");
			if (!f.exists()) {
				log.warn("Not complete (not found "+f+"), keep looking...");
				for (int j=0; j<10; j++) {
					if (f.exists()) {
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
				if (!f.exists()) throw new IOException("Not complete (not found "+f+")");
			}
		}
		RandomAccessFile rafo = null;
		RandomAccessFile raf[] = new RandomAccessFile[last+1];
		try {
			raf[last] = new RandomAccessFile(file, "rw");
			for (int i=0; i<last; i++) {
				File f = new File(file.getParentFile(), fn + "." + i + ".part");
				raf[i] = new RandomAccessFile(f, "rw");
				raf[i].getChannel().lock();
			}
			File fout = new File(file.getParentFile(), fn);
			fout.deleteOnExit();
			fout.createNewFile();
			rafo = new RandomAccessFile(fout, "rw");
			rafo.getChannel().lock();
			for (int i=0, il=raf.length; i<il; i++) {
				raf[i].getChannel().transferTo(0, raf[i].length(), rafo.getChannel());
			}
			exchange.getIn().setHeader("complete", true);
			exchange.getIn().setHeader("CamelFileName", fn);
			exchange.getIn().setBody(fout);
		} finally {
			if (rafo != null) {
				try {
					rafo.close();
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}
			for (int i=0, il=raf.length; i<il; i++) {
				if (raf[i] != null) {
					try {
						raf[i].close();
					} catch (Exception e) {
						log.warn(e.getMessage(), e);
					}
				}
			}
		}
		for (int i=0; i<last; i++) {
			File f = new File(file.getParentFile(), fn + "." + i + ".part");
			f.delete();
		}
	}

}
