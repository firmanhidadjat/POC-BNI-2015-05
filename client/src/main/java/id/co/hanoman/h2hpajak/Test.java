package id.co.hanoman.h2hpajak;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class Test {
	static Logger log = Logger.getLogger(TestVerify.class);

	public static void main(String[] args) {
		try {
			byte b[] = Base64.decodeBase64("MIIDaTCCAlGgAwIBAgIEQ8V5KzANBgkqhkiG9w0BAQsFADBlMQswCQYDVQQGEwJJRDEQMA4GA1UECBMHSmFrYXJ0YTEQMA4GA1UEBxMHSmFrYXJ0YTEMMAoGA1UEChMDZm9vMQswCQYDVQQLEwJJVDEXMBUGA1UEAxMOY2xpZW50LmZvby5jb20wHhcNMTUwMzA3MTQzMjEwWhcNMTYwMzAxMTQzMjEwWjBlMQswCQYDVQQGEwJJRDEQMA4GA1UECBMHSmFrYXJ0YTEQMA4GA1UEBxMHSmFrYXJ0YTEMMAoGA1UEChMDZm9vMQswCQYDVQQLEwJJVDEXMBUGA1UEAxMOY2xpZW50LmZvby5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCbOB8wvuZq/6PDcC5ZsNnTK71b+JNWF4j3IAqhI1jFVYdP8Usg4qNQBHWFR6M+OhCrh/+SE0Vb+L/qkqZ4UUJq67GfV0R9ZzfRbv2WP5k8O0g9CR06tJmBiGhYMwHXeyAmLr5x4RuII3BqbTZBNqYwwNVN9wQS7ExJr5o9GqypzzLxIeHAm4lZ/XhUoL2Yd/uuJdcwdckp4edyZ78V1Kzseg7OFVkylUKk2xNg8XSrrVlRaUy7RIn8eRwQ/sIcM6vumsYbVPwRqXB6bWCQ7jWQaSX2oZ1idB4amn6utxIZ1+vqSouwFA5JfGEFO55dRQLgVuODBS4hJvacBMHg8zWHAgMBAAGjITAfMB0GA1UdDgQWBBRxGe5NjMbJoMyUUpcEeasGBq0ASzANBgkqhkiG9w0BAQsFAAOCAQEAmyUDSc4zJi5vJ4xO+udvtZOGTbBNRTF4DO0K+MYkeAEQZJCOMRsiy5Ya3retmKC0/B9mcY/yyQrO9Q4lyse2Qr/hapHm2agbfBRh3asJ9QbDsF/Gg0b8oDvUtH6XsN+lkAnppDf9TMwybtyHeSCAeh/MuY9dN9H7rCoMj32z39giPqFe6KqACIQLiicJdiscX7z57q7LCCPEbxx4UPjSmsPGfyV75OFxHpdvJgcGaHJTtBwNf9rcXgz5p0/VI9wTJsTH5UWzwXSBFrb3gkTo8TU4Vw988VbbRXGkffOeBK5ueW76p9+yxewSQvIJcVL/cy1PLRZkbVI1VgcOo87B4Q==");

			CertificateFactory certFac = CertificateFactory.getInstance("X.509");
			log.info("DATA "+certFac.generateCertificate(new ByteArrayInputStream(b)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
