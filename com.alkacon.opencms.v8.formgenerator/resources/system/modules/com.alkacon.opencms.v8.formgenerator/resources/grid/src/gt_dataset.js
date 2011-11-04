//

Sigma.Const.DataSet={
		KEY : '__gt_ds_key__',
		INDEX : '__gt_ds_index__',

		ROW_KEY : '__gt_row_key__',

		NOT_VAILD : '__gt_no_valid__',
		
		SN_FIELD : '__gt_sn__',
		
		SORT_VALUE : '__gt_sort_value__',
		
		SORT_S : '__gt_'

};

/**
  * @description {Class} DataField 
  * This class represents one data field of a dataset
  * @inherit Object
  * @namespace Sigma
  */
 
/**
 * @description {Config} name  
 * {String} Name of field. 
 */

/**
 * @description {Config} type
 * {String} Type of field. Could be "string"(default), "int", "float".
 */

/**
 * @description {Event} initValue:function(record)
 * This function help developers define a formular cell.	 
 * @param {Object or Array} record Data record of this row.
 * @return {Any} Value of the cell.
 * @sample
 *     var dsConfig= {
 *         fields :[
 *             {name : 'price',  type: 'float'  },
 *             {name : 'units'   ,  type: 'int'  },
 *             {name : 'totalPrice'   , type: 'float',
 *                initValue : function(record){
 *                     return record['price'] * record['units'];
 *                 }
 *             }
 *         ]
 *     }; 
 *         
 */ 
 
 
/**
  * @description {Class} DataSet
  * This class contains all definitions of one dataset. 
  * @inherit Object
  * @namespace Sigma
  */
Sigma.DataSetDefault = {
	
	SEQUENCE : 0 ,
    /**
     * @description {Config} uniqueField  
     * {String or Number} Unique field name or unique field index in fields .
     */   
	uniqueField : Sigma.Const.DataSet.SN_FIELD , 
    /**
     * @description {Config} recordType  
     * {String} Record type. Could be "array" or "object".
     */
	recordType : 'object', // null , object ,array , xml xpath
	recordXpath :null,

	dataXML : null,

	currentBegin : 0 ,
	cursor : 0,

	startRecordNo : 0 ,
	
	cacheData : false,

	cacheModifiedData : true,

	modified : false,
	
	properties : function(){return {
        /**
        * @description {Config} fields  
        * {Array} Schema of data. 
        * @see Sigma.DataSet.Field
        */
		fields: [], // { name: ..., type:..., key: ... } 
		fieldsName : [],
		fieldsMap : {} ,
		fieldsInfo : {} ,
       /**
        * @description {Config} data  
        * {Array} Data behind grid.
        */     
		data : null,

		dataProxy : [],
		dataProxyBak : null,

		additional :[],
		
		sortInfo : [],
		
		queryInfo : [],


		reocrdIndex : {},

		updatedRecords : {} ,
		updatedRecordsBak : {} ,
		updatedFields : {} ,

		insertedRecords : {} ,
		deletedRecords : {} ,


		onRecordInsert : Sigma.$empty,
		onRecordUpdate : Sigma.$empty

	};},

	initialize : function(options){
		Sigma.$extend(this,options);
		this.recordType = this.recordType ||  'object';
		this.fields && this.setFields(this.fields);
		this.data && this.setData(this.data);
		this.unfieldIdx = this.uniqueField;

	},
	initValues : Sigma.$empty,
	isEqualRecord : function(record1,record2){
		for (var index in this.fieldsInfo ){
			if (record1[index]!==record2[index]){
				return false;
			}
		}
		return true;
	},
	
	clean : function(force){
		if (!this.cacheData || force===true){
			this.data=null;
			this.currentBegin=0;
			this.dataProxy=[];
		}
		this.cleanModifiedData();
	},

	cleanModifiedData : function(force){
		if (!this.cacheModifiedData || force){
			this.updatedRecords = {};
			this.updatedRecordsBak = {};
			this.updatedFields = {};
			this.insertedRecords = {};
			this.deletedRecords = {};		
		}	
	},

	setData :function(data){
		if (!data){ return false; }
		this.clean();
		return this.appendData(data);
	},


	setFields :function(fields){
			this.fields=fields ;
			this.fieldsName=[];
			var initValueFuncs=null;
			for (var i=0,len=this.fields.length;i<len;i++ ){
				var f=this.fields[i] || {};
				if (Sigma.$type(f,'string')) {
					f={name:f};
				}
				f.name= f.name || String(i);
				f.type= f.type || 'string';
				f.index = f.index || ( this.getRecordType() == 'array'? i : f.name );

				if (f.initValue){
					initValueFuncs=initValueFuncs||{};
					initValueFuncs[f.index]=f.initValue;
				}
				this.fieldsMap[f.name]=f;
				this.fieldsInfo[f.index]=f;
				this.fieldsName[i]=f.name;

			}
			if (initValueFuncs){
				this.initValues=(function(vfs){
					return function(_record,rn,dataset){
						for (var idx in vfs ){
							_record[idx]=vfs[idx](_record,rn,dataset);
						}
					};					
				})(initValueFuncs);
			}else{
				this.initValues=Sigma.$empty;
			}
			
	},

	appendData : function(data){
		if (!data){ return false; }
		this.data = this.data || [] ;
		var Me=this;
		var auk=Sigma.Const.DataSet.SN_FIELD;
		for (var i=0,len=data.length;i<len;i++){
			var record=data[i];
			record[auk]= record[auk]||this.SEQUENCE++ ;
			this.data.push(record);
			this.dataProxy.push(this.currentBegin++);
			this.initValues(record,i,this);
		}
		return true;
	},

	getDataProxySize :function(){
		return this.dataProxy.length;
	},
	resetDataProxy : function(size){
		this.dataProxy=[];
		size=size||this.getSize();
		for (var i=0; i<size; i++) {
			this.dataProxy[i]=i;
		}
	},
/* todo */
	loadData : function( loader ) {
		if (loader){
			return this.setData(loader.load()) ;
		}else{

		}
	},
	
	setRecordType : function( recordType ){
		if (recordType && this.recordType!=recordType) {
			this.recordType=recordType;
			/* todo */
			this.setFields( this.fields );
		}
	},
	
	getRecord : function(rn){
		return this.data?this.data[this.dataProxy[rn]]:null;
	},
	
	getDataRecord : function(i){
		return this.dataset.data[i];
	},


	setValueByName : function(record,fieldName,value){
		var index=this.fieldsMap[fieldName].index;
		if (Sigma.$type(record,'number')){
			record=this.getRecord(record);
		}
		record[index]=value;
	},


	getValueByName : function(record,fieldName){
		var index=this.fieldsMap[fieldName].index;
		if (Sigma.$type(record,'number')){
			record=this.getRecord(record);
		}
		return record[index];
	},

	getFields : function(){


	},



	getRecordType : function(recordType,_record){
		this.recordType = recordType ||  this.recordType ;
		if ( !Sigma.$type(this.recordType,'string') && (this.data && this.getSize()>0) ) {
			_record=this.data[0];
			if (Sigma.$type( _record,'array')) {
				this.recordType = 'array';
			}else{
				this.recordType = 'object';
			}
		}
		return this.recordType;

	},


	filterCheck : {
		equal		: function(v,cv){
				return v==cv;
			},
		notEqual	: function(v,cv){
				return v!=cv;
			},
		less		: function(v,cv){
				return v<cv;
			},

		great		: function(v,cv){
				return v>cv;
			},

		lessEqual	: function(v,cv){
				return v<=cv;
			},

		greatEqual	: function(v,cv){
				return v>=cv;
			},

		like		: function(v,cv){
				return (''+v).indexOf(cv+'')>=0;
			},

		startWith	: function(v,cv){
				return (''+v).indexOf(cv+'')===0;
			},

		endWith	: function(v,cv){
				v=v+'';
				cv=cv+'';
				return v.indexOf(cv)==v.length-cv.length;
			}

	},

	filterData : function(filterInfo){

		var Me=this;
		var dataProxy=[];
		filterInfo=[].concat(filterInfo);
		Sigma.$each(this.data,function(record,idx){
			
			var rs=true;
			for (var i=0,j=filterInfo.length; i<j; i++) {
				var index=Me.fieldsMap[filterInfo[i].fieldName].index;
				var cv=filterInfo[i].value;
				var logic=filterInfo[i].logic;
				var v=record[index] ;
				rs=Me.filterCheck[logic](v,cv);
				if (!rs) {
					break;
				}
			}
			if ( rs ) {
				dataProxy.push(idx);
			}else{

			}
		});

		return dataProxy;
	},


	insertRecord : function(record){
		/* todo */
		//this.appendData( [record]);
		var k=(this.SEQUENCE++);
		record[Sigma.Const.DataSet.SN_FIELD]=k ;
		this.insertedRecords[ k ]=record;		
		Sigma.$invoke(this,'onRecordInsert',[record]);
		this.modified=true;
	},

/* todo */
	updateRecord : function(record,fieldName, newValue){
		if (Sigma.$type(record,'number')) {
			record=this.data[record];
		}
		var sn=record[Sigma.Const.DataSet.SN_FIELD];
		var uk=record[ this.unfieldIdx ];

		var type=this.fieldsMap[fieldName].type;
		var index=this.fieldsMap[fieldName].index;
		var upRecord;
		if (!this.insertedRecords[ sn  ]){
			this.updatedRecordsBak[ uk ]= this.updatedRecordsBak[ uk ] || {};
			this.updatedRecordsBak[ uk ][index]=record[ index ];
			this.updatedRecordsBak[ uk ][ this.unfieldIdx ]=uk;
			this.updatedRecords[ uk ]= record;
		}

		
		if (this.insertedRecords[ sn  ] || Sigma.$invoke(this,'onRecordUpdate',[record,fieldName, newValue])!==false){
			if (type=='int') {
				newValue=parseInt(newValue);
				newValue=isNaN(newValue)?'':newValue;
			}else if (type=='float') {
				newValue=parseFloat(newValue);
				newValue=isNaN(newValue)?'':newValue;
			}else{
				newValue= Sigma.$chk(newValue)?String(newValue):'';
			}
			this.updatedFields[ uk ]= this.updatedFields[ uk ] || {};
			this.updatedFields[ uk ][ index ]=newValue;
			this.updatedFields[ uk ][ this.unfieldIdx ]=uk;
			
			record[ index ]= newValue;
			this.modified=true;
		}
	},


	/* todo */
	undeleteRecord : function(recordNoOrRecord){
		var recordNo=-1 ,record , recordIndex;
		if (Sigma.$type(recordNoOrRecord,'number')) {
			recordNo=recordNoOrRecord;
			if (recordNo>=0){
				recordIndex=this.dataProxy[recordNo];
				record= this.data[ recordIndex ];
			}
		}else if (recordNoOrRecord && (Sigma.$type(recordNoOrRecord,'object') || Sigma.$type(recordNoOrRecord,'array')) ){
			record=recordNoOrRecord;
			//recordNo = Sigma.$indexOf(this.data,record);
		}

		if (record)	{
			var sn=record[Sigma.Const.DataSet.SN_FIELD];
			var uk=record[ this.unfieldIdx ];
			this.deletedRecords[ uk ]=null;
			delete this.deletedRecords[ uk ];
		}
	},

	deleteRecord : function(recordNoOrRecord){
		var recordNo=-1 ,record , recordIndex;
		if (Sigma.$type(recordNoOrRecord,'number')) {
			recordNo=recordNoOrRecord;
			if (recordNo>=0){
				recordIndex=this.dataProxy[recordNo];
				record= this.data[ recordIndex ];
			}
		}else if (recordNoOrRecord && (Sigma.$type(recordNoOrRecord,'object') || Sigma.$type(recordNoOrRecord,'array')) ){
			record=recordNoOrRecord;
			//recordNo = Sigma.$indexOf(this.data,record);
		}

		if (record)	{
			var sn=record[Sigma.Const.DataSet.SN_FIELD];
			var uk=record[ this.unfieldIdx ];

			if (this.insertedRecords[ sn ]){
				/* todo */
				delete this.insertedRecords[ sn ];
				/* todo */
				//this.data[ recordNo ]= null;
			}else{
				if (this.updatedRecords[ uk ]){
					delete this.updatedRecords[ uk ];
					delete this.updatedRecordsBak[ uk ];
				}
				this.deletedRecords[ uk ]=record;
				this.modified=true;
				/* todo */
				//this.dataProxy.splice(recordNo,1);
			}

		}

	},

	addUniqueKey : function(record){
		
	},

	isInsertedRecord : function(record){
		return 	record && this.insertedRecords[ record[ Sigma.Const.DataSet.SN_FIELD ] ]==record;
	},
	isDeletedRecord : function(record){
		return 	record && this.deletedRecords[ record[ this.unfieldIdx ] ]==record;
	},
	isUpdatedRecord : function(record){
		return 	record && this.updatedRecords[ record[ this.unfieldIdx  ] ]==record;
	},

	
	sortFunction : null ,

	negative : function (func) {   
		return function(a,b) {   
			return 0- func(a,b);
		};
	},


//		this.dataset.sort( this.sortInfo[0].fieldName,this.sortInfo[0].sortOrder ,this.sortInfo[0].getSortValue );
//fieldName ,sortOrder , getSortValu
	sort : function( sortInfo ){
		var sortInfos=[].concat(sortInfo);
		var sortFuncs=[];

		for (var i=0; i<sortInfos.length; i++) {
			var s=sortInfos[i];
			if (s) {
				var field,type,fieldIndex;
				var isDefault=s.sortOrder.indexOf('def')===0;
				if (!s.sortOrder || isDefault){
					fieldIndex = Sigma.Const.DataSet.SN_FIELD;
					type = 'int';
				}else{
					 field = this.fieldsMap[s.fieldName];
					 if (field) {
						 fieldIndex = field.index;
						 type=field.type;
					 }
				}
				sortFuncs.push( !isDefault&&s.sortFn?s.sortFn: this.getSortFuns( fieldIndex,s.sortOrder,type,s.getSortValue));
			}
		}

		var Me=this;
		var len=sortFuncs.length;
		var multiSort=function(a,b) {   
			var r1=Me.data[a] , r2=Me.data[b];

			 for (var i=0;i<len;i++) {   
				var result = sortFuncs[i](r1,r2,sortInfos[i].sortOrder);   
				if (result!==0) { return result; }  
			 }   
			 return 0;           
		};

		this.dataProxy.sort(multiSort);
	},

	getSortFuns : function(fieldIndex, sortOrder , type,getSortValue  ){
		var Me=this;
		
		var svKey=Sigma.Const.DataSet.SORT_VALUE;

		var svCac={};
		
		var compSort = this.sortFunction;
		if (!compSort){
			var getSortValueFn= getSortValue&& sortOrder.indexOf('def')!==0?
				function(r){
					var value =r[ fieldIndex ];
					var s=getSortValue( value, r );
					svCac[r[Sigma.Const.DataSet.SN_FIELD]]=s;
					return s;
				}:function(r){
					var value = r[ fieldIndex ];
					var s=Sigma.U.convert( value, type );
					svCac[r[Sigma.Const.DataSet.SN_FIELD]]=s;
					return  s;
				};

			compSort=sortOrder=='desc'?function (r1,r2) {
				var v1= svCac[r1]||getSortValueFn(r1);
				var v2= svCac[r2]||getSortValueFn(r2);
				return v1 < v2?1:(v1 > v2?-1:0);
			}:function(r1,r2) {
				var v1= svCac[r1] ||getSortValueFn(r1);
				var v2= svCac[r2] ||getSortValueFn(r2);
				return v1 < v2?-1:(v1 > v2?1:0);
			};
		}
		return compSort;
	},

	query : function(field,filterFunc,outFilterRS,inFilterRS){


	},
	
	getSize : function(){
		return !this.data ? -1 : this.data.length;
		
	},
	/**
     * @description {Method} getFieldsNum Get number of fields which dataset contains.   
     * @return {Number} Number of fields.
     */
	getFieldsNum : function(){
		return this.fields.length;
		
	},

	sum : function(field){

	},
	avg : function(field){

	}

};

Sigma.DataSet = Sigma.$class( Sigma.DataSetDefault );

//