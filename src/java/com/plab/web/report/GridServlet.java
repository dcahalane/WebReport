package com.plab.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.plab.js.report.ConnectionManager;
import com.plab.js.report.JsReport;
import com.plab.js.report.ReportFactory;

/**
 * Servlet implementation class GridServlet
 */
public class GridServlet extends ReportingServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GridServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        
        String repName = request.getParameter("report-name");
        
        



        JsReport report = new JsReport();
        String[] reports = repName.split(",");
        for(int i=0; i<reports.length; i++){
            String respString = report.getScript(reports[i], convertParameters(request));
            System.out.println("GridServlet\t" + repName);
            System.out.println(respString);
            response.getWriter().println(respString);
            response.getWriter().flush();
        }
        response.getWriter().close();
    }

    
}
