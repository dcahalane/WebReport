/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//Takes an array of fields
function displayBoundDialog(fields, data){
    Ext.form.Field.prototype.msgTarget = 'side';   
    /*
                 *  Create a generic change-listener that handles binding to the data.
                 */
    var changeListener = function(scope, options){
        var n = 'fs.data.' + this.name;
        if(this instanceof Ext.form.Checkbox){
            var value = (this.getValue())?'Y':'N'
            eval(n + " = '" + value + "'");
        }else{
            eval(n +  " = '"+ this.getValue() + "'");
        }
    };                        
    //Form Panel
    var fs = new Ext.FormPanel({
        frame: false,
        border: false,
        labelAlign: 'right',
        labelWidth: 85,
        width:340,
        waitMsgTarget: true,
        items: [
        new Ext.form.FieldSet({
            title: 'Contact Information',
            defaultType: 'textfield',
            autoHeight:true,
            items: fields
        })
        ]
    });

                
    
    fs.addButton('Submit', function(){
        //alert(JSON.stringify(fs.data));
        $.ajax({
            url: "http://localhost:8084/myOffice/ORM?ACTION=UPDATE",
            type: "POST",
            data: {
                'DATA': JSON.stringify(fs.data)
                }
        
        });
        $( "#dialog" ).dialog("close");
    	
    });

    fs.data = data;//Binds the returned JSON to the form.
    var fields = fs.getForm().getFieldValues();
    for(var o in fields){
        var fullName = 'fs.data.' + o;
        val = eval(fullName);
        var f = fs.getForm().findField(o);
        if(f instanceof Ext.form.Checkbox){
            f.setValue( val == 'Y' || val == 'y'  );
            f.addListener('check', changeListener);
        }else{
            f.setValue(val);
            f.addListener('change', changeListener); 
        }
                                
    }

    fs.render('form-ct');
/*
    fs.on({
        actioncomplete: function(form, action){
            // Only enable the submit button if the load worked
            if(action.type == 'load'){
                submit.enable();
            }
        }
    });  
*/    
};
