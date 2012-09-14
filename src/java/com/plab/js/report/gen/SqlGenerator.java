/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plab.js.report.gen;

import com.plab.js.report.ConnectionManager;
import com.plab.js.report.ReportFactory;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dcahalane
 */
public class SqlGenerator implements ReportGenerator {

    protected String[] labels;
    protected String[][] data;
    protected int total = -1;

    public void generateData(String dbName, String sql, String reportName, Map<String, String> parameters) {
        List<ReportFactory.ReportInfo> reports = ReportFactory.getReportInfoList(reportName);
        if (reports.size() == 1) {

            String dbDriver = reports.get(0).driver;
            Connection con = null;
            String start = parameters.get("start");
            String limit = parameters.get("limit");
            int l = (limit != null) ? Integer.parseInt(limit) : -1;
            try {
                con = ConnectionManager.getInstance().getConnection(dbName, dbDriver);
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
System.out.println("***** " + sql);
               total = getRowCount(con, sql);               
                ResultSet rs = stmt.executeQuery(sql);

System.out.println("***** Query Returned " + total + " records with a limit of " + l);
                ResultSetMetaData rsmd = rs.getMetaData();
                labels = new String[rsmd.getColumnCount()];
                if ( (total >= l) &&  start != null) {
                    int s = Integer.parseInt(start);
                    rs.first();
                    if (total < 0 && rs.last()) {
                        total = rs.getRow();
                        rs.first();
                    }
                    
                    if (s > 0) {
                        rs.absolute(s);
                    }
                }
                for (int i = 0; i < getLabels().length; i++) {
                    labels[i] = rsmd.getColumnLabel(i + 1);
                }
                List<String[]> dataList = new ArrayList<String[]>();
                while (rs.next()) {
                    List<String> row = new ArrayList<String>();
                    for (int i = 0; i < getLabels().length; i++) {
                        String data = rs.getString(getLabels()[i]);
                        if(data != null){
                            data = data.trim();
                            data = data.replaceAll("\n", "");
                        }
                        row.add(data);
                    }
                    String[] dataRow = row.toArray(new String[]{});
                    dataList.add(dataRow);
                    if (rs.getRow() == l) {
                        break;
                    }
                }

                this.data = dataList.toArray(new String[][]{});
                if(this.data == null){
                    Logger.getLogger(SqlGenerator.class.getName()).log(Level.WARNING, "No Data returned for Report "+  reportName + "by SQL \n********" + sql);
                }
            } catch (Exception e) {
                Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, "ERROR WITH SQL");
                Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, sql);
                Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, null, e);
                
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        
    }
    
    int getRowCount(Connection con, String sql) {
        int retVal = -1;
        sql = sql.toUpperCase();
 
            Statement stmt = null;
            String newSql = "";
            try{
                stmt = con.createStatement();
                newSql = handleUnions(sql);  
                Logger.getLogger(ReportFactory.class.getName()).log(Level.INFO, "Count SQL " + newSql);
                ResultSet rs = stmt.executeQuery(newSql);
                while(rs.next()){
                    retVal = rs.getInt(1);
                }
            }catch(Exception e){
                Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, "ERROR Calculation resultset size\n"+ newSql);
                Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, "****************************");
                    try{
                        retVal = 0;
                        stmt.close();
                        stmt = con.createStatement();
                        newSql = "SELECT COUNT(*) FROM (SELECT * FROM ( " + sql + " ) )";
                        ResultSet rs2 = stmt.executeQuery(newSql);
                        while(rs2.next()){
                            retVal += rs2.getInt(1);
                        }
                    }catch(Exception eInner){
                        Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE,"Error recalcuating result set size\n" + newSql, eInner);
                        retVal = -1;
                    }
            }finally{
                try{stmt.close();}catch(Exception e){}
            }
        return retVal;
    }
    
    
    String handleUnions(String sql){
        String[] s = sql.split("UNION ALL");
        StringBuffer retVal = new StringBuffer();
        if(s.length > 1){
            for(int i=0; i<s.length; i++){
                String result = handleUnions(s[i]);
                if(retVal.length() > 0){
                    retVal.append(" UNION ALL ");
                }
                retVal.append(result);
            }
        }else{
            s = s[0].split("UNION");
            if(s.length > 1){
                for(int j=0; j<s.length; j++){
                    String result = handleUnions(s[j]);
                    if(retVal.length() > 0){
                        retVal.append(" UNION ");
                    }
                    retVal.append(result);
                }
            }else{
                retVal.append(handleSql(s[0]));
            }
        }
        return retVal.toString();
    }
    
    String handleSql(String sql){
        int index = sql.indexOf("FROM");
        String retVal = "SELECT COUNT(*) FROM (" + sql + ")";
        return retVal;
    }

    /**
     * @return the labels
     */
    public String[] getLabels() {
        return labels;
    }

    /**
     * @return the data
     */
    public String[][] getData() {
        return data;
    }

    public int getTotalRows() {
        return total;
    }
}
