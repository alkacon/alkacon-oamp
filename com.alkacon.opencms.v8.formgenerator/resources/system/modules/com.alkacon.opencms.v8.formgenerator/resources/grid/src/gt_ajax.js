//

////////////////////PageInfo Data Structure////////////////////////////
/**
  * @description {Class} PageInfo 
  * This class define data structure needed for paging. 
  * @inherit Object
  * @namespace Sigma
  */

/**
  * @description {Config} endRowNum   
  * {Number} Last row index of this page.
  */


/**
  * @description {Config} pageNum    
  * {Number} To specify index of current page.
  */


/**
 * @description {Config} pageSize   
 * {Number} To specify how many records are presented per page.
 */


/**
 * @description {Config} startRowNum   
 * {Number} First row index of this page.
 */

/**
 * @description {Config} totalPageNum   
 * {Number} To specify total number of pages.
 */
 
/**
 * @description {Config} totalRowNum   
 * {Number} To specify total number of rows.
 */

////////////////////SortInfo Data Structure////////////////////////////
/**
  * @description {Class} SortInfo 
  * This class define data structure needed for sorting a grid
  * @inherit Object
  * @namespace Sigma
  */

/**
  * @description {Config} columnId   
  * {String} Column Id.
  */

/**
  * @description {Config} fieldName    
  * {String} Specify name of field which data is sorted by.
  */

/**
 * @description {Config} sortstate   
 * {String} Could be "asc" or "desc".
 */

////////////////////FilterInfo Data Structure////////////////////////////
/**
  * @description {Class} FilterInfo 
  * This class define data structure needed for filtering a grid
  * @inherit Object
  * @namespace Sigma
  */

/**
  * @description {Config} fieldName   
  * {String} Column Id. Specify name of field which filter is apply to.
  */

/**
  * @description {Config} logic  
  * {String} Logic operator. Could be "=","!=","<",">","<=",">=","like","startWith","endWith".
  */

/**
 * @description {Config} value   
 * {Any} The target value.
 */


Sigma.AjaxDefault={
	paramName : '_gt_json'
};




Sigma.Ajax = Sigma.$class( {

	properties : function(){return {
			method: 'post',
			jsonParamName : Sigma.AjaxDefault.paramName,
			async: true,
			urlEncoded: true,
			encoding: null, //'GBK',
			mimeType: null, //'text/html',

			beforeSend: Sigma.$empty,
			onComplete: Sigma.$empty,
			onSuccess: Sigma.$empty,
			onFailure: Sigma.$empty,
			onCancel: Sigma.$empty,

			xhr : '',
			url: '',
			data: '',
			
			paramType : 'jsonString', // jsonString queryString xml

			headers: {
				'X-Requested-With': 'XMLHttpRequest',
				'Accept': 'text/javascript, text/html, application/xml,application/json, text/xml, */*'
			},

			autoCancel: false,

			evalScripts: false,
			evalResponse: false,
			responseContentType :'',

			dataUrl : false,
		
			queryParameters : null
	};},

		
	setQueryParameters : function(queryParameters){
		this.queryParameters=queryParameters;
	},


	initialize: function(options){
		options= options || {};
		if (Sigma.$type(options,'string')){
			options={ url : options };
		}

		if (!(this.xhr = this.getXHR())) { return; }
		var _header=Sigma.$extend(this.headers,options.headers);
		Sigma.$extend(this,options);
		if (this.mimeType){
			_header['X-Response-MimeType']=this.mimeType;
		}
		this.headers = _header;
	},


	send: function(options){

		this.running = true;

		if (Sigma.$type(options,'string')){
			options={ data : options };
		}
		options = Sigma.$extend({data: this.data, url: this.url, method: this.method}, options);
		var data = options.data, url = options.url, method = String(options.method).toLowerCase();

		if (Sigma.$invoke(this,'beforeSend',[this.xhr,data])===false){
			return this;
		}

		if (this.urlEncoded && method == 'post'){
			var encoding = (this.encoding) ? '; charset=' + this.encoding : '';
			this.setHeader('Content-type','application/x-www-form-urlencoded' + encoding);
		}

		switch(Sigma.$type(data)){
			case 'object': 
				if (this.paramType =='jsonString') {
					var _data=Sigma.$json(data);
					data = { };
					data[this.jsonParamName]=_data;
				}
				data = Sigma.toQueryString(data);
				break;
			default:
				// do nothing;
		}


		var _queryParameters;
		if (this.queryParameters && Sigma.$type(this.queryParameters,'object')){
			_queryParameters =  Sigma.toQueryString(this.queryParameters);
		}else if (Sigma.$type(this.queryParameters,'string')){
			_queryParameters = this.queryParameters;
		}

		if (_queryParameters && Sigma.$type(data,'string')){
			data = data + '&'+ _queryParameters;
		}

		//alert(_queryParameters)
		if ( method == 'post'){
			// todo fixed url too long. err code : 122
			var idx= url.indexOf('?');
			if (idx>=0){
				data=url.substring(idx+1)+'&'+data;
				url=url.substring(0,idx);
			}

		}else if (data && ( method == 'get' || this.dataUrl) ){
			url = url + (url.indexOf('?')>=0 ? '&' : '?') + data;
			data = null;
		}

		var _ajax=this;

		this.xhr.open(method.toUpperCase(), url, this.async);
		this.xhr.onreadystatechange = function(){
			return _ajax.onStateChange.apply(_ajax,arguments);
		};
		for (var key in this.headers ){
			try{
				this.xhr.setRequestHeader(key, this.headers[key]);
			}catch(e){

			}
		}
		//alert(data);
		this.xhr.send(data);
		if (!this.async) { this.onStateChange();}
		return this;
	},

	onStateChange: function(){
		if (this.xhr.readyState != 4 || !this.running) { return; }
		this.running = false;
		this.status = 0;
		try {
			this.status = this.xhr.status;
		}catch (e){

		}
		this.onComplete();
		if (this.isSuccess()){
			this._onSuccess();
		}else{
			this._onFailure();
		}
		this.xhr.onreadystatechange = Sigma.$empty;
	},

	isScript: function(){
		return (/(ecma|java)script/).test(this.getHeader('Content-type'));

	},


	isSuccess: function(){
		var status=this.xhr.status;
		return ((status >= 200) && (status < 300));
	},

	_onSuccess: function(){
		this.response = {
			'text': this.xhr.responseText,
			'xml': this.xhr.responseXML
		};
		this.onSuccess(this.response);
	},

	_onFailure: function(e){
		this.onFailure(this.xhr,e);
	},

	setHeader: function(name, value){
		this.headers[name]= value;
		return this;
	},

	getHeader: function(name){
		try{
			return this.xhr.getResponseHeader(name);
		}catch (e){
			return null;
		}
	},


	getXHR: function(){
		return (window.XMLHttpRequest) ? new XMLHttpRequest() : ((window.ActiveXObject) ? new ActiveXObject('Microsoft.XMLHTTP') : false);
	},

	cancel: function(){
		if (!this.running) { return this; }
		this.running = false;
		this.xhr.abort();
		this.xhr.onreadystatechange = Sigma.$empty;
		this.xhr = this.getXHR();
		this.onCancel();
		return this;
	}


} );


Sigma.JSON = {
	encode: function(obj,cP,format){
		var string;
		var br=format?'\n':'';
		switch (Sigma.$type(obj)){
			case 'string':
				return '"' + obj.replace(/[\x00-\x1f\\"]/g, Sigma.JSON.$replaceChars) + '"';
			case 'array':
				string = [];
				Sigma.$each(obj, function(item, idx){
					var json = Sigma.JSON.encode(item,cP,format);
					if (json || json===0 ) { string.push(json); }
				});
				return '['+br +   (format?string.join(','+br):string)  + ']'+br;
			case 'object':
				if (obj===null){
					return 'null';
				}
				string = [];
				Sigma.$each(obj, function(value, key){
					if (!cP || key.indexOf(cP)!==0){
						var json = Sigma.JSON.encode(value,cP,format);
						if (json) { string.push(Sigma.JSON.encode(key,cP,format) + ':' + json);}
					}
				},null,cP);
				return '{'+br + (format?string.join(','+br):string) +br+ '}'+br;
			case 'number': case 'boolean': return String(obj);
		}
		return null;
	},

	decode: function(string, secure){
		if ( !Sigma.$type(string, 'string' )|| !string.length) { return null; }
		if (secure && !(/^[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]*$/).test(string.replace(/\\./g, '@').replace(/"[^"\\\n\r]*"/g, ''))) {
			return null;
		}
		return eval('(' + string + ')');
	},

	$specialChars: {'\b': '\\b', '\t': '\\t', '\n': '\\n', '\f': '\\f', '\r': '\\r', '"' : '\\"', '\\': '\\\\'},

	$replaceChars: function(chr){
		return Sigma.JSON.$specialChars[chr] || '\\u00' + Math.floor(chr.charCodeAt() / 16).toString(16) +
					(chr.charCodeAt() % 16).toString(16);
	}

};





//