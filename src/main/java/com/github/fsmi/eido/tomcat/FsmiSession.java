package com.github.fsmi.eido.tomcat;

import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

public class FsmiSession implements SessionAware {

	private SessionMap<String, Object> sessionMap;
	private String username;
	private String password;

	@Override
	public void setSession(Map<String, Object> session) {
		this.sessionMap = (SessionMap<String, Object>) session;
	}
	
	public String execute() {
		return "";
	}

}
