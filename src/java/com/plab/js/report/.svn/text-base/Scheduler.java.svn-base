/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.js.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
/**
 *
 * @author dcahalane
 */
public class Scheduler {
    static final String INSERT_SCHEDULE = "INSERT INTO REPORT_SCHEDULE (RSC_ID, CNT_ID_FK, REPORT_NAME, SCHEDULE) " + 
            " SELECT NEXT_VAL('REPORT_SCHEDULE'), CNT_ID, ?, ? FROM CONTACT WHERE EMAIL = ?";
    static final String UPDATE_SCHEDULE = "UPDATE REPORT_SCHEDULE SET SCHEDULE = ? WHERE CNT_ID_FK = ? and REPORT_NAME = ?";
    
    public static synchronized void updateSchedule(String cntId, String reportName, String schedule) throws IOException, SQLException{
        
        Connection con = null;
        try{
            con = ConnectionManager.getInstance().getConnection("MYOFFICE");
        }finally{
            if(con != null) {
                try{con.close();}catch(Exception eCls){}
            }
        }
    }
    
    public static synchronized void clearSchedules(String cntId, String reportName){
        
    }
    
    public static synchronized Map<String,List<String>> getAllSchedules(String cntId){
        Map<String,List<String>> reportSchedule = new HashMap<String,List<String>>();
        
        return reportSchedule;
    }
    public static synchronized List<String> getSchedules(String cntId, String reportName){
        List<String> reportSchedule = new ArrayList<String>();
        
        return reportSchedule;
    }
    
}
