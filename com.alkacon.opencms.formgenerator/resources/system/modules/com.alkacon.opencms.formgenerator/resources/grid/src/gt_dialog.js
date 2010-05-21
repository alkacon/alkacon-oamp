
/////////////////////////////////////////////////


Sigma.DialogDefault = {
	hasCloseButton : true,
	autoRerender : true,
	title : null,
	body : null,

	buttonZone : null,
	

	headHeight : 20,

	hidden : false ,

	initialize : function(options){
		if (options) {
			Sigma.$extend(this,options);
		}
		this.domId=(this.gridId?this.gridId+'_' : '') + this.id;
		
		this.buttonLayout = this.buttonLayout || 'h';
		this.dialogId = this.id ;
		Sigma.WidgetCache[this.id]=this;

	},
	
	destroy : function(){
		this.container=null;
		Sigma.U.removeNode(this.bodyDiv);
		this.bodyDiv=null;
		Sigma.U.removeNode(this.dom);
		this.dom=null;
		Sigma.WidgetCache[this.id]=null;
		delete Sigma.WidgetCache[this.id];
	},	

	titleRender : function(title){
		this.title = title || this.title;
		return this.title;

	},

	show : function(){
		var grid=Sigma.$grid(this.gridId);
		grid.closeGridMenu();
		if ( Sigma.$invoke(this,'beforeShow',[grid])!==false ){
			//todo 
			/*
			if (Sigma.isIE) {
				Sigma.eachGrid(function(g){
					if (g!=grid){
						g.gridDiv.style.position=g.viewport.style.position="static";
						g.toolBarBox && (g.toolBarBox.style.position="static");
					}
				});	
				
			}
			*/
			this.locked=true;
			grid.showMask();
			this.autoRerender && this.render(grid.gridMask);
			grid.gridMask.appendChild(this.dom );
			if (this.width/1 && this.width>0) {
				this.dom.style.marginLeft=(0-this.width/2)+'px';
			}
			this.dom.style.marginTop='0px';
			this.dom.style.top='25px';
			this.dom.style.display="block";
			grid.activeDialog=this;
			this.hidden=false;
			Sigma.$invoke(this,'afterShow',[grid]);
		}
	},
	
	hide : function(){
		var grid=Sigma.$grid(this.gridId);
		if ( Sigma.$invoke(this,'beforeHide',[grid])!==false ){
			this.locked=false;
			grid.hideMask();
			if (this.dom) {
				this.dom.style.display="none";
				grid.gridEditorCache.appendChild(this.dom );
			}
			grid.activeDialog=null;
			this.hidden=true;
			//todo 
			/*
			if (Sigma.isIE) {
				Sigma.eachGrid(function(g){
					if (g!=grid){
						//todo 
						g.gridDiv.style.position=g.viewport.style.position="relative";
						g.toolBarBox && (g.toolBarBox.style.position="relative");
					}
				});
			}
			*/
			Sigma.$invoke(this,'afterHide',[grid]);
		}
		

	},

	close : function(){
		var grid=Sigma.$grid(this.gridId);
		this.hide();
	},

	confirm : function(){
		var grid=Sigma.$grid(this.gridId);
		if (grid.activeEditor==this) {
			this.locked=false;
			grid.endEdit();
			grid.activeEditor=this;
		}

	},
 
	render : function(container){
		this.container=container || this.container;
		if (!this.rendered) {
			this.dom= this.dom || Sigma.$e('div' ,{className:'gt-grid-dialog'} );
			this.dom.id= this.domId + '_dialog';
			this.container = this.container || Sigma.doc.body;
			this.container.appendChild(this.dom);
			this.dom.innerHTML=Sigma.T_D.create(this);
			this.titleDiv = Sigma.$(this.domId+'_dialog_title');
			this.bodyDiv = Sigma.$(this.domId+'_dialog_body');
			if (this.height) {
				this.bodyDiv.style.height=this.height-(this.headHeight||0) + 'px';
			}
			this.setBody();
			this.setButtons();
			this.setTitle();
			Sigma.$invoke(this,'afterRender',[this]);
		}

		this.setSize();
		this.setPosition();
		if (Sigma.$type(this.valueDom,"function")){
				this.valueDom=this.valueDom();
			}
		this.valueDom=Sigma.$(this.valueDom);

		this.rendered=true;
	},


	setBody : function(body){
		var grid=Sigma.$grid(this.gridId);
		this.body=body||this.body;
		this.bodyDiv.innerHTML="";
		if ( Sigma.$type(this.body,'function')) {
			this.body=this.body(grid);
		}
		if (!this.body) {
			
		}else if ( Sigma.$type(this.body,'string')) {
			this.bodyDiv.innerHTML= this.body;
		}else{
			this.bodyDiv.appendChild(this.body);
		}
	},

	setButtons : function(buttons){
		this.buttons= buttons || this.buttons;
		if (!this.buttons){
			return;
		}
		buttons=[].concat(this.buttons);
		if (buttons.length>0){
			this.buttonZone = this.buttonZone || Sigma.$e("div", {className :'gt-dialog-buttonzone-'+this.buttonLayout, id :this.domId+'_div' } );
			if (this.buttonLayout=='h'){
				this.buttonZone.style.width=this.width-12+'px';
			}

			for (var i=0;i<buttons.length;i++ ){
				var btn=null;
				if (buttons[i].breakline){
					btn = Sigma.$e("br");
				}else if (buttons[i].html){
					btn = Sigma.$e("span" , { innerHTML : buttons[i].html });
				}else{
					btn=Sigma.$e("button", { id :this.domId+'_'+buttons[i].id ,className :'gt-input-button', innerHTML : buttons[i].text } );
					Sigma.U.addEvent( btn ,"click",buttons[i].onclick );
				}
				this.buttonZone.appendChild( btn );				
			}
		}
		this.bodyDiv.appendChild(this.buttonZone);
	},

	setTitle : function(title){
		this.titleDiv.innerHTML= this.titleRender(title);
	}
};

Sigma.Dialog = Sigma.Widget.extend( Sigma.DialogDefault );



//////////////////////

Sigma.createColumnTable=function(grid,chkCfg){

	grid=Sigma.$grid(grid);
	chkCfg = chkCfg || {};
	chkCfg.checkType = chkCfg.checkType || 'checkbox';
	chkCfg.canCheck = chkCfg.canCheck || function(col) { return !col.hidden; } ;
	function createCheck(_cfg,r){
		var canCheck= _cfg.canCheck===true  || _cfg.canCheck(r)!==false ;
		return '<input type="'+_cfg.checkType+'" name="'+ _cfg.name+'" value="'+_cfg.value(r)+'" class="gt-f-check" '
			+ (_cfg.checked(r)?' checked="checked" ':'')
			+ (!canCheck?' disabled="disabled" ':'')
			+' />';
	}

	function createCheckHD(_cfg){
		return _cfg.checkType=='checkbox'? '<input type="checkbox" name="'+_cfg.name+'" class="gt-f-totalcheck" />'
				: '<input type="radio" name="'+_cfg.name+'" />';
	}

	var cl=grid.columnList;
	var h=[ '<table class="gt-table" style="margin-left:0px" cellSpacing="0"  cellPadding="0" border="0" >',
			'<col style="width:25px" /><col style="width:105px" />',
			'<thead>',
			Sigma.T_G.rowStart(grid,0),
			Sigma.T_G.cellStartHTML,
			createCheckHD(chkCfg ),
			Sigma.T_G.cellEndHTML,
			Sigma.T_G.cellStartHTML, grid.getMsg('COLUMNS_HEADER'), Sigma.T_G.cellEndHTML,
			Sigma.T_G.rowEndHTML,
			'</thead>',
			'<tbody>'
		];
	for (var i=0;i<cl.length; i++ ){
			h.push( [
						Sigma.T_G.rowStart(grid,i),
						Sigma.T_G.cellStartHTML,
						createCheck(chkCfg,cl[i]),
						Sigma.T_G.cellEndHTML,
						Sigma.T_G.cellStartHTML, cl[i].header ||cl[i].title  , Sigma.T_G.cellEndHTML,
						Sigma.T_G.rowEndHTML
				].join('')
			);
	}
	h.push( '</tbody></table>' );
	return h.join("\n");

};

Sigma.checkChecked = function(grid){
	grid=Sigma.$grid(grid);

	var chkAll=  grid.chkAll; //Sigma.$('g1_chk');

	var htd=Sigma.U.getParentByTagName('td',chkAll); 

	var cellIdx=Sigma.U.getCellIndex(htd);	



	var rows=grid.getAllRows();
	var cno=0;
	for (var i=0,j=rows.length;i<j; i++ ){
		var cell=rows[i].cells[cellIdx];
		if (cell){
			var _chk=cell.getElementsByTagName('input')[0];	
			if (_chk && grid.checkedRows[_chk.value]){
				_chk.checked=true;
				cno++;
			}
		}
	}
	chkAll.checked= cno==rows.length ;
};


Sigma.createColumnDialog = function(dType,cfg){

	var checkName= dType+'ColCheck';
	var gridId=cfg.gridId;
	var dialogId= gridId+'_'+dType+'ColDialog';
	var grid=Sigma.$grid(gridId);
	var okFn= function(){

		var colDiv=Sigma.$( dialogId+'_div');
		var tableObj= (Sigma.U.getTagName(colDiv)=='TABLE')?colDiv: colDiv.getElementsByTagName('table')[0];
		var tbodyObj=tableObj.tBodies[0];
		var inputs= tbodyObj.getElementsByTagName('input');

		var rs=Sigma.U.getCheckboxState(inputs,checkName);
		var gids=[],i;
		for (i=0;i<grid.columnList.length;i++ )	{
			gids.push(grid.columnList[i].id);
		}
		for (i=0;i<gids.length;i++ ){
			var col=grid.columnMap[gids[i]];
			if (rs[col.id]){
				col[cfg.checkFn]();
			}else{
				col[cfg.uncheckFn]();
			}
		}

		if (cfg.autoClose!==false){
			grid._onResize();
			Sigma.WidgetCache[dialogId].close();
		}

	};

	var cancelFn= function(){ Sigma.WidgetCache[dialogId].close(); };

	var dialog=new Sigma.Dialog({
		id:  dialogId,
		gridId : gridId ,
		title : cfg.title,
		width  :  260,
		height  :  220 ,
		buttonLayout : 'v',
		body : [
			'<div id="'+dialogId+'_div'+'" onclick="Sigma.clickHandler.onTotalCheck(event)" class="gt-column-dialog" >',
			'</div>'
		].join(''),
		buttons : [
				{ text : grid.getMsg('TEXT_OK') , onclick : okFn },
				{ text : grid.getMsg('TEXT_CLOSE') , onclick : 	cancelFn }
			],
		afterShow : function(){
			var grid=Sigma.$grid(this.gridId);
			var tt=Sigma.createColumnTable(this.gridId, {
					type : "checkbox",
					name : checkName,
					value :  function(r){ return r.id; } ,
					checked : cfg.checkValid,
					checkType : cfg.checkType,
					canCheck : cfg.canCheck
				});
			Sigma.$(this.id+'_div').innerHTML=tt;
		}

 		});

	return  dialog;
};


//////////////////////////////////

Sigma.createFilterDialog = function(cfg){

	var gridId=cfg.gridId;
	var grid=Sigma.$grid(gridId);
	var dialogId= gridId+'_filterDialog';

	// 
	grid.justShowFiltered= cfg.justShowFiltered===true?true :(cfg.justShowFiltered===false?false:grid.justShowFiltered) ;
	grid.afterFilter = cfg.afterFilter || grid.afterFilter  ;

	var addFn= function(){
		if (grid._noFilter){
			clearFn();
			grid._noFilter=false;
		}
		var colSelect=Sigma.$(dialogId+'_column_select');
		if (colSelect && colSelect.options.length>0){
			var cid= colSelect.value;
			var cname=colSelect.options[colSelect.selectedIndex].text;
			Sigma.$(dialogId+'_div').appendChild(Sigma.createFilterItem(grid,cid,cname));
		}

	};

	var clearFn= function(){
		Sigma.$(dialogId+'_div').innerHTML=  '';
	};

	var okFn= function(){
		var colDiv=Sigma.$( dialogId+'_div');

		var filterInfo=[];

		var items= colDiv.childNodes;
		for (var i=0;i<items.length;i++ ){
			if (Sigma.U.getTagName(items[i])=="DIV" && items[i].className=='gt-filter-item' ){
				var colS=items[i].childNodes[1];
				var condS=items[i].childNodes[2];
				var f=items[i].childNodes[3].firstChild;
				var cid=Sigma.U.getValue(colS);
				var colObj = grid.columnMap[cid];
				if (colObj && colObj.fieldName){
					filterInfo.push( {
						columnId : cid ,
						fieldName : colObj.fieldName ,
						logic : Sigma.U.getValue(condS),
						value : Sigma.U.getValue(f)
					} );
				}
			}
		}
		if (filterInfo.length>0){
			var rowNos=grid.applyFilter(filterInfo);
		}else{
			grid.applyFilter([]);
		}

		if (cfg.autoClose!==false){
			grid._onResize();
			Sigma.WidgetCache[dialogId].close();
		}

	};

	var cancelFn= function(){ Sigma.WidgetCache[dialogId].close(); };

	var outW=430 , outH=220;
	var inW= outW-(Sigma.isBoxModel?16:18) , inH= outH-(Sigma.isBoxModel?93:95);
	var dialog=new Sigma.Dialog({
		id:  dialogId,
		gridId : gridId ,
		title : cfg.title,
		width  :  outW,
		height  :  outH ,
		buttonLayout : 'h',
		body : [
			'<div id="'+dialogId+'_div" class="gt-filter-dialog" style="width:'+inW+'px;height:'+inH+'px;" onclick="Sigma.clickHandler.onFilterItem(event)" >',
			'</div>'
		].join(''),
		buttons : [
				{ html : Sigma.createColumnSelect(grid,dialogId+'_column_select') },
				{ text : grid.getMsg('TEXT_ADD_FILTER') , onclick : addFn },
				{ text : grid.getMsg('TEXT_CLEAR_FILTER') , onclick : clearFn },
				{ breakline : true },
				{ text : grid.getMsg('TEXT_OK') , onclick : okFn },
				{ text : grid.getMsg('TEXT_CLOSE') , onclick : cancelFn }
			],
		afterShow : function(){
			var grid=Sigma.$grid(this.gridId);
			var filterInfo=grid.filterInfo||[];
			clearFn();
			for (var i=0;i<filterInfo.length;i++ ){
				var cid=filterInfo[i].columnId;
				var col=grid.getColumn(cid);
				var cname=(col.header||col.title);
				var tt= Sigma.createFilterItem(grid,cid,cname);
				var colS= tt.childNodes[1];
				var condS= tt.childNodes[2];
				var f= tt.childNodes[3].firstChild;
				Sigma.U.setValue(colS,cid);
				Sigma.U.setValue(condS,filterInfo[i].logic);
				Sigma.U.setValue(f,filterInfo[i].value);
				Sigma.$(this.id+'_div').appendChild(tt);
			}
			if (filterInfo.length<1){
				Sigma.$(this.id+'_div').innerHTML='<div style="color:#999999;margin:10px;">'+grid.getMsg('DIAG_NO_FILTER')+'</div>';
				grid._noFilter=true;
			}
		}

 		});

	return  dialog;
};


Sigma.createColumnSelect = function(grid,id){
	grid=Sigma.$grid(grid);
	var colSelect= ['<select'+ (id?(' id="'+id+'" '):' ')+' class="gt-input-select">'];
	for (var i=0;i<grid.columnList.length;i++ )	{
		var col=grid.columnList[i];
		if (col&& col.filterable!==false){
			colSelect.push('<option value="'+col.id+'" >'+(col.header||col.title)+'</option>');
		}
	}
	colSelect.push('</select>');
	return colSelect.join('');
};


Sigma.createFilterField = function(grid,cid){
	grid=Sigma.$grid(grid);
	var colObj=grid.getColumn(cid),field;
	if ( typeof colObj.filterField == 'function'){
		field= colObj.filterField(colObj);
	}else if(colObj.filterField){
		field= colObj.filterField;
	}
	field= field ||  '<input type="text" class="gt-input-text gt-filter-field-text" value="" />';
	return '<div class="gt-filter-field-box">'+field+'</div>';
};

Sigma.createFilterItem = function(grid,cid,cname){
	grid=Sigma.$grid(grid);
	var div=Sigma.$e('div' , {className : 'gt-filter-item' });

	//var colInfo =Sigma.createColumnSelect(grid);
	var colInfo='<input type="text" readonly="readonly" class="gt-input-text gt-filter-col-text" value="'+cname+'" />';
		colInfo+='<input type="hidden"  value="'+cid+'" />';
	
	var condInput= Sigma.createFilterField(grid,cid);
	var fButton= '<button class="gt-input-button gt-filter-del" >'+grid.getMsg('TEXT_DEL')+'</button>'
				+'<button class="gt-input-button gt-filter-up" >'+grid.getMsg('TEXT_UP')+'</button>'
				+'<button class="gt-input-button gt-filter-down" >'+grid.getMsg('TEXT_DOWN')+'</button>';

	div.innerHTML= colInfo + Sigma.T_D.filterCondSelect + condInput + fButton;
	return div;
};


/////////////////////////////////////////////////

Sigma.clickHandler ={

	currentElement : null,

	onFilterItem : function(evt){
			evt=evt || window.event;
			var et= Sigma.U.getEventTarget(evt);
			var tableObj = Sigma.U.getParentByTagName('table',null,evt,10);

			if ( Sigma.U.getTagName(et)=='BUTTON' ){
				var className=' '+et.className;
				var item=et.parentNode;
				if (className.indexOf(' gt-filter-del')>=0 ){
					Sigma.U.removeNode(item);
				}else if (className.indexOf(' gt-filter-up')>=0 ){
					var p_item=item.previousSibling;
					if(p_item){
						item.parentNode.insertBefore(item,p_item); 
					}
				}else if (className.indexOf(' gt-filter-down')>=0 ){
					var n_item=item.nextSibling;
					if(n_item){
						item.parentNode.insertBefore(n_item,item); 
					}
				}
			}

		},

	onTotalCheck : function(evt){
			evt=evt || window.event;
			var et= Sigma.U.getEventTarget(evt);
			var tableObj = Sigma.U.getParentByTagName('table',null,evt,10);

			if (!et || ( et.type!='checkbox' &&  et.type!='radio' ) ){
				return;
			}

			if ( Sigma.U.hasClass(et,'gt-f-totalcheck') ){

				var tbodyObj=tableObj.tBodies[0];
				var inputs= tbodyObj.getElementsByTagName('input');

				for (var i=0;i<inputs.length ;i++ ){
					if (inputs[i].name==et.name && inputs[i].type==et.type){
						inputs[i].checked=et.checked;
					}
				}
			}else if ( Sigma.U.hasClass(et,'gt-f-check') ){
				var theadObj=tableObj.tHead;
				var tinput=theadObj.getElementsByTagName('input')[0];
				if (tinput){
					tinput.checked=false;
				}
			}
		}

};

//
