/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.web.report;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.plab.js.report.ConnectionManager;
import com.plab.js.report.ReportFactory;
import com.plab.js.report.auth.*;
import org.plab.Converter;

/**
 *
 * @author dcahalane
 */
@WebServlet(name = "Authenticate", urlPatterns = {"/auth"})
public class AuthenticationServlet extends HttpServlet {


    
    static boolean INITIALIZED = false;
    public void init() throws ServletException{
        super.init();
        if(!INITIALIZED){
            String repHome = getServletContext().getInitParameter("REPORT_HOME");
            String defDataMap = getServletContext().getInitParameter("DATA_MAP");
            try {
                ConnectionManager.getInstance().initializeProperties(ReportFactory.getProperties());
            } catch (IOException ex) {
                Logger.getLogger(AuthenticationServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            INITIALIZED = true;
        }
    }
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String domain = request.getParameter("domain");
            String user = request.getParameter("user");
            if(!user.contains("@")){
                user = user + ReportFactory.DOMAIN_NAME;
            }
System.out.println("Authenticating User " + user);
            String passwd = request.getParameter("password");           
            if (authorizeUser(domain, user, passwd, request)) {
                String cntId = getContactID(user);
                String resultUser = "\"" + user + "\"";
                if(cntId != null){
                    String dataMap = getServletContext().getInitParameter("DATA-MAP");
                    Converter c = new Converter(dataMap, "MYOFFICE", ReportFactory.getProperties());
                    
                    try {
                        String json = c.retrieve("CONTACT", cntId);
System.out.println(json);
                        resultUser = json;
                    } catch (SQLException ex) {
                        Logger.getLogger(AuthenticationServlet.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(AuthenticationServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
System.out.println("{\"success\": \"true\", \"msg\": \"User Authenticated\", \"user\":" + resultUser + " }");
                    out.println("{\"success\": \"true\", \"msg\": \"User Authenticated\", \"user\":" + resultUser + " }");
                }
                else{
                    out.println("{\"success\": \"false\", \"msg\": \"User information not in database\" }");
                }
            }else{
                out.println("{\"success\": \"false\", \"msg\": \"Unable to authenticate user\" }");
            }
        } finally {
            out.flush();
            out.close();
        }
    }
    
    String SQL_CONTACT = "SELECT CNT_ID FROM CONTACT WHERE EMAIL = ?";
    String getContactID(String user){
        String retVal = null;
        Connection con = null;
        try{
            System.out.println(user + "\t" + SQL_CONTACT);   
            org.plab.ConnectionManager.PROPERTY_FILE = ReportFactory.PROP_FILE;
            org.plab.ConnectionManager.PROPERTY_DIR = ReportFactory.REPORT_HOME;
            
            con = ConnectionManager.getInstance().getConnection("MYOFFICE"); 
         
            PreparedStatement pStmt = con.prepareStatement(SQL_CONTACT);
            pStmt.setString(1, user);
            ResultSet rs = pStmt.executeQuery();
            if(rs.next()){
                retVal = rs.getString(1);
            }
        }catch(Exception e){
             Logger.getLogger(AuthenticationServlet.class.getName()).log(Level.SEVERE, "Unable to get Contact ID", e);
        }finally{
            if(con != null){
                try{con.close();}catch(Exception e){}
            }
        }
        return retVal;
    }

    public boolean authorizeUser(String domain, String user, String password, HttpServletRequest request) {
        Authorization auth;
        try {
            auth = (Authorization)Class.forName("com.plab.js.report.auth.ADAuthorization").newInstance();
        } catch (Exception ex) {
            Logger.getLogger(AuthenticationServlet.class.getName()).log(Level.WARNING, "Unable to create authorization, running default AD Authorization");
            auth = new ADAuthorization();//Defaults to AD Authorization.
        }
        return auth.authorize(domain, user, password, request);
    }

   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
