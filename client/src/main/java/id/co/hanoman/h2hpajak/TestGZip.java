package id.co.hanoman.h2hpajak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

public class TestGZip {

	public static void main(String[] args) {
		try {
			FileOutputStream fout = new FileOutputStream("messages/tmp/crypt.dat");
			
			InputStream in = new FileInputStream("messages/sample/msg-0311132102.xml");
			OutputStream out = new GZIPOutputStream(fout);
			byte buf[] = new byte[1024];
			int len;
			
			while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
			in.close();
			out.close();
			fout.close();
			
			System.out.println("RESULT LEN "+new File("messages/tmp/crypt.dat").length());
			
			InputStream fin = new FileInputStream("messages/tmp/crypt.dat");
			
			in = new GZIPInputStream(fin);
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
