package id.co.hanoman.bni.sms;

public class Provider {

	public void sendSMS(PushSMS sms) throws InterruptedException {
		Thread.sleep(1000);
		sms.setStatus("ok");
	}
}
