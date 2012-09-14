
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
	    if (that.updating==true) { return false; }
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
	   }else{
               alert(responseStatus + ' Error calling report ' + responseText);
           }
	}
	
	function loadGrid(servlet, params){
		var loader = new ajaxObject(servlet, attachScript);
		loader.update(params, 'GET');
	}
        
        
        
// Vtype for phone number validation
Ext.apply(Ext.form.VTypes, { 
    'phoneText': 'Not a valid phone number. Must be in the format (123) 456-7890.', 
    'phoneMask': /[\-\+0-9\(\)\s\.Ext]/, 
    'phoneRe': /^(\({1}[0-9]{3}\){1}\s{1})([0-9]{3}[-]{1}[0-9]{4})$|^(((\+44)? ?(\(0\))? ?)|(0))( ?[0-9]{3,4}){3}$|^Ext. [0-9]+$/, 
    'phone': function (v) {
        return this.phoneRe.test(v); 
    }
});

// Function to format a phone number
Ext.apply(Ext.util.Format, {
	phoneNumber: function(value) {
		var phoneNumber = value.replace(/\./g, '').replace(/-/g, '').replace(/[^0-9]/g, '');
		
		if (phoneNumber != '' && phoneNumber.length == 10) {
			return '(' + phoneNumber.substr(0, 3) + ') ' + phoneNumber.substr(3, 3) + '-' + phoneNumber.substr(6, 4);
		} else {
			return value;
		}
	}
});

Ext.namespace('Ext.ux.plugin');

// Plugin to format a phone number on value change
Ext.ux.plugin.FormatPhoneNumber = Ext.extend(Ext.form.TextField, {
	init: function(c) {
		c.on('change', this.onChange, this);
	},
	onChange: function(c) {
		c.setValue(Ext.util.Format.phoneNumber(c.getValue()));
	}
});