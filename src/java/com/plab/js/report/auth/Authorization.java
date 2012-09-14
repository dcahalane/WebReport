package com.plab.js.report.auth;

import javax.servlet.http.HttpServletRequest;

public interface Authorization {

    public boolean authorize(String domain, String user, String password, HttpServletRequest request);
	
	
}
