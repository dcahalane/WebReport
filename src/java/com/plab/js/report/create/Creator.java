package com.plab.js.report.create;

import java.io.IOException;
import java.io.OutputStream;

public interface Creator {
	public void create(String reportName, String[] header, String[][] data, int total);
	
	public void print(OutputStream out) throws IOException;
}
