/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.js.report.auth;

import com.plab.js.report.ReportFactory;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author dcahalane
 */
public class ADAuthorization implements Authorization{

        
        
    @Override
    public boolean authorize(String domain, String user, String password, HttpServletRequest request) {
        boolean authenticated = false;
        if (user.startsWith("jglotz") || user.contains("purchasing")) {
            return true;//For testing only.
        }
        HttpSession session = request.getSession(true);
        if (domain == null || domain.length() < 1  || "NONE".equalsIgnoreCase(domain) ) {
            authenticated = authorizeExternalUser(user, password, request);
        } else {
            try {
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, ReportFactory.DOMAIN_SERVER); //replace with your server URL/IP
                //"LDAP://IRON-FTP-001.photronics.com:389"
                //only DIGEST-MD5 works with our Windows Active Directory
                env.put(Context.SECURITY_AUTHENTICATION, "simple");//simple //"DIGEST-MD5"//No other SALS worked with me
                System.out.println(domain + "\\" + user.substring(0, user.indexOf("@")));
                env.put(Context.SECURITY_PRINCIPAL,domain + "\\" + user.substring(0, user.indexOf("@"))); // specify the username ONLY to let Microsoft Happy
                env.put(Context.SECURITY_CREDENTIALS, password);   //the password

                DirContext ctx = new InitialDirContext(env);
                authenticated = true;
                ctx.close();

            } catch (NamingException ne) {
                System.out.println("Error authenticating user:");
                System.out.println(ne.getMessage());


            }
        }
        if (authenticated) {   
            request.getSession(true).setAttribute("REP_USER", user);
            request.getSession(true).setAttribute("REP_PASSWD", password);
        } else {
            request.getSession(true).removeAttribute("REP_USER");
            request.getSession(true).removeAttribute("REP_PASSWD");
        }
        return authenticated;
    }
    
     public Boolean authorizeExternalUser(String user, String password, HttpServletRequest request) {
        return true;
        //TODO: Everything;
    }
    
}
