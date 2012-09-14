<%@page import="com.plab.js.report.ReportFactory"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String flush = request.getParameter("flush");
if("TRUE".equalsIgnoreCase(flush)) ReportFactory.REPORT_MAP.clear();

String REPORT_HOME = System.getProperty("REPORT_HOME");
if(REPORT_HOME == null){
    System.setProperty("REPORT_HOME", getServletContext().getInitParameter("REPORT_HOME"));
    System.setProperty("PROP_FILE", getServletContext().getInitParameter("REPORT_PROPERTY"));
}

    String url = request.getRequestURL().toString();
    java.net.URL URL = new java.net.URL(url);
    String HOST = URL.getHost();
    int PORT = URL.getPort();
    String HOST_PORT = HOST + ":" + PORT;
%>

<html>
    <head>
        <script type="text/javascript" src="https://getfirebug.com/firebug-lite.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>My Maskshop</title>

        <!-- <script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script> --> 
        <link rel="stylesheet" type="text/css" href="js/ext-3.4.0/resources/css/ext-all.css" /> 
        <link rel="stylesheet" type="text/css" href="js/ext-3.4.0/examples/grid/grid-examples.css" />
        <link rel="stylesheet" type="text/css" href="css/FileUploadField.css" /> 
        <link rel="stylesheet" type="text/css" href="css/superboxselect.css" /> 
        <link rel="Stylesheet" type="text/css" href="js/jquery-ui-1.8.14.custom/css/custom-theme/jquery-ui-1.8.14.custom.css"  />
        <link rel="stylesheet" type="text/css" href="css/SuperBoxSelect/superboxselect.css" /> 
        <link rel="stylesheet" type="text/css" href="css/SuperBoxSelect/superboxselect-gray-extend.css" />
        
        <script type="text/javascript" src="js/ext-3.4.0/adapter/ext/ext-base.js"></script> 
        <script type="text/javascript" src="js/ext-3.4.0/ext-all.js"></script>
        <script type="text/javascript" src="js/AccordionVboxLayout.js"></script>
        <script type="text/javascript" src="js/utilities.js"></script>
        <script type="text/javascript" src="js/Ext.ux.Notification.js"></script>
        <script type="text/javascript" src="js/Ext.MessageBox2.js"></script>
        <script type="text/javascript" src="js/slidingtabs.js"></script>
        <script type="text/javascript" src="js/tabclose.js"></script>
        <script type="text/javascript" src="js/SuperBoxSelect.js"></script>
        <script type="text/javascript" src="js/FileUploadField.js"></script>
        <script type="text/javascript" src="js/jquery-ui-1.8.14.custom/js/jquery-1.5.1.min.js"></script>
        <script type="text/javascript" src="js/jquery-ui-1.8.14.custom/js/jquery-ui-1.8.14.custom.min.js"></script>
        <script type="text/javascript" src="js/jsjac.js"></script>
        <script type="text/javascript" src="js/jquery.contextMenu.js" ></script>
        <script type="text/javascript" src="js/contacts.js" ></script>
        <script type="text/javascript" src="js/SuperBoxSelect.js"></script>
        <script type="text/javascript" src="js/Exporter-all.js" ></script>
        <script type="text/javascript" src="js/splitter/splitter.js" ></script>
        <!--<script type="text/javascript" src="/myOffice/xmpp.js" ></script>-->

        <style type="text/css" media="all">
            #main-panel {/* Main splitter element overflow: auto;*/
                height:95%;width:100%;margin:0;padding:0;visibility:hidden;
            }
            #leftPane{
                float:left;width:20%;height:100%;

            }
            #rightPane{	/*Contains toolbar and horizontal splitter*/
                float:right;width:80%;height:100%;
            }

        </style>

        <script>
            function exportCSV(grid){
                var gridStore = grid.getStore();
                var gridColumnModel = grid.getColumnModel();
                var header = '';
                var body = '';
                var headComplete = false;
                var newLine = true;
                gridStore.each(function(record, index){
                    gridColumnModel.getColumnsBy(function(c) {
                        if(! c.hidden) {
                            if(!headComplete){
                                if(header.length > 1){
                                    header = header + ', ';
                                } 
                                header = header + c.dataIndex;
                            }
                            if(newLine){
                                body = body + '/n';
                                newLine = false;
                            }else{
                                body = body + ', ';
                            } 
                            body = body + record.get(c.dataIndex);
                        }
                    });
                    newLine = true;
                    body = body + '\n';
                    if(header.length > 1){
                        headComplete = true;
                    }
                });
                var csvText = header + body;
                return Base64.encode(csvText);

                
            };
        </script>

        <script>
            var tabs; //Primary tab panel
            var tree = null;
            var USER;
            Ext.onReady(function(){
                    $('#tabs').height($(window).height() - 20);
                    $('#tabs').width($(window).width() - 220);
                    
                $("#main-panel").css("visibility", "visible");
                //$(window).unload( function () { alert("Bye now!"); } );
                //This allows the column visibility and grouping state to be stored in cookies.
                Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
                Ext.Component.prototype.stateful = false;
                Ext.QuickTips.init();
                Ext.util.CSS.swapStyleSheet('theme', "/WebReport/ext-3.4.0/resources/css/xtheme-blue.css");
                 window.loaded = true;
                });
        </script>

        <script>
                $().ready(function(){
                    var authenticated = false;
                    
                    var fs = new Ext.FormPanel({
                        frame: false,
                        border: false,
                        labelAlign: 'right',
                        labelWidth: 85,
                        width:340,
                        waitMsgTarget: true,
                        monitorValid:true,
                        items: [
                            new Ext.form.FieldSet({
                                title: 'Login',
                                defaultType: 'textfield',
                                autoHeight:true,
                                items: [
                                    new Ext.form.ComboBox({
                                        typeAhead: true
                                        ,forceSelection: true
                                        ,allowBlank:false
                                        ,triggerAction: 'all'
                                        ,fieldLabel: 'Domain'
                                        ,name: 'domain'
                                        ,mode: 'local'
                                        ,store: new Ext.data.ArrayStore({
                                            fields:[
                                                'domain'
                                            ]
                                            ,data:[['NONE'],['BRK'], ['ALN'], ['BGD']]  
                                        })
                                        ,valueField: 'domain'
                                        ,displayField: 'domain'
                                        ,value:'NONE'
                                    }),
                                    new Ext.form.TextField({
                                        fieldLabel: 'Name'
                                        ,name: 'user'
                                        ,allowBlank: false
                                    }),
                                    new Ext.form.TextField({
                                        fieldLabel: 'Password'
                                        ,name: 'password'
                                        ,inputType: 'password',
                                        allowBlank: false
                                    })
                                ]
                            })
                        ]
                    });
                    fs.addButton({text:'Log in', iconCls:'login-icon',formBind:true, handler:function(){
                            fs.getForm().submit({
                                url: 'auth',
                                success: function(form, action){
                                    if(!action.result.user){
                                        createAlert(Ext.Msg.ERROR, 'Error', 'Invalid USER.fields.XMPP_USER_NAME.value and/or password');
                                        fs.getForm().reset();
                                        return;
                                    }
                                    //Block user from accessing site until everything is loaded. Removed during final step in login sequence
                                    globalLoadMask = new Ext.LoadMask(Ext.getBody(),{msg:"Loading user information..."});
                                    globalLoadMask.show();
                                    authenticated = true;
                                   
                                    buildReportDomains('domain?REP_USER=' + action.result.user.fields.EMAIL.value);
                                    USER = action.result.user;   
                                   
                                    globalLoadMask.hide();
                                    $( "#authenticate" ).dialog("close");
                                    var defDomain = action.result.user.fields.DEFAULT_DOMAIN.value;
                                    if(defDomain){
                                        loadGrid('report', 'report-name=' + defDomain);
                                    }
                                },
                                failure: function(form, action){
                                    authenticated = false;
                                    createAlert(Ext.Msg.ERROR, 'Failure', action.result.msg);
                                },
                                waitMsg: 'Authenticating...'
                            });
                        }});
                    fs.render('login');
                
                    $( "#authenticate" ).dialog({
                        autoOpen: false,
                        modal: true,
                        width: 370,
                        title: 'Login',
                        closeOnEscape: false,
                        close: function() {
                            $('#login').empty();
                        }
                    });
                    $( "#authenticate" ).dialog( {beforeClose: function(event, ui){
                            return authenticated;
                        }} );
                    if (USER == null) {
                        $( "#authenticate" ).dialog( "open" );
                    } 
                });
                var fsSchedule = null;
                function scheduleReport(reportName, user, params){
                    fsSchedule = new Ext.FormPanel({
                        frame: false,
                        border: false,
                        labelAlign: 'right',
                        labelWidth: 85,
                        width:340,
                        waitMsgTarget: true,
                        items: params
                    });
                    fsSchedule.addButton('Submit', function(){
                        var days = fsSchedule.getForm().findField('days').getValue();
                        var hours = fsSchedule.getForm().findField('hours').getValue();
                        var schedule = fsSchedule.getForm().findField('schedule');
                        if(!schedule.getValue()){
                            days = '1,2,3,4,5,6,7';
                        }
                        fsSchedule.getForm().submit({
                            url: 'schedule',
                            success: function(form, action){
                                
                            },
                            failure: function(form, action){
                                createAlert(Ext.Msg.ERROR, 'Failure', 'Failure');
                            },
                            waitMsg: 'Processing...'
                        });
                         
                        $( "#schedule" ).dialog("close");
                    });
                    fsSchedule.render('diaSchedule');
                
                    $( "#schedule" ).dialog({
                        autoOpen: false,
                        modal: true,
                        width: 370,
                        title: 'Schedule Report',
                        closeOnEscape: false,
                        close: function() {
                            $('#diaSchedule').empty();
                        }
                    });

                    $( "#schedule" ).dialog( "open" );
                };
                
                onunload = function(){if(con){var p = new JSJaCPresence();
                        $.ajax({
                            url: 'Contact?ACTION=LOGOUT'
                        });
                        p.setType("unavailable");
                        p.setTo(USER.fields.XMPP_USER_NAME.value+"@myoffice.photronics.com");
                        con.send(p);
                        con.disconnect();
                    }}
        </script>
                        
                        
<script>
    Ext.onReady(function(){
                                    
    });
    
    function buildReportDomains(url){
                var Tree = Ext.tree;
                tree = new Tree.TreePanel({
                    useArrows: true,
                    autoScroll: false,
                    animate: true,
                    enableDD: true,
                    containerScroll: false,
                    border: false,
                    autoHeight: true,
                    width: 195,
                    // auto create TreeLoader
                    dataUrl: url,
                    contextMenu: new Ext.menu.Menu({
                        items: [{
                                    id: 'default',
                                    text: 'Set as Default'
                                },
                                {
                                    id: 'export',
                                    text: 'Export as HTML'
                                }
                        ],
                        listeners: {//Contect Menu Listeners
                            itemclick: function(item) {
                                var node = this.contextNode;
                                if(node.leaf){
                                    var reportId = replaceAll(node.id, ' ', '/');
                                    switch (item.id) {
                                        case 'default':
                                            //$.post("http://<%=HOST_PORT%>/WebReport/Contact?ACTION=DEFAULT&default=" + id + "&REP_USER=" + USER.fields.EMAIL.value);
                                            $.post("Contact?ACTION=DEFAULT&default=" + reportId + "&REP_USER=" + USER.fields.EMAIL.value);
                                            break;
                                        case 'export':
                                            //loadGrid('convert', 'name='+reportId + '&converter=HTML&params=true');
                                            alert('QueryStr ' + qryStr);
                                            document.location.href = "convert?name=" + reportId + "&converter=HTML&" + qryStr ;
                                            break;
                                        }
                                    }
                                }
                            }
                        }),
                        listeners: {
                            click: function(node, event){
                                if(node.leaf){
                                    //Confirm that they want to open the report.
                                    var id = replaceAll(node.id, ' ', '/');
                                    $('#grid-cmp').empty();
                                    wait();
                                    CURRENT_REPORT = id;
                                    //You can pass in width, height, or page.
                                    if(id.match(/.html/)){
                                        //TODO: get the file and embed it into the 
                                    }else{
                                        //loadGrid('http://<%=HOST_PORT%>/WebReport/report', 'report-name=' + id + "&page=500&height=" + (Math.round($(window).height() - 80)) + "&width=" + (Math.round($(window).width() - $('#accordion').width() - 20)) );
                                        loadGrid('report', 'report-name=' + id + "&page=500&height=" + (Math.round($(window).height() - 80)) + "&width=" + (Math.round($(window).width() - $('#accordion').width() - 20)) );
                                    }
                                }
                            },
                            contextmenu: function(node, e) {
                                node.select();
                                var c = node.getOwnerTree().contextMenu;
                                c.contextNode = node;
                                c.showAt(e.getXY());
                            }
                        },
                        root: {
                            nodeType: 'async',
                            text: 'Domains',
                            draggable: false,
                            id: 'src' 	
                        }
                    });
	   
                    // render the tree
                    tree.render('tree-div');
                    tree.getRootNode().expand();

 
	
                };
                function replaceAll(txt, replace, with_this) {
                    return txt.replace(new RegExp(replace, 'g'),with_this);
                }
	

                $(function() {
                    $( "#accordion" ).accordion({
                        autoHeight: false,
                        navigation: true,
                        fillSpace: true
                    });
                });

                $(function() {
                    $( "#tabs" ).tabs({
                        select: function(event, ui){
                            var activePanel = $(ui.tab).text();
                            if('Remote View' == activePanel){
                                if(!PORTAL_OPEN){
                                    $("#Remote_View").empty();
                                    //Do not remove the last double single-quote.  For some reason that allows the page to open within the iFrame.
                                    $("#Remote_View").append("<iframe width='1060px' height='660px' src='http://portal.photronics.com/sgd/authentication/ttaAuthentication.jsp?Username=rviewtest&Password=remoteview''  /> </iframe>");
                                    //Load RemoteView.  TODO:  Need to turn off the external frame code in the ttaAuthentication.jsp
                                    PORTAL_OPEN = true;
                                }
                            }
                            $( "#accordion" ).accordion("activate", ui.index);
                        }
                    });
                });

                $(document).ready(function() {
                    $("#accordion h3").click(function() {
                        var activePanel = replaceAll($(this).text(), ' ', '_');
                        $( "#tabs" ).tabs("select", activePanel);
                    });
                    $( "#tabs" ).tabs("select", 0);
                });
	
	
                function openTab(tabName, url){
                    $( "#tabs" ).tabs("url", tabName, url);
                    $( "#tabs" ).tabs("load", tabName);
                    $( "#tabs" ).tabs("select",tabName);
                }
</script>                                

        <style type="text/css"> 
             button.login-icon{background-image:url(images/login.png)}
            .upload-icon {
                background: url('../shared/icons/fam/image_add.png') no-repeat 0 0 !important;
            }
            #fi-button-msg {
                border: 2px solid #ccc;
                padding: 5px 10px;
                background: #eee;
                margin: 5px;
                float: left;
            }
        </style> 

        <style>
            .ui-accordion .ui-accordion-content { padding-top: 2px; padding-left: 2px;}

            #Reporting { padding-top:5px; padding-left: 0px;}
        </style>

        <style type="text/css"> 
            .upload-icon {
                background: url('../shared/icons/fam/image_add.png') no-repeat 0 0 !important;
            }
            #fi-button-msg {
                border: 2px solid #ccc;
                padding: 5px 10px;
                background: #eee;
                margin: 5px;
                float: left;
            }
        </style> 
  </style> 

    </head>
    <body>

        <iframe src="" id="hiddenFrm" style="display:none;" frameborder="0"></iframe>
        <div id="authenticate">
            <div id="login"></div>
        </div>
        <div id="schedule">
            <div id="diaSchedule"></div>
        </div>


        <!-- <button onClick="loadGrid('http://localhost:8080/WebReport/report', 'report-name=Order-Status1&flush=true');" >Load</button>-->
        <table border=0>
            <tr valign="top"><td >
                    <!--  Left Panel Accordion -->
                    <div id="accordion" style="width: 200px; height: 800px;">
                        <h3><a href="#">Reporting</a></h3>
                        <div id="tree-div"></div>
                        <!-- Contacts -->
                        <!--
                        <h3><a href="#" class="contact-header-menu">Contacts</a></h3>
                        <div >
                            <div id="contactsdataview"></div>
                            <div id="contacts">
                            </div>
                            <div id="dialog">
                                <div id="form-ct"></div>
                            </div>
                        </div>
                        
                        <h3><a href="#">Web Ordering</a></h3>
                        <div>

                        </div>
                        <h3><a href="#">Remote View</a></h3>
                        <div>
                        -->
                        </div>
                    </div>
                </td><td >
                    <!--  Tab Panel -->
                    <div id="tabs" style="height: 1200px;">
                        <ul>
                            <li><a href="#Reporting">Reports</a></li>
                            <!--<li><a href="#Contacts">Contacts</a></li>-->
                            <!--<li><a href="#Web_Ordering">Web Ordering</a></li>-->
                            <!--<li><a href="#Remote_View">Remote View</a></li>-->
                        </ul>
                        <div id="Reporting" >
                            <div id='grid-cmp'></div>
                            <iframe src="" id="view" style="display:none;" frameborder="0"></iframe>
                        </div>
                        <!--
                        <div id="Contacts">
                            <div id="stabs"></div>
                        </div>
                        <div id="Web_Ordering">
                            <iframe src="http://www.mymaskshop.com/cgi-bin/orderform/login.cgi?username=testuser&password=testuser" width="100%" height="700">
                                <p>Your browser does not support iframes.</p>
                            </iframe>
                        </div>
                        <div id="Remote_View">
                            
                        </div>
                        -->
                    </div>
                </td></tr>
        </table>

        <div id="loading" style="display: none; position: absolute; left: 180px; top: 15px;"><img src="images/ajax-loader.gif"/></div>


    </body>
</html>
