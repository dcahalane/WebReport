<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- http://localhost:8084/WebReport/main-page.jsp?REP_USER=dcahalane&report-name=/domains/users/plab/dcahalane/external/OrderStatus-->
<!--
If you end the report-name with .xml, then it will refer to the absolute path.  Otherwise it will refer relative to REPORT_HOME
-->
<%
    String domainID = request.getParameter("report-name");
    String repUser = request.getParameter("REP_USER");
    String url = request.getRequestURL().toString();
    java.net.URL URL = new java.net.URL(url);
    String query = request.getQueryString();
    String HOST = URL.getHost();
    int PORT = URL.getPort();
    String HOST_PORT = HOST + ":" + PORT;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>My Maskshop</title>


        <!-- <script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script> --> 
        <link rel="stylesheet" type="text/css" href="/WebReport/ext-3.4.0/resources/css/ext-all.css" /> 
        <link rel="stylesheet" type="text/css" href="/WebReport/ext-3.4.0/examples/grid/grid-examples.css" /> 
        <script type="text/javascript" src="/WebReport/ext-3.4.0/adapter/ext/ext-base.js"></script> 
        <script type="text/javascript" src="/WebReport/ext-3.4.0/ext-all.js"></script>
        <script type="text/javascript" src="/WebReport/ext-3.4.0/Exporter-all.js"></script>
        <script type="text/javascript" src="/WebReport/utilities.js"></script>


        <link type="text/css" href="/WebReport/jquery-ui/css/blitzer/jquery-ui-1.8.12.custom.css" rel="Stylesheet" />	
        <script type="text/javascript" src="/WebReport/jquery-ui/js/jquery-1.5.1.min.js"></script>
        <script type="text/javascript" src="/WebReport/jquery-ui/js/jquery-ui-1.8.12.custom.min.js"></script>
        <script type="text/javascript" src="/WebReport/jquery-ui/jquery.cookie.js"></script>

        <script type="text/javascript" src="/WebReport/SuperBoxSelect/SuperBoxSelect.js"></script>
        <link rel="stylesheet" type="text/css" href="/WebReport/SuperBoxSelect/superboxselect.css" /> 
        <link rel="stylesheet" type="text/css" href="/WebReport/SuperBoxSelect/superboxselect-gray-extend.css" /> 


        <script>

            Ext.onReady(function (){
                
                
                loadGrid('http://<%=HOST_PORT%>/WebReport/report', '<%=query%>');


                
            });
        </script>   

    </head>
    <body>




        <!-- <button onClick="loadGrid('http://localhost:8080/WebReport/report', 'report-name=Order-Status1&flush=true');" >Load</button>-->

        <div id="Reporting" >
            <div id='grid-cmp'></div>
        </div>


        <div id="loading" style="display: none; position: absolute; left: 180px; top: 15px;"><img src="images/ajax-loader.gif"/></div>
    </body>
</html>