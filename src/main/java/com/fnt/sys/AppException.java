package com.fnt.sys;

import java.util.Set;

public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	Integer code;
	String msg;

	public AppException(Integer code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

	public AppException(Integer code, String msg, Throwable t) {
		super(msg, t);
		this.code = code;
		this.msg = msg;
	}

	public AppException(Integer code, Set<String> violationMessages) {
		super(violationMessages.toString());
		String msg = "";
		for (String s : violationMessages) {
			msg += s;
		}

		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
