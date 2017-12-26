package com.mz.akiwrapper.entities;

public class Token {
	
	private long signature;
	private int session;
	
	public Token(long signature, int session) {
		this.signature = signature;
		this.session = session;
	}

	public long getSignature() {
		return signature;
	}

	public int getSession() {
		return session;
	}
	
}
