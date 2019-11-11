package com.github.fsmi.eido;

import com.opensymphony.xwork2.ActionSupport;

public class MyStruts extends ActionSupport {
		  private String message;
		  private String username;
		  
		  @Override
		public String execute() {
		    if (username != null) {
		    	username = username+"'s struts";
		    	return "success";
		    }
		    else {
		    	return "none";
		    }
		  }
}
