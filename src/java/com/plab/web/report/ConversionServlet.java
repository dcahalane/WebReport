/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.web.report;

import com.plab.js.report.JsReport;
import com.plab.js.report.ReportFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dcahalane
 */
public class ConversionServlet extends ReportingServlet {

    public static final String EXCEL = "EXCEL";
    public static final String PDF = "PDF";
    public static final String HTML = "HTML";

    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String repName = request.getParameter("name");
        
        if ("true".equalsIgnoreCase(request.getParameter("params"))) {
            JsReport report = new JsReport();
            Map<String,String> map = convertParameters(request);
            map.remove("params");
            String respString = report.getParameterDialog("convert", repName, map);
//System.out.println(respString);		
            response.getWriter().println(respString);
            response.getWriter().flush();
        } else {
            String converter = request.getParameter("converter");
            OutputStream out = response.getOutputStream();
            PrintWriter writer = new PrintWriter(out);
            String fileName = repName + ".txt";
            
            if (EXCEL.equalsIgnoreCase(converter)) {
                converter = ReportFactory.EXCEL_CONVERTER;
                fileName = repName.substring(repName.lastIndexOf("/") + 1) + ".xls";
            } else if (PDF.equalsIgnoreCase(converter)) {
                converter = ReportFactory.PDF_CONVERTER;
                fileName = repName.substring(repName.lastIndexOf("/") + 1) + ".pdf";
            } else if (HTML.equalsIgnoreCase(converter)) {
                converter = ReportFactory.HTML_CONVERTER;
                fileName = repName.substring(repName.lastIndexOf("/") + 1) + ".html";//downloads instead of opens.
                      
        
            } else {
                if (ReportFactory.getProperty(converter) != null) {
                    converter = ReportFactory.getProperty(converter);
                }
            }

            if (repName != null && converter != null) {
                response.setHeader("Content-Type", getServletContext().getMimeType(fileName));
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                if(ReportFactory.HTML_CONVERTER.equalsIgnoreCase(converter)){
                writer.println("<!DOCTYPE html>");
        writer.println("<html><head></head><body>");
        writer.flush();
                }
                JsReport report = new JsReport();

                String result = report.convertReport(repName, convertParameters(request), converter, out);
                if(ReportFactory.HTML_CONVERTER.equalsIgnoreCase(converter)){
                    writer.println("</body></html>");
                }
                //writer.println(result);
            } else {
                if (repName == null) {
                    repName = "null";
                }
                if (converter == null) {
                    converter = "null";
                }
                Logger.getLogger(ConversionServlet.class.getName()).log(Level.SEVERE, "Cannot convert report for Converter = " + converter + " and Report = " + repName);
            }
            writer.flush();

            writer.close();
        }
    }
}
