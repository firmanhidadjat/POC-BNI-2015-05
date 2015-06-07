package id.co.hanoman.bni;

import id.co.hanoman.U;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.FileCodexFactory;
import id.co.hanoman.codex.PojoValueHandler;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBancs {
	private final static Logger LOG = LoggerFactory.getLogger(TestBancs.class);

	public static void main(String[] args) {
		try {
			CodexFactory cf = new FileCodexFactory(new File("src/main/resources/META-INF/codex"));
			
			PojoValueHandler valueHandler = new PojoValueHandler(cf);
			
//			Codex c = cf.getCodex("bancs-req");
//
//			Object obj = c.decode(new CodexContext(valueHandler, U.getBytes(msg)), null);
//			
//			LOG.info("RES "+U.dump(obj));

			List<String[]> resps = new LinkedList<String[]>();
			resps.add(new String[] {"bancs-req", " 0768                    **            00302691884297402001000000000000000   0 000000  0026936020900135000000001245000000+            KAS NEGARA PERSEPSI                                                                                     IDR00000001245000000+IDR00000001245000000+00000000000000000+00000000000000000+00                                                                                                                                                                                                                                                                                                                                                                                       SIPN-SSP-C-003738101423000                                                      "});
//			resps.add(new String[] {"bancs-resp", " 0354    0270            0000    000573003018157349612020010116544000042110000 000000  09KAS NEGARA PERSEPSI                                            18136020900135001814961211654422042015085644BFHP      6.000.000,00     576.102.806,00               0,00                             181360209001350IDR000000BEKASI                        SETOR TUNAI         "});
//			resps.add(new String[] {"bancs-resp", " 0162    0078            0000    000573003018157349612020010116544000042110200 000000  080000 O.K.                                                                     "});
//			resps.add(new String[] {"bancs-resp", 
//					" 0354    0270            0000    000573003018157349612020010116544000042110000 000000  09KAS NEGARA PERSEPSI                                            18136020900135001814961211654422042015085644BFHP      6.000.000,00     576.102.806,00               0,00                             181360209001350IDR000000BEKASI                        SETOR TUNAI         "+
//					" 0162    0078            0000    000573003018157349612020010116544000042110200 000000  080000 O.K.                                                                     "
//			});
//			resps.add(" 0354    0270            0000    000012003031001248867020010118464000042110000 000000  09KAS NEGARA PERSEPSI                                            31036020900135003104886711846422042015085701BFHP         88.445,00     299.279.935,00               0,00                             310360209001350IDR000000BUMI SERPONG DAMAI            SETOR TUNAI         ");
			
			for (String resp[] : resps) {
				Codex c = cf.getCodex(resp[0]);
				CodexContext cci = new CodexContext(valueHandler, U.getBytes(resp[1]));
				CodexContext cco = new CodexContext(valueHandler);
				Object obj;
				do {
					obj = c.decode(cci, null);
					
					if (obj != null) {
						LOG.info("RESP ["+resp[0]+"] "+U.dump(obj));
						
						c.encode(cco, obj);
					}
				} while (obj != null);
				LOG.info("TEST "+Arrays.equals(U.getBytes(resp[1]), cco.getBytes())+"\n"+U.dump(U.getBytes(resp[1]))+"\n"+U.dump(cco.getBytes()));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
