package id.co.hanoman;

import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.GroupCodex;
import id.co.hanoman.config.Config;
import id.co.hanoman.config.ConfigXML;

import java.io.File;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class TestBancs {
	private final static Logger LOG = LoggerFactory.getLogger(TestBancs.class);

	public static void main(String[] args) {
		try {
			String msg = " 1063    0979            0000    000165003001525141355032000391021000040320600 000000  0300000000000001231   IDRBUKA              0259TABUNGAN PLUS (TAPLUS)        20000001DDCV               3,0000                                                            Sdri NAMA1  NAMA2                                                                   31/05/201031/05/201031/05/201031/05/201031/05/2010          288.814.813.108,00               0,00 288.814.813.108,00   1750699605,67335               0,00             ,00000 0                              ,00000                         0,00 99/99/99990                  0                                                                                                                                                                                       N00000006                    0001                                            0,00               0,00     0031/05/20100259IDR00002100113100    99/99/9999            ,00000 0028881481310800000288779808107000       000000          0                  00000000000000000000028879";
			
			Config cfg = ConfigXML.load(new File("bancs.xml"));
			
			Codex c = new GroupCodex();
			c.init(null, cfg);

			JsonObject obj = new JsonObject();
			boolean res = c.decode(new CodexContext(), obj, ByteBuffer.wrap(U.getBytes(msg)));
			
			LOG.info("RES "+res+" = "+U.dump(obj));
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
