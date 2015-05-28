package id.co.hanoman.h2hpajak;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class XMLGenerator {
	static Random rnd = new Random();
	static char[] chz = "qwertyuiop asdfghjkl zxcvbnm 1234567890 ".toCharArray();
	
	public static void main(String[] args) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("MMddHHmmss");
			File file = new File("messages/sample/msg-"+df.format(new Date())+".xml");
			file.getParentFile().mkdirs();
			System.out.println("WRITE TO "+file.getCanonicalPath());
			FileOutputStream out = new FileOutputStream(file);
			long len = 0;
			long id = 1;
			out.write("<data>\n".getBytes());
			while (len < 100*1024*1024L) {
//			while (len < 64*1024L) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(bout);
				pw.println("\t<record>");
				pw.println("\t\t<id>"+(id++)+"</id>");
				pw.println("\t\t<code>"+rnd.nextLong()+"</code>");
				pw.println("\t\t<amount>"+rnd.nextLong()+"</amount>");
				char txt[] = new char[1024];
				for (int i=0, il=txt.length, cl=chz.length; i<il; i++) {
					txt[i] = chz[rnd.nextInt(cl)];
				}
				pw.println("\t\t<description>"+String.valueOf(txt)+"</description>");
				pw.println("\t</record>");
				pw.close();
				
				out.write(bout.toByteArray());
				len += bout.toByteArray().length;
				if (id % 100 == 0) {
					System.out.println("RECORD "+id+" LENGTH "+len);
				}
			}
			System.out.println("DONE RECORD "+id+" LENGTH "+len);
			out.write("</data>".getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
