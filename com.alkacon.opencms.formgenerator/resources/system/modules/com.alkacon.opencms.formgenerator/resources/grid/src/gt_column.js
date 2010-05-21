//

/**
  * @description {Class} Column
  * This class represent one column of a grid.
  * @inherit Object
  * @namespace Sigma
  */
Sigma.ColumnDefault =  {
	/**
     * @description {Config} chartColor
     * {String} To specify color of this column in chart. 
     * @sample
     * chartColor: 'eecc99'
     */
	/**
     * @description {Config} header
     * {String} To specify caption of this column. 
     */
	/**
     * @description {Config} inChart
     * {Boolean} To specify whether value of this column will appear in chart or not.
     */
	CLASS_PREFIX : '.' , // '.'

	destroyList : [ "sortIcon", "hdTool", "menuButton", "separator", "frozenSortIcon", "frozenHdTool","frozenHeadCell", "headCell", "firstCell" ],
    /**
     * @description {Config} id
     * {String} Id of column.
     */
	id : 0, 
	fieldName : null,
    /**
     * @description {Config} width 
     * {Number} Width of column in pixel. Percentage not supported.
     */
	width: 120,
	/**
     * @description {Config} minWidth
     * {Number} To specify minimum column width when user resizes column.
     */
	minWidth : 45 ,

	header: null,
    
	/**
     * @description {Config} styleClass
     * {String} To specify css style of the column.
     */
	styleClass : null,
    /**
     * @description {Config} align
     * {String} Alignment of this column. Could be "left", "right", "center". Set to 'left' by default. 
     */
	align : 'left' ,
	/**
     * @description {Config} headAlign
     * {String} Alignment of this column header. Could be "left", "right", "center". Set to 'left' by default. 
     */
	headAlign : 'left' ,
	/**
     * @description {Config} emptyText
     * {String} To specify what to show in case of null on this column.
     */
	emptyText : '',
    /**
     * @description {Property} sortable
     * {Boolean} To specify whether end user can sort grid by clicking this column header
     * @config
     */
	sortable: true ,
	/**
     * @description {Property} resizable
     * {Boolean} To specify whether end user can resize this column.
     * @config
     */
	resizable: true,
	/**
     * @description {Property} moveable
     * {Boolean} To specify whether end user can move this column by dragging it to some place.
     * @config
     */
	moveable: true,
	/**
     * @description {Property} editable
     * {Boolean} To specify whether end user can edit cells of this column.
     * @config
     */
	editable : true,
	/**
     * @description {Property} hideable
     * {Boolean} To specify whether end user can show/hide this column via main menu.
     * @config
     */
	hideable : true,
	/**
     * @description {Property} freezeable 
     * {Boolean} To specify whether end user can freaze this column via main menu..
     * @config
     */
	freezeable : true,
	groupable: true,
	/**
     * @description {Property} filterable
     * {Boolean} To specify whether end user can take this colum as a criteria
     * @config
     */
	filterable : true,
    /**
     * @description {Config} printable
     * {Boolean} To specify whether this column will be printed out. Set to true by default.
     * @config
     */
	printable : true,
	exportable : true,
    /**
     * @description {Config} sortOrder
     * {String} Could be 'asc', 'desc' or null.
     */
	sortOrder : null,
	enableDefaultSort : false ,
	
	/**
	 * @description {Property} hidden
	 * {Boolean} Whether column is hidden initially
	 */
 	hidden  : false,
	/**
	 * @description {Property} frozen
	 * {Boolean} Whether column is frozen initially
	 */
	frozen : false,
	/**
     * @description {Config} toolTip
     * {Boolean} To specify whether tip pop should show up on this column.
     */
	toolTip: false,
	
	beforEdit : null,
	afterEdit : null,
    
	/**
     * @description {Property} renderer
     * {Function} Cell renderer .
     * @param {Any} value Value of cell. 
     * @param {record} Data record of row. 
     * @param {Object} colObj Column object.
     * @param {Object} grid Grid object.
     * @param {Number} colNo Column number.
     * @param {Number} rowNo Row number.                    
     * @return HTML code for cell.
     * @sample
     * var colsConfig = [
     *         { id : 'score'   , header : 'Score' , width : 70 ,
     *             renderer : function(value ,record,columnObj,grid,colNo,rowNo){
     *                    if (value>4) {
     *                         total = '<span style="color:green" >'+ total +'</span>';
     *                    }else if (value<3) {
     *                         total = '<span style="color:red" >'+ total +'</span>';
     *                    }
     *                    return '<span style="color:yellow" >'+ total +'</span>';
     *             }
     *         }
     * ];  
     *                 
     */
	renderer : function(value,record,col,grid,colNo,rowNo){
		return value!==null&&value!==undefined? value : col.emptyText;
	},

	hdRenderer : function(header,cobj){
		return header;
	},
    /**
     * @description {Config} editor
     * {Object} To specify an editor for this column. See Sigma.Column.Editor
     */
	editor : null,

	fieldIndex: 0,  // field.index
	gridId : null,

/* todo */
	filterField : null, 
	newValue : null,
	cellAttributes : '',
	/**
     * @description {Property} getSortValue
     * {Function} To specify comparable value.
     * @param {Any} value Value of cell. 
     * @param {record} Data record of row.   
     * @return Compare result. 
     * @sample
     * var colsConfig = [
     *         { id : 'score', header : "Score" , width : 100
     *             getSortValue : function(value , record){
     *                  var goals = value.split(":");
     *                  return Number(goals[0]) - Number(goals[1]);
     *             }        
     *         }
     * ];                     
     */
	getSortValue : null,
	/**
     * @description {Property} sortFn
     * {Function} This function is for developer to define how to sort record.
     * @param {Object or Array} r1 Data record of row1.
     * @param {Object or Array} r1 Data record of row2.                    
     * @return {Number} Return 1 if r1 is geater than r2; return -1 if r2 is greater than r1; return 0 if r1 is equal to r2.        
     */
	sortFn : null,

/* todo */
	format : null ,
	syncRefresh : true ,
	expression : null,
	isExpend:false,

	initialize : function(options,idx){
		var Me=this;

		if (Sigma.$type(options,'string')){
				this.id=options;
		}else{
			Sigma.$extend(this,options);
		}

		this.id = this.id || encodeURIComponent(this.header) ;
		this.header = this.header || this.id;
		// todo 
		this.fieldName = this.fieldName ||  this.fieldIndex || this.id;
		this.fieldIndex = this.fieldIndex || this.fieldName  || this.id;
		this.CLASS_PREFIX = '.gt-grid ' + this.CLASS_PREFIX;
	},

	destroy : function(){
	
		if (this.editor && this.editor.destroy) {
			this.editor.destroy();
		}
		this.editor=null;

		Sigma.$each(this.destroyList,function(k,i){
			Sigma.U.removeNode(this[k]);
			this[k]=null;
		});

	},

    /**
     * @description {Method} getColumnIndex To get index of this column.
     * @return {Number} Index of this column
     */
	getColumnIndex :function(){
		return this.colIndex; //this.headCell.cellIndex;

	},

    /**
     * @description {Method} setWidth To set column width.
     * @param {Number} newWidth Column new width in pixel.
     */
	setWidth : function(newWidth){
		var grid=this.grid;
		newWidth= newWidth<this.minWidth?this.minWidth:newWidth;
		this.width= newWidth +'px';
		Sigma.U.CSS.updateRule(this.CLASS_PREFIX + this.styleClass ,'width',(newWidth + grid.cellWidthFix)+ 'px' );
		Sigma.U.CSS.updateRule(this.CLASS_PREFIX + this.innerStyleClass ,'width',(newWidth + grid.innerWidthFix)+'px' );
	},

	/**
	 * @description {Method} setHeader To update header inner html.
	 * @param {String} header Header inner html
	 */
	setHeader : function(header){
		this.header=header;
		var div=this.headCell.getElementsByTagName('div')[0];
		if (div){
			var span=div.getElementsByTagName('span')[0]||div;
			span.innerHTML=header;
		}
	},
	
	rerender : function(){
		if (!this.grid.renderHiddenColumn) {
			this.grid.kickHeader();
			this.grid.refresh();
		}
	},
	
	/**
	 * @description {Method} hide To hide this column.
	 */
	hide : function(){
		if ( this.frozen  ){
			return false;
		}
		Sigma.U.CSS.updateRule(this.CLASS_PREFIX + this.styleClass,'display','none');
		this.hidden=true;
		this.rerender();
	},
	
	/**
	 * @description {Method} show To show this column.
	 */
	show : function(){
		if ( this.frozen ){
			return false;
		}
		Sigma.U.CSS.updateRule(this.CLASS_PREFIX + this.styleClass,'display','');
		this.hidden=false;
		this.rerender();
	},

    /**
     * @description {Method} toggle To show/hide this column. 
     */
	toggle : function(){
		return this.hidden?this.show():this.hide();
	},

	/**
	 * @description {Method} group To group or ungroup records by value of this column
	 * @param {Boolean} grouped. Group or ungroup.
	 */
	group : function(grouped){
		if(grouped !== false) {
			grouped = true;
		}
		this.grouped=grouped;
		this.grid.refresh();
	},
	
	//for lagcy
	ungroup: function(){
		this.group(false);
	},

	freezeCell : function(row, freezeTable ,freezeRow,rowNo,colNo,cellTemplate,grid,isHead){
		if (!grid.hasIndexColumn){
			freezeRow= row.cloneNode(false);
			freezeRow.id="";
			freezeRow.appendChild(cellTemplate.cloneNode(true));
			freezeTable.appendChild(freezeRow);
		}
		var colL=row.cells[colNo].cloneNode(true);
		freezeRow.appendChild(colL);
		if (isHead && rowNo===0){
			this.frozenHeadCell = colL;
			this.frozenSortIcon = Sigma.Grid.getSortIcon(this,this.frozenHeadCell);
			this.frozenHdTool = Sigma.Grid.getHdTool(this,this.frozenHeadCell);
			if (!Sigma.isIE){
				Sigma.Grid.initColumnEvent(grid,this,this.frozenHeadCell,this.frozenSortIcon);
			}
		}

	},

    /**
     * @description {Method} freeze To freeze this column.
     */
	freeze : function(always){
		var grid=this.grid;

		var colNo=this.getColumnIndex();
		if ( !always && colNo <grid.frozenColumnList.length ){
			return false;
		}
		var rows=grid.headTable.tBodies[0].rows;
		var freezeRows=grid.freezeHeadTable.tBodies[0].rows;

		var headCellTemplate,cellTemplate,i;
		var indexColumnWidth=10;
			if (!grid.hasIndexColumn){
				headCellTemplate=Sigma.T_G.freezeHeadCell(grid,indexColumnWidth,null);
				cellTemplate=Sigma.T_G.freezeBodyCell(grid,indexColumnWidth,null);
				//grid.tableMarginLeft = indexColumnWidth + 2 ; 
			}

		for (i=0;i<rows.length ;i++ ){
			this.freezeCell(rows[i], grid.freezeHeadTable.tBodies[0],freezeRows[i],i,colNo,headCellTemplate,grid,true);
		}

		if (grid.rowNum<1){
			//return;
		}

		grid.isEmptyfreezeZone=false;
		if (grid.overRow){
			grid.overRow.className=grid.overRow.className.replace(" gt-row-over",'');
		}

		rows=grid.getAllRows();
		freezeRows=grid.freezeBodyTable.tBodies[0].rows;

		for (i=0;i<rows.length ;i++ ){
			this.freezeCell(rows[i],grid.freezeBodyTable.tBodies[0],freezeRows[i],i,colNo,cellTemplate,grid);
		}

		if (!always){
			grid.moveColumn(colNo,grid.frozenColumnList.length);
			grid.frozenColumnList.push(this.id);
		}

		this.frozen=true;

		grid.freezeHeadDiv.style.display=grid.freezeBodyDiv.style.display="block";
		//grid.freezeBodyDiv.style.top=grid.freezeHeadDiv.style.height;
		grid.freezeHeadDiv.style.height= grid.headDiv.offsetHeight+"px";
		grid.freezeBodyDiv.style.height= grid.bodyDiv.clientHeight+"px";
		
		if (!grid.hasIndexColumn){
			grid.freezeHeadDiv.style.left=grid.freezeBodyDiv.style.left= 0- (indexColumnWidth + grid.cellWidthFix)+grid.freezeFixW +"px";
		}

		grid.hasIndexColumn=true;

		grid.syncScroll();

		Sigma.U.removeNode(headCellTemplate,cellTemplate);



	},

	unfreezeCell : function(freezeRows,colNoF){
		for (var i=0;i<freezeRows.length ;i++ ){
			Sigma.U.removeNodeTree(freezeRows[i].cells[colNoF]);
		}
	},

    /**
     * @description {Method} unfreeze To unfreeze this column.
     * @return {Array} Array of records.
     */
	unfreeze :  function(){

		var grid=this.grid;

		var colNo=this.getColumnIndex();

		if (!grid.frozenColumnList || colNo >= grid.frozenColumnList.length ){
			return false;
		}

		this.frozenHeadCell=this.frozenHdTool=this.frozenSortIcon=null;

		grid.moveColumn(colNo,grid.frozenColumnList.length-1);

		grid.frozenColumnList.splice(colNo,1);

		var freezeRows = grid.freezeHeadTable.tBodies[0].rows;
		this.unfreezeCell(freezeRows,colNo+1);

		if (grid.rowNum<1){
			//return;
		}

		freezeRows=grid.freezeBodyTable.tBodies[0].rows;

		this.unfreezeCell(freezeRows,colNo+1);
		
		this.frozen=false;

		if (grid.frozenColumnList.length<1 ){
			if ( !grid.showIndexColumn)	{
				grid.freezeHeadDiv.style.display= grid.freezeBodyDiv.style.display="none";
			}

		}

		grid.syncScroll();

			
	}

};


Sigma.Column=Sigma.$class( Sigma.ColumnDefault );
