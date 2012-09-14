/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.js.report;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dcahalane
 */
public class SqlProcessor {
    /*
	 * Find and replace all the parameters with values.
	 */
	static final Pattern PARAM = Pattern.compile("\\#![a-zA-Z\\-\\_]*!\\#");
	public String processSQL(String sql, Map<String, String> parameters){
		Matcher match = PARAM.matcher(sql);
		StringBuffer buffer = new StringBuffer();
		boolean result = match.find();
		while(result){
			String parameter = match.group().substring(2,match.group().length()- 2);
			match.appendReplacement(buffer, getParameterValue(parameter, parameters));
			result = match.find();
		}
		match.appendTail(buffer);
		
		return buffer.toString();
	}
	
	String getParameterValue(String param, Map<String,String> parameters){
		String value = null;
		value = parameters.get(param);
		if(value == null){
			value = "";
		}
		return value;
	}
}
