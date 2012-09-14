/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.js.report.create;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author dcahalane
 */
public class HTMLCreator implements Creator {
    StringBuffer output = new StringBuffer();
    @Override
    public void create(String reportName, String[] header, String[][] data, int total) {
        output.append("<table>\n<tr>");
        for(int i=0; header != null && i<header.length;i++){
            output.append("<th>" + ((header[i] == null || header[i].trim().length() < 1)?"&nbsp;":header[i] )+ "</th>");
        }
        output.append("</tr>\n");
        for(int i=0; data != null && i<data.length; i++){
            output.append("<tr>");
            for(int j=0; j<data[i].length; j++){
                output.append("<td>" + ((data[i][j] == null || data[i][j].trim().length() < 1)?"&nbsp;":data[i][j] ) + "</td>");
            }
            output.append("</tr>\n");
        }
        output.append("</table>\n");
    }

    @Override
    public void print(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        writer.println(output.toString());
        writer.flush();
    }
    
}
