package id.co.hanoman.bni.ws1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import id.co.hanoman.U;

public class Processor {
	private static final Logger LOG = LoggerFactory.getLogger(Processor.class);
	private static String namaServer;

	public Processor() {
		this.bacaFile();
	}

	public void dodol(Exchange d) {
		// List<List<String>> dd = (List<List<String>>) d.getIn().getBody();
		LOG.info("GGGGGGGGG ========================  "
				+ U.dump(d.getIn().getBody()));
		// LOG.info("GGGG " + dd.get(0).get(0) + " " + dd.get(0).get(1) + " "
		// + dd.get(0).get(2) + " " + dd.get(0).get(3) + " "
		// + dd.get(0).get(4) + " " + dd.get(0).get(5) + " "
		// + dd.get(0).get(6));
		// d.getIn().setBody(d.getIn().getBody(List.class).get(0));
		// d.getIn().setBody("HHHHHHHHHHHHHHHHHHHHHHHHHHHH");
		d.getIn().setBody(this.namaServer);
	}

	public void bacaFile() {
		String namaFile = "/home/devadm/namaServer";
		BufferedReader br = null;
		String stringHasil = "";

		try {
			File f = new File(namaFile);
			if (f.isFile()) {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(f));
				while ((sCurrentLine = br.readLine()) != null) {
					stringHasil = stringHasil + sCurrentLine;
				}
			} else {
				stringHasil = "LOCAL";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		this.namaServer = stringHasil;
	}

}
