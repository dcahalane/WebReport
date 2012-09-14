
function wait() {
    $('#loading').show();
}
function done(){
    $('#loading').hide();
}


function ajaxObject(url, callbackFunction) {
    var that=this;      
    this.updating = false;

    this.update = function(passData,postMethod) { 
        if (that.updating==true) {
            return false;
        }
        that.updating=true;                       
        var AJAX = null;                          
        if (window.XMLHttpRequest) {              
            AJAX=new XMLHttpRequest();              
        } else {                                  
            AJAX=new ActiveXObject("Microsoft.XMLHTTP");
        }                                             
        if (AJAX==null) {                             
            return false;                               
        } else {
            AJAX.onreadystatechange = function() {  
                if (AJAX.readyState==4) {             
                    that.updating=false;                
                    that.callback(AJAX.responseText,AJAX.status,AJAX.responseXML);        
                    delete AJAX;                                         
                }                                                      
            }                                                        
            var timestamp = new Date();                              
            if (postMethod=='POST') {
                var uri=urlCall + '&time=' + timestamp.getTime();
                AJAX.open("POST", uri, true);
                AJAX.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                AJAX.send(passData);
            } else {
                var uri=urlCall + '?'+passData; 
                AJAX.open("GET", uri, true);                             
                AJAX.send(null);                                         
            }              
            return true;                                             
        }                                                                           
    }
    var urlCall = url;        
    this.callback = callbackFunction || function () { };
}  

function attachScript(responseText, responseStatus) {
    // This function is called by the ajaxObject when the server has finished
    // sending us the requested script.
    if (responseStatus==200) {
        eval(responseText);
    }
}
	
function loadGrid(servlet, params){
    //alert(servlet + '?' + params);
    var loader = new ajaxObject(servlet, attachScript);
    loader.update(params, 'GET');
}
        
function formatCurrency(n_value) {

    if (isNaN(Number(n_value)))

        return 'ERROR';



    // save the sign

    var b_negative = Boolean(n_value < 0);

    n_value = Math.abs(n_value);

	

    // round to 1/100 precision, add ending zeroes if needed

    var s_result = String(Math.round(n_value*1e2)%1e2 + '00').substring(0,2);



    // separate all orders

    var b_first = true;

    var s_subresult;

    while (n_value > 1) {

        s_subresult = (n_value >= 1e3 ? '00' : '') + Math.floor(n_value%1e3);

        s_result = s_subresult.slice(-3) + (b_first ? '.' : ',') + s_result;

        b_first = false;

        n_value = n_value/1e3;

    }

    // add at least one integer digit

    if (b_first)

        s_result = '0.' + s_result;

	

    // apply formatting and return

    return  b_negative

    ? '($' + s_result + ')'

    : '$' + s_result;

}
