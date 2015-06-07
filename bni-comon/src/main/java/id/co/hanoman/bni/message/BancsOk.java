package id.co.hanoman.bni.message;

import id.co.hanoman.U;

import java.io.Serializable;

@U.IgnoreNull
public class BancsOk implements BancsData, Serializable {
	private static final long serialVersionUID = -7690407452110685271L;

	String string1;
	String string2;
	String string3;
	
	public String getHeaderOutputType() {
		return "08";
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String getString2() {
		return string2;
	}

	public void setString2(String string2) {
		this.string2 = string2;
	}

	public String getString3() {
		return string3;
	}

	public void setString3(String string3) {
		this.string3 = string3;
	}
}
