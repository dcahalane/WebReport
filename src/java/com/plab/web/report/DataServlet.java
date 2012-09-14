package com.plab.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.plab.js.report.ConnectionManager;
import com.plab.js.report.JsReport;
import com.plab.js.report.ReportFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Servlet implementation class DataServlet
 */
public class DataServlet extends ReportingServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String repName = request.getParameter("name");
        
        JsReport report = new JsReport();
        String result = report.runJson(repName, "DWML", convertParameters(request));
System.out.println("DataServlet " + repName);

        response.getWriter().println(result);
        response.getWriter().flush();  
        response.getWriter().close(); 
        
    }
}
