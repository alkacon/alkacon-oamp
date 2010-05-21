//

/**
  * @description {Class} Editor
  * This class defines all kinds of editors for grid inline editing 
  * @inherit Object
  * @namespace Sigma
  */
Sigma.EditorDefault = {
	/**
     * @description {Config} defaultText
     * {String} Default text. Valid for "select" type only.
     */
	
	/**
     * @description {Config} type 
     * {String} Editor type. Value could be
     * "text" - Text box    
     * "select" - Drop down list
     * "date" - Date picker
     * "textarea" - Text area  
     * @sample
     * var colsConfig = [
     *         { id : 'status'  , header : "Status" , width : 50 ,
     *             editor : { type :'select' ,options : {'S': 'Shiped' , 'P':'Packed', 'N':'NA'}  },
     *         }
     * ];                      
     */
	
	/**
     * @description {Config} validRule
     * {Array} Valid rules. Array element could be
     * 'R' - Required
     * 'N' - Number
     * 'E' - Email
     * 'F' - Float
     * @sample
     * var colsConfig = [
     *     { id : 'age'    , header : "Age" , width : 60 , 
     *             editor: { type :'text' ,
     *                 validator : function(value,record,colObj,grid){ 
     *                     value=Number(value);
     *                     if ( !isNaN(value) && ( value>0 && value< 100 ) ) {
     *                         return true;
     *                     }
     *                     return "0~100 accepted only";
     *                 } 
     *             }  
     *         }
     * ];                    
     */

	gridId : null ,
	left:0,
	top:0,	
	render : Sigma.$empty,
    /**
     * @description {Config} validator
     * {Function} Validator of this column.
     * @param {Any} Value of the cell.
     * @param {Object or Array} Data record of row.
     * @param {Object} Column object.
     * @param {Object} Grid object.
     * @sample
     * var colsConfig = [
     *     { id : 'age'    , header : "Age" , width : 60 , 
     *             editor: { type :'text' ,
     *                 validator : function(value,record,colObj,grid){ 
     *                     value=Number(value);
     *                     if ( !isNaN(value) && ( value>0 && value< 100 ) ) {
     *                         return true;
     *                     }
     *                     return "0~100 accepted only";
     *                 } 
     *             }  
     *         }
     * ];                    
     */
	validator : null,

	isFocus : Sigma.$empty ,
	onKeyPress : Sigma.$empty ,

	errMsg : null ,
	isActive : null,
	
	valueDom : null,
	
	locked : false,


	initialize : function(options){
		if (options) {
			Sigma.$extend(this,options);
		}

		this.validator= this.validator || this.defaultValidator;
		if ( Sigma.$type(this.validRule,'string') ) {
			this.validRule=this.validRule.split(',');
		}
		if (this.required){
			this.validRule=['required'].concat(this.validRule);
		}

		this.dom= this.dom || Sigma.$e('div' ,{className:'gt-editor-container'} );

		Sigma.U.addEvent(this.dom,"click",function(event){ 
			Sigma.U.stopEvent(event);
		});

		Sigma.U.addEvent(this.dom,"dblclick",function(event){ 
			Sigma.U.stopEvent(event);
		});
	},

	destroy : function(){
		this.container=null;
		Sigma.U.removeNode(this.valueDom);
		this.valueDom=null;
		Sigma.U.removeNode(this.dom);
		this.dom=null;
		Sigma.WidgetCache[this.id]=null;
		delete Sigma.WidgetCache[this.id];
	},	

	setValue : function(value,record,colObj,grid,colNo,rowNo,tdObj){

				Sigma.U.setValue(this.valueDom ,value);
		},

	getValue : function(value,record,colObj,grid,colNo,rowNo,tdObj){
				return Sigma.U.getValue(this.valueDom);
		},
	parseValue : function(value,record,colObj,grid,colNo,rowNo,tdObj){
		return value;
	},
	getDisplayValue :function(value){
		return value===undefined? this.getValue():value;
	},

	defaultValidator:function(value,record,colObj,grid,editor){
		var errMsg=[];
		var validRule=[].concat(editor.validRule);

		for (var i=0;i<validRule.length;i++ ){
			var rule=validRule[i];
			var validParameter=[value];
			if (Sigma.$type(rule,'array')&&rule.length>0)	{
				rule=rule[0];
				validParameter=validParameter.concat(rule.slice(1));
			}

				var vat=Sigma.Validator.getValidator(rule);
				var validResult=true;
				if (Sigma.$type(vat,'function')){
					validResult=vat.apply(vat,validParameter);
				}
				if (validResult!==true){
					var v_msg=Sigma.Validator.getMessage(this.validRule[i]) || String(validResult) ;
					errMsg.push(v_msg);
				}
		}
		if (!errMsg || errMsg.length<1) {
			errMsg='';
		}
		return errMsg;
		
	},


	doValid :function(value,record,colObj,grid){
		if (!this.validRule && !this.validator){
			return true;
		}

		value= ( value===undefined||value===null)?this.getValue():value;
		var validResult=this.validator( value,record,colObj,grid,this);
		if ( validResult===true || validResult===undefined || validResult===null || validResult==='' ){
			return true;
		}
		return validResult;
		
	},

	active : function(){
		Sigma.U.focus(this.valueDom);
	}

	/* todo */
};


Sigma.Editor = Sigma.Widget.extend( Sigma.EditorDefault );

Sigma.DialogEditor = Sigma.Editor.extend( Sigma.$extend({

	getDom : function(){
		if (!this.dom && this.render){
			var grid=Sigma.$grid(this.gridId);
			this.render(grid.gridMask);
		}
		return this.dom;
	}

},Sigma.DialogDefault )  );
Sigma.EditDialog = Sigma.DialogEditor ;

Sigma.Calendar = window.Calendar || { trigger : Sigma.$empty} ;

Sigma.$extend(Sigma.Editor,{ 

	factroy : function(editorInfo,grid){
		if (Sigma.$type(editorInfo,'function')) {
			editorInfo=editorInfo(grid);
		}

		if ((editorInfo instanceof Sigma.DialogEditor) || (editorInfo instanceof Sigma.Dialog) ) {
			editorInfo.gridId=grid.id;
			editorInfo.container=grid.gridMask ;
			return editorInfo;
		}

		if (editorInfo instanceof Sigma.Editor) {
			return editorInfo;
		}


		editorInfo =  Sigma.$type(editorInfo,'string')? {type:editorInfo }: editorInfo ;
		return editorInfo && Sigma.Editor.EDITORS[editorInfo.type]?Sigma.Editor.EDITORS[editorInfo.type](editorInfo):null;
		
	},

	register  : function(type,editor){
		if (editor instanceof Sigma.Editor) {
			editor=function(){
				return editor;
			};
		}
		Sigma.Editor.EDITORS[type]=editor;
	},

	EDITORS : {

		text : function(editor){
			editor=new Sigma.Editor(editor);
			editor.valueDom=Sigma.$e('input',{type:'text',value:editor.defaultValue||'',className:'gt-editor-text'});
			editor.dom.appendChild(editor.valueDom);
			return editor;
		},

		textarea : function(editor){
			editor=new Sigma.Editor(editor);
			editor.valueDom=Sigma.$e('textarea',{ style :{width : editor.width||'100px' , height: editor.height||'50px'}
												,value:editor.defaultValue||'',className:'gt-editor-text'});
			editor.dom.appendChild(editor.valueDom);
			editor.dom.style.width=editor.valueDom.style.width;
			editor.dom.style.height=editor.valueDom.style.height;
			editor.setSize =Sigma.$empty;
			return editor;
		},
		select : function(editor){
			editor=new Sigma.Editor(editor);
			editor.valueDom=Sigma.U.createSelect( editor.options ,null,{ className:'gt-editor-text' });
			editor.dom.appendChild(editor.valueDom);
			editor.getDisplayValue = function(value){
				value= value===undefined?this.getValue():value;
				for (var i=0; i<this.valueDom.options.length; i++) {
					if ( String(this.valueDom.options[i].value)===String(value) ) {
						return this.valueDom.options[i].text || this.valueDom.options[i].innerHTML;
					}
				}
				return (this.defaultText || this.defaultText==='')?this.defaultText : null;
			};
			return editor;
		},
		checkbox : function(editor){
			editor=new Sigma.Editor(editor);
			editor.valueDom=Sigma.U.createSelect( editor.options,null ,{});
			editor.dom.appendChild(editor.valueDom);
			return editor;
		},
		/*
		pop : function(editor){
			editor=new Sigma.Editor(editor);
			editor.dom=Sigma.$e('div',{ 
				style: {
					width : editor.width,
					height : editor.height;
				}
			});
			if (editor.core){
				editor.dom.innerHTML='<input id="test_value" type="text" />';
			}			

			editor.valueDom=Sigma.$e('input',{type:'text',value:editor.defaultValue||'',className:'gt-editor-text'});
			editor.dom.appendChild(editor.valueDom);

			editor.locked=true;
			var grid=Sigma.$grid(editor.gridId);
			editor.setSize=Sigma.$empty;
			editor.dom.style.overflow='hidden';
			editor.dom.appendChild(input);
		},
		*/
		date : function(editor){
			editor=new Sigma.Editor(editor);
			var input=Sigma.$e('input',{type:'text',value:editor.defaultValue||'',className:'gt-editor-text',style:{width:'78px',styleFloat :'left'}});
			var button=Sigma.$e('input',{type:'button',value:editor.defaultValue||'',className:'gt-editor-date',styleFloat :'left'});
			editor.dom.style.overflow='hidden';
			editor.dom.appendChild(input);
			editor.dom.appendChild(button);

			editor.setSize = function(w,h){
				this.width=w||this.width;
				this.height=h||this.height;
				if (this.width/1 && this.width>0) {
					this.dom.style.width= ( this.width-1)+'px';
				}
				if (this.height/1 && this.height>0) {
					this.dom.style.height= ( this.height-1)+'px';
				}
				this.dom.firstChild.style.width= ( this.width-20)+'px';
			};


			var fillDate=function(calObj){
				editor.onClose && editor.onClose();
				calObj.hide();
			};

			var showCalendar=function(){
				var format= editor.format ||  "%Y-%m-%d";
				format=Sigma.U.replaceAll(format,"yyyy","%Y");
				format=Sigma.U.replaceAll(format,"MM","%m");
				format=Sigma.U.replaceAll(format,"dd","%d");
				format=Sigma.U.replaceAll(format,"HH","%H");
				format=Sigma.U.replaceAll(format,"mm","%M");
				format=Sigma.U.replaceAll(format,"ss","%S");
				Sigma.Calendar.trigger({
					inputField     :    input,			// id of the input field
					ifFormat       :    format,       // format of the input field
					showsTime      :    true,            // will display a time selector
					button         :    "date_button",   // trigger for the calendar (button ID)
					singleClick    :    true, 
					onClose	: fillDate,
					step           :    1                // show all years in drop-down boxes (instead of every other year as default)
				});

			};
			Sigma.U.addEvent(button,'click',showCalendar);

			editor.valueDom=input;
			return editor;
		}

	}


});



//

