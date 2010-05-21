//

Sigma.Validator={
	
	hasDepend	: /^datetime|^date|^time|^minlength|^maxlength|^DT|^D|^T|^MINL|^MAXL/ ,
	hasArgument : /^equals|^lessthen|^EQ|^LT/ ,


	DATE_FORMAT : 'yyyy-MM-dd',
	TIME_FORMAT : 'HH:mm:ss',
	DATETIME_FORMAT : 'yyyy-MM-dd HH:mm:ss',

	KeyMapping	: {
		'R'     	:	'required',     
		'DT'		:	'datetime',
		'D'			:	'date',         
		'T'			:	'time',         
		'E'     	:	'email',
		'ID'    	:	'idcard',
		'N'     	:	'number',       
		'int'     	:	'integer',			
		'I'     	:	'integer',
		'F'			:	'float',
		'M'			:	'money',
		'RG'		:	'range',        
		'EQ'		:	'equals',       
		'LT'		:	'lessthen',
		'GT'		:	'greatethen',
		'U'     	:	'url',
		'ENC'		:	'enchar',
		'CNC'		:	'cnchar',
		'MINL'		:	'minlength',
		'MAXL'		:	'maxlength'

	},


	RegExpLib	: {
		'email'		: /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/ ,
		'number'	: /^\d+$/ ,
		'integer'	: /^[1-9]\d*|0$/ ,
		'float'		: /^([1-9]\d*\.\d+|0\.\d+|[1-9]\d*|0)$/ ,
		'money'		: /^([1-9]\d*\.\d{1,2}|0\.\d{1,2}|[1-9]\d*|0)$/ ,
		'telephone'	: /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,16})+$/ ,
		'enchar'	: /^[ \w]*$/ ,
		'cnchar'	: /^[\u4E00-\u9FA5\uF900-\uFA2D]*$/,
		'idcard'	: /^(\d{15}|\d{18}|\d{17}X?)$/i
	
	},
	getValidator : function(rule){
		return Sigma.Validator[rule];
	},
	getMessage : function(msgKey){
			var msg=Sigma.Msg.Validator['default'][msgKey];
			if (!msg){
				msg=Sigma.Msg.Validator['default'][Sigma.Validator.KeyMapping[msgKey]];
			}
			var _format= ((Sigma.Validator.KeyMapping[msgKey]||msgKey)+'_FORMAT').toUpperCase();
			_format=Sigma.Validator[_format];
			
			var wordNum=(' '+msg).split(/\{[0-9]/).length-1;
			for (var i=1;i<=wordNum ;i++ ){
				var ns=arguments[i];
				if(i==2){
					ns=ns||_format;
				}
				var rex;
				eval("rex = /\{("+(i-1)+"[^#\}]*)#?([^\}]*)\}/;");
				var ostring=rex.exec(msg);
				if (ostring && ostring.length>2){
					if (!ns){
						msg=Sigma.U.replaceAll(msg,ostring[0],' '+ostring[2]+' ');
						//msg=msg.replace(ostring[0],ostring[2]);
					}else{
						msg=Sigma.U.replaceAll(msg,ostring[0],' '+ns+' ');
					}
				}
			}
			return msg;

		},

	'required'	: function(values){ 
					if (values===null || values === undefined ){
						return false;
					}
					
					if (typeof(values) != 'string' && values.length){
						if (values.length<1) {return false;}

						for (var i=0;i<values.length;i++){
							var r =Sigma.Validator.required(values[i]);
							if (r) { return true;}
						}
						return false;
					}
					return Sigma.U.trim(values+'').length>0;
	
				},
	'telephone'	: function(value){
						if (!Sigma.Validator.RegExpLib.telephone.exec(value)) { return false;}
						return true;
				},
	'email'		: function(value){  
						return value && Sigma.Validator.RegExpLib['email'].test(value);			
				},
	'enchar'	: function(value){  
						return value && Sigma.Validator.RegExpLib['enchar'].test(value);			
				},
	'cnchar'	: function(value){  
						return value && Sigma.Validator.RegExpLib['cnchar'].test(value);			
				},
	'number'	: function(value){  
					 return  !isNaN(value/1);	
				},
	'integer'	: function(value){ 
						return  (String(value).indexOf('.')<0)&&!isNaN(value/1) && Sigma.Validator.RegExpLib['integer'].test(Math.abs(value));		
				},
	'float'		: function(value){ 
						return !isNaN(value/1) && Sigma.Validator.RegExpLib['float'].test(Math.abs(value));			
				},
	'money'		: function(value){ 
						return !isNaN(value/1) && Sigma.Validator.RegExpLib['money'].test(value);			
				},

	
	'idcard'	: function(value){ 
				if (!value|| value.length<15 || !Sigma.Validator.RegExpLib['idcard'].test(value)){return false;}
				var birthday;
				if (value.length==18){
					birthday=value.substr(6,8);
				}else{
					birthday='19'+value.substr(6,6);
				}
				return Sigma.Validator.date(birthday,'YYYYMMDD');
				
		},



	'date'		: function(dateValue,format){ 
					dateValue=[].concat(dateValue);

					if (!format||format.length<1){
						format=Sigma.Validator.DATE_FORMAT;
					}
					format=format.toUpperCase();

					var formatRex = format.replace(/([$^.*+?=!:|\/\\\(\)\[\]\{\}])/g, "\\$1");

					formatRex = formatRex.replace( "YYYY", "([0-9]{4})" );
					formatRex = formatRex.replace( "YY", "([0-9]{2})" );
					formatRex = formatRex.replace( "MM", "(0[1-9]|10|11|12)" );
					formatRex = formatRex.replace( "M", "([1-9]|10|11|12)" );
					formatRex = formatRex.replace( "DD", "(0[1-9]|[12][0-9]|30|31)" );
					formatRex = formatRex.replace( "D", "([1-9]|[12][0-9]|30|31)" );
					formatRex = "^" + formatRex + "$";
					var re = new RegExp(formatRex);

					var year = 0, month = 1, date = 1;

					var tokens = format.match( /(YYYY|YY|MM|M|DD|D)/g );

					for (var ii=0;ii<dateValue.length;ii++ ){
						if (!re.test(dateValue[ii])) { return false; }

						var values = re.exec(dateValue[ii]);

						for (var i = 0; i < tokens.length; i++) {
							switch (tokens[i]) {
							case "YY":
							case "yy":
								var v=Number(values[i+1]);
								year = 1900+(v<=30?v+100:v); 
								break;
							case "YYYY":
							case "yyyy":
								year = Number(values[i+1]); 
								break;
							case "M":
							case "MM":
								month = Number(values[i+1]); 
								break;
							case "D":
							case "d":
							case "DD":
							case "dd":
								date = Number(values[i+1]); 
								break;
							}
						}
						var leapyear = (year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0));
						if (date > 30 && (month == 2 || month == 4 || month == 6 || month == 9 || month == 11)) { return false; }
						if (month == 2 && ( date == 30 || date == 29 && !leapyear )  ) { return false; }

					}
					return true;
				},

	'time'		: function(timeValue,format){ 
					timeValue=[].concat(timeValue);

					if (!format||format.length<1){
						format= Sigma.Validator.TIME_FORMAT;
					}
					var formatRex = format.replace( /([.$?*!=:|{}\(\)\[\]\\\/^])/g, "\\$1");
					formatRex = formatRex.replace( "HH", "([01][0-9]|2[0-3])" );
					formatRex = formatRex.replace( "H", "([0-9]|1[0-9]|2[0-3])" );
					formatRex = formatRex.replace( "mm", "([0-5][0-9])" );
					formatRex = formatRex.replace( "m", "([1-5][0-9]|[0-9])" );
					formatRex = formatRex.replace( "ss", "([0-5][0-9])");
					formatRex = formatRex.replace( "s", "([1-5][0-9]|[0-9])");
					formatRex = "^" + formatRex + "$";
					var re = new RegExp(formatRex);
					for (var ii=0;ii<timeValue.length;ii++ ){
						if (!re.test(timeValue[ii])) { return false; }
					}
					return true;	
	
				},

	'datetime'	: function(timeValue,format){ 

						timeValue=[].concat(timeValue);

						var trex= /^\S+ \S+$/ ;
						if (!format||format.length<1){
							format=Sigma.Validator.DATETIME_FORMAT;
						}else if ( !trex.test(format) ){
							return false;
						}

						for (var ii=0;ii<timeValue.length;ii++ ){
							if (!trex.test(timeValue[ii]) ){ return false; }
							var values= timeValue[ii].split(' ');
							var fatms= format.split(' ');
							var rs=Sigma.Validator.date(values[0],fatms[0])&&Sigma.Validator.time(values[1],fatms[1]);
							if (!rs){ return false; }
						}

						return true;
					},

	'range'		: function(value,min,max){ 
						
						if (!Sigma.$chk(min)){
							return value<=max;
						}else if (!Sigma.$chk(max)){
							return value>=min;
						}
						return value>=min && value<=max ;
				},

	'equals'	: function(value,values2){ 
					values2=[].concat(values2);
					for (var i=0;i<values2.length;i++ ){
						if (value==values2){
							return true;
						}
					}
					return false;
					
				},
	'lessthen'	:  function(value,values2){

					values2=[].concat(values2);
					for (var i=0;i<values2.length;i++ ){
						if (value<=values2){
							return true;
						}
					}
					return false;
					
				},
	'greatethen'	:  function(value,values2){

					values2=[].concat(values2);
					for (var i=0;i<values2.length;i++ ){
						if (value>=values2){
							return true;
						}
					}
					return false;
					
				},

	'minlength' : function(value,lt){
					return Sigma.$chk(value) && (value+'').length>=lt;
				},
	'maxlength' : function(value,lt){
					return  Sigma.$chk(value) && (value+'').length<=lt;
				}


};
(function(){
	for (var k in Sigma.Validator.KeyMapping ){
		Sigma.Validator[k]=Sigma.Validator[Sigma.Validator.KeyMapping[k]];
	}
})();

//