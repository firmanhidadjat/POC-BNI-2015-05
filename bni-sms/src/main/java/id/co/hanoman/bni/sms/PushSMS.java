package id.co.hanoman.bni.sms;

import java.io.Serializable;

public class PushSMS implements Serializable {
	private static final long serialVersionUID = 1L;

	long id;
	String text;
	String status;
	String notelp;
	String pesan;

	public PushSMS() {
	}

	public PushSMS(long id, String text, String notelp, String pesan) {
		this.id = id;
		this.text = text;
		this.notelp = notelp;
		this.pesan = pesan;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotelp() {
		return notelp;
	}

	public void setNotelp(String notelp) {
		this.notelp = notelp;
	}

	public String getPesan() {
		return pesan;
	}

	public void setPesan(String pesan) {
		this.pesan = pesan;
	}

	
	@Override
	public String toString() {
		return "PushSMS [id=" + id + ", text='" + text + "', status='" + status
				+ "']";
	}
}
