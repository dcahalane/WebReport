package com.plab.web.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.plab.js.report.ConnectionManager;
import com.plab.js.report.JsReport;
import com.plab.js.report.ReportFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ReportingServlet
 */
public class ReportingServlet extends HttpServlet {

    public static final String REP_USER = "REP_USER";
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if(getUser(request) != null){
            initParams(request);
            execute(request, response);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if(getUser(request) != null){
            initParams(request);
            execute(request, response);
        }
    }

    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String repName = request.getParameter("report-name");
            System.out.println(request.getQueryString());

            if ("TRUE".equalsIgnoreCase(request.getParameter("flush"))) {
                ReportFactory.REPORT_MAP.clear();
            }

            JsReport report = new JsReport();

            if ("TRUE".equalsIgnoreCase(request.getParameter("data"))) {
                String parameterName = request.getParameter("param-name");
                String value = request.getParameter("KEY");
                for (ReportFactory.ReportInfo info : ReportFactory.getReportInfoList(repName)) {
                    for (ReportFactory.ParameterInfo p : info.parameters) {
                        if (parameterName.equals(p.name)) {
                            if (value != null) {
                                p.boundValue = value;
                                p.clearValues();
                            }
                            String respStr = report.getParameterData(p, convertParameters(request));
//System.out.println(respStr);		
                            response.getWriter().println(respStr);
                            response.getWriter().flush();
                        }
                    }
                }
            } else {
                String respString = report.getParameterDialog(repName, convertParameters(request));
//System.out.println(respString);		
                response.getWriter().println(respString);
                response.getWriter().flush();
            }
    }

    void initParams(HttpServletRequest request) {
            if (!ReportFactory.INITIALIZED) {
                String reportHome = getServletContext().getInitParameter("REPORT_HOME");
                System.out.println("Getting REPORT_HOME from the conext of " + reportHome);
                if (reportHome != null) {
                    ReportFactory.REPORT_HOME = reportHome;
                    try {
                        ReportFactory.initialize();
                    } catch (FileNotFoundException e) {
                        Logger.getLogger(ReportingServlet.class.getName()).log(Level.SEVERE, null, e);
                    } catch (IOException e) {
                        Logger.getLogger(ReportingServlet.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
    }

    String getUser(HttpServletRequest request) {
        String user = request.getParameter("REP_USER");
        if (user == null) {
            user = (String) request.getSession(true).getAttribute(REP_USER);
        }
        if (user != null && !user.equals((String) request.getSession(true).getAttribute(REP_USER))) {
            user = null;
        }
        return user;
    }

    public Map<String, String> convertParameters(HttpServletRequest request) {
        Map<String, String> retVal = new HashMap<String, String>();
        Map<String, String[]> paramMap = request.getParameterMap();
        HttpSession session = request.getSession(true);
        String repUser = (String) session.getAttribute(REP_USER);
        if (repUser != null && repUser.length() > 0) {
            retVal.put(REP_USER, repUser);
        } else {
            repUser = request.getParameter(REP_USER);
            if (repUser != null && repUser.length() > 0) {
                session.setAttribute(REP_USER, repUser);
            }
        }
        for (String key : paramMap.keySet()) {
            String[] values = paramMap.get(key);
            StringBuffer valueBuffer = new StringBuffer();
            for (int i = 0; i < values.length; i++) {
                if(valueBuffer.indexOf(values[i])> -1) continue;
                if (i > 0) {
                    valueBuffer.append(",");
                }
                valueBuffer.append(values[i]);
            }
            retVal.put(key, valueBuffer.toString());
        }
        return retVal;
    }
}
