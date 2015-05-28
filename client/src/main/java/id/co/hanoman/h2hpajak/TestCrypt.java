package id.co.hanoman.h2hpajak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilderFactory;

public class TestCrypt {

	public static void main(String[] args) {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(1024);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey skey = keyGen.generateKey();
			
			FileOutputStream fout = new FileOutputStream("messages/tmp/crypt.dat");
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
			byte bx[] = cipher.doFinal(skey.getEncoded());
			System.out.println("KEY LEN "+bx.length);
			fout.write(bx);
			fout.flush();

			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			
			InputStream in = new FileInputStream("messages/sample/msg-0311132102.xml");
			OutputStream out = new CipherOutputStream(fout, cipher);
			byte buf[] = new byte[1024];
			int len;
			
			while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
			in.close();
			out.close();
			fout.close();
			
			System.out.println("RESULT LEN "+new File("messages/tmp/crypt.dat").length());
			
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			
			InputStream fin = new FileInputStream("messages/tmp/crypt.dat");
			byte bb[] = new byte[cipher.getOutputSize(1)];
			len = fin.read(bb);
			System.out.println("KEY LEN "+bb.length+"  "+len);

			SecretKey skey2 = new SecretKeySpec(cipher.doFinal(bb), "AES");
			
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey2);
			
			in = new CipherInputStream(fin, cipher);
			out = new FileOutputStream("messages/tmp/plain.xml");
			
			buf = new byte[256];
			
			while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
			in.close();
			fin.close();
			out.close();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.newDocumentBuilder().parse(new File("messages/tmp/plain.xml"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
