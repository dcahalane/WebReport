package com.plab.js.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.plab.js.report.create.*;
import com.plab.js.report.gen.ReportGenerator;
import com.plab.js.report.gen.SqlGenerator;
import com.plab.web.report.ConversionServlet;

public class JsReport {
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String PAGE = "page";
    
            
    /*
     * JsReport report = new JsReport();
     */
    public JsReport() {
        super();
    }

    public boolean authorize(String username) {
        boolean retVal = false;

        return retVal;
    }

    public ResultSetMetaData runMetaData(String sql, String dbName) {
        return null;

    }

    public String runJson(String reportFile, String dbName, Map parameters) {
        StringBuffer retVal = new StringBuffer();
        parameters.put("DB_NAME", dbName);
        retVal.append(convertReport(reportFile, parameters, "com.plab.js.report.create.JSONCreator", null));
        return retVal.toString();
    }

    public void runExcel(String reportFile, String dbName, Map parameters, OutputStream out) {
        convertReport(reportFile, parameters, ReportFactory.EXCEL_CONVERTER, out);
    }
    
    public String getProperty(Map<String, String> paramMap, String key, String defaultKey){
        String retVal = paramMap.get(key);
        if(retVal == null){
            retVal = ReportFactory.getProperty(defaultKey);
        }
        return retVal;
    }

    /*
     * This returns the headerType DIV for the report
     */
    public String getHeader(String reportFile) {
        //TODO: Everything.
        return null;
    }

    /*
     * returns the JavaScript to embed in a JSP for creating a ext grid.
     * 
     * 		   report.getScript("ship.xml", 
     * 							"Daily Ship",
     * 							new String[]{"COLUMN_NAME:COLUMN_LABEL:DATA_TYPE", "COLUMN_NAME:COLUMN_LABEL:DATA_TYPE"},
     * 							500,
     * 							500); 
     */
    public String getScript(String reportFile, Map<String, String> paramMap) {
        String widthStr = paramMap.get(WIDTH);
        String heightStr = paramMap.get(HEIGHT);
        String pageStr = paramMap.get(PAGE);
        
        int width = -1;
        if(widthStr != null ){
            width = Integer.parseInt(widthStr);
        }
        int height = -1;
        if(heightStr != null){
            height = Integer.parseInt(heightStr);
        }
        int page = 250;
        if(pageStr != null){
            page = Integer.parseInt(pageStr);
        }
         
        
        StringBuffer retVal = new StringBuffer();
        for (ReportFactory.ReportInfo report : ReportFactory.getReportInfoList(reportFile)) {
            String[] columnData = new String[report.columns.size()];
            int i = 0;
            for (ReportFactory.ColumnInfo c : report.columns) {
                columnData[i++] = c.toString();
            }
            String title = report.title;
            if (title == null || title.length() < 1) {
                title = report.name;
            }
            if (title == null || title.length() < 1) {
                title = reportFile;
            }
            if (report.property) {
                retVal.append("\n" + getPropertyScript(report, reportFile, title, paramMap, columnData, width, -1));
            } else if (report.pivot) {
                retVal.append("\n" + getPivotScript(report, reportFile, title, paramMap, columnData, width, -1));
            } else if (report.headerType) {
                retVal.append("\n").append(getHeaderScript(report, reportFile, title, paramMap, columnData, width, -1));
            } else {
                retVal.append("\n").append(getScript(report, reportFile, title, paramMap, columnData, width, height, page));//Everything is the default.
            }
        }
        return retVal.toString();
    }
    /*
    public String getScript(String reportFile, String title, Map<String, String> paramMap) {
    ReportFactory.ReportInfo report = ReportFactory.getReportInfoList(reportFile);
    String[] columnData = new String[report.columns.size()];
    int i = 0;
    for (ReportFactory.ColumnInfo c : report.columns) {
    columnData[i++] = c.toString();
    }
    return getScript(reportFile, title, paramMap, columnData, -1, -1);//Everything is the default.
    }
     */
    
    public String getComponent(Map<String, String> param){
        String retVal = getProperty(param, "component", ReportFactory.DISPLAY_COMPONENT);
        if(retVal == null) retVal = "grid-cmp";
        return retVal;
    }

    public String getScript(ReportFactory.ReportInfo report, String reportFile, String title, Map<String, String> paramMap, String[] columnData) {
        return getScript(report, reportFile, title, paramMap, columnData, -1, -1, 250);
    }

    public String getScript(ReportFactory.ReportInfo report, String reportFile, String title, Map<String, String> paramMap, String[] columnData, int width) {
        return getScript(report, reportFile, title, paramMap, columnData, width, -1, 250);
    }
    
    public String getUpdateScript(ReportFactory.ReportInfo report, String reportName, String title, Map<String, String> paramMap){
        String retVal = "Update failed with no reason specified";
        //TODO: Apply the parameters to sql and run an update/insert.  Return the success message in the report-info
        return retVal;
    }
    
    public String getPassthroughScript(ReportFactory.ReportInfo report, String reportName, String title, Map<String, String> paramMap){
        StringBuffer retVal = new StringBuffer();
        retVal.append("window.location = '" + report.location + "?" + ReportFactory.getQueryString(paramMap) + "';");
        //TODO: Change this so the new location shows up in the reports iFrame.
        return retVal.toString();
    }

    public String getHeaderScript(ReportFactory.ReportInfo report, String reportName, String title, Map<String, String> paramMap, String[] columnData, int width, int height) {
        
        StringBuffer retVal = new StringBuffer();
        retVal.append("$(\"#"+getComponent(paramMap)+"\").append(\"");
        ReportFactory.ReportInfo repInfo = ReportFactory.getReportInfo(reportName);
        String sql = ReportFactory.getProcessor().processSQL(repInfo.getSql(), paramMap);
        String dbName = repInfo.getDbName();
        if (dbName == null || dbName.length() < 1) {
            dbName = paramMap.get("DB_NAME");
        }
        ReportGenerator generator = new SqlGenerator();
        generator.generateData(dbName, sql, reportName, paramMap);
        String[][] data = generator.getData();
        
        String[] labels = generator.getLabels();
        retVal.append("<table>");
        for (int i = 0; data != null && i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                String value = (data[i][j] != null)?data[i][j]:"";
                retVal.append("<tr><td>" + labels[j] + ":&nbsp;&nbsp;</td><td>" + value + "</td></tr>");
            }
        }
        retVal.append("</table>");
        retVal.append("\");");
        return retVal.toString();
    }

    public String getPivotScript(ReportFactory.ReportInfo report, String reportName, String title, Map<String, String> paramMap, String[] columnData, int width, int height) {
        if (width < 0) {
            width = 1000;
        }
        if (height < 0) {
            height = 600;
        }
        ReportFactory.ReportInfo repInfo = ReportFactory.getReportInfo(reportName);
        StringBuffer retVal = new StringBuffer();
        paramMap.remove("name");
        String qryStr = ReportFactory.getQueryString(paramMap);
        boolean afterFirst = false;
        retVal.append("var records = Ext.data.Record.create([\n");
        for (ReportFactory.ColumnInfo c : repInfo.getDefaultColumns()) {
            if (afterFirst) {
                retVal.append(",");
            }
            afterFirst = true;
            retVal.append("{name: '" + c.name.toUpperCase() + "', type: '" + c.type + "'}\n");

        }
        retVal.append("]);\n");


        retVal.append("var conn = new Ext.data.Connection({\n");
        retVal.append("    timeout: 180000,\n");
        retVal.append("    url: 'data?name=" + reportName + "&" + qryStr + "', \n");
        retVal.append("    method: 'POST'\n");
        retVal.append("});\n");

        retVal.append("var proxy = new Ext.data.HttpProxy(conn);\n");
        //TODO: Pass along the parameter map.

        retVal.append("var reader = new Ext.data.JsonReader({\n");
        retVal.append("    root: 'records',\n");
        retVal.append("    totalProperty: 'total'\n");
        retVal.append("}, records);\n");

        retVal.append("var store =new Ext.data.Store({\n");
        retVal.append("proxy: proxy,\n");
        retVal.append("reader: reader,\n");
        retVal.append("listeners: {\n");
        retVal.append("'load': function(){done();},\n");
        retVal.append("'exception': function(){done();}\n");
        retVal.append("}\n");
        retVal.append("});	\n");



        //Context Menu Actions.

        retVal.append("var exportExcel = new Ext.Action({\n");
        retVal.append("   text: 'Export Sheet to Excel',\n");
        retVal.append("   handler: function(widget, event){\n");
        //Doesn't work in IE.  Need to get an iFrame and the document
        retVal.append("        document.location.href = 'data:text/csv;base64,' + exportCSV(grid);\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        retVal.append("var exportPDF = new Ext.Action({\n");
        retVal.append("   text: 'Export to PDF',\n");
        retVal.append("    handler: function(widget, event){\n");
        retVal.append("        alert('Export to PDF Called');\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        //Schedule the report
        retVal.append("var schedule = new Ext.Action({\n");
        retVal.append("   text: 'Schedule the Report',\n");
        retVal.append("    handler: function(widget, event){\n");
        retVal.append("        var repName = '" + reportName + "';\n");
        retVal.append("        scheduleReport(repName, USER.fields.EMAIL.value);\n");
        retVal.append("        //alert('Schedule Report ' + repName + '  ' + USER.fields.EMAIL.value);\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        //The Context Menu
        retVal.append("var contextMenu = new Ext.menu.Menu({\n");
        retVal.append("    items:[exportExcel, exportPDF, schedule]});\n");

        retVal.append("var grid = new Ext.grid.PivotGrid({\n");
        retVal.append("store: store,\n");
        ReportFactory.ColumnInfo agg = repInfo.getAggregatorColumn();
        if (agg != null) {
            retVal.append("aggregator: '" + agg.aggregator + "',\n");
            retVal.append("measure   : '" + agg.name.toUpperCase() + "',\n");
            retVal.append("viewConfig: {\n     title: '"+repInfo.title+"'\n   },\n");
            List<ReportFactory.ColumnInfo> leftAxis = repInfo.getLeftAxis();
            int c = 0;
            if (leftAxis.size() > 0) {
                retVal.append("     leftAxis: [\n");
                for (ReportFactory.ColumnInfo left : leftAxis) {
                    retVal.append("{\n");
                    retVal.append("    width: " + left.width + ",\n");
                    retVal.append("    dataIndex: '" + left.name.toUpperCase() + "'\n");
                    retVal.append("}\n");
                    if (++c < leftAxis.size()) {
                        retVal.append(",\n");
                    }
                }
                retVal.append("     ],\n");
            }
            List<ReportFactory.ColumnInfo> topAxis = repInfo.getTopAxis();
            int d = 0;
            if (topAxis.size() > 0) {
                retVal.append("     topAxis: [\n");
                for (ReportFactory.ColumnInfo top : topAxis) {
                    retVal.append("{\n");
                    retVal.append("    dataIndex: '" + top.name.toUpperCase() + "'\n");
                    retVal.append("}\n");
                    if (++d < topAxis.size()) {
                        retVal.append(",\n");
                    }
                }
                retVal.append("     ],\n");
            }

        }

        retVal.append("id: '" + title + "',\n");
        retVal.append("stateId: '" + title + "', \n");
        retVal.append("stateful: true,");
        retVal.append("columns:[\n");
        for (int i = 0; i < columnData.length; i++) {
            String[] c = columnData[i].split(":");
            retVal.append("{ header: '" + c[1] + "', id: '" + c[1] + "', sortable: true, width: " + c[3] + ", dataIndex: '" + c[0].toUpperCase() + "'}");
            if (i + 1 < columnData.length) {
                retVal.append(",");
            }
        }
        retVal.append("],\n");
        retVal.append("viewConfig: {\n");
        //retVal.append("   forceFit: true\n");
        retVal.append("   stripeRows: true\n");
        retVal.append("   ,listeners: {\n");
        retVal.append("      itemcontextmenu: function(view, rec, node, index, e){\n");
        retVal.append("         e.stopEvent();\n");
        retVal.append("         contextMenu.showAt(e.getXY());\n");
        retVal.append("         return false;\n");
        retVal.append("      }\n");
        retVal.append("   }\n");
        retVal.append("},\n");

        retVal.append("renderTo: '"+getComponent(paramMap)+"',//Div name in HTML\n");
        retVal.append("title: '" + title + "',//Grid Title\n");
        //retVal.append("width: " + width + ",//Grid Width\n");
        retVal.append("height: " + height + ",\n");
        retVal.append("autoHeight: false,\n");
        retVal.append("frame: true\n");
        retVal.append("});\n");
        //This is what actually calls the context menu.
        retVal.append("grid.on('rowcontextmenu', function(grid, rowindex, e){\n");
        retVal.append("         e.stopEvent();\n");
        retVal.append("         contextMenu.showAt(e.getXY());\n");
        retVal.append("         return false;\n");
        retVal.append("      });\n");
        retVal.append("store.load();\n");

//System.out.println(retVal.toString());


        return retVal.toString();
    }

    public String getPropertyScript(ReportFactory.ReportInfo report, String reportName, String title, Map<String, String> paramMap, String[] columnData, int width, int height) {
        if (width < 0) {
            width = 1000;
        }
        if (height < 0) {
            height = 600;
        }
        ReportFactory.ReportInfo repInfo = ReportFactory.getReportInfo(reportName);
        String sql = ReportFactory.getProcessor().processSQL(repInfo.getSql(), paramMap);
        String dbName = repInfo.getDbName();
        if (dbName == null || dbName.length() < 1) {
            dbName = paramMap.get("DB_NAME");
        }
        ReportGenerator generator = new SqlGenerator();
        generator.generateData(dbName, sql, reportName, paramMap);
        String[] labels = generator.getLabels();
        String[][] data = generator.getData();

        StringBuffer retVal = new StringBuffer();
        retVal.append("var grid = new Ext.grid.PropertyGrid({\n");
        retVal.append("renderTo: '"+getComponent(paramMap)+"',\n");
        retVal.append("title: '" + title + "',\n");
        //retVal.append("width: " + width + ",//Grid Width\n");
        retVal.append("height: " + height + ",\n");
        retVal.append("autoHeight: "+((report.autoHeight)?"true":"false")+",\n");
        retVal.append("frame: true,\n");
        retVal.append("source: {\n");
        for (int i = 0; i < labels.length; i++) {
            retVal.append("        \"" + labels[i] + "\": \"" + data[0][i] + "\" ");
            if (i + 1 < labels.length) {
                retVal.append(",");
            }
        }
        retVal.append("    }\n");
        retVal.append("});\n");
        return retVal.toString();
    }
    
    public String getScript(ReportFactory.ReportInfo report, java.lang.String reportFile, java.lang.String title, java.util.Map<java.lang.String,java.lang.String> paramMap, java.lang.String[] columnData, int width, int height){
        return getScript(report, reportFile, title, paramMap, columnData, width, height, 250);
    }

    /*
     * This requires that the HTML has a div with the id of grid-cmp.
     * <div id='grid-cmp'></div>
     */
    public String getScript(ReportFactory.ReportInfo report, java.lang.String reportFile, java.lang.String title, java.util.Map<java.lang.String,java.lang.String> paramMap, java.lang.String[] columnData, int width, int height, int pageSize) {

        int totalColWidth = 0;
        ReportFactory.ReportInfo repInfo = ReportFactory.getReportInfo(reportFile);
        if (width < 1) {
            width = repInfo.width;
        }
        if (height < 1) {
            height = repInfo.height;
        }
        if (repInfo.title != null) {
            title = repInfo.title;
        }
        StringBuffer retVal = new StringBuffer();
        paramMap.remove("name");
        String qryStr = ReportFactory.getQueryString(paramMap);

        retVal.append("var conn = new Ext.data.Connection({\n");
        retVal.append("    timeout: 180000,\n");
        retVal.append("    url: 'data?name=" + reportFile + "&" + qryStr + "', \n");
        retVal.append("    method: 'POST'\n");
        retVal.append("});\n");

        retVal.append("var proxy = new Ext.data.HttpProxy(conn);\n");
        //TODO: Pass along the parameter map.

        retVal.append("var reader = new Ext.data.JsonReader({\n");
        retVal.append("    root: 'records',\n");
        retVal.append("    totalProperty: 'total',\n");
        retVal.append("    fields: [");
        for (int i = 0; i < columnData.length; i++) {
            retVal.append("'" + columnData[i].split(":")[0] + "'");
            if (i + 1 < columnData.length) {
                retVal.append(",");
            }
        }
        retVal.append("]});\n");

        retVal.append("var store =new Ext.data.GroupingStore({\n");
        retVal.append("proxy: proxy,\n");
        retVal.append("reader: reader,\n");
        retVal.append("listeners: {\n");
        retVal.append("'load': function(){ done();},\n");
        retVal.append("'exception': function(){done();}\n");
        retVal.append("}\n");
        retVal.append("});	\n");
        //Paging Toolbar
        retVal.append("var pagesize = " + pageSize + ";\n");
        retVal.append("var paging_toolbar = new Ext.PagingToolbar({\n");
        retVal.append("    pageSize: pagesize,\n");
        retVal.append("    displayInfo: true,\n");
        retVal.append("    emptyMsg: 'No data found',\n");
        retVal.append("    store: store\n");
        retVal.append("});\n");


        //Context Menu Actions.

        retVal.append("var exportExcel = new Ext.Action({\n");
        retVal.append("   text: 'Export Sheet to Excel',\n");
        retVal.append("   handler: function(widget, event){\n");
        //Doesn't work in IE.  Need to get an iFrame and the document
        //retVal.append("        document.location.href = 'data:application/vnd.ms-excel;base64,' + Ext.ux.Exporter['exportGrid'](grid, null, null);\n");
        retVal.append("         document.location.href = 'convert?name=" + reportFile + "&converter="+ ConversionServlet.EXCEL + "';\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        //Export to HTML
        retVal.append("var exportHtml = new Ext.Action({\n");
        retVal.append("   text: 'Export Sheet to HTML',\n");
        retVal.append("   handler: function(widget, event){\n");
        //Doesn't work in IE.  Need to get an iFrame and the document
        //retVal.append("        document.location.href = 'data:application/vnd.ms-excel;base64,' + Ext.ux.Exporter['exportGrid'](grid, null, null);\n");
        retVal.append("         document.location.href = 'convert?name=" + reportFile + "&converter="+ ConversionServlet.HTML + "';\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        retVal.append("var exportPDF = new Ext.Action({\n");
        retVal.append("   text: 'Export to PDF',\n");
        retVal.append("    handler: function(widget, event){\n");
        retVal.append("        alert('Export to PDF Called');\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        //Schedule the report
        retVal.append("var schedule = new Ext.Action({\n");
        retVal.append("   text: 'Schedule the Report',\n");
        retVal.append("    handler: function(widget, event){\n");
        retVal.append("        var repName = '" + reportFile + "';\n");
        StringBuffer ParamBuffer = new StringBuffer();
        for (String key : paramMap.keySet()) {
            if(key.equals("days") || key.equals("hours") || key.equals("schedule")){
                continue;
            }
            if (ParamBuffer.length() > 0) {
                ParamBuffer.append(",");
            }
            ParamBuffer.append("{xtype: 'hidden', name: '" + key + "', value: '" + paramMap.get(key) + "'}\n");
        }
        ParamBuffer.append(",new Ext.form.Radio({\n");
        ParamBuffer.append("name: 'schedule',\n");
        ParamBuffer.append("value: 'weekly',\n");
        ParamBuffer.append("fieldLabel: 'Frequency',\n");
        ParamBuffer.append("boxLabel: 'Weekly',\n");
        ParamBuffer.append("listeners:{\n");
        ParamBuffer.append("    check: function(checkbox, checked){\n ");
        ParamBuffer.append("        var dayField = fsSchedule.getForm().findField('days');\n");
        ParamBuffer.append("        if(dayField && checked){\n");
        ParamBuffer.append("            dayField.enable();\n");
        ParamBuffer.append(" \n");
        ParamBuffer.append("        }\n");
        ParamBuffer.append("    }\n");
        ParamBuffer.append("}\n");
        ParamBuffer.append("}),\n");
        ParamBuffer.append("new Ext.form.Radio({\n");
        ParamBuffer.append("    name: 'schedule',\n");
        ParamBuffer.append("    value: 'daily',\n");
        ParamBuffer.append("    boxLabel: 'Daily',\n");
        ParamBuffer.append("    checked: true,\n");
        ParamBuffer.append("    listeners:{\n");
        ParamBuffer.append("        check: function(checkbox, checked){\n");
        ParamBuffer.append("            var dayField = fsSchedule.getForm().findField('days');\n");
        ParamBuffer.append("            if(dayField && checked){\n");
        ParamBuffer.append("                dayField.clearValue();\n");
        ParamBuffer.append("                dayField.disable();\n");
        ParamBuffer.append("            }\n");
        ParamBuffer.append("        }\n");
        ParamBuffer.append("    }\n");
        ParamBuffer.append("}),\n");
        ParamBuffer.append("new Ext.ux.form.SuperBoxSelect({\n");
        ParamBuffer.append("    typeAhead: true\n");
        ParamBuffer.append("    ,triggerAction: 'all'\n");
        ParamBuffer.append("    ,fieldLabel: 'Days of Week'\n");
        ParamBuffer.append("    ,width: 200\n");
        ParamBuffer.append("    ,name: 'days'\n");
        ParamBuffer.append("    ,mode: 'local'\n");
        ParamBuffer.append("    ,disabled: true\n");
        ParamBuffer.append("    ,store: new Ext.data.ArrayStore({\n");
        ParamBuffer.append("        fields:[\n");
        ParamBuffer.append("            'days','no'\n");
        ParamBuffer.append("        ]\n");
        ParamBuffer.append("        ,data:[['Sunday','0'],['Monday','1'], ['Tuesday','2'], ['Wednesday','3'],['Thursday','4'], ['Friday','5'], ['Saturday','6']]  \n");
        ParamBuffer.append("    })\n");
        ParamBuffer.append("    ,valueField: 'no'\n");
        ParamBuffer.append("    ,displayField: 'days'\n");
        ParamBuffer.append("}),\n");
        ParamBuffer.append("new Ext.ux.form.SuperBoxSelect({\n");
        ParamBuffer.append("    typeAhead: true\n");
        ParamBuffer.append("    ,triggerAction: 'all'\n");
        ParamBuffer.append("    ,fieldLabel: 'Hours'\n");
        ParamBuffer.append("    ,width: 200\n");
        ParamBuffer.append("    ,name: 'hours'\n");
        ParamBuffer.append("    ,mode: 'local'\n");
        ParamBuffer.append("    ,store: new Ext.data.ArrayStore({\n");
        ParamBuffer.append("        fields:[\n");
        ParamBuffer.append("            'hours', 'no'\n");
        ParamBuffer.append("        ]\n");
        ParamBuffer.append("        ,data:[");
        for(int i=1; i<25; i++){
            if(i > 1){
                ParamBuffer.append(",");
            }
            ParamBuffer.append("['" + i + ":00', '" + i + "']");
        }
        ParamBuffer.append("               ]  \n");
        ParamBuffer.append("    })\n");
        ParamBuffer.append("    ,valueField: 'no'\n");
        ParamBuffer.append("    ,displayField: 'hours'\n");
        ParamBuffer.append("})\n");

retVal.append("var params = [" + ParamBuffer.toString() + "];\n");

        retVal.append("        scheduleReport(repName, USER.fields.EMAIL.value, params);\n");
        retVal.append("        //alert('Schedule Report ' + repName + '  ' + USER.fields.EMAIL.value);\n");
        retVal.append("    }\n");
        retVal.append("});\n");
        //The Context Menu
        retVal.append("var contextMenu = new Ext.menu.Menu({\n");
        retVal.append("    items:[exportExcel, exportHtml, exportPDF, schedule]});\n");

        retVal.append("var grid = new Ext.grid.GridPanel({\n");
        retVal.append("store: store,\n");
        if (repInfo.paginate) {
            retVal.append("bbar:paging_toolbar,\n");
        }
        retVal.append("id: '" + title + "',\n");
        retVal.append("stateId: '" + title + "', \n");
        retVal.append("stateful: true,\n");
        retVal.append("columns:[\n");
        for (int i = 0; i < columnData.length; i++) {
            String[] c = columnData[i].split(":");
            ReportFactory.ColumnInfo cInfo = repInfo.getColumn(c[0]);
            if (cInfo.hidden) {
                continue;
            }
            retVal.append("{ header: '" + c[1] + "', \n");
            retVal.append("   id: '" + c[1].replace(" " , "_") + "',\n");
            retVal.append("   sortable: true, \n");
            retVal.append("   width: " + c[3] + ", \n");
            totalColWidth += Integer.parseInt(c[3]);
            String renderer = cInfo.getRenderer();
            if (cInfo.renderer != null && cInfo.renderer.length() > 0 && renderer != null) {
                retVal.append("   renderer: function(value, metaData, record, row, col, store){\n");
                renderer = ReportFactory.getProcessor().processSQL(renderer, paramMap);
                retVal.append("       " + renderer.trim() + "\n");
                retVal.append("    },\n");
            }
            retVal.append("   dataIndex: '" + c[0] + "'}\n");
            if (i + 1 < columnData.length) {
                retVal.append(",");
            }
        }
        if (width < 1) {
            width = totalColWidth;
        }
        retVal.append("],\n");
        retVal.append("viewConfig: {\n");
        //retVal.append("   forceFit: true\n");
        retVal.append("   stripeRows: true\n");
        retVal.append("   ,listeners: {\n");
        retVal.append("      itemcontextmenu: function(view, rec, node, index, e){\n");
        retVal.append("         e.stopEvent();\n");
        retVal.append("         contextMenu.showAt(e.getXY());\n");
        retVal.append("         return false;\n");
        retVal.append("      }\n");
        retVal.append("   }\n");
        retVal.append("},\n");
        retVal.append("view: new Ext.grid.GroupingView({\n");
        //retVal.append("    forceFit: true \n");


        retVal.append("   listeners: {\n");
        retVal.append("      itemcontextmenu: function(view, rec, node, index, e){\n");
        retVal.append("         e.stopEvent();\n");
        retVal.append("         contextMenu.showAt(e.getXY());\n");
        retVal.append("         return false;\n");
        retVal.append("      }\n");
        retVal.append("   },\n");

        retVal.append("groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Items\" : \"Item\"]})'\n");
        retVal.append("}),\n");
        retVal.append("renderTo: '"+getComponent(paramMap)+"',//Div name in HTML\n");
        retVal.append("title: '" + title + "',//Grid Title\n");
        retVal.append("width: " + width + ",//Grid Width\n");
        retVal.append("height: " + height + ",\n");
        if (repInfo.autoHeight) {
            retVal.append("autoHeight: true,\n");
        }
        retVal.append("collapsible: true,\n");
        if (repInfo.header) {
            retVal.append("header: true,\n");
        }
        retVal.append("frame: false\n");
        retVal.append("});\n");
        //This is what actually calls the context menu.
        retVal.append("grid.on('rowcontextmenu', function(grid, rowindex, e){\n");
        retVal.append("         e.stopEvent();\n");
        retVal.append("         contextMenu.showAt(e.getXY());\n");
        retVal.append("         return false;\n");
        retVal.append("      });\n");
        retVal.append("store.load({params:{start:0,limit:pagesize}});\n");


System.out.println(retVal.toString());
        return retVal.toString();
    }
    
    public String getParameterDialog(String reportFile, Map<String, String> paramMap) {
        return getParameterDialog("grid",   reportFile, paramMap);
    }
    
   

    public String getParameterDialog(String servlet,  String reportFile, Map<String, String> paramMap) {
        StringBuffer fieldBuffer = new StringBuffer();
        StringBuffer dataBuffer = new StringBuffer();
        StringBuffer reportName = new StringBuffer();

        for (ReportFactory.ReportInfo report : ReportFactory.getReportInfoList(reportFile)) {
            if (reportName.length() > 0) {
                reportName.append(",");
            }
            reportName.append(report.name);
            for (ReportFactory.ParameterInfo p : report.parameters) {
                if (!p.prompt) {
                    continue;
                }
                if (fieldBuffer.length() > 0) {
                    fieldBuffer.append(",\n");
                }
                //TODO: Field type based on Parameter.type, List/Sql and distinct.
                if (p.getValues(paramMap).size() > 0) {
                    //dataBuffer.append(getParameterDataWithVariable(p));
                    dataBuffer.append(getParameterStore(p));
                    dataBuffer.append("\n");
                }
                fieldBuffer.append(getParameterField(p, paramMap));
            }
        }
        paramMap.put("report-name", reportName.toString());






        StringBuffer retVal = new StringBuffer();
        if (fieldBuffer.length() < 1) {
            //retVal.append(getScript(reportFile, paramMap));
            //Set the queryString to null and load the grid.
            retVal.append("qryStr = null;\nloadGrid('" + servlet + "', '"+ReportFactory.getQueryString(paramMap) +"');\n");
        } else {

            retVal.append("var paramDlg = new Ext.Window({ \n");
            retVal.append("width:400 \n");
            retVal.append("}); \n");

            retVal.append(dataBuffer.toString());

            retVal.append("var paramForm = new Ext.FormPanel({ \n");
            retVal.append("labelWidth: 75, // label settings here cascade unless overridden \n");
            //TODO: Change the URL
            retVal.append("url:'" + servlet + "', \n");
            retVal.append("name: 'mainForm', \n");
            retVal.append("frame:true, \n");
            retVal.append("title: 'Report Parameters', \n");
            retVal.append("bodyStyle:'padding:5px 5px 0', \n");
            retVal.append("defaultType: 'textfield', \n");
            retVal.append("items: [ \n");
            for (String key : paramMap.keySet()) {
                if (fieldBuffer.length() > 0) {
                    fieldBuffer.append(",");
                }
                fieldBuffer.append("{xtype: 'hidden', name: '" + key + "', value: '" + paramMap.get(key) + "'}\n");
            }

            retVal.append(fieldBuffer.toString());
            retVal.append("]\n");
            retVal.append(",buttons: [ \n");
            retVal.append("new Ext.Button({text:'Submit', handler: function(button, event){\n");
            retVal.append("qryStr = paramForm.getForm().getValues(true); \n");
            retVal.append("loadGrid('grid', qryStr)" + ";\n");//Location
            retVal.append("paramDlg.close();\n");
            retVal.append("}\n");
            retVal.append("}),\n");
            retVal.append("{text:'Cancel'}\n");
            retVal.append("]\n");
            retVal.append("});\n");
            retVal.append("paramDlg.add(paramForm); \n");
            retVal.append("paramDlg.show(); \n");
        }
        retVal.append(getMessageDialog(reportFile));
//System.out.println(retVal.toString());
        return (retVal != null) ? retVal.toString() : null;
    }
    
 

    public String getParameterDataWithVariable(ReportFactory.ParameterInfo p, Map parameterMap) {
        StringBuffer retVal = new StringBuffer(getParameterData(p, parameterMap));
        retVal.insert(0, "var " + p.columnName + "DataArray = ");
        retVal.append(";\n");
        return retVal.toString();
    }

    public String getParameterData(ReportFactory.ParameterInfo p, Map parameterMap) {
        StringBuffer retVal = new StringBuffer();
        for (String key : p.getValues(parameterMap).keySet()) {
            if (retVal.length() > 0) {
                retVal.append(",");
            }
            retVal.append("{\"key\":\"" + key + "\",\"value\":\"" + p.getValues(parameterMap).get(key) + "\"}\n");
        }
        retVal.insert(0, "{\"data\":[\n");
        retVal.append("]}");
        return retVal.toString();
    }

    String getParameterStore(ReportFactory.ParameterInfo p) {
        StringBuffer retVal = new StringBuffer();
        retVal.append("var " + p.columnName + "STORE = new Ext.data.JsonStore({\n");

        retVal.append("   url: 'report?report-name=" + p.parentReport.name + "&data=true&param-name=" + p.name + "',\n");
        retVal.append("   root: 'data',");
        retVal.append("   fields: [\n");
        retVal.append("      'key',\n");
        retVal.append("      'value'\n");
        retVal.append("   ]\n");
        retVal.append("});\n");
        retVal.append(p.columnName + "STORE.load(); \n");
        return retVal.toString();
    }

    String getParameterField(ReportFactory.ParameterInfo p, Map<String, String> params) {
        String fieldType = "";
        StringBuffer boundListener = new StringBuffer();
        if (p.boundParameter != null && p.boundParameter.length() > 0) {
            boundListener.append(",listeners:\n");
            boundListener.append("{ change: { fn:function(combo, value)\n");
            boundListener.append("      { \n");
            boundListener.append("        Ext.getCmp('" + p.boundParameter + "CBO').clearValue();\n");
            boundListener.append("        var key = 'KEY=' + value;  \n");
            boundListener.append("        var urlStr = Ext.getCmp('" + p.boundParameter + "CBO').getStore().url;\n");
            boundListener.append("        Ext.getCmp('" + p.boundParameter + "CBO').getStore().proxy.conn.url = urlStr + '&' + key;\n");

            boundListener.append("        Ext.getCmp('" + p.boundParameter + "CBO').getStore().reload( );\n");
            boundListener.append("      }\n");
            boundListener.append("}  }\n");
        }
        if (!p.prompt) {
            return fieldType;
        }

        //TODO: Field type based on Parameter.type, List/Sql and distinct.

        if ("DATE".equalsIgnoreCase(p.type)) {
            fieldType = "new Ext.form.DateField({ "
                    + "fieldLabel: '" + p.name + "' "
                    + ",name: '" + p.columnName + "' "
                    + ",width: 150 "
                    + "}) ";
        } else if ("BOOLEAN".equalsIgnoreCase(p.type)) {
            fieldType = "{xtype: 'checkbox',fieldLabel: '" + p.name + "',name: '" + p.columnName + "'}";
        } else {
            if (p.getValues(params).size() > 0) {
                String boxType = (p.multiSelect) ? "Ext.ux.form.SuperBoxSelect" : "Ext.form.ComboBox";
                fieldType = "new " + boxType + "({"
                        + "fieldLabel: '" + p.name + "'"
                        + ",name: '" + p.columnName + "CBO'"
                        + ",id: '" + p.columnName + "CBO'"
                        + ", width: 250 "
                        + ", fields: ['key', 'value'] "
                        + ", store: " + p.columnName + "STORE "
                        + boundListener.toString()
                        + //",store: new Ext.data.SimpleStore({" +
                        //"	fields: ['key', 'value']," +
                        //"	data : "+p.columnName + "DataArray" +
                        //"})" +
                        ",displayField: 'value'"
                        + ",valueField: 'key'"
                        + ",hiddenName: '" + p.columnName + "'"
                        + ",typeAhead: true"
                        + //",mode: 'local'" +
                        ",triggerAction: 'all'"
                        + ",emptyText: 'Select...'"
                        + ",selectOnFocus: true"
                        + "})";
            } else {
                fieldType = "new Ext.form.TextField({fieldLabel: '" + p.name + "',name: '" + p.columnName + "', allowBlank: false})";
            }
        }
        return fieldType;
    }

    public String getMessageDialog(String reportName) {
        StringBuffer retVal = new StringBuffer();
        ReportFactory.ReportInfo info = ReportFactory.getReportInfo(reportName);
        if (info != null && info.message != null && info.message.length() > 0) {
            retVal.append("Ext.Msg.alert('Status', '" + info.message.trim() + "');\n");
        }
        return retVal.toString();
    }

    public String convertReport(String reportName, Map<String, String> parameters, String creatorClassName, OutputStream out) {
        Map<String, String> parameterMap = parameters;
        String retVal = null;
        for (ReportFactory.ReportInfo repInfo : ReportFactory.getReportInfoList(reportName)) {
            String sql = ReportFactory.getProcessor().processSQL(repInfo.getSql(), parameterMap);
            String dbName = repInfo.getDbName();
            if (dbName == null || dbName.length() < 1) {
                dbName = parameters.get("DB_NAME");
            }
            ReportGenerator generator = new SqlGenerator();
            generator.generateData(dbName, sql, repInfo.name, parameters);
            try {
                retVal = convert(repInfo.name, generator.getLabels(), generator.getData(), generator.getTotalRows(), creatorClassName, out);
            } catch (SQLException ex) {
                Logger.getLogger(JsReport.class.getName()).log(Level.SEVERE, "SQL Error", ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JsReport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retVal;
    }

    public String convert(String title, String[] labels, String[][] data, int total, String creatorClassName, OutputStream out) throws SQLException, ClassNotFoundException {
        Creator c = null;
        if(creatorClassName == null) creatorClassName = ReportFactory.DEFAULT_CREATOR;
        System.out.println(creatorClassName);
        try {
            c = (Creator) Class.forName(creatorClassName).newInstance();
            c.create(title, labels, data, total);
            if (out != null ) {
                c.print(out);
            }
        } catch (Exception e) {
            Logger.getLogger(JsReport.class.getName()).log(Level.SEVERE, "Error converting report with converter " + creatorClassName, e);
            throw new ClassNotFoundException(e.getMessage());
        }
        return c.toString();
    }

    public static void main(String[] args) {
        ReportFactory.REPORT_HOME = "C:\\";
        try {
            ReportFactory.initializeProperties();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JsReport js = new JsReport();
        ConnectionManager.getInstance().initializeProperties(ReportFactory.PROPERTY);
        System.out.println(js.getParameterDialog("Order-Status1", new HashMap()));


    }
}
;