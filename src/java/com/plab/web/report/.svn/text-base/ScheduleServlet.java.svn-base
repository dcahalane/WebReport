/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.web.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dcahalane
 */
public class ScheduleServlet extends ReportingServlet {

    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = getUser(request);
        Map<String,String> map = convertParameters(request);        
        String reportName = map.get("report-name");
        String days = map.get("days");
        String hours = map.get("hours");
//System.out.println(reportName + "\t" + days + "\t" + hours);  
        //REPORTS have one schedule.  The database access is a MERGE.  Try to UPDATE, if not there, then INSERT.
        
    }
}
