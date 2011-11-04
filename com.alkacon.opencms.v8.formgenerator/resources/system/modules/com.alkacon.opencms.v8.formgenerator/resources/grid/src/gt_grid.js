//

if (!window.Sigma){
	window.Sigma={};
}

SIGMA_GRID_VER ="Sigma-Grid 2.4";

Sigma.WidgetCache ={};

Sigma.GridCache ={};
Sigma.GridNum=0;
Sigma.activeGrid = null;

Sigma.$widget =function(wid){
	return Sigma.$type(wid, 'string')?Sigma.WidgetCache[wid]:wid;
};

/**
  * @description {Class} Sigma
  * This is the root namespace.
  * @inherit Object
  */
/**
 * @description {Method} $grid
 * This function is for developer to get a grid object by id.
 * @param {Object or String} grid Grid or grid id.                    
 * @return {Object} Grid object.
 */
Sigma.$grid =function(grid){
	grid= grid || Sigma.Const.Grid.DEFAULT_ECG_ID;
	return Sigma.$type(grid, 'string' )?Sigma.GridCache[grid]:grid;
};

Sigma.destroyGrids = function(){
	Sigma.eachGrid(function(grid){
		try{ 
			grid.destroy();
		}catch(e){}
	});
};

Sigma.destroyWidgets = function(){
	for (var key in Sigma.WidgetCache){
		var widget=Sigma.WidgetCache[key];
		if (widget && widget.destroy) {
			try{ 
				widget.destroy();
			}catch(e){}
		}
	}
};

Sigma.eachGrid = function(fn){
	for (var key in Sigma.GridCache){
		var grid=Sigma.GridCache[key];
		fn(grid);
	}
};

/**
  * @description {Class} Grid
  * This class is the main class.
  * @inherit Object
  * @namespace Sigma
  */
 
Sigma.GridDefault = {
        /**
		 * @description {Config} id   
		 * {String} Id of grid. Every grid should have unique id if more than one grid appear on same page.
		 */
		id	: Sigma.Const.Grid.DEFAULT_ECG_ID,

		defaultColumnWidth : 70,
		/**
		 * @description {Config}  container
		 * {Sting or DOM} Id or DOM object which grid is built up based on. Generally, container should be div or span.  
		 */
		/**
		 * @description {Config}  customHead
		 * {String or DOM} Id, dom object or HTML code of a table, which defines layout of grid header.
		 */
		/**
		 * @description {Config} replaceContainer
		 * {Boolean} To specify whether container is placed with grid. If not, grid will be a child of container.
		 */
		/**
		 * @description {Config} pageSize  
		 * {Number} To specify how many rows presented per page.
		 */
		domList : [ 
				"gridWaiting", "separateLine", "gridMask", "gridGhost", 
				"gridFormTextarea", "gridFormFileName", "gridFormExportType", "gridForm", "gridIFrame",
				"activeDialog", "gridDialog", "gridDialogTitle", "gridDialogBody", "gridFilterRSCache", "titleBar", "toolTipDiv", 
				"freezeHeadTable", "freezeHeadDiv", "freezeBodyDiv", "freezeView",
				"resizeButton", "pageSizeSelect", "pageStateBar", "toolBar", "toolBarBox",
				"columnMoveS", "headerGhost", "headFirstRow", "bodyFirstRow", "headTable", "headDiv", "bodyDiv",
				"gridDiv", "viewport", "container"
			],

		defaultConst :	[ 'action', 'recordType', 'exportType' , 'exportFileName' ,'exception', 'parameters','queryParameters', 'data',
						'pageInfo', 'filterInfo', 'sortInfo','columnInfo',
						'fieldsName', 'insertedRecords', 'updatedRecords','updatedFields', 'deletedRecords',
						'success', 'succeedData', 'failedData','remotePaging' , 'remoteSort' , 'remoteFilter' , 'remoteGroup' ],

		/**
		 * @description {Config}  language
		 * {String} Language option.
		 * By default, messages on toolbar, on menu, of warning are in english.<br> 
		 * These words for presentation are defined in a language file named gt_msg_en.js.<br> 
		 * Developer can work out profile of other language. Take Spaish for example, developer need two steps.<br>
		 * 1. Work out language file named gt_msg_sp.js and put into the directory where gt_msg_en.js is.<br>	
		 * 2. Specify language of grid to be sp.      	 
		 */
		language : 'default',
		/**
		 * @description {Config} skin
		 * {String} Name of skin. Could be "pink", "default", "mac", "vista".
		 * Developer can also made own skin and put in /grid/skin.
		 */
		skin : 'default',

		dataPageInfo : null ,
		dataException : null ,
		formid		: null,
		/////////////////////////////////

		isNative : false ,
        /**
		 * @description {Property} loadURL
		 * {String} Url which grid data is loaded from. 
		 * A function could not be assigned to loadURL since version 2.1, 
		 * please assign records to grid by setConent. See Sigma.Grid.setContent
         * Sample - To assign a real url to loadURL.
         * loadURL:"http://localhost/server/getStudentsInfo.php"
         * @config     
		 */
		loadURL		: null,
		/**
		 * @description {Property} saveURL
		 * {String} Url which grid data is saved to. 
		 * @config
		 */
		saveURL		: null,
		exportURL	: null,
		exportType	: null,
		/**
         * @description {Property} 
         * 
         */
		exportFileName	: null,

		sortInfo : null,
        /**
         * @description {Property} 
         * {Boolean}
         */
		editable :true,
        /**
		 * @description {Property} resizable
		 * {Boolean} To specify grid be resized by end user. 
		 * If this flage to be set true, a resizing handle will appears at right-bottom corner. 
		 * End user could resize grid by dragging this handle. 
		 */
		resizable : false,

        /**
		 * @description {Property} allowCustomSkin
         * {Boolean} To specify whether skin item appear on main menu. 
         */
		allowCustomSkin : false,
		/**
         * @description {Property} allowFreeze
         * {Boolean} To specify whether [freeze columns] item appear on main menu.
         * @config
         */
		allowFreeze	: false,
		/**
         * @description {Property} allowHide 
         * {Boolean} To specify whether [hide column] item appear on main menu.
         * @config
         */
		allowHide		:false,
		/**
         * @description {Property} allowGroup
         * {Boolean} To specify whether [group column] item appear on main menu.
         * @config
         */
		allowGroup		:false,

		allowResizeColumn : true,

		simpleScrollbar : true ,
		scrollbarClass : null,

		monitorResize : false ,

		readOnly : false ,
        /**
		 * @description {Config} stripeRows
		 * {Boolean} To specify whether to show rows in stripe.
		 */ 
		stripeRows : true,
		/**
		 * @description {Property}  lightOverRow
		 * {Boolean} To specify whether to hightlight one row when mouse is hovering over it.
		 * @config
		 */
		lightOverRow : true,
		evenRowCss : 'gt-row-even',
        /**
         * @description {Property} clickStartEdit
         * {Boolean} To specify whether cell becomes editable once it is clicked. 
         * If this flag set to false, user need to click once more to edit an active cell.
         * @config
         */
		clickStartEdit	: true,
        /**
         * @description {Property} remotePaging
         * {Boolean} To specify whether server side does paging work.
         * @config
         */
		remotePaging : true,
		/**
         * @description {Property} remoteSort
         * {Boolean} To specify whether server side does sorting work.
         * @config
         */
		remoteSort : false,
		/**
         * @description {Property} remoteFilter 
         * {Boolean} To specify whether server side does filtering work.
         * @config
         */
		remoteFilter : false,
		remoteGroup : false,
		
		autoLoad : true ,
		submitColumnInfo : true ,

		autoUpdateSortState : true,
		autoUpdateEditState : true,
		autoUpdateGroupState : true,
		autoUpdateFreezeState : true ,
		autoSelectFirstRow : true ,
		autoEditNext : false ,
        /**
		 * @description {Property} submitUpdatedFields
		 * {Boolean} To specify whether only modified fields are submitted to server. 
		 * @config
		 */
		submitUpdatedFields : false , 

		autoSaveOnNav : false ,
        /**
		 * @description {Property} reloadAfterSave 
		 * {Boolean} To specify whether grid data is reloaded after data being saving.
		 * @config  
		 */
		reloadAfterSave : true,
		recountAfterSave  : true ,
		recount  : false ,
        /**
		 * @description {Config} showGridMenu
		 * {Boolean} To specify whether main menu shows up.
		 */
		showGridMenu : false,

		showEditTool   : true ,
		showAddTool    : true ,
		showDelTool    : true ,
		showSaveTool   : true ,

		showReloadTool : true ,
		showPrintTool : true ,
		showFilterTool : true ,
		showChartTool  : true ,

		showPageState : true ,
		
		/**
		 * @description {Config} showIndexColumn
		 * {Boolean} To specify whether to show row number at front of every row.
		 */
		showIndexColumn :  false,  /* todo: 1 or rowstart */
		renderHiddenColumn  : true ,
        /**
		 * @description {Property} transparentMask
		 * {Boolean} To spcify whether mask is transparent or not. False by default. 
		 * @config
		 */ 
		transparentMask : false ,

		justShowFiltered : true,
        /**
		 * @description {Config} toolbarPosition
		 * {String} Position of toolbar, could be "top", "bottom" or null.
		 */
		toolbarPosition : 'bottom',
		/**
		 * @description {Config} toolbarContent 
		 * {String} It's set to 'nav | pagesize | reload | add del save | print | filter chart | state' by default.
		 * Developer can change its order or hide some buttons. Sample
		 * To show add, delete, save buttons only. There would be a seperator between delete and save.
         * toolbarContent : "add del | save"		
		 */
		toolbarContent :  "nav | goto | pagesize | reload | add del save | print | filter chart | state" ,
		/**
		 * @description {Config}  width
		 * {Number or String} Width of whole grid. Pixel and percentage are supported. 
		 * Sample
		 * width:500		 
		 * width:'100%'		 
		 */
		width			: "100%",
		/**
         * @description {Config} height 
         * {String or Number} Height of whole grid. Pixel and percentage are supported.
         */
		height			: "100%",

        /**
		 * @description {Property}  minWidth
		 * {Number} To specify minimum width if grid can be resized by end user. 
		 * @see Sigma.Grid.resizable
		 * @config
		 */
		minWidth : 50,
		/**
		 * @description {Property}  minHeight
		 * {Number} To specify minimum height if grid can be resized by end user. 
		 * @see Sigma.Grid.resizable
		 * @config
		 */
		minHeight : 50,		

		/* todo  */
		dataRoot : 'data' ,
		custom2Cookie : true,

		multiSort : false ,
		multiGroup : false ,
		multiSelect : true ,
        /**
		 * @description {Property} selectRowByCheck
		 * {Boolean} To specify whether end user can select row by checkbox. 
		 * @config
		 */
		selectRowByCheck : false,
		
		html2pdf : true ,
		SigmaGridPath : '../../gt-grid',

/////////////////////


	properties : function(){return {

		skinList : [
			{ text:this.getMsg('STYLE_NAME_DEFAULT'),value:'default'},
			{ text:this.getMsg('STYLE_NAME_PINK'),value:'pink'},
			{ text:this.getMsg('STYLE_NAME_VISTA'),value:'vista'},
			{ text:this.getMsg('STYLE_NAME_MAC'),value:'mac'}
		] ,

		encoding: null, //Sigma.$encoding(), //'utf-8',
		mimeType: null, //'text/html',
		/**
		 * @description {Config} jsonParamName
		 * {String}  To specify parameter name submitted to server side. 
		 * It's set to "_gt_json" by default. 
		 * For example, PHP developer could get whole JSON string by $_POST["_gt_json"] in php. 
		 */
		jsonParamName : null,

		/* todo */
		title : null,
		lastAction : null,
		ajax : null ,

		autoExpandColumn : null,
		autoColumnWidth : false,

		cellWidthPadding : Sigma.isBoxModel?0:4,
		cellHeightPadding : Sigma.isBoxModel?0:2,

		cellWidthFix : Sigma.isBoxModel?0:0, //-1 ,
		innerWidthFix : Sigma.isBoxModel?0:(Sigma.isIE8?-1:-4) ,

		freezeFixH : Sigma.isBoxModel?0:0,
		freezeFixW : Sigma.isIE?-1:-2,

		toolbarHeight :24 ,
		toolBarTopHeight :0,

		tableMarginLeft : 0 ,
		freezeColumns : 0,
		separateLineMinX : 0,
        /**
		 * @description {Property} defaultRecord 
		 * {Object or Array} Data record template. This template will be adopted if a new row is added or appended. 
		 * @config
		 */
		defaultRecord : null,

		isWaiting		: false,
		isColumnResizing	: false,

		requesting: false ,

		hasGridDivTemp	: false,
		resizeColumnId	: -1,
		moveColumnDelay	: 800,
		mouseDown		: false,


		/* todo */
		footDiv			:null,
		footTable		:null,
		footFirstRow	:null,


		freezeBodyTable	:null,

		/* todo  freezeLeft  freezeRight */

		titleBar		:null,
		/* todo */
		nearPageBar		:null,

		//todo : when cleanTable , reset lastOverHdCell ???
		lastOverHdCell : null ,
		toolTipDiv : null ,
		gridTable : null  ,
		overRow : null ,
		overRowF : null,
        
		/**
		 * @description {Property} activeCell 
		 * {Object} Dom object of current active cell. Read only.
		 */
		activeCell		: null ,
		/**
		 * @description {Property} activeColumn 
		 * {Object} Column object. Object of active/last active column. Read only.
		 */
		activeColumn    : null,
		/**
		 * @description {Property} activeRow 
		 * {Object} Td DOM object of active/last active row. Read only.
		 */
		activeRow		: null,
		/**
		 * @description {Property} activeRecord 
		 * {Object} Data set of current active row. Read only.
		 */
		activeRecord	: null,
		activeEditor : null,
		activeDialog : null,
		scrollLeft:0,
		scrollTop:0,
		complatedTimes : 0 ,

        ///////////////Public Event Handlers Goes Here//////////
        /**
	 	 * @description {Event} onComplete:function(grid) Fired when grid is completely loaded. 
	 	 * Refresh/sort/paging will also fire this event 
	     * @param {Object} grid Grid of the column resized.	         
	     */
		onComplete : Sigma.$empty ,
		/**
	 	 * @description {Event} onResize:function() Fired when width or height of grid changes. 
         */
		onResize : Sigma.$empty ,
        /**
	 	 * @description {Event} beforeRowSelect:function(record,row, rowNo,grid)
	 	 * Fired before end user selects one row.	 	 
	     * @param {Object or array} record Data record of the row to be selected.
	     * @param {Object} row Dom(tr) of row to be selected.
	     * @param {Number} rowNo Index of the row where mouse pointer is at. Base on 0..	         
	     * @param {Object} grid Grid of the column resized.
	     * @return {Boolean} Return false to prevent row from being selected.  	         
	     */
		beforeRowSelect : Sigma.$empty,
		/**
	 	 * @description {Event} afterRowSelect:function(value, record , cell, row, colNO, rowNO, columnObj,grid)
	 	 * Fired after end user selects one row.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.      	 	 
	     * @param {Object or array} record Data record of the row to be selected.
	     * @param {Object} cell Dom of the cell where mouse pointer is at.	         
	     * @param {Object} row Dom(tr) of row to be selected.
	     * @param {Number} colNo Index of the column where mouse pointer is at. Base on 0.
	     * @param {Object} columnObj Column object.                    
	     * @param {Object} grid Grid of the column resized.
	     */
		afterRowSelect : Sigma.$empty,
		/**
	 	 * @description {Event} onHeadClick:function(event,headCell,colObj,grid)
	 	 * Fired when end user click column header.
	 	 * @param {Object} event See Sigma.Grid.Event.     	 	 
	     * @param {Object} headCell Dom of the head cell.      
	     * @param {Object} colObj Column object.     
	     * @param {Object} grid Grid of the header clicked on.
	     * @return {Boolean} Return false to prevent default behavior.	         
	     */
		onHeadClick : Sigma.$empty ,
		/**
	 	 * @description {Event} onCellClick:function(value, record , cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when end user click one cell.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row where the cell is at.      
	     * @param {Object} cell Dom(td) of the cell.      
	     * @param {Object} row Dom of row where the cell is at.
	     * @param {Number} colNo Index of the column where the cell is at.
	     * @param {Object} columnObj Column object.	             	   
	     * @param {Object} grid Grid of the cell clicked on.
	     * @return {Boolean} Return false to prevent default behavior.	         
	     */
		onCellClick : Sigma.$empty,
        /**
	 	 * @description {Event} onCellDblClick:function(value, record, cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when end user double click on one cell.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row where the cell is at.      
	     * @param {Object} cell Dom(td) of the cell.      
	     * @param {Object} row Dom of row where the cell is at.
	     * @param {Number} colNo Index of the column where the cell is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid of the cell clicked on.
	     * @return {Boolean} Return false to prevent default behavior.	         
	     */
		onCellDblClick :Sigma.$empty,
        /**
	 	 * @description {Event} onRowClick:function(value, record, cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when end user double click on one row.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row.      
	     * @param {Object} cell Dom(td) of the cell where mouse pointer is at.      
	     * @param {Object} row Dom of the row .
	     * @param {Number} colNo Index of the column where mouse pointer is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid of the row clicked on.
	     * @return {Boolean} Return false to prevent default behavior.	         
	     */
		onRowClick : Sigma.$empty,
		/**
	 	 * @description {Event} onRowDblClick:function(value, record, cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when end user double click on one row.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row.      
	     * @param {Object} cell Dom(td) of the cell where mouse pointer is at.      
	     * @param {Object} row Dom of the row .
	     * @param {Number} colNo Index of the column where mouse pointer is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid of the row double clicked on.
	     * @return {Boolean} Return false to prevent default behavior.	         
	     */
		onRowDblClick : Sigma.$empty,
		/**
	 	 * @description {Event} beforeEdit:function(value, record, col, grid) 
	 	 * Fired before cell is open for editing. 
	 	 * @param {Any} value Value of the cell to be open for editing.
	 	 * @param {Object or array} record Data record of the row.
	 	 * @param {Object} col Column object.
	 	 * @param {Object} grid Grid Object.
	     * @return {Boolean} Return false to prevent cell being open for editing.
	     */
		beforeEdit : Sigma.$empty,
		/**
	 	 * @description {Event} afterEdit:function(value,oldValue,record,col,grid) 
	 	 * Fired before cell is closed after editing. 
	 	 * @param {Any} value New value of the cell.
	 	 * @param {Any} oldValue Old value of the cell.
	 	 * @param {Object or array} record Data record of the row.
	 	 * @param {Object} col Column object.
	 	 * @param {Object} grid Grid object.       
	     */
		afterEdit : Sigma.$empty,
		beforeRefresh : Sigma.$empty,
		/**
	 	 * @description {Event} beforeExport:function(type, fileName, url, name, action,grid) 
	 	 * Fired before Data is sent to server side for exporting.	 
	 	 * @param {String} type File type, could be xml/pdf/csv/xls.
	 	 * @param {String} fileName File name.
	 	 * @param {String} url Url specified to export data.
	 	 * @param {String} name Json name. Server side can get all the stuff by $_POST[name].
	 	 * @param {String} action Reserved.
	 	 * @param {String} fileName File name.
	 	 * @return {Boolean} Return false to prevent data from being exported.             
	     */
		beforeExport : Sigma.$empty,
        /**
	 	 * @description {Event} beforeSave:function(requestParameter) 
	 	 * Fired before changed data is to be posted to server side.	 
	 	 * @param {Object} requestParameter See DataExchange.Saving.
	 	 * @return {Boolean} Return false to prevent data from being posted to server side.             
	     */
		beforeSave : Sigma.$empty,
		/**
	 	 * @description {Event} beforeLoad:function(requestParameter) 
	 	 * Fired before grid is to about to load data from server side.	 	 
	 	 * @param {Object} requestParameter See DataExchange.Loading.
	 	 * @return {Boolean} Return false to prevent grid from loading data from server side.             
	     */
		beforeLoad : Sigma.$empty,
		/**
	 	 * @description {Event} loadResponseHandler:function(response,requestParameter) 
	 	 * This function is a call back function to deal with the received content from server side. 
	 	 * Developer could do some data translation work within this function.	 
	 	 * @param {String} response Response plain text content.
	 	 * @param {Object} requestParameter See DataExchange.Loading.
	 	 */
		loadResponseHandler : Sigma.$empty,
		/**
	 	 * @description {Event} saveResponseHandler:function(response,requestParameter)
	 	 * This function is a call back function to deal with the received content from server side. 
	 	 * Developer could do some data translation work within this function.	 
	 	 * @param {String} response Response plain text content.
	 	 * @param {Object} requestParameter See DataExchange.Saving.            
	     */
		saveResponseHandler : Sigma.$empty,
		
		beforeDestroy : Sigma.$empty,

		beforeDatasetUpdate : Sigma.$empty ,

		/* todo */
		onRecordUpdate : Sigma.$empty,

		/* todo */
		
		/**
	 	 * @description {Event} beforeInsert:function(record)
	 	 * Fired before a new row is to be added to the grid.
	 	 * @param {Record or Object} Data record behind the row.
	 	 * @return {Boolean} Return false to prevent row from being added.          
	     */
		beforeInsert : Sigma.$empty,
		
		afterInsert : Sigma.$empty,
		/**
	 	 * @description {Event} beforeUpdate:function(record,fieldName,newValue)
	 	 * Fired before one record will be updated with a new value.
	 	 * @param {Record or Object} Data record to be updated.
	 	 * @param {String} fieldName Name of field to be updated.	
	 	 * @param {Any} newValue New value of the cell updated. 	 
	 	 * @return {Boolean} Return false to prevent record to be updated.          
	     */
		beforeUpdate : Sigma.$empty,
		afterUpdate : Sigma.$empty,
		/**
	 	 * @description {Event} beforeDelete:function(record,row,grid)
	 	 * Fired before an existing row is to be removed from grid.
	 	 * @param {Record or Object} Data record behind the row.
	 	 * @param {Object} row Dom(td) of row to be remove.	 	
	 	 * @param {Object} grid Grid object. 
	 	 * @return {Boolean} Return false to prevent row from being removed.          
	     */
		beforeDelete : Sigma.$empty,
		afterDelete : Sigma.$empty,

		/**
	 	 * @description {Event} customRowAttribute:function(record,rn,grid)
	 	 * Fired when row style is to specified.
	 	 * @param {Object or array} record Data record of the row where the cell is at.
	 	 * @param {Number} rowNo Index of the row.
	 	 * @param {Object} grid Grid object.
	 	 * @return {String} Css style string. For example : "background-color:red;"
	 	 */
		customRowAttribute : Sigma.$empty,	
		/**
	 	 * @description {Event} onContextMenu:function(value, record, cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when end user double click on one cell.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row where the cell is at.      
	     * @param {Object} cell Dom(td) of the cell.      
	     * @param {Object} row Dom of row where the cell is at.
	     * @param {Number} colNo Index of the column where the cell is at.
	     * @param {Number} rowNo Index of the row where the cell is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid of the cell clicked on.
	     * @return {Boolean} Return false to prevent default behavior.	         
	     */
		onContextMenu : Sigma.$empty,
		/**
	 	 * @description {Event} afterColumnResize:function(colObj,newWidth,grid) 
	 	 * Fired after one column is resized.
	     * @param {Object} colObj Column object resized.
	     * @param {Number} newWidth Column new width.
	     * @param {Object} grid Grid of the column resized.
	     */
		afterColumnResize : Sigma.$empty,
		/**
	     * @description {Event} beforeColumnMove:function(oldIndex, newIndex) 
	     * Fired before column being moved.
	     * @param {Number} oldIndex Index of column to be moved.
	     * @param {Number} newIndex Index of new position.
	     * @return {Boolean} Return false to prevent column from being moved.
	     */
        beforeColumnMove : Sigma.$empty,
		/**
	     * @description {Event} onKeyDown:function(event) 
	     * Fired when end user press a key down.
	     * @param {Object} event Browser raw event object.
	     * @return {Boolean} Return false to prevent grid default behavior.
	     */
		onKeyDown : Sigma.$empty,
		/**
	 	 * @description {Event} onMouseMove:function(value, record, cell, row, colNo, rowNo,columnObj,grid)
	 	 * Fired when mouse pointer is moving over grid.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row where mouse pointer is at.      
	     * @param {Object} cell Dom(td) of the cell.      
	     * @param {Object} row Dom of row where mouse pointer is at.
	     * @param {Number} colNo Index of the column where mouse pointer is at.
	     * @param {Number} rowNo Index of the row where mouse pointer is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid object.	         
	     */
		onMouseMove: Sigma.$empty,
		/**
	 	 * @description {Event} onMouseOut:function(value, record, cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when mouse pointer is to be out of grid.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row where mouse pointer is at.      
	     * @param {Object} cell Dom(td) of the cell.      
	     * @param {Object} row Dom of row where mouse pointer is at.
	     * @param {Number} colNo Index of the column where mouse pointer is at.
	     * @param {Number} rowNo Index of the row where mouse pointer is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid object.	         
	     */
		onMouseOut : Sigma.$empty,
		/**
	 	 * @description {Event} onMouseOver:function(value, record, cell, row, colNO, rowNO,columnObj,grid)
	 	 * Fired when mouse pointer is over grid.
	 	 * @param {Any} value Value of the cell where mouse pointer is at.	 
	     * @param {Object or array} record Data record of the row where mouse pointer is at.      
	     * @param {Object} cell Dom(td) of the cell.      
	     * @param {Object} row Dom of row where mouse pointer is at.
	     * @param {Number} colNo Index of the column where mouse pointer is at.
	     * @param {Object} columnObj Column object.	            	   
	     * @param {Object} grid Grid object.	         
	     */
		onMouseOver : Sigma.$empty,
		/**
	 	 * @description {Event} onRowCheck:function(row, chk, grid)
	 	 * Fired when one row is to be checked or unchecked.     
	     * @param {Object} row Dom of row where mouse pointer is at.
	     * @param {Boolean} chk If row is to be checked or unchecked.	            	   
	     * @param {Object} grid Grid object.	         
	     */
		onRowCheck : Sigma.$empty,

	////////////////////////////////////


		editing :false,
		
		rendered : false,

		isFirstLoad : true ,

		customPrintCss : null,


		gridTbodyList : []  ,

		gridRowList : []  ,
		gridFreezeRowList : []  ,
		
		checkedRows : {},

		rowBegin :0 ,
		rowNum :0 ,
		rowEnd : 0,
		
		currentRowNum : 0 ,

		filterDataProxy : null ,
		dataProxyBak : null ,

		cacheData : [] ,
		/**
		 * @description {Config} dataset
		 * {Object} Dataset object of grid. Read only. See Sigma.Dataset
		 */
		dataset : null ,
        
		/**
		 * @description {Property} selectedRows
		 * {Array} Array of selected row objects. Read only. 
		 */
		selectedRows : [] ,

		cacheBodyList : [] ,
		frozenColumnList : [] ,
		sortedColumnList : [],

		/* todo */
		countTotal : true ,
        /**
		 * @description {Config}  pageSizeList
		 * {Array} To specify page size options. See Sigma.Grid.pageSize
		 * The following sample will show a dropdown list of page size for end user to choose. 
		 * @sample 
		 * pageSizeList :[5,10,20,50,100]     		 
		 */
		pageSizeList : [] ,

		tools : {},
		toolCreator : {},
        /**
		 * @description {Property} columns 
		 * {Array} Array of all the column objects. Read only. See Sigma.Column
		 */
		columns : [],

		columnList : [] ,
		/**
         * @description {Property} columnMap
         * {Object} Object of column objects in in key-value formate, where key is column id and value is column object. Read only.
         */
		columnMap : {} ,

		CONST : null,

		queryParameters : {},
		parameters : {}

		}; },


	activeMe : function(){
		Sigma.activeGrid=this;
	},

	clearCheckedRows : function(refresh){
		this.checkedRows={};
		if (refresh){
			this.refresh();
		}
	},

	initialize : function(id, options){
		Sigma.GridNum++;
		var _const={};
		var grid=this;
		Sigma.$each(this.domList,function(k,i){
			grid[k]=null;
		});
		Sigma.$each(this.defaultConst,function(k,i){
			_const[k]=k;
		});
	
		this.id=''+id;
		options = options ||{};
		if ( Sigma.$type(id,"object") ){
			options=id;
			this.id='gtgrid_'+Sigma.GridNum;
		}
		Sigma.$extend(_const,options.CONST);
		this.CONST=_const;

		Sigma.$extend(this,options);

		this.gridId=this.id;
		this.rowKey ='__gt_'+this.id+'_r_';

		Sigma.GridCache[this.id]=this;
		
		////////////////////////
		this.legacy();

		if (!this.dataset && this.columns){
			var _dataset={
				fields :[]
			};
			for (var ci=0;ci<this.columns.length;ci++){
				var col= this.columns[ci];
				var field={
					name : col.name || col.fieldName || col.id ,
					type : col.type ,
					index : (col.fieldIndex || col.fieldIndex===0)?col.fieldIndex : null
				};
				_dataset.fields.push(field);
			}
			this.dataset=_dataset;
		}

		if ( this.dataset && !(this.dataset instanceof Sigma.DataSet) ){
			this.dataset.recordType = this.dataset.recordType || this.recordType;
			this.dataset = new Sigma.DataSet(this.dataset);
		}
		this.loadURL= this.loadURL || this.dataset.loadURL;
		this.saveURL= this.saveURL || this.dataset.saveURL;
		this.evenRowCss = ' '+this.evenRowCss;

		this.toolbarContent = this.toolbarContent===false?false:this.toolbarContent;
		
		if (this.toolCreator){
			for (var key in this.toolCreator ){
				Sigma.ToolFactroy.register(key,this.toolCreator[key]);
			}
		}

		/* todo */		
		var pageInfo = options.pageInfo || (this.dataset?this.dataset.pageInfo:null) || {};
		pageInfo.pageSize = pageInfo.pageSize || options.pageSize || 0;
		if (pageInfo.pageSize===0) {
			delete pageInfo.pageSize;
		}
		delete options.pageInfo;
		delete options.pageSize;
		delete this.pageInfo;
		delete this.pageSize;
		this.navigator=new Sigma.Navigator( { gridId : this.id , pageInfo : pageInfo } );
		

		/* todo */
		//this.activeMe();

	},

	/* todo : for old. */
	legacy : function(){
		var grid=this;
		grid.beforeRowSelect= grid.beforeRowSelect||grid.beforeSelectRow;
		grid.afterRowSelect= grid.afterRowSelect||grid.afterSelectRow;
		grid.onCellClick= grid.onCellClick||grid.onClickCell;
		grid.onRowClick= grid.onRowClick||grid.onClickRow;
		grid.onCellDblClick= grid.onCellDblClick||grid.onDblClickCell;
		grid.onRowDblClick= grid.onRowDblClick||grid.onDblClickRow;
	},

	initColumns :function( colsOptions ){
			this.columns=colsOptions || this.columns;

			if (!this.columns) {
				return ;
			}

			this.gridEditorCache = this.gridEditorCache || Sigma.$e('div',{className :'gt-editor-cache'});
			var colNum=this.columns.length;
			var tableColumnIndex=0;
			var hasNewRecord=true;

			var _defaultRecord={};
			function _render(value ,record,columnObj,grid,colNo,rowNo){
				return this.editor.getDisplayValue(value);
			}
			for (var i=0 ;i< colNum; i++) {
				var col=this.columns[i];
				col.grid=this;
				col.gridId =this.id;
				if (col.isCheckColumn){
					col=Sigma.Grid.createCheckColumn(this,col);
					
				}
				var colObj=new Sigma.Column(col);
				this.columnMap[colObj.id]=colObj;
				this.columnList.push( colObj );
				colObj.colIndex=i;

				this.checkColumn=colObj.isCheckColumn?colObj:this.checkColumn;
				
				if (colObj.frozen){
					this.frozenColumnList.push(colObj.id);
				}

				var field= this.dataset.fieldsMap[colObj.fieldName];
				if ( field ){
					colObj.fieldIndex = field.index;
				}
				if (colObj.editable!==true && colObj.editable!==false){
					colObj.editable=this.editable;
				}
				colObj.editor=Sigma.Editor?Sigma.Editor.factroy(colObj.editor,this):null;
				if (colObj.editor && colObj.editor.getDom() ) {
					this.gridEditorCache.appendChild( colObj.editor.getDom() );
				}
				
				if (colObj.renderer=='by editor' && colObj.editor){
					colObj.renderer=_render;
				}else if (Sigma.$type(colObj.renderer,'string')){
					colObj.renderer= Sigma.U.parseExpression(colObj.renderer,'record','value,record,col,grid,colNo,rowNo');
				}

				_defaultRecord[colObj.fieldIndex]= colObj.newValue || '';

				colObj.styleClass = Sigma.T_G.getColStyleClass(this,colObj.id);
				colObj.innerStyleClass =  colObj.styleClass+' .gt-inner';

				colObj.minWidth = colObj.minWidth  + this.cellWidthFix ;

				colObj.innerStartHTML = Sigma.T_G.innerStart(colObj);

				if (colObj.sortOrder){
					this.addSortInfo( this.createSortInfo(colObj)) ;
				}

				if (colObj.separator)	{
					colObj.separator.gridId=colObj.gridId;
				}

				if (colObj.hidden){

				}
			}
			this.defaultRecord =this.defaultRecord || _defaultRecord;
			return this;
	},

	getMsg : function(v){
		var msg=Sigma.Msg.Grid[this.language][v];
		return msg;
	},
	_onComplete : function(){
		if (this.autoSelectFirstRow && !this.selectRowByCheck){
			this.selectFirstRow();
		}
		this.toggleEmptyRow();
		Sigma.$invoke(this,'onComplete',[this]);
		this.hideWaiting();
		this.complatedTimes++;
	},

    getEl : function(){
        return this.gridDiv;
    },

	initHead : function(){
		var grid=this;
		this.headDivHeight=this.headDiv.clientHeight;

		if (this.customHead) {
			this.headDiv.style.height=this.headDivHeight-2+'px';
			/* todo : small to big */
			Sigma.$thread(function(){
				grid.headDiv.scrollTop=2;
			});
			this.headDivHeight-=2;
		}

		this.freezeHeadDiv.style.height = this.headDivHeight+'px';
		this.freezeHeadDiv.style.top = 0 + this.freezeFixH + "px";
		this.freezeBodyDiv.style.top = this.headDivHeight + this.freezeFixH + 0 + "px";

	},
		
	initCSS : function(){
		var _hidden = 'display:none;';
		Sigma.Const.Grid.SCROLLBAR_WIDTH = 20; // Sigma.U.getScrollerWidth();
		var grid=this;
		grid.evenRowCss=grid.stripeRows? grid.evenRowCss :'';

		var cssText=[];
		Sigma.$each(this.columnList,function(col,i){
			col.width = col.width || grid.defaultColumnWidth;
			var cWidth = ''+col.width;
			if ( cWidth.indexOf("px")<1 && cWidth.indexOf("%")<1 ){
				// col.width= col.width- grid.cellWidthPadding- ;
				cWidth=parseInt(cWidth) ;
			}else if (cWidth.indexOf("%")>0){

			}else{

			}
			cssText[i]= [
					col.CLASS_PREFIX + col.styleClass +" { width:"+ (cWidth + grid.cellWidthFix)+ "px;"+( col.hidden?_hidden:'')+" } " ,
					col.CLASS_PREFIX + col.innerStyleClass+" { width:"+ (cWidth + grid.innerWidthFix) + "px; } "
				].join("\n");
			}
		);
		Sigma.U.CSS.createStyleSheet(cssText.join("\n"));

	},

	createMain : function(){
		var grid=this;

		// this.navigator=new Sigma.Navigator( { gridId : this.id , pageInfo : this.pageInfo } );

		this.pageStateBar = Sigma.$(this.pageStateBar);

		if (this.isNative){
			this.gridDiv=Sigma.$(this.id +"_div");
		}else{
			var classNames= [
				(Sigma.isBoxModel ?'gt-b-ie ':(Sigma.isSafari?'gt-b-safari ':( Sigma.isOpera?'gt-b-opera ': (Sigma.isStrict?'gt-b-strict':'') ) )),
				'gt-grid', 'gt-skin-'+this.skin
			];
			this.gridDiv=Sigma.$e('div', {id : this.id+'_div' ,className : classNames.join(' ') } );

			this.container = Sigma.$(this.container);
			if (!this.container || !this.container.appendChild
					|| !this.container.tagName || Sigma.U.getTagName(this.container)=='BODY' ){
				Sigma.doc.body.appendChild(this.gridDiv);
			}else if (this.replaceContainer===true){
				this.container.parentNode.insertBefore(this.gridDiv,this.container);
				Sigma.U.removeNode(this.container);
				this.container=null;
			}else{
				this.container.appendChild(this.gridDiv);
			}

			this.gridDiv.innerHTML=Sigma.T_G.main(this);

		}

		this.gridDiv.hideFocus=true;

		this.gridMask=Sigma.$byId(this.id +"_mask");
		this.gridWaiting=Sigma.$(this.id +"_waiting");
		this.gridDialog=Sigma.$(this.id +"_dialog");
		this.gridDialogTitle=Sigma.$(this.id +"_dialog_title");
		this.gridDialogBody=Sigma.$(this.id +"_dialog_body");

		this.gridDiv.appendChild(this.gridEditorCache);

		this.gridFilterRSCache = this.gridFilterRSCache || Sigma.$e('table',{className :'gt-filter-rs-cache'});
		this.gridDiv.appendChild(this.gridFilterRSCache);

		//this.gridEditorCache=Sigma.$(this.id +"_editor_cache");

		//this.gridEditorContainer=Sigma.$(this.id +"_editor_container");


		this.showMask();

		this.viewport=Sigma.$(this.id +"_viewport");

		this.toolBarBox=Sigma.$(this.id +"_toolBarBox");

		this.headDiv=Sigma.$(this.id +"_headDiv");
		this.bodyDiv=Sigma.$(this.id +"_bodyDiv");

		this.freezeView = Sigma.$(this.id +"_freezeView");
		this.freezeHeadDiv=Sigma.$(this.id +"_freeze_headDiv");
		this.freezeBodyDiv=Sigma.$(this.id +"_freeze_bodyDiv");

		this.createHeader();

		this.separateLine=Sigma.$(this.id +"_separateLine");
		this.toolBarTopHeight  =  this.toolbarPosition=='top' || this.toolbarPosition=='t'? this.toolbarHeight + (Sigma.isBoxModel?0:1) :0;
		//var _f_top = 0;
		if (this.separateLine){
			this.separateLine.style.top = this.toolBarTopHeight + 'px';
		}

		this.columnMoveS=Sigma.$(this.id +"_columnMoveS");
		this.headerGhost=Sigma.$(this.id +"_headerGhost");

		//this.bodyTable=Sigma.$(this.id +"_bodyTable") || Sigma.U.firstChildElement(this.bodyDiv);
		//this.bodyTbody= Sigma.U.firstChildElement(this.bodyTable);

		if (this.toolBarBox){
			this.toolBar=Sigma.$e("div",{id : this.id +'_toolBar', className :  'gt-toolbar' });
			this.toolBarBox.appendChild(this.toolBar);
		}

		var newWidth=''+this.width ;
		var newHeight=''+this.height ;
		this.setDimension(newWidth,newHeight,true);

		this.showWaiting();

		this.syncLeftTop();

	},


	syncLeftTop : function(){
		this.left= Sigma.U.getPosLeftTop(this.gridDiv);
		this.top=this.left[1];
		this.left=this.left[0];
		this.viewportXY=Sigma.U.getXY(this.viewport);
	},

    _onResize : function(isInit){
		this.gridMask.style.width= this.width  ;
		this.gridMask.style.height= this.height ;
		this.gridDiv.style.width= this.width  ;
		this.gridDiv.style.height= this.height ;

		var gHeight= (''+this.height).indexOf('%')>0?this.gridDiv.clientHeight:parseInt(this.height);
		var _fix= 0 - (Sigma.isBoxModel?2:3) ;

		this.bodyDiv.style.height= gHeight - (this.headDivHeight + this.toolbarHeight )+ _fix   +"px";

		/* todo  :  bug opera  */
		if (Sigma.isOpera){
			var _w=this.gridDiv.clientWidth + _fix +"px";
			this.viewport.style.width= _w;
			//var _h=this.gridDiv.clientHeight + _fix +"px";
			//this.viewport.style.height= _h;
			if (this.toolBarBox) {
				this.toolBarBox.style.width= _w;
			}
		}

		if (this.freezeBodyDiv) {
			this.freezeBodyDiv.style.height= this.bodyDiv.clientHeight  +"px";
		}

		if (isInit!==true){
			this.onResize();
		}
	},

	createFormIFrame :function(){
		Sigma.U.createElementFromHTML( Sigma.T_G.formIFrame(this),Sigma.doc.body );
		this.gridForm=Sigma.$(this.id +"_export_form");
		this.gridFormTextarea=Sigma.$(this.id +"_export_form_textarea");
		this.gridFormFileName=Sigma.$(this.id +"_export_filename");
		this.gridFormExportType=Sigma.$(this.id +"_export_exporttype");
		this.gridIFrame=Sigma.$(this.id +"_export_iframe");
	},

	createGridGhost :function(){

		this.gridGhost=Sigma.$e("div", { id:this.id +"_gridGhost",	className:"gt-grid-ghost-rect" });
		this.gridGhost.style.top=this.top+"px";
		this.gridGhost.style.left=this.left+"px";
		this.gridGhost.style.width= this.gridMask.style.width;
		this.gridGhost.style.height=this.gridMask.style.height;
		//this.gridGhost.style.display="block";
		Sigma.doc.body.appendChild(this.gridGhost);

	},
	
	orphanTr : function(){ 
			if (!this.renderHiddenColumn) {
				var tr=Sigma.doc.createElement('tr'); 
				tr.className="gt-orphan-tr";
				return tr;
			}
		 }(),

	kickHeader : function(){
		var grid=this;
		var headRow=grid.headTbody.rows[0];
		if (!this.renderHiddenColumn && headRow ) {
			Sigma.$each(this.columnList,function(col,idx){
				var orp=col.headCell&&col.headCell.parentNode==grid.orphanTr;
				if (col.hidden&& orp) {
					grid.orphanTr.appendChild(col.headCell);
				}else if(orp){
					var cell=headRow.cell[idx];
					if (cell) {
						headRow.insertBefore(col.headCell,cell);
					}
				}
			},this 	);	
		}

	},

	createHeader	: function(){
		/* todo */
		var grid=this ,headRow;
		if (this.customHead) {
			this.renderHiddenColumn=true;
			Sigma.U.removeNode(this.orphanTr);
			this.orphanTr=null;
			this.createCustomHeader();			
		}else{
			this.headTable=Sigma.$e("table", { id: this.id +"_headTable", className:"gt-head-table",
				cellSpacing : "0"  ,  cellPadding : "0" ,border :"0" } );
			this.headTbody=Sigma.$e("tbody");
			this.headTable.appendChild(this.headTbody);

			headRow=Sigma.$e("tr", { className : 'gt-hd-row' } );
			this.headTbody.appendChild(headRow);
			Sigma.$each(this.columnList,function(col,idx){
				var cell=Sigma.T_G.createHeaderCell(grid,col);
				headRow.appendChild(cell);
				col.headCell=cell;
				Sigma.Grid.initColumnEvent(grid,col);
			},this 	);
			this.kickHeader();
		}

		this.headTable.style.marginRight= 100 + 'px' ; //Sigma.Const.Grid.SCROLLBAR_WIDTH +"px";

		var tagName= this.headDiv.firstChild ? String(Sigma.U.getTagName(this.headDiv.firstChild)):null;
		if (tagName=="DIV" ||  tagName=="SPAN" ){
			this.headDiv.firstChild.appendChild(this.headTable);
		}else{
			this.headDiv.appendChild(this.headTable);
		}

		this.headFirstRow=this.headTbody.rows[0];

		this.freezeHeadTable=Sigma.$e("table", { id: this.headTable.id +"_freeze", className:"gt-head-table",
				cellSpacing : "0"  ,  cellPadding : "0" ,border :"0" } );
		this.freezeHeadTable.appendChild(Sigma.$e("tbody"));

		this.freezeHeadTable.style.height="100%";
		this.freezeHeadDiv.appendChild(this.freezeHeadTable);

		this.initHead();
	},

	createBody : function(){
		var htmlTable=Sigma.$(this.id+"_bodyTable");
		if (htmlTable){
			this.gridTable=htmlTable;
			this.gridTbodyList.push(htmlTable.tBodies[0]);
			this.bodyFirstRow=this.getFirstRow();
		}else{
			//this.reload();
			this.gotoPage();
		}
	},

	createCustomHeader : function(){
			var grid=this ,headRow;
			if (Sigma.$type(this.customHead,'string')) {
				if (this.customHead.indexOf('<table')===0) {
					Sigma.U.orphanDiv.innerHTML=this.customHead;
					this.customHead=Sigma.U.orphanDiv.firstChild;
				}else{
					this.customHead=Sigma.$(this.customHead);
				}
			}
			this.customHead.style.display='';


			this.headTable=Sigma.$e( this.customHead , { id: this.id +"_headTable", className:"gt-head-table",
				cellSpacing : "0"  ,  cellPadding : "0" ,border :"0" } );
			this.headTbody= this.headTable.tBodies[0];

			for (var i=0; i<this.headTbody.rows.length; i++) {
				var row=this.headTbody.rows[i];
				row.className='gt-hd-row';
				for (var j=0; j<row.cells.length; j++) {
					var cell=row.cells[j];
					var header=cell.innerHTML;

					var columnId= cell.getAttribute('columnId');
					if (columnId) {
						cell.columnId=columnId;
						var col=this.columnMap[columnId];
						if (String(cell.getAttribute('resizable')).toLowerCase()=='false'){
							col.resizable=false;
						}
						if (String(cell.getAttribute('sortable')).toLowerCase()=='false'){
							col.sortable=false;
						}
						if (cell.colSpan<2){
							cell.className=col.styleClass;
						}

						col.headCell=cell;
					}
					cell.innerHTML=[
					"<div class=\"gt-inner",
					cell.rowSpan<2?'':' gt-inner-tall2',
					"\" unselectable=\"on\" >",
					'<span>',
					header,
					'</span>',
					columnId?Sigma.T_G.hdToolHTML:'',
					"</div>"
					].join('');

				}
			}

			headRow=Sigma.$e("tr", { className : 'gt-hd-row'+ (this.customHead?' gt-hd-hidden':'')  } );
			Sigma.$each(this.columnList,function(col,idx){
				var cell=Sigma.T_G.createHeaderCell(grid,col,true);
				headRow.appendChild(cell);
				Sigma.Grid.initColumnEvent(grid,col);
			} 	);
			this.headTbody.insertBefore( headRow, this.headTbody.rows[0] );

	},

	scrollGrid : function(x,y){
				var mL = this.tableMarginLeft;
				var fW = this.freezeBodyDiv.clientWidth; //- mL ; 
				var acLeft=this.activeCell.offsetLeft + ((Sigma.isFF2 || Sigma.isFF1)?0:mL)  ;

				var acRight=acLeft+this.activeCell.offsetWidth;
				var acTop=this.activeCell.offsetTop;
				var acBottom= acTop+this.activeCell.offsetHeight;
				var bdLeft = this.bodyDiv.scrollLeft;
				var bdTop = this.bodyDiv.scrollTop;
				
				var bdRight = bdLeft + this.bodyDiv.clientWidth +  mL;
				var bdBottom = bdTop + this.bodyDiv.clientHeight;
				
				if (Sigma.$chk(x)){
					this.bodyDiv.scrollLeft = x;
				}else if (acLeft<=bdLeft+fW) {
					this.bodyDiv.scrollLeft= acLeft-fW-(fW>0?1:0); //acLeft - ((Sigma.isFF2 || Sigma.isFF1)?0:mL) ;
				}else if (acRight>=bdRight){
					this.bodyDiv.scrollLeft = bdLeft + acRight-bdRight +  mL;
				}

				if (Sigma.$chk(y)){
					this.bodyDiv.scrollTop = y;
				}else if (acTop<=bdTop) {
					this.bodyDiv.scrollTop=acTop ;
				}else if (acBottom>=bdBottom){
					this.bodyDiv.scrollTop = bdTop + acBottom-bdBottom;
				}

	},
	
	syncActiveObj : function(cell){
		this.activeCell= cell = (cell || this.activeCell);
		this.activeRow= cell?cell.parentNode:null;
		this.activeColumn=this.getColumn(cell);
		this.activeEditor = this.activeColumn?this.activeColumn.editor :null;
		this.activeRecord=this.getRecordByRow(this.activeRow);	
	},



	_prveEditableCell : function(currentCell){
			var newCell= Sigma.U.prevElement(currentCell);
			while( newCell &&  newCell.offsetWidth<1){
					newCell= Sigma.U.prevElement(newCell);
			}
			if (!newCell){
				newCell= Sigma.U.prevElement(currentCell.parentNode);
				if (newCell){
					newCell=newCell.cells[newCell.cells.length-1];
				}
			}
			while( newCell &&  newCell.offsetWidth<1){
					newCell= Sigma.U.prevElement(newCell);
			}
			return newCell;
	},
	_nextEditableCell : function(currentCell){
			var newCell= Sigma.U.nextElement(currentCell);
				while( newCell &&  newCell.offsetWidth<1){
					newCell= Sigma.U.nextElement(newCell);
			}
			if (!newCell){
				newCell= Sigma.U.nextElement(currentCell.parentNode);
				if (newCell){
					newCell=newCell.cells[0];
				}
			}
			while( newCell &&  newCell.offsetWidth<1){
					newCell= Sigma.U.nextElement(newCell);
			}
			return newCell;
	},



	_onKeydown : function(event){
				var oldCell=this.activeCell;
				var newCell=null;
				var kcode=event.keyCode;
				var grid=this;

				function editCell(_cell){
					if (_cell){
						grid.endEdit();
						Sigma.U.stopEvent(event);
						Sigma.Grid.handleOverRowCore(event,grid,_cell.parentNode);
						grid.initActiveObj_startEdit(event,_cell);
					}
				}

				if (kcode ==  Sigma.Const.Key.ESC) {
					if (this.endEdit()===false){
						return;
					}else{
						Sigma.U.stopEvent(event);
					}

				}else if (kcode ==  Sigma.Const.Key.ENTER) {
					var et =Sigma.U.getEventTarget(event);
					if (this.editing && Sigma.U.getTagName(et)=='TEXTAREA') {
						return;
					}		
					Sigma.U.stopEvent(event);
					if (this.editing) {
						if ( !this.autoEditNext){
							this.endEdit();
							return;
						}
						newCell= this._nextEditableCell(oldCell);
						editCell(newCell);							
					}else{
						this.syncActiveObj(newCell);
						this.startEdit();
					}
				}else if (this.editing && kcode ==  Sigma.Const.Key.TAB && event.shiftKey) {
					newCell= this._prveEditableCell(oldCell);
					editCell(newCell);
				}else if (this.editing && kcode ==  Sigma.Const.Key.TAB) {
					newCell= this._nextEditableCell(oldCell);
					editCell(newCell);
				}else if (oldCell && !this.editing){
					switch (kcode){
						case Sigma.Const.Key.LEFT :
						case Sigma.Const.Key.TAB :
							if (kcode==Sigma.Const.Key.LEFT || event.shiftKey){
								newCell= this._prveEditableCell(oldCell);
								while (this.isGroupRow(newCell)){
									newCell= this._prveEditableCell(newCell);
								}
								break;
							}
						case Sigma.Const.Key.RIGHT :
							newCell= this._nextEditableCell(oldCell);
							while (this.isGroupRow(newCell)){
								newCell= this._nextEditableCell(newCell);
							}
							break;
						case Sigma.Const.Key.DOWN :
							newCell= Sigma.U.nextElement(oldCell.parentNode);
							while (this.isGroupRow(null,newCell)){
								newCell= Sigma.U.nextElement(newCell);
							}
							if (newCell){
								newCell=newCell.cells[ Sigma.U.getCellIndex(oldCell) ];
							}
							break;
						case Sigma.Const.Key.UP :
							newCell= Sigma.U.prevElement(oldCell.parentNode);
							while (this.isGroupRow(null,newCell)){
								newCell= Sigma.U.prevElement(newCell);
							}
							if (newCell){
								newCell=newCell.cells[ Sigma.U.getCellIndex(oldCell)];
							}
							break;
					}

					if (newCell) {
						Sigma.U.stopEvent(event);
						var or=oldCell.parentNode,nr=newCell.parentNode;
						//if (or!= nr){
							//this.unselectRow(or);
							this._onRowSelect(nr,event);
							Sigma.Grid.handleOverRowCore(event,this,nr);
						//}
						
						this.initActiveObj(event,newCell);
					}

				}


	},



	startEdit : function(){

			if (!this.readOnly && this.activeCell && this.activeEditor && (this.activeColumn.editable || this.isInsertRow(this.activeRow) )
				&& Sigma.$invoke(this.activeColumn,'beforeEdit',[this.activeValue, this.activeRecord,this.activeColumn,this])!==false
				&& Sigma.$invoke(this,'beforeEdit',[this.activeValue, this.activeRecord,this.activeColumn,this])!==false 
				&& !this.isDelRow(this.activeRow ) ) {
				this.editing=true;
				this.showEditor(this.activeValue , this.activeRecord);
			}

	},


	
	showEditor : function(value,record){
			var cell=this.activeCell ,bodydiv=this.bodyDiv;
			var leftFix=this.tableMarginLeft;
			if (this.activeColumn.frozen) {
				cell=this.getTwinCells(this.activeCell)[1];
				bodydiv=this.freezeBodyDiv;
				leftFix=0;
			}

			if (this.activeEditor &&  this.activeEditor instanceof Sigma.Dialog) {
				//this.gridMask.appendChild(this.activeEditor.getDom());
			}else{
				bodydiv.appendChild(this.activeEditor.getDom());
			}


			this.activeEditor.show();

			this.activeEditor.setValue(value,record);

			if (this.activeEditor!==this.activeDialog ) {
				this.activeEditor.setPosition( ((Sigma.isFF2 || Sigma.isFF1)?0:leftFix) + cell.offsetLeft,cell.offsetTop );
				this.activeEditor.setSize(cell.offsetWidth,cell.offsetHeight);
			}

			this.activeEditor.active();
	},
	
	validValue : function(colObj,value,record,cell){
		if (colObj.editor) {
			var validResult=colObj.editor.doValid(value,record,colObj,this);
			if (validResult!==true) {
				this.showValidResult(value,validResult,cell,colObj);
			}
			return validResult;
		}
		return true;
	},

	hideEditor : function(){
			if (this.editing){
				var row=this.activeRow;
				var oldValue=this.activeValue;
				var value= this.activeEditor.parseValue( this.activeEditor.getValue() );
				var validResult= this.validValue(this.activeColumn,value,this.activeRecord,this.activeCell);
				if (validResult===true &&  String(this.activeValue)!==String(value) ) {
					this.updateRecordField(this.activeCell ,value );
					this.refreshRow(row,this.activeRecord);
					this.dirty(this.activeCell);
					this.activeValue=value;
				}
				
				Sigma.$invoke(this.activeColumn,'afterEdit',[value,oldValue,this.activeRecord,this.activeColumn,this]);
				Sigma.$invoke(this,'afterEdit',[value,oldValue,this.activeRecord,this.activeColumn,this]);

			}
			if (this.activeEditor &&  this.activeEditor instanceof Sigma.Dialog ) {

			}else{
				this.gridEditorCache.appendChild(this.activeEditor.getDom() );
			}
			this.activeEditor.hide();
			//this.activeEditor=null;
			//this.activeValue=null;
	},

	showValidResult : function(value,validResult,cell,colObj){
		var cells=this.getTwinCells(cell);
		Sigma.U.addClass(cells[0],'gt-cell-vaildfailed');
		Sigma.U.addClass(cells[1],'gt-cell-vaildfailed');
		validResult=[].concat(validResult);
		alert(validResult.join('\n') + '\n\n'+ value);

		Sigma.$thread(function(){
			Sigma.U.removeClass(cells[0],'gt-cell-vaildfailed');
			Sigma.U.removeClass(cells[1],'gt-cell-vaildfailed');
		} ,1500);

	},



	getTwinRows : function(row){
		var row1=row, rowNo=row.rowIndex;
		var tbody=!row1.id?this.gridTbodyList[0]:this.freezeBodyTable.tBodies[0];
		var row2= tbody?tbody.rows[rowNo]:null;
		if (!row2 && tbody && tbody.parentNode.tFoot){
			row2=tbody.parentNode.tFoot.rows[rowNo-tbody.rows.length];
		}
		return row1.id? [row1,row2,rowNo]:  [row2,row1,rowNo];

	},

	getTwinCells : function(cell,rows){
		var cell1=cell,colNo= Sigma.U.getCellIndex(cell),cell2=null , colNo2=0;
		var row=cell.parentNode;
		rows=rows||this.getTwinRows(row);
		if (rows[1]==row) {
			colNo2= colNo - (this.showIndexColumn? 1:1);
			return [ rows[0]?rows[0].cells[colNo2]:null, cell,colNo];
		}
		colNo2= colNo + (this.showIndexColumn? 1:1);
		return [cell, rows[1]?rows[1].cells[colNo2]:null, colNo];
	},

	syncTwinRowCell : function(row , cell){
		if (!row && !cell) {
			return;
		}
		row = row || cell.parentNode;
		var rows=this.getTwinRows(row);
		var rowF =rows[1];
		row=rows[0];
		if (rowF && row) {
			rowF.className=row.className;
			Sigma.U.removeClass(rowF,'gt-row-over');
		}
		if (cell) {
			var cells = this.getTwinCells( cell,rows );
			var cellF =cells[1];
			cell=cells[0];

			if (cellF && cell){
				cellF.className=cell.className;
				if (cellF.getElementsByTagName("input")[0]) {
					cell.innerHTML=cellF.innerHTML;
				}else{
					cellF.innerHTML=cell.innerHTML;
				}
			}
		}


	},

	initActiveObj : function(event , newActiveCell){
			newActiveCell = newActiveCell || Sigma.U.getParentByTagName("td",null, event);
			return this.focusCell(newActiveCell);
	},
	
	
	initActiveObj_startEdit : function(event,newCell,dbl){
			if ( this.rowNum<1  ){
				return;
			}
			var p_activeCell=  this.activeCell;
			this.initActiveObj(event,newCell);
			if (this.activeEditor && ( this.clickStartEdit || p_activeCell && p_activeCell==this.activeCell ) ) {
				Sigma.U.stopEvent(event);
				this.syncActiveObj(newCell);
				this.startEdit();
			}
	},
	
	//_onSelectionChange : function(){
	//},
	
	getLastSelectRow : function(){
		return this.selectedRows[this.selectedRows.length-1];
	},

	_onCellSelect : function(ets){
		Sigma.$invoke(this,'onCellSelect',[this.activeValue, this.activeRecord , ets.cell,ets.row, ets.colNo, ets.rowNo,ets.column,this]);
	},

	_onRowSelect : function(row,event){
		if(!row || Sigma.U.hasClass(row.cells[0],'gt-nodata-cell') ) {
			return;
		}

		if (this.multiSelect && event.shiftKey){
			var lastRow= this.getLastSelectRow();
			if (lastRow && lastRow.parentNode == row.parentNode){
				this.unselectAllRow();
				var sibling= lastRow.rowIndex>row.rowIndex?'prevElement':'nextElement';
				while (lastRow && lastRow!=row){
					this.selectRow(lastRow, true);
					lastRow= Sigma.U[sibling](lastRow);
				}
				this.selectRow(row, true);
				return row;
			}
		}

		var multi= event.ctrlKey;
		if ( Sigma.Grid.isSelectedRow(row)  ){
				if (this.multiSelect  && multi!==true && this.selectedRows.length>1){
					this.unselectAllRow();
					this.selectRow(row, true);
				}else if(multi) {
					this.selectRow(row, false);
				}
		}else{
				if ( !this.multiSelect || multi!==true ){
					this.unselectAllRow();
				}
				this.selectRow(row,true);
		}
		return row;
	},

	_onCellClick : function(event,cell,ets){
		Sigma.$invoke(this,'onCellClick',[ets.value, ets.record , 
			ets.cell,ets.row, ets.colNo, ets.rowNo,ets.column,this,event]);
	},

	_onCellDblClick : function(event,cell,ets){
		Sigma.$invoke(this,'onCellDblClick',[ets.value, ets.record , 
			ets.cell,ets.row, ets.colNo, ets.rowNo,ets.column,this,event]);
	},

	_onRowClick : function(event,row,ets){
		Sigma.$invoke(this,'onRowClick',[ets.value, ets.record , 
			ets.cell,ets.row, ets.colNo, ets.rowNo,ets.column,this,event]);
	},

	_onRowDblClick : function(event,row,ets){
		Sigma.$invoke(this,'onRowDblClick',[ets.value, ets.record , 
			ets.cell,ets.row, ets.colNo, ets.rowNo,ets.column,this,event]);
	},

// freezeBodyDiv bodyDiv
//var cellF =  Sigma.U.getParentByTagName("td",null, event);

	_onBodyClick : function(event,dbl,bodyEl,ets){
			this.endEdit();
			this.activeMe();

			var cell = ets.cell , row ;

			if (!cell || cell==bodyEl ){
				return;
			}
			
			cell= this.getTwinCells(cell)[0];
			if (cell) {
				row = cell.parentNode;
			}else{
				row = this.getTwinRows(ets.row)[0];
			}

			var et = ets.eventTarget;
			var clickCheckColum = (Sigma.U.getTagName(et)=='INPUT' && et.className=='gt-f-check');
			if (  clickCheckColum  ){
				Sigma.checkOneBox(et,this);
			}
			
			this._onCellSelect(ets);
			if (!this.selectRowByCheck ){
				this._onRowSelect(row,event);
			}
			//this._onRowSelect(row);

			if (dbl) {
				this._onCellDblClick(event,cell,ets);
				this._onRowDblClick(event,row,ets);
			}else{
				this._onCellClick(event,cell,ets);
				this._onRowClick(event,row,ets);
			}


			if (!Sigma.U.hasClass(cell,'gt-index-col')){
				this.initActiveObj_startEdit(event,cell,dbl);
			}else{

			}

			this.syncTwinRowCell( null,cell);


		},
	clickCount : 0,
	clickInterval : 500,

	_eventHandler : function(event, name,el ){
		var grid=this;
		if (el==grid.bodyDiv){
			if ( name=="scroll") {
				grid.activeMe(); 
				grid.closeGridMenu();
				grid.syncScroll();
				return;
			}
		}else if (el==grid.freezeBodyDiv){

		}
		var ets=this.getEventTargets(event);

		if (grid.lightOverRow && name=="mousemove") {
			Sigma.Grid.handleOverRow(event,grid,el);
		}

		switch(name){
			case "contextmenu":
				Sigma.$invoke(grid,'onContextMenu',[ets.value, ets.record, ets.cell, ets.row, ets.colNo, ets.rowNo, ets.column, grid]);
				break;
			case "dblclick":
				this.clickCount=0;
				return grid._onBodyClick(event,true,el,ets);
				//break;
			case "click":
				Sigma.$thread( function(){
					grid.clickCount=0;
				},grid.clickInterval );

				this.clickCount++;
				//if (this.clickCount<2) {
					return grid._onBodyClick(event,false,el,ets);
				//}
				//break;
			case "mouseover":
			    Sigma.$invoke(grid,'onMouseOver',[ets.value, ets.record, ets.cell, ets.row, ets.colNo, ets.rowNo, ets.column, grid]);
			    break; 
			case "mousemove":
			    Sigma.$invoke(grid,'onMouseMove',[ets.value, ets.record, ets.cell, ets.row, ets.colNo, ets.rowNo, ets.column, grid]);
			    break;
			case "mouseout":
			    Sigma.$invoke(grid,'onMouseOut',[ets.value, ets.record, ets.cell, ets.row, ets.colNo, ets.rowNo, ets.column, grid]);
			    break;
			default:
				name= (name.indexOf("on")!==0 ? "on"+name : name).toLowerCase();
				return Sigma.$invoke(grid, name ,[event,grid,ets,name,el]);
		}
	},

	bindEvent : function(el,name){
		var grid=this;
		Sigma.U.addEvent(el, name , function(event){
			grid._eventHandler(event,name,el);
		});
	},

	initMainEvent : function(){

		var grid=this;

		Sigma.initGlobalEvent();

		if (grid.monitorResize){
			Sigma.U.addEvent(window, 'resize' ,function(event){
				grid._onResize();
			});
			grid.hasResizeListener=true;
		}
		
		Sigma.U.addEvent(grid.gridDiv,'mousedown',	function(event){ grid.activeMe(); } ) ;
	
		grid.bindEvent(grid.bodyDiv,"scroll");
		grid.bindEvent(grid.bodyDiv,"click");
		grid.bindEvent(grid.bodyDiv,"dblclick");
		grid.bindEvent(grid.bodyDiv,"contextmenu");

		grid.bindEvent(grid.freezeBodyDiv,"click");
		grid.bindEvent(grid.freezeBodyDiv,"dblclick");
		

		Sigma.U.addEvent(grid.headDiv,'selectstart',function(event){ Sigma.U.stopEvent(event);return false;});

		grid.bindEvent(grid.bodyDiv,"mouseover");
		grid.bindEvent(grid.bodyDiv,"mouseout");
		grid.bindEvent(grid.bodyDiv,"mousemove");
		grid.bindEvent(grid.freezeBodyDiv,"mousemove");


		// todo : when mouseout ... ?
		function overHdCell(event){ 
			//Sigma.U.stopEvent(event);
			event=event||window.event;
			var cell=Sigma.U.getParentByTagName("td",null,event);
			if (cell){
				Sigma.U.addClass( cell ,'gt-hd-row-over');
			}
			if (grid.lastOverHdCell!=cell){
				Sigma.U.removeClass( grid.lastOverHdCell ,'gt-hd-row-over');
			}
			grid.lastOverHdCell=cell;
		}

		Sigma.U.addEvent(grid.headTable,'mousemove',overHdCell);
		Sigma.U.addEvent(grid.freezeHeadTable,'mousemove',overHdCell);


	},

    /***
     * @description {Method} showCellToolTip
     * To show tool tip for cell.
     * @param {Object} cell On which cell tip will show.
     * @param {Object} width Tip balloon width.
     */ 
	showCellToolTip : function(cell,width){
		if (!this.toolTipDiv) {
			this.toolTipDiv=Sigma.$e("div",{className : 'gt-cell-tooltip gt-breakline'});
			this.toolTipDiv.style.display="none";
		}
		this.toolTipDiv.innerHTML=Sigma.$getText(cell);
		this.gridDiv.appendChild(this.toolTipDiv);

		this.toolTipDiv.style.left=cell.offsetLeft+ this.bodyDiv.offsetLeft- this.bodyDiv.scrollLeft + ((Sigma.isFF2 || Sigma.isFF1)?0:this.tableMarginLeft)  + 'px';
		this.toolTipDiv.style.top=cell.offsetTop+cell.offsetHeight + this.bodyDiv.offsetTop- this.bodyDiv.scrollTop+ this.toolBarTopHeight+(Sigma.isFF?1:0) +'px';
		width && (this.toolTipDiv.style.width=width +'px');
		this.toolTipDiv.style.display="block";
	},

	setParameters : function(parameters){
		this.parameters=parameters;
	},

	setQueryParameters : function(queryParameters){
		this.queryParameters=queryParameters;
	},
	cleanQueryParameters : function(){
		this.queryParameters={};
	},

	addQueryParameter : function(key,value){
		this.queryParameters=Sigma.U.add2Map(key,value,this.queryParameters);
	},

	removeQueryParameter : function( key){
		var v=this.queryParameters[key];
		this.queryParameters[key]=undefined;
		delete this.queryParameters[key];
		return v;
	},

	/* todo */
	exceptionHandler : function(exception,optype){
		alert(optype + '\n\n'+exception);
	},
	
	getUpdatedFields : function(){
		return Sigma.$array(this.dataset.updatedFields );
	},

	getColumnInfo : function(){
		var colList=[];
		for (var cn=0;cn<this.columnList.length ;cn++ )	{
			var c=this.columnList[cn];
			var col={
				id : c.id, 
				header : c.header || c.title,
				fieldName : c.fieldName, 
				fieldIndex : c.fieldIndex , 
				sortOrder : c.sortOrder ,
				hidden : c.hidden, 
				exportable : c.exportable, 
				printable : c.printable
			};
			colList.push(col);
		}
		return colList;
	},

	getSaveParam : function(reqParam){
		reqParam= reqParam || {};
		reqParam[this.CONST.fieldsName]= this.dataset.fieldsName;
		reqParam[this.CONST.recordType]= this.dataset.getRecordType();
		reqParam[this.CONST.parameters]= this.parameters;
		this.submitUpdatedFields && (reqParam[this.CONST.updatedFields]= this.getUpdatedFields());
		return reqParam;
	},

	getLoadParam : function(reqParam){
		reqParam= reqParam || {};
		reqParam[this.CONST.recordType]= this.dataset.getRecordType();
		reqParam[this.CONST.pageInfo]= this.getPageInfo(true);
		this.submitColumnInfo && (reqParam[this.CONST.columnInfo]= this.getColumnInfo());
		reqParam[this.CONST.sortInfo]= this.getSortInfo();
		reqParam[this.CONST.filterInfo]= this.getFilterInfo();

		reqParam[this.CONST.remotePaging]= this.remotePaging;
		//reqParam[this.CONST.remoteSort]= this.remoteSort;
		//reqParam[this.CONST.remoteFilter]= this.remoteFilter;
		//reqParam[this.CONST.remoteGroup]= this.remoteGroup;

		reqParam[this.CONST.parameters]= this.parameters;

		if (this.recount){
			reqParam[this.CONST.pageInfo].totalRowNum=-1;
		}
		return reqParam;
	},

	request : function(url,reqParam,parameterType , onSuccess,onFailure){
				var grid=this;

				grid.requesting=true;
				var action = reqParam[grid.CONST.action];
	
				if (url) {
					try{
						grid.ajax=new Sigma.Ajax(url);
						grid.ajax.encoding= grid.encoding || grid.ajax.encoding;
						grid.ajax.method = grid.ajaxMethod || grid.ajax.method;
						grid.ajax.mimeType= grid.mimeType || grid.ajax.mimeType;
						grid.ajax.jsonParamName = grid.jsonParamName || grid.ajax.jsonParamName;
						grid.ajax.onSuccess=onSuccess || Sigma.$empty;
						grid.ajax.onFailure=onFailure || Sigma.$empty;
						grid.ajax.setQueryParameters(grid.queryParameters);

						grid.ajax.send( { data : reqParam });
					}catch (e){
						onFailure( {status : "Exception "+e.message },e);
					}
				}else{
					onFailure( {status : 'url is null'});
				}

	},

	load : function(recount,force){
		var grid= this ;
		var url=this.loadURL  ;
		
		// todo
		var lazyLoad = (!this.autoLoad && !this.rendered);
		if (lazyLoad) {
			grid.hideWaiting();
			grid.hideFreezeZone();
			return;
		}

		this.remotePaging = this.remotePaging===false?false : !!url ;
		// todo 
		//this.getPageInfo().pageSize=this.getPageInfo().pageSize || (this.remotePaging?20:0);
		var reqParam= this.getLoadParam();
		if (recount===true) {
			reqParam[this.CONST.pageInfo].totalRowNum=-1;
		}
		reqParam[this.CONST.action]= 'load';

		if (Sigma.$invoke(this,'beforeLoad',[reqParam,this])!==false ){
			if ( !url  || (force!==true && this.remotePaging===false && !this.isFirstLoad)  ){
				// todo 
				//if (this.dataset && this.dataset.getSize()> this.pageInfo.startRowNum-1 ){
					grid.requesting=true;
					grid.loadCallBack(function(){
								var respT={};
								respT[grid.dataRoot] = grid.dataset.data||[]; 
								
								var pInfo=grid.getPageInfo(); //grid.getPageInfo(true);
								var tn=grid.dataset.getSize() ;
								pInfo.totalRowNum = tn>0?tn:0;  // todo 
								respT[grid.CONST.pageInfo]= pInfo;
								
								return respT;
						}(grid) ,reqParam);
					return;
				//}

			}
			this.showWaiting();
			var r = this.request(url,
				reqParam, 'json',
				function(response){
					grid.loadCallBack(response,reqParam);
				},
				function(xhr,e){
					var er={};
					er[grid.CONST.exception]= ' XMLHttpRequest Status : '+xhr.status;
					grid.loadFailure( er ,e);
					grid.hideWaiting();
				});
			this.isFirstLoad=false;
			return r;
		}else{
			grid.hideWaiting();
		}
		return false;

	},
	
	query : function(param){
		this.setQueryParameters(param);
		this.lastAction='query';
		this.reload(true,true);

	},

    saveCallBack : function(response,reqParam,onNav ){
		var respT= this.responseCallBack(response,reqParam);
		if (this.requesting){
			var respD=Sigma.$extend({}, respT );
			this.requesting=false;
			var success= !(respD[this.CONST.success]===false || respD[this.CONST.success]==='false');
			
			//if ( Sigma.$invoke(this,'afterSave',[respD,success,this])!==false ){
				if (success) {
					this.saveSuccess(respD,onNav);				
				}else{
					this.saveFailure(respD);
				}
			//}
			this.hideWaiting();
			
			Sigma.$invoke(this,'afterSave',[respD,success,this]);

			/* todo */
			// succeedData   handler
			// failedData    handler
		}
	},
	
	
	saveSuccess : function(respD,onNav){
			this.dataset.cleanModifiedData(true);
			if (this.reloadAfterSave || this.autoSaveOnNav && onNav ){
				//this.dataset.clean(true);
				if (this.recountAfterSave){
					this.getPageInfo().totalRowNum=-1;
				}else if (respD[this.CONST.pageInfo]){
					this.getPageInfo().totalRowNum = respD[respD.CONST.pageInfo].totalRowNum || this.getPageInfo().totalRowNum;
				}
				this.reload();
			}
	},

	loadSuccess : function(respD){
		this.setContent (respD);
	},

	loadCallBack : function(response,reqParam){
		var respT= this.responseCallBack(response,reqParam);
		if (this.requesting){
			var respD=Sigma.$extend({}, respT );
			if (respD[this.CONST.success]===false || respD[this.CONST.success]==='false') {
				this.loadFailure(respD);
				this.hideWaiting();
			}else{
				this.loadSuccess(respD);
			}
			this.requesting=false;
		}
	},

	responseCallBack : function(response,reqParam,action){
		action = action || reqParam[this.CONST.action] ;
		response= Sigma.$invoke(this,action+'ResponseHandler',[response,reqParam]) || response;
		if (!response || Sigma.$type(response,'string','number') ){
			response={ text : response};
		}
		var respT=null;
		try{
			respT=  response.text?eval('(' + response.text + ')'):response ;
		}catch(e){
			respT={ text : response.text};
			respT[this.CONST.exception]=respT.text;
		}
			/* todo */

		if (respT[this.CONST.exception]) {
			respT[this.CONST.success]=false;
			//this.requesting=false;
			//this.exceptionHandler.exHandler(respT[this.CONST.exception],'action');
			//return false;
		}
		return  respT;

	},
    
	/**
	 * @description {Event} loadFailure:function(respD, e)
	 * Fired when failing to load data from server. The following is a default implementaion.
	 * @param {String} respD.exception record to be updated.
	 * @param {Boolean} respD.success Name of field to be updated.	 	 
	 * @param {String} e.message Error message description.          
	 */
	loadFailure : function(respD,e){
		var msg=respD[this.CONST.exception]|| (e?e.message:undefined);
		alert(' LOAD Failed! '+'\n Exception : \n'+ msg );
	},
    
	/**
	 * @description {Event} saveFailure:function(respD, e)
	 * Fired when failing to load data from server. The following is a default implementaion.
	 * @param {String} respD.exception record to be updated.
	 * @param {Boolean} respD.success Name of field to be updated.	 	 
	 * @param {String} e.message Error message description.          
	 */
	saveFailure : function(respD,e){
		var msg=respD[this.CONST.exception]|| (e?e.message:undefined);
		alert(' SAVE Failed! '+'\n Exception : \n'+msg);
	},
	
	/**
	 * @description {Event} exportFailure:function(respD, e)
	 * Fired when failing to export data to xml/excel/pdf etc. The following is a default implementaion.
	 * @param {String} respD.exception record to be updated.
	 * @param {Boolean} respD.success Name of field to be updated.	 	 
	 * @param {String} e.message Error message description.          
	 */
	exportFailure : function(respD,e){
		var msg=respD[this.CONST.exception]|| (e?(e.message||''):'');
		alert(' Export '+ respD.type+' ( '+respD.fileName+' ) Failed! '+'\n Exception : \n'+ msg );
	},


	updateRecordField :function(td,newValue){
		var colObj= this.getColumn(td);
		if (colObj ) {
			var record=this.getRecordByRow(td.parentNode);
			return this.update(record,colObj.fieldName, newValue);
		}
		return false;
	},


	update : function(record,fieldName,newValue){
		if (Sigma.$invoke(this,'beforeUpdate',[record,fieldName,newValue])!==false) {
				this.dataset.updateRecord(record,fieldName, newValue);
				return true;
		}

	},

	cloneDefaultRecord : function(){
		var defR= this.defaultRecord;
		if (Sigma.$type(defR,'function')){
			defR=defR(this,this.dataset,this.dataset.getSize());
		}
		return  Sigma.$clone(defR);
	},
	
	renderInsertedRow : function(record){
			var  newTR ,newFTR;
			var colNum = this.colNum;
			var trStart = Sigma.T_G.rowStart(this,this.rowNum);
			if (!this.gridTable.tFoot){
				this.gridTable.appendChild(Sigma.$e('tfoot'));
			}
			if (!this.freezeBodyTable.tFoot){
				this.freezeBodyTable.appendChild(Sigma.$e('tfoot'));
			}
			newTR=Sigma.U.createTrFromHTML( this.buildGridRow( trStart , record, this.rowNum , colNum),this.gridTable.tFoot );
			if (this.showIndexColumn){
				newFTR=Sigma.U.createTrFromHTML( this.buildGridIndexRow( trStart , record, this.rowNum , colNum) ,this.freezeBodyTable.tFoot);
			}else{
				newFTR=Sigma.U.createTrFromHTML(trStart+"</tr>",this.freezeBodyTable.tFoot);
				newFTR.appendChild(Sigma.T_G.freezeBodyCell(this,10,null));
		
			}
			Sigma.U.addClass(newTR,'gt-row-new');
			Sigma.U.addClass(newFTR,'gt-row-new');
			var rowId=record[Sigma.Const.DataSet.ROW_KEY];
			newTR.id=rowId;
			newTR.setAttribute(Sigma.Const.DataSet.INDEX, record[Sigma.Const.DataSet.SN_FIELD] );
			return [newTR,newFTR];

	},


	hasDataRow : function(){
		var hasRow = this.getRows().length>0;
		var hasNew = !!(this.gridTable.tFoot&&this.gridTable.tFoot.rows.length>0);
		return hasRow || hasNew;
	},

	toggleEmptyRow : function(){
		if (!this.hasDataRow()){
			var rowHTML=['<tr class="gt-row gt-row-empty" >'];
			for (var cn=0;cn<this.colNum ;cn++ )	{
				rowHTML.push(Sigma.T_G.cell(this.columnList[cn],'&#160;'));
			}
			rowHTML.push(Sigma.T_G.rowEndHTML);
			Sigma.U.createTrFromHTML(rowHTML.join(''),this.gridTbodyList[0]);
		}else{
			var row=this.getFirstRow();
			if(this.isEmptyRow(row)){
				Sigma.U.removeNode(row);
			}
		}


	},

//load, changePage

		refreshGrid : function(grid){
			grid= grid || this;
			
			var rowNo=grid.dataset.getSize();

			var pInfo=grid.getPageInfo();
			
			if (!grid.remotePaging && !pInfo.pageSize){
				pInfo.pageSize=rowNo;
			}

			rowNo=rowNo>pInfo.pageSize ? pInfo.pageSize :rowNo;

			var rowBegin =  grid.dataset.startRecordNo;

			grid.rowNum=rowNo;
			grid.rowBegin= rowBegin ;
			grid.rowEnd= rowBegin + rowNo ;
			grid.colNum=grid.columnList.length;


			grid.tableMarginLeft = 0;


			var tableHTML=[];
			var freezeTableHTML=[];

			// TODO : "IE close" bug ??? 
			//if (grid.freezeHeadTable) {
				//grid.freezeHeadTable.appendChild(Sigma.$e("tbody"));
			//}

			grid.buildGridTable(grid, tableHTML, freezeTableHTML);

			/* todo 
			if ( grid.rowNum>0){
				// create index column in this 'for'
				grid.buildGridTable(grid, tableHTML, freezeTableHTML);
			}else{
				grid.buildGridTable(grid, tableHTML, freezeTableHTML);
			//	grid.buildEmptyGridTable(grid, tableHTML, freezeTableHTML);
			}
			*/

			grid.bodyDiv.innerHTML=tableHTML.join('');

			grid.freezeBodyDiv.innerHTML=freezeTableHTML.join('');

			//var testDiv=Sigma.$e("div",  { innerHTML :'abcde12345', className : 'test-div' });
			//grid.bodyDiv.appendChild(testDiv);


			var gTable=Sigma.U.firstChildElement(grid.bodyDiv);
			if (gTable){
				if ( Sigma.U.getTagName(gTable)!='TABLE'){
					gTable=Sigma.U.nextElement(gTable);
				}
				if (Sigma.U.getTagName(gTable)=='TABLE'){
					grid.gridTable=gTable;
					grid.gridTbodyList.push(gTable.tBodies[0]);
				}

			}

			gTable=Sigma.U.firstChildElement(grid.freezeBodyDiv);
			if (gTable){
				if (Sigma.U.getTagName(gTable)!='TABLE'){
					gTable=Sigma.U.nextElement(gTable);
				}
				grid.freezeBodyTable=gTable;
				///if (!Sigma.isIE) {
				//	grid.freezeHeadDiv.style.left="-1px";
				//	grid.freezeBodyDiv.style.left="-1px";
				///}
			}

			grid.bodyFirstRow=grid.getFirstRow();
			if (grid.rowNum<1){

				for (var cn=0;cn<grid.colNum ;cn++ )	{
					var colObj=grid.columnList[cn];
					if (grid.bodyFirstRow) {
					colObj.firstCell=grid.bodyFirstRow.cells[cn];
					colObj.firstCell.style.height="0px";
					colObj.firstCell.style.borderBottomWidth="0px";

					}
				}
			}

			grid.hasIndexColumn = grid.showIndexColumn;

			//grid.freezeBodyDiv.style.height= grid.bodyDiv.clientHeight +"px";
			grid.isEmptyfreezeZone=true;
			//Sigma.Grid.handleGridFreeze(grid);


			Sigma.$thread( function(){
				grid.freezeBodyDiv.style.height= grid.bodyDiv.clientHeight+"px";
				grid.syncScroll();
				//grid.getAllRows();
			} );
	},

	hideFreezeZone : function(){
		this.freezeHeadDiv && (this.freezeHeadDiv.style.display="none");
		this.freezeBodyDiv && (this.freezeBodyDiv.style.display="none");
	},
	
	cleanFreezeHead : function(){
		var fhBody=this.freezeHeadTable.tBodies[0];
		for (var rn=fhBody.rows.length-1; rn>=0; rn--) {
			Sigma.U.removeNodeTree(fhBody.rows[rn]);
		}
	},

	buildGridTable : function(grid,tableHTML, freezeTableHTML ){

			var indexColumnWidth=(''+ grid.rowEnd ).length;
			indexColumnWidth= (indexColumnWidth<2?1.5:indexColumnWidth)*7+ 2 + 1 ;
			var indexCellWidth = indexColumnWidth + this.cellWidthFix;
			var indexInnerWidth = indexColumnWidth + this.innerWidthFix;
			var tdWidthStyle='style="width:'+ indexCellWidth   +'px;"';
			var divWidthStyle='style="width:'+ indexInnerWidth +'px;"';

			this.indexColumnCell=['<td class="gt-index-col" '+tdWidthStyle+' ><div class="gt-inner" '+divWidthStyle+' >',
					'</div></td>'
			];

		if (grid.showIndexColumn){

			grid.tableMarginLeft = indexColumnWidth  ;

			// TODO : "IE close" bug ??? 
			grid.cleanFreezeHead();

			//var colN=Sigma.$e("td",{ className: 'gt-index-col', innerHTML:'<div class=\"gt-inner\" '+divWidthStyle +'>&#160;</div>'} );
			//colN.style.width= indexCellWidth +"px";
			var headRow=Sigma.$e("tr",{ className : "gt-hd-row" });
			var colN=Sigma.T_G.freezeHeadCell(grid,indexColumnWidth,null);
			headRow.appendChild(colN);
			grid.freezeHeadTable.tBodies[0].appendChild(headRow);

			grid.freezeHeadDiv.style.left= grid.freezeBodyDiv.style.left= this.freezeFixW+'px'; //(0-grid.freezeHeadTable.offsetWidth)+"px"; //(0-grid.freezeBodyTable.offsetWidth)+"px";
			grid.headTable.style.marginLeft= grid.tableMarginLeft +"px";
			grid.freezeHeadDiv.style.display=grid.freezeBodyDiv.style.display="block";
			grid.freezeBodyDiv.style.height= parseInt(grid.bodyDiv.style.height)+"px";

		}else{
			grid.freezeHeadDiv.style.display=grid.freezeBodyDiv.style.display="none";
			//grid.freezeHeadDiv.style.left=  (0-this.freezeHeadDiv.offsetWidth)+"px";
			//grid.freezeBodyDiv.style.left= (0-this.freezeBodyDiv.offsetWidth)+"px";
		}
		freezeTableHTML.push(Sigma.T_G.tableStartHTML);

		tableHTML.push( Sigma.U.replaceAll(Sigma.T_G.tableStartHTML,"margin-left:0px","margin-left:"+grid.tableMarginLeft+"px") ); //id = grid.id +"_bodyTable"

		var startR= grid.rowBegin ;
		var endR= grid.rowEnd; // startR+grid.rowNum;
		grid.currentRowNum = startR;

		grid.getGridBodyHTML(startR,endR,-1,tableHTML,freezeTableHTML);

		freezeTableHTML.push(Sigma.T_G.tableEndHTML);

	},

	// todo 
	isNextGroup : function(record,lastRecord,rn){
		//return rn==3;
	},

	isGroupRow : function(cell,row){
		cell=cell|| (row?row.cells[0]:null);
		return Sigma.U.hasClass(cell ,'gt-group-row');
	},
	isEmptyRow : function(row){
		return !row||Sigma.U.hasClass(row,'gt-row-empty');
	},

	isInsertRow : function(row){
		return Sigma.U.hasClass(row,'gt-row-new');
	},
	isDelRow : function(row){
		return Sigma.U.hasClass(row,'gt-row-del');
	},

	buildGridIndexRow : function( trStart , record,rn ,colNum){
		var rowHTML=[trStart];
		rowHTML.push( this.indexColumnCell[0]  );
		rowHTML.push( rn+ 1 + this.indexColumnCell[1] );
		rowHTML.push( Sigma.T_G.rowEndHTML );
		return rowHTML.join('');
	},

	buildGridRow : function( trStart , record,rowNo,colNum ,groupInfo){
			var rowHTML=[trStart];
			var _cn=0;
			for (var cn=0;cn<colNum ;cn++ )	{
				var col=this.columnList[cn];
				if (col.hidden && !this.renderHiddenColumn) {
					continue;
				}
				var attr=groupInfo&&groupInfo[col.id]?groupInfo[col.id].attr:null;
				rowHTML.push(Sigma.T_G.cell(col,
						col.renderer(record[col.fieldIndex],record,col,this,_cn,rowNo) , attr)
					);
				_cn++;
			}
			rowHTML.push( Sigma.T_G.rowEndHTML );
			return rowHTML.join('');
	},

	buildGroupGridRow : function( trStart , record,rowNo,colNum){
			var rowHTML=[trStart];
			var cell='<td colspan="'+colNum+'" class="gt-group-row" > + '+rowNo+' -------------</td>';
				rowHTML.push(cell);
			rowHTML.push( Sigma.T_G.rowEndHTML );
			return rowHTML.join('');
	},

	resetFreeze: function(grid){

	},

	updateFreezeState : function(){
		if (this.frozenColumnList){
			var i,colObj;
			for (i=0;i<this.frozenColumnList.length;i++){
				colObj= this.columnMap[ this.frozenColumnList[i] ];
				if (colObj)	{
					this.moveColumn( colObj.colIndex ,i,true );
				}
			}
			for (i=0;i<this.frozenColumnList.length;i++){
				colObj= this.columnMap[ this.frozenColumnList[i] ];
				if (colObj)	{
					colObj.freeze(true);
				}
			}
		}
	},

	/* todo */ 
	getGroupInfo : function(startR,endR){
		return this.getMergeGroupInfo(startR,endR);
	},
	getSeparateGroupInfo : function(startR,endR){
		var colNum=this.colNum;
		var groupCol=null;
		for (var cn=0;cn<colNum ;cn++ )	{
			var col=this.columnList[cn];
			if (col.grouped) {
				groupCol=col;
				break;
			}
		}
		var rows={};
		if (groupCol) {
			var rowNum=endR-startR;
						var rn=startR;
			for (var i=0; i<rowNum; i++) {
				var record= this.dataset.getRecord(rn++);
				if (!record){
					continue;
				}
				var key=this.getUniqueField(record);
			}
		}
	},

	getMergeGroupInfo : function(startR,endR){

		var colNum=this.colNum;
		var rowNum=endR-startR;
		var row,cell ,rspan=1;
		var groupH={} , groupValue=null ;
		var rows=[];
		for (var cn=0;cn<colNum ;cn++ )	{
			var colObj=this.columnList[cn];
			var rn=startR;
			for (var i=0; i<rowNum; i++) {
				var record= this.dataset.getRecord(rn++);
				if (!record){
					continue;
				}
				row=rows[i]=rows[i]||{};
				var ss=row['__gt_group_s_'];

				if (colObj.grouped) {
					cell=row[colObj.id]=row[colObj.id]||{};
					if (groupValue==record[colObj.fieldIndex] && (!ss&&ss!==0||ss>cn)){
						cell.attr=' style="display:none;" ';
						rspan++;
					}else{
						groupH.attr=' rowspan="'+rspan+'" style="background-color: #eef6ff;"  ';
						rspan=1;
						groupH=cell;
						groupValue=record[colObj.fieldIndex];
						row['__gt_group_s_']=cn;

					}
				}
			}
			groupH.attr=' rowspan="'+rspan+'" style="background-color: #eef6ff;"  ';

		}

		return 	rows;	
	},


	createSortInfo : function(colObj){
		return { columnId : colObj.id , fieldName : colObj.fieldName , sortOrder : colObj.sortOrder , getSortValue : colObj.getSortValue , sortFn : colObj.sortFn };
	},


	_doSort : function(){
		if (!this.sortInfo || this.sortInfo.length<1){
			return ;
		}
		this.dataset.sort( this.sortInfo );
	},

	/* todo */
	addSortInfo : function(sortI,multi) {
		multi= multi||multi===false?multi:this.multiSort;
		var groupSort=[],hasAdd=false;
		for (var cn=0;cn<this.columnList.length ;cn++ )	{
			var colObj=this.columnList[cn];
			if (colObj.grouped) {
				if (!hasAdd && colObj.id==sortI.columnId){
					colObj.sortOrder = sortI.sortOrder;
					hasAdd=true;
				}else{
					var s=colObj.sortOrder;
					s=s=='asc'||s=='desc'?s:'asc';
					colObj.sortOrder=s;
				}
				groupSort.push( this.createSortInfo(colObj) );

			}
		}
		if (!hasAdd && multi!==true) {
			this.sortInfo=groupSort.concat(sortI);
			return;
		}

		this.sortInfo = this.sortInfo || [];
		var id=sortI.columnId;
		var has=false,sI,i;
		for (i=0; i<this.sortInfo.length; i++) {
			sI=this.sortInfo[i];
			if (sI && sI.columnId === id) {
				sI.sortOrder=sortI.sortOrder;
				has=true;
				break;
			}
		}
		!has && (this.sortInfo.push(sortI) );
		for (i=0;i<this.sortInfo.length;i++) {
			sI=this.sortInfo[i];
			if (!sI ||  ( !sI.sortOrder || sI.sortOrder=='defaultsort') ) {
				this.sortInfo.splice(i,1);
				i--;
			}
		}

	},

	updateSortState : function(){
		var colObj,cn;
		for (cn=0;cn<this.colNum ;cn++ )	{
				colObj=this.columnList[cn];
				colObj.sortIcon && (colObj.sortIcon.className="gt-hd-icon");
				colObj.frozenSortIcon && (colObj.frozenSortIcon.className="gt-hd-icon");
				colObj.sortOrder=null;
		}
	
		if (!this.sortInfo || this.sortInfo.length<1){
			return ;
		}

		for (var i=0; i< this.sortInfo.length; i++) {
			var sI=this.sortInfo[i];
			if (sI) {
				colObj= this.columnMap[ sI.columnId];
				var sortOrder = sI.sortOrder || 'defaultsort';

				colObj.sortOrder= sortOrder ;

				Sigma.U.addClass(colObj.sortIcon,"gt-hd-"+sortOrder);
				Sigma.U.addClass(colObj.frozenSortIcon,"gt-hd-"+sortOrder);
						
			}		
		}

		var newFirstRow= this.getFirstRow();
		if (newFirstRow && !this.isEmptyRow(newFirstRow)){
			//if (this.bodyFirstRow != newFirstRow){
				this.bodyFirstRow = newFirstRow;
				var _cn=0;
				for (cn=0;cn<this.colNum ;cn++ )	{
						var _colObj=this.columnList[cn];
						if (!_colObj.hidden||this.renderHiddenColumn) {
							_colObj.firstCell=this.bodyFirstRow.cells[_cn];
							_colObj.firstCell.className=_colObj.styleClass;
							_cn++;
						}
				}
			//}
		}

	},

	getRecordByRow  : function(row){
		// this.insertedRecords[ record[Sigma.Const.DataSet.SN_FIELD]  ]
		if (!row){
			return null;
		}
		if (this.isInsertRow(row)){
			var key= row.getAttribute(Sigma.Const.DataSet.INDEX);
			return this.dataset.insertedRecords[key];
		}
		var rowNo = row.getAttribute(Sigma.Const.DataSet.INDEX)/1 ;
		return rowNo||rowNo===0?this.dataset.getRecord(rowNo):null;
	},

	
	getRowByRecord : function(record){
		var rowId= record[Sigma.Const.DataSet.ROW_KEY];
		//var row=this.getRow(rowIndex);
		return Sigma.doc.getElementById(rowId);
	},

	getUniqueField : function(record){
		return record[this.dataset.uniqueField];
	},

	// checkColumn
	updateCheckState : function(){
		var cCol=this.checkColumn;
		if (cCol){
			var idx=cCol.colIndex;
			var rows=this.getRows();
			for (var i=0,len=rows.length; i<len; i++) {
				var row=rows[i];
				var cell=row.cells[idx];
				var chk=cell?cell.getElementsByTagName('input'):null;
				chk=chk?chk[0]:chk;
				
				if (chk && chk.checked){
					
					this.selectRow(row, true);
				}
			}
		}
	},

	updateEditState : function(){
		var newRecords=this.getInsertedRecords(), row, record ,i,j,k;
		for (i=0;i<newRecords.length;i++ ){
			this.renderInsertedRow(newRecords[i]);
		}

		for (k in this.dataset.updatedRecords ) {
			record = this.dataset.updatedRecords[k];
			var fields= this.dataset.updatedRecordsBak[k];
			// todo
			row=this.getRowByRecord(record);

			if (row){
				var recordN=this.getRecordByRow(row);
				if (recordN){
					for (var f in fields) {
						recordN[f]=record[f];
						for (i=0,j=this.columnList.length; i<j; i++) {
							var colObj=this.columnList[i];
							if (f==colObj.fieldIndex && row.cells) {
								this.dirty(row.cells[i]);
								row.cells[i].firstChild.innerHTML= colObj.renderer(
									recordN[colObj.fieldIndex],
									recordN,colObj,
									this,i,
									row.rowIndex);
							}
						}
					}
				}
				this.dataset.updatedRecords[k]=recordN;
			}
		}

		for (k in this.dataset.deletedRecords ) {
			record = this.dataset.deletedRecords[k];
			row=this.getRowByRecord(record);
			this.del(row);
		}

	},

	syncScroll : function(left,top){
		if (Sigma.$chk(left)){
			this.bodyDiv.scrollLeft=left;
		}
		if (Sigma.$chk(top)){
			this.bodyDiv.scrollTop=top;
		}
		this.headDiv.scrollLeft=this.bodyDiv.scrollLeft;
		this.freezeBodyDiv.scrollTop=this.bodyDiv.scrollTop;
		this.scrollLeft=this.bodyDiv.scrollLeft;
		this.scrollTop=this.bodyDiv.scrollTop;

		/* sync other gt-grid */
	},


	initToolbar : function(){

		if (this.resizable && (this.toolbarPosition=='bottom'||this.toolbarPosition=='b')  && this.toolBarBox){
			this.resizeButton=Sigma.$e("div",{ id:this.id +"_resizeButton",className:"gt-tool-resize", innerHTML:'&#160;'
				});
			this.resizeButton.setAttribute('unselectable','on');
			this.toolBarBox.appendChild(this.resizeButton);
			var grid=this;
			Sigma.U.addEvent(this.resizeButton,"mousedown",function(event){ Sigma.Grid.startGridResize(event,grid); } ) ;
		}
		
		this.createGridMenu();

		if (this.toolbarContent && this.toolbarPosition && this.toolbarPosition!='none'){

			this.toolbarContent = this.toolbarContent.toLowerCase();
			var barbutton= this.toolbarContent.split(' ');

				var lastt=null;
				for (var j=0;j<barbutton.length ;j++ ){
					var b=barbutton[j];
					if (b=='|'){
						var sp= Sigma.ToolFactroy.create(this,'separator',true);
						if (lastt){
							lastt.separator=sp;
						}
					}else if (b=='state' || b=="info" || b=='pagestate'){
						if (!this.pageStateBar){
							this.pageStateBar = Sigma.ToolFactroy.create(this,'pagestate',this.showPageState);
						}
						if (j!=barbutton.length-1){
							 this.pageStateBar.className+=' gt-page-state-left';
						}

						lastt=this.pageStateBar;
					}else if (b=="nav") {
						this.navigator.buildNavTools(this);
						lastt = this.navigator;
					}else{
						var ub=b.charAt(0).toUpperCase()+b.substring(1);
						lastt = this.tools[b+'Tool'] = Sigma.ToolFactroy.create(this,b,this['show'+ub+'Tool']);
					}
				}
		}

		this.expendMenu={};

		this.over_initToolbar=true;

		//this.refreshToolBar();

	},


	refreshToolBar : function(pageInfo,doCount){
		pageInfo && ( this.setPageInfo(pageInfo) );
		if (this.over_initToolbar){
			this.navigator.refreshState(pageInfo,doCount);
			this.navigator.refreshNavBar();
			var pageInput= this.navigator.pageInput;
			if (this.pageStateBar){
				pageInfo=this.getPageInfo();
				//this.pageStateBar.innerHTML="";
				Sigma.U.removeNode(this.pageStateBar.firstChild);
				if (pageInfo.endRowNum-pageInfo.startRowNum<1) {
					this.pageStateBar.innerHTML= '<div>'+this.getMsg('NO_DATA')+'</div>';
				}else{
					this.pageStateBar.innerHTML= '<div>'+Sigma.$msg( this.getMsg( pageInput?'PAGE_STATE':'PAGE_STATE_FULL') ,
						pageInfo.startRowNum,pageInfo.endRowNum,pageInfo.totalPageNum,pageInfo.totalRowNum , pageInfo.pageNum )+'</div>';
				}
			}
		}

	},

	createGridMenu :function(){
		if (!this.showGridMenu || !this.toolBarBox || !this.toolBar){
			return;
		}
		var grid=this;
		var gridId=grid.id;
		this.gridMenuButton=new Sigma.Button({gridId:this.id,parentItem:this, container : this.toolBar,
									cls:"gt-tool-gridmenu", withSeparator : true });
		var groupColDialog = !this.allowGroup? null : Sigma.createColumnDialog('group' , {gridId : gridId ,
							checkValid : function(r){ return r.grouped; }, checkFn :'group' , uncheckFn:'ungroup' , checkType :grid.multiGroup?'checkbox':'radio',
					canCheck : function(col){ return  col.filterable!==false&&!col.hidden; } });

		var freezeColDialog = !this.allowFreeze? null : Sigma.createColumnDialog('freeze' , {gridId : gridId ,
							checkValid : function(r){ return r.frozen; }, checkFn :'freeze' , uncheckFn:'unfreeze',
					canCheck : function(col){ return col.freezeable!==false&&!col.hidden; } });

		var hideColDialog = !this.allowHide ? null : Sigma.createColumnDialog('show' , {gridId : gridId ,
							checkValid : function(r){ return !r.hidden; }, checkFn :'show' , uncheckFn:'hide',
					canCheck : function(col){ return col.hideable!==false&&!col.frozen; } });


		function showColDialog(columnDialog,title){
			if (!columnDialog) { return; }
			columnDialog.show();
			columnDialog.setTitle(title);
			grid.gridMenuButton.closeMenu();
		}
		var p = this.toolbarPosition!='bottom'?'B':'T';

		function _changeSkin(skin){
			return function(){ Sigma.Grid.changeSkin(grid,skin); };
		}

		var skinItem=null;
		
		if (this.allowCustomSkin){
			skinItem=new Sigma.MenuItem({gridId:this.id,type :'',text: this.getMsg('CHANGE_SKIN'),cls:'gt-icon-skin'  });
			var skinItemList=[];
			for (var i=0;i<this.skinList.length;i++){
				skinItemList.push(new Sigma.MenuItem({gridId:this.id,type :'radiobox',text:this.skinList[i].text,checked:i===0,onclick : _changeSkin(this.skinList[i].value) } ) );
			}
			skinItem.addMenuItems(skinItemList,'R');
		}

		this.gridMenuButton.addMenuItems( [

			skinItem,

			groupColDialog ? new Sigma.MenuItem({gridId:grid.id,type :'',text: grid.getMsg('MENU_GROUP_COL'),cls:'gt-icon-groupcol',
				onclick : function(){ showColDialog(groupColDialog, grid.getMsg('MENU_GROUP_COL') ); } }):null,


			freezeColDialog ? new Sigma.MenuItem({gridId:grid.id,type :'',text: grid.getMsg('MENU_FREEZE_COL'), cls:'gt-icon-freeze',
				onclick : function(){ showColDialog(freezeColDialog, grid.getMsg('MENU_FREEZE_COL') ); } }):null,


			hideColDialog ? new Sigma.MenuItem({gridId:grid.id,type :'',text: grid.getMsg('MENU_SHOW_COL'),cls:'gt-icon-hidecol',
				onclick :function(){ showColDialog(hideColDialog,grid.getMsg('MENU_SHOW_COL') ); } }):null,

				new Sigma.MenuItem({gridId:this.id,type :'',text:SIGMA_GRID_VER })
		 ],p );

	},

	closeDialog : function(){
		this.activeDialog && this.activeDialog.close();
		this.activeDialog=null;
	},

	getSortInfo : function(){
		return this.sortInfo || [];
	},
	
	setTotalRowNum : function(tn){
		this.getPageInfo().totalRowNum=tn;
	},

	getTotalRowNum : function(refresh){
		return this.getPageInfo(refresh).totalRowNum;
	},

	addSkin:function(skin){
			if (Sigma.$type(skin,'string') ) {
				skin={ text:this.getMsg('STYLE_NAME_'+skin.toUpperCase()),value:skin.toLowerCase()};
			}
			this.skinList.push(skin);
	},

	addRows :function(rows){
			for (var i=0;i<rows.length ;i++ ){
				this.gridRowList.push(rows[i]);
			}
	},

	getFirstRow :function(){
			return this.gridTbodyList[0]?this.gridTbodyList[0].rows[0]:null;
		},

	getRows :function(){
			return this.gridTbodyList[0].rows;
	},
	getRow :function(rowNo){
		if ( Sigma.U.isNumber(rowNo) ) {
			return this.getRows()[rowNo];
		}
		return rowNo;
	},

	getRowNumber : function(){
		return this.rowNum;
	},
	hasData : function(){
		return this.rowNum>0 ;
	},

	dirty : function(cell){
		Sigma.U.addClass(cell,'gt-cell-updated');
	},

	/* todo : selectRow  selectCell */
	selectFirstRow : function(){
		var row=this.getRow(0);
		if (row){
			this.selectRow(row, true);
		}
	},

	unselectAllRow : function(){
		var grid=this;
		Sigma.$each(this.selectedRows,function(row) { 
			Sigma.U.removeClass(row,"gt-row-selected");
			grid.syncTwinRowCell(row);
		} );
		this.selectedRows=[];
		this.activeRow=null;
	},

	getSelectedRecord : function(){
		return this.getRecordByRow(this.selectedRows[this.selectedRows.length-1])||this.activeRecord ;
	},



	getGridHTML : function(multiPage , gridHTML){
		var hdHTML=this.getGridHeadHTML() ;
		gridHTML = gridHTML || [ 	Sigma.T_G.bodyTableStart(this.id,false), hdHTML ,'<tbody>' ];
		var startR= 0 ;
		var endR= this.dataset.getSize(); // startR+grid.rowNum;
		var pageSize = multiPage?this.getPageInfo().pageSize:-1;
		gridHTML.push( this.getGridBodyHTML(startR ,endR , pageSize,gridHTML,null,true)  );
		return gridHTML.join('');

	},
	
	getGridHeadHTML : function(){
			var hdHTML= this.headTable.innerHTML ;
			var f_TR= hdHTML.toLowerCase().indexOf('<tr');
			var f_cTR= hdHTML.toLowerCase().lastIndexOf('</tr>');
			if (this.customHead){
				f_TR= hdHTML.toLowerCase().indexOf('<tr',f_TR+3);
			}
			hdHTML = hdHTML.substring(f_TR,f_cTR+ '</tr>'.length);

			return '<!-- gt : head start  -->'+hdHTML+'<!-- gt : head end  -->';

	},
	

	getGridBodyHTML : function(startR, endR, pageSize, tableHTML,freezeTableHTML,isExport){
		var grid=this;

		var colNum =grid.colNum ;
		var groupInfo=grid.getGroupInfo(startR,endR);
		var TRE=" >" ,record=null,lastRecord=null, gRn=0 , rowId='';
		
        /* start : ********** main for-loop ************ */
		for (var rn=startR;rn<endR;rn++){
			record=grid.dataset.getRecord(rn);
			if (!record) {
				break;
			}
			
			var rowAttribute=grid.customRowAttribute(record,rn,grid)||'';
			var trStartS = Sigma.T_G.rowStartS(grid,rn,rowAttribute);

			if (!isExport) {
				rowId=grid.rowKey + grid.getUniqueField(record);
				record[Sigma.Const.DataSet.ROW_KEY]=rowId;
				grid.currentRowNum++;
				if (grid.showIndexColumn){
					freezeTableHTML.push( grid.buildGridIndexRow( trStartS+ TRE , record,rn , colNum));
				}
			}else{
				if (rn>0 && pageSize>0 && (rn % pageSize) ===0 ) {
					tableHTML.push( '\n<!-- gt : page separator  -->\n' );
				}
			}

			/* todo */
			if (grid.isNextGroup(record,lastRecord,rn )){
				tableHTML.push(  grid.buildGroupGridRow( trStartS + TRE , record,rn , colNum));
			}
			var gI=groupInfo[gRn++];


			tableHTML.push(  grid.buildGridRow( trStartS+ ' id="'+rowId+'"'+ TRE , record,rn , colNum ,gI));
			lastRecord=record;

		}
		tableHTML.push(Sigma.T_G.tableEndHTML);
        /* end : ********** main for-loop ************ */

	},
		
	getHiddenColumnStyle : function(attr){
		var grid=this;
		var hiddenCssText=[];
		Sigma.$each(grid.columnList,function(colObj,i){
				if (  colObj.hidden===true || (attr&&colObj[attr]===false) ) {
					hiddenCssText.push(colObj.CLASS_PREFIX + colObj.styleClass +" { display:none;width:0px; }" );
					hiddenCssText.push(colObj.CLASS_PREFIX + colObj.innerStyleClass +" { display:none;width:0px; }" );
				}
			}
		);
		var css=hiddenCssText.join('\n');
		return 	Sigma.U.replaceAll(css,".gt-grid ","");
	},
    
	getAllRows : function(){
		var grid=this;
		if (grid.gridRowList.length===0){
			Sigma.$each(grid.gridTbodyList,function(gTbody){
				grid.addRows(gTbody.rows);
			} );
		}
		return grid.gridRowList;
	},

	getAllFreezeRows : function(){
		var grid=this;
		if (grid.gridFreezeRowList.length===0){
				var rows= grid.freezeBodyTable.tBodies[0].rows;
				for (var i=0;i<rows.length ;i++ ){
					grid.gridFreezeRowList.push(rows[i]);
				}
		}
		return grid.gridFreezeRowList;
	},

	filterHandler : {
		hide : function(filterInfo){
			filterInfo= filterInfo || this.filterInfo;
			var rs= Sigma.Grid.filterCheck[filterInfo.checkType](filterInfo.value,filterInfo.checkValue);
			if (rs) {

			}else{

			}
		}
	},
			
	cleanActiveObj : function(){
		this.activeCell=null;
		this.activeRow=null;
		this.activeColumn=null;
		this.activeEditor=null;
		this.activeRecord=null;
		this.activeValue=null;
	},
	cleanTable : function(all){

		this.closeGridMenu();
		if (this.endEdit()===false){
			return;
		}
		this.lastOverHdCell=null;
		if (all!==false) {
			this.cleanActiveObj();
			//TODO 
			for (var i=0;i<this.selectedRows.length ;i++ ){
				this.selectedRows[i]=null;
			}
			this.selectedRows=[];
			this.overRow=null;
			this.overRowF=null;
		}

		this.gridRowList=[];
		this.bodyFirstRow=null;
		for (var cn=0;cn<this.colNum ;cn++ )	{
				var colObj=this.columnList[cn];
				colObj.firstCell=null;
				colObj.frozenSortIcon=null;
				colObj.frozenHdTool=null;
				colObj.frozenHeadCell=null;
		}
		var grid=this;
		Sigma.$each(this.gridTbodyList,function(gTbody,idx){
			Sigma.U.removeNode(gTbody);
			grid.gridTbodyList[idx]=null;
		});
		Sigma.U.removeNode(this.gridTable);
		this.gridTable=null;
		this.gridTbodyList=[];

		// TODO : "IE close" bug ??? 
		this.cleanFreezeHead();

		if (this.freezeBodyTable) {
			Sigma.U.removeNode(this.freezeBodyTable.tBodies[0],this.freezeBodyTable);
			this.freezeBodyTable=null;
		}

		//this.bodyDiv.innerHTML='';
		//this.freezeBodyDiv.innerHTML='';
	},

	destroy : function(){
		var grid=this;
		Sigma.$invoke(this,'beforeDestroy');
		this.cleanTable();
		var dList=['gridMenuButton','filterDialog','charDialog','navigator'];
		Sigma.$each(dList,function(k,i){
			if (grid[k] && grid[k].destroy) {
				grid[k].destroy();
			}
			grid[k]=null;
		});

		for (var k in this.tools) {
			var t=this.tools[k];
			if (t && t.destroy) {
				t.destroy();
			}
			this.tools[k]=null;
		}

		Sigma.U.removeNodeTree(this.gridDialogTitle);
		Sigma.U.removeNodeTree(this.gridDialogBody);
		Sigma.U.removeNodeTree(this.gridDialog);
		this.gridDialog=this.gridDialogBody=this.gridDialogTitle=null;


		for (var cn=0;cn<this.colNum ;cn++ )	{
				var colObj=this.columnList[cn];
				colObj.destroy();
		}


		Sigma.$each(this.domList,function(k,i){
			Sigma.U.removeNode(grid[k]);
			grid[k]=null;
		});

		this.freezeBodyTable=this.gridTable=this.bodyFirstRow=this.lastOverHdCell=this.overRowF=this.overRow=null;

		this.gridFreezeRowList = [];
		this.selectedRows = [];
		this.cacheBodyList = [];
		this.frozenColumnList = [];
		this.sortedColumnList = [];
		this.checkedRows = {};
		this.gridTbodyList=[];
		this.gridRowList=[];

		if (Sigma.activeGrid==this) {
			Sigma.activeGrid=null;
		}
		Sigma.GridCache[this.id]=null;
		delete Sigma.GridCache[this.id];

	},
	
	//------------------------------------------------------
	//-----the following function is open to public---------
	//------------------------------------------------------
	
	/** 
	 * @description {Method} addRow To append a new row.
     * @param {Object} record Data of the row to be added. 
     * @param {Boolean} startEdit Indicate if open the first cell for input. 
     */
	addRow : function(record,startEdit){
		if ( this.readOnly ) {
			return ;
		}
		this.insert(record,startEdit);
	},
	
	/**
	 * @description {Method} deleteRow To remove row.
	 * @param {Object} row DOM TR presenting a row.
	 */
	deleteRow : function(row){
		var rows=[].concat(row ||  this.selectedRows );
		for (var i=0;i<rows.length ;i++ ){
			row=rows[i];
			var record = this.getRecordByRow(row);
			if (row!=this.activeRow) {
				this.selectRow(row, false);
			}

			if (!record){
				continue;
			}

			if (this.isInsertRow(row)) {
				if (this.activeCell && this.activeRow==row)	{
					this.cleanActiveObj();
				}
				var ntrs=this.getTwinRows(row);
				Sigma.U.removeNode(ntrs[0],ntrs[1]);
				this.dataset.deleteRecord(record);
				this.toggleEmptyRow();
				continue;
			}

			if ( Sigma.$invoke(this,'beforeDelete',[record,row,this])!==false) {
				var isDel=this.dataset.isDeletedRecord(record);
				if (!isDel){
					this.dataset.deleteRecord(record);
					Sigma.U.addClass(row,'gt-row-del');
				}else{
					this.dataset.undeleteRecord(record);
					Sigma.U.removeClass(row,'gt-row-del');
				}
				this.syncTwinRowCell(row);
			}
		}

	},
	
	/**
	 * @description {Method} selectRow To remove row.
	 * @param {Object} row DOM TR presenting a row.
	 * @param {Boolean} selected True for seleted, false for unselected.
	 */
	selectRow : function(row, selected){
		if (selected) {
			var rows = [].concat(row);
			for (var i = 0, len = rows.length; i < len; i++) {
				row = this.getRow(rows[i]);
				if (!this.isEmptyRow(row)) {
					var rowNo = row.rowIndex;
					var record = this.getRecordByRow(row);
					if (Sigma.$invoke(this, 'beforeRowSelect', [record, row, rowNo, this]) !== false) {
						Sigma.U.addClass(row, "gt-row-selected");
						this.syncTwinRowCell(row);
						this.selectedRows.push(row);
						this.activeRow = row;
						Sigma.$invoke(this, 'afterRowSelect', [record, row, rowNo, this]);
					}
				}
			}
			
		}
		else {
			if (row) {
				Sigma.U.removeClass(row, "gt-row-selected");
				this.syncTwinRowCell(row);
				Sigma.U.remove(this.selectedRows, row);
			//this.activeRow=null;
			}
		}
		
	},
	
	/**
	 * @description {Method} addParameters To add an additional parameter pair for submittal. By default, only data defined here will be submitted to server side. By this function, developers could add more parameter pairs.
     * @param {String} key Parameter key.
     * @param {String} value Parameter value.
     */
	addParameters : function(key,value){
		this.parameters=Sigma.U.add2Map(key,value,this.parameters);
	},
	
	/**
	 * @description {Method} cleanContent To clear all the data showed and make grid empty.
	 */
	cleanContent : function(){
		this.setContent({
			data : [],
			pageInfo : {
				pageNum :1,
				totalPageNum : 1,
				totalRowNum : 0,
				startRowNum : 0
			}
		});
	},
	
	/**
	 * @description {Method} cleanParameters Remove all additional parameters. 
	 * See Sigma.Grid.addParameters
	 */
	cleanParameters : function(){
		this.parameters={};
	},
	
	/**
     * @description {Method} closeGridMenu To Close main menu.
     */ 
	closeGridMenu : function(){
		if (this.gridMenuButton){
			this.gridMenuButton.closeMenu();
		}

	},
	
	/**
     * @description {Method} endEdit To close current cell editor and update current cell value. 
     */
	endEdit : function(){
		if (this.activeEditor && this.activeEditor.locked===true
			|| (this.activeDialog!=this.activeEditor)  && this.activeDialog && !this.activeDialog.hidden
		){
			return false;
		}
		if (this.activeCell && this.activeEditor && (this.activeColumn.editable || this.isInsertRow(this.activeRow ) ) ) {
			this.hideEditor();
			this.editing=false;
			this.syncTwinRowCell(null,this.activeCell);
		}

	},
	
	/**
	 * @description {Method} exportGrid To export grid to a file. 
	 * This function need server side programming supported.
	 * @param {Object} type. Export file type. Could be "xls" for MS Excel file, "pdf" for Adobe PDF file, "csv" for
	 * @param {String} fileName File name.
	 * @param {String} url Url specified to export data.
	 * @param {String} name Json name. Server side can get all the stuff by $_POST[name].
	 * @param {String} action Reserved.
	 */
	exportGrid : function(type, fileName, url, name, action) {
		var grid=this;
		if ( Sigma.$invoke(grid,'beforeExport',[type, fileName, url, name, action,grid])!==false){
			try{
				type = type ||  this.exportType;
				fileName = fileName || this.exportFileName;
				url = url || this.exportURL;
				name = name || this.jsonParamName || (this.ajax?this.ajax.jsonParamName:Sigma.AjaxDefault.paramName);
				action = 'export'; // 'export_'+type;
				if (this.html2pdf && type=='pdf') {
					this.gridFormTextarea.name= '__gt_html';
					
					var exportCssText=[
							'<style type="text/css">',
							 this.getHiddenColumnStyle('exportable'),
							'</style>'
						];
					this.gridFormTextarea.value= exportCssText.join('\n')+'\n'+this.getGridHTML(true);
				}else{
					var reqParam=this.getLoadParam();
					reqParam[this.CONST.action]= action;
					reqParam[this.CONST.exportType]= type;
					reqParam[this.CONST.exportFileName]= fileName ;

					this.gridFormTextarea.name= name;
					this.gridFormTextarea.value=Sigma.$json(reqParam);
				}
				//alert(this.gridFormTextarea.value)
				this.gridFormFileName.value=fileName;
				this.gridFormExportType.value=type;
				this.gridForm.action= url ; //+ (url.indexOf('?')>=0 ? '&' : '?') + 'exportType='+type+'&exportFileName='+fileName;

				url && (this.gridForm.submit());
				this.gridFormTextarea.value='';

			}catch(e){
				this.exportFailure( { type : type , fileName : fileName } , e);
			}
		}
	},
	
	/**
     * @description {Method} getColumn Get column object by column id or DOM object of cell.
     * @param {Object} colNoIdTd Column id or DOM object of cell.
     * @return {Object} Column object
     */
	getColumn : function(colNoIdTd){
		if ( Sigma.U.isNumber(colNoIdTd) && colNoIdTd>=0) {
			return this.columnList[colNoIdTd];
		}else if (Sigma.U.getTagName(colNoIdTd) == 'TD'){
			return this.columnList[ Sigma.U.getCellIndex(colNoIdTd) ];
		}else if (Sigma.$type(colNoIdTd)=='object'){
			return colNoIdTd;
		}else{
			return this.columnMap[colNoIdTd];
		}
	},
    
	/**
     * @description {Method} getCellValue Get cell value by specifying column id and row number.
     * @param {String} colNoIdTd Id of column where cell is in.
     * @param {Number} recordOrRowNo  Number of row where cell is at. 
     * @return {Any} New cell value.
     */
	
    getCellValue : function(colNoIdTd,recordOrRowNo){
		var colObj=this.getColumn(colNoIdTd);
		var record= this.getRecord(recordOrRowNo);
		var vl = record?record[colObj.fieldIndex]:null;
		return vl;
	},
	
	//lagcy function name
	getColumnValue : function(colNoIdTd,recordOrRowNo){
		return this.getCellValue(colNoIdTd,recordOrRowNo);
	},

    /**
     * @description {Method} getDeletedRecords To get all records deleted.
     * @return {Array} Array of records. 
     */
	getDeletedRecords : function(){
		return Sigma.$array(this.dataset.deletedRecords );
	},
	
	/**
     * @description {Method} getDisplayColumns To get columns displayed or invisible.
     * @param {Boolean} display visible or invisible.
     * @return {Array} Column array. See Sigma.Column  
     */
	getDisplayColumns : function(display){
		var cs=[];
		for (var cn=0;cn<this.columnList.length ;cn++ )	{
			var col=this.columnList[cn];
			if (col.hidden!==(display!==false)) {
				cs.push(col);						
			}
		}
		return cs;
	},
	
	/**
	 * @description {Method} getEventTargets To get event relevant info.
	 * @param {Object} event Sigma.Grid.Event object. See Sigma.Grid.Event
	 * @param {Object} el Sigma.Grid.EventTarget object. See Sigma.Grid.EventTarget
	 * @return {Object} event relevant info
	 */
	getEventTargets : function(event,el){
		el=el||Sigma.U.getEventTarget(event);
		var row=null , colNo=-1, rowNo=-1 ,column=null ,record = null , value=null ;
		var cell= Sigma.U.getParentByTagName("td",el, event);
		if (cell) {
			row=cell.parentNode;
			colNo= Sigma.U.getCellIndex(cell);
			rowNo=row.rowIndex;
			column=this.columnList[colNo];
			record=this.getRecordByRow(row)||{};	
			value= record[column.fieldIndex];
		}
		return { cell : cell ,
				row : row , 
				colNo : colNo , 
				rowNo : rowNo , 
				column : column,
				record : record,
				value : value,
				eventTarget : el	
		};

	},
	
	/**
     * @description {Method} getInsertedRecords To get all records deleted.
     * @return {Array} Array of records.
     */
	getInsertedRecords : function(){
		return Sigma.$array(this.dataset.insertedRecords );
	},
	
	/**
     * @description {Method} getRecord Get record by row number or DOM of td or tr.
     * @param {Object} rowNoTdTr Row number or DOM of td or tr.
     * @return {Object} Record object.
     */
	getRecord :function( rowNoTdTr){
		var rowNo;
		if ( Sigma.U.isNumber(rowNoTdTr)) {
			rowNo =  rowNoTdTr;
		}else if (Sigma.U.getTagName(rowNoTdTr) == 'TD'){
			return  this.getRecordByRow(rowNoTdTr.parentNode);
		}else if (Sigma.U.getTagName(rowNoTdTr) == 'TR'){
			return  this.getRecordByRow(rowNoTdTr);
		}else if (Sigma.$type(rowNoTdTr,'object') && !rowNoTdTr.tagName) {
			return  rowNoTdTr;
		}else if (this.selectedRows.length>0){
			rowNo = this.selectedRows[this.selectedRows.length-1].getAttribute(Sigma.Const.DataSet.INDEX)/1;
		}else{
			rowNo=0;
		}
		return this.dataset.getRecord(rowNo);
	},
	
	/**
	 * @description {Method} getSelectedRecords To get selected records.
     * @return {Array} Array of array/object. Records of selected rows. 
	 */
	getSelectedRecords : function(){
		var rs=[];
		for (var i=0; i<this.selectedRows.length; i++) {
			rs.push(this.getRecordByRow(this.selectedRows[i]) );
		}
		return rs;
	},
	
	/**
	 * @description {Method} getUpdatedRecords To get all records updated.
     * @return {Array} Array of records. 
	 */
	getUpdatedRecords : function(){
		return Sigma.$array(this.dataset.updatedRecords );
	},
	
	/**
     * @description {Method} hideCellToolTip To hide tool tip for cell.
     */
	hideCellToolTip : function(){
		if (this.toolTipDiv) {
			this.toolTipDiv.style.display="none";
			this.gridEditorCache.appendChild(this.toolTipDiv);
			this.toolTipDiv.innerHTML="";
		}

	},
	
	/**
     * @description {Method} hideDialog To hide current active dialog.
     */ 
	hideDialog : function(){
		this.activeDialog && this.activeDialog.hide();
		this.activeDialog=null;
	},

    /**
     * @description {Method} hideMask To hide mask.
     */
	hideMask :function(){
		if (this.gridMask) {
			this.gridMask.style.cursor='auto';
			this.gridMask.style.display="none";
		}
		this.pageSizeSelect&&(this.pageSizeSelect.style.visibility = "inherit");
	},
	
	/**
     * @description {Method} insert To insert a row into grid.
     * @param {Array} record Array of array/object. Data of row to be inserted. defaultRecord will be adopted if this parameter is null. See Sigma.Grid.defaultRecord
     * @param {Boolean} startEdit To set true to start to edit row right after inserting.
     */
	insert : function(record,startEdit){
		record = record || this.cloneDefaultRecord() || ( this.dataset.getRecordType()=='array'?[]:{});
		if ( Sigma.$invoke(this,'beforeInsert',[record])!==false) {

				this.dataset.insertRecord(record);

				record[Sigma.Const.DataSet.NOT_VAILD]=true;

				//var rowId=this.rowKey + this.rowNum;
				var rowId=this.rowKey + this.getUniqueField(record);
				record[Sigma.Const.DataSet.ROW_KEY]=rowId ; //this.rowNum;

				var newTRS=this.renderInsertedRow(record);


				this.bodyDiv.scrollTop=this.bodyDiv.scrollHeight;

				this.rowNum++;
				/* todo */
				if (startEdit!==false){
					var cn=0;
					var e1=-1;
					for (cn=0;cn<this.columnList.length ;cn++ )	{
						var colObj=this.columnList[cn];
						if (e1<0&& !colObj.hidden && colObj.editor ) {
							e1=cn;
						}
						if (colObj.frozen && newTRS[1]){
							var colL=newTRS[0].cells[cn].cloneNode(true);
							newTRS[1].appendChild(colL);
						}
					}
					//var _firstEditableCell= newTRS[0].cells[e1];
				}else{
					
				}
			this.syncScroll();
		}
		
		this.toggleEmptyRow();

	},
	
	/**
	 * @description {Method} printGrid To print grid. 
	 * This function will popup a printer-choosing dialog box. 
	 */
	printGrid : function(){
			var grid=this ,docT;
			//grid.gridIFrame.location="about:blank";
			grid.closeGridMenu();
			grid.showWaiting();

				var printCssText=[
					' body { margin :5px;padding:0px;}' ,
					'.gt-table { width:100%;border-left:1px solid #000000 ; border-top:1px solid #000000; }' ,
					'.gt-table TD { font-size:12px;padding:2px; border-right:1px solid #000000 ; border-bottom:1px solid #000000; }' ,
					'.gt-hd-row TD { padding:3px; border-bottom:2px solid #000000 ;background-color:#e3e3e3; white-space:nowrap;word-break:keep-all;word-wrap:normal; }' ,
					'.gt-hd-hidden { }',
					'.gt-row-even {	background-color:#f6f6f6; }'
				];
				printCssText.push( this.getHiddenColumnStyle('printable') );
				grid.customPrintCss && printCssText.push(grid.customPrintCss);
				printCssText=printCssText.join('\n');

			//alert(printCssText)

			/* todo : print all dataset ?? */
	
			var gridHTML = grid.getGridHTML();

			var focused = Sigma.doc.activeElement;

			function printBody(_doc) {
				// Sigma.U.CSS.createStyleSheet(printCssText, null, _doc);
				// _doc.body.innerHTML= gridHTML;
				_doc.writeln( '<style>' );
				_doc.writeln( printCssText );
				_doc.writeln( '</style>' );
				_doc.writeln(gridHTML);
				_doc.close();
			}

			if ( Sigma.isIE || Sigma.isGecko || Sigma.isSafari ){
				docT=grid.gridIFrame.contentWindow.document;
				printBody(docT);
				grid.gridIFrame.contentWindow.focus();
				grid.gridIFrame.contentWindow.print();

			}else if (  Sigma.isOpera ){
				var pwin=window.open('');
				docT=pwin.document;
				printBody(docT);
				pwin.focus();
				// todo 
				Sigma.$thread(	function(){
						pwin.print();
						Sigma.$thread(	function(){
							pwin.close();
						},2000);
					}
				);

			}
			Sigma.$thread(	function(){
				grid.hideWaiting();
			},1000);
			
	},
	
	/**
     * @description {Method} refresh To rebuild grid without data reloaded.
     * @param {Object} data New content data.
     */
	refresh : function(data){
		if ( this.dataset && data){
			this.dataset.setData(data);
		}
		var grid=this;
		/* todo */
		//grid.cleanTable();
		var l=grid.scrollLeft , t=grid.scrollTop;
		if (grid.remotePaging===false){
			grid.dataset.startRecordNo = (grid.getPageInfo().startRowNum||1)-1 ;
		}

		function _refresh(){
			if ( Sigma.$invoke(grid,'beforeRefresh',[grid])!==false){
				grid.cleanTable();

				!grid.remoteSort && grid._doSort();

				grid.refreshGrid();

				grid.autoUpdateSortState && grid.updateSortState();
				grid.sorting=false;

				grid.autoUpdateEditState && grid.updateEditState();
				//grid.autoUpdateGroupState && grid.updateGroupState();
				grid.updateCheckState();
				grid.autoUpdateFreezeState && grid.updateFreezeState();

				grid.refreshToolBar();
				grid.syncScroll(l,t);
				
				Sigma.$invoke(grid,'afterRefresh',[grid]);
			
				grid._onComplete();
			}

		}

		Sigma.$thread( _refresh );
		//_refresh();
	},
	
	/**
     * @description {Method} reload To reload data from server side.
     * @param {Boolean} recount Whether recount record number.
     * @param {Boolean} force Flase for refresh only.
     */ 
	reload : function(recount,force){
		if (force!==false || !this.dataset || this.dataset.getSize()<0 ){
			this.load(recount,true);
		}else{
			this.refresh();
		}
	},
	
	/**
     * @description {Method} render To render a grid.
     * @param {Object} container The parent div or something
     */
	render : function(container){
		if(!this.rendered){
			container=Sigma.getDom(container);
			this.container =container||this.container;
			this.initColumns();
			this.initCSS();
			this.createMain();
			this.createFormIFrame();
			this.createGridGhost();
			this.initToolbar();
			this.initMainEvent();
			this.createBody();
			this.rendered = true;
		}
		 return this;
	},
	
	/**
	 * @description {Method} save To save modifications to server side.
	 * @param {Object} onNav Whether pop up alter if nothing changed.
	 */
	save : function(onNav){
		if (this.endEdit()===false){
			return;
		}
		var url=this.saveURL;
		var _insert =this.getInsertedRecords();
		var _update =this.getUpdatedRecords();
		var _delete =this.getDeletedRecords();

		var hasModified= (_insert && _insert.length>0 || _update && _update.length>0 || _delete && _delete.length>0);
		
		if (!onNav && !hasModified) {
			alert( this.getMsg('NO_MODIFIED') );
		}else{
			//var validResult=this.activeEditor.doValid(value,this.activeRecord,this.activeColumn,this);
			var rows= this.gridTable.tFoot ? this.gridTable.tFoot.rows : [];
			for (var i=0,len=_insert.length;i<len ;i++) {
				var _ir=_insert[i];
				for (var cn=0;cn<this.columnList.length ;cn++ )	{
					var colObj=this.columnList[cn];
					if (colObj.editor) {
						var value=_ir[colObj.fieldIndex];
						var cell = rows[i]?rows[i].cells[colObj.colIndex]:null;
						if (  this.validValue(colObj,value,_ir,cell) !==true) {
							return false;
						}							
					}
				}
				
			}

			var reqParam= this.getSaveParam();
			reqParam[this.CONST.action]= 'save';
			reqParam[this.CONST.insertedRecords]= _insert;
			reqParam[this.CONST.updatedRecords]= _update;
			reqParam[this.CONST.deletedRecords]= _delete;

			/* todo  : valid  modified-record */
			if ( Sigma.$invoke(this,'beforeSave',[reqParam,this])!==false){
				this.showWaiting();
			
				var grid=this;
				return this.request(url,
					reqParam, 'json',
					function(_onNav){
						return function(response){
							grid.saveCallBack(response,reqParam,_onNav);
						};
					}(onNav),
					function(xhr,e){
						var er={};
						er[grid.CONST.exception]= ' XMLHttpRequest Status : '+xhr.status;
						grid.saveFailure( er,e );
						grid.hideWaiting();
					} );
			}
		}
		if (onNav===true){
			this.load();
		}
		return false;
	},
	
	/**
     * @description {Method} setCellValue Set cell value by specifying column id and row number.
     * @param {String} colID Id of column where cell is in.
     * @param {Number} recordOrRowNo Number of row where cell is at.
     * @param {Object} newValue New cell value.
     */
	setCellValue : function(colID,recordOrRowNo,newValue){
		var record= recordOrRowNo;
		if ( Sigma.U.isNumber(record)){
			record=this.dataset.getRecord(record);
		}
		this.update(record,this.columnMap[colID].fieldName, newValue);
	},
	
	setColumnValue : function(colID,recordOrRowNo,newValue){
	    return this.setCellValue(colID,recordOrRowNo,newValue);	
    },
	
	/**
     * @description {Method} setContent To set grid data.
     * @param {Array} respD Array of array/object. Data records.
     */
	setContent : function(respD){
				var pageInfo=this.getPageInfo();

				if (Sigma.$type(respD,'array')){
					respD[this.dataRoot]= respD;
				}else{
					//respD[this.CONST.data]=respD[ this.dataRoot || this.CONST.data ]; 
					respD[this.CONST.pageInfo]=respD[ this.dataPageInfo || this.CONST.pageInfo ]; 

					if (respD[this.CONST.recordType]) {
						this.dataset.setRecordType(respD[this.CONST.recordType]);
					}
					if (respD[this.CONST.pageInfo]) {
						Sigma.$extend(pageInfo,respD[this.CONST.pageInfo]);
						//this.refreshToolBar();
					}
					pageInfo.totalRowNum = respD.totalRowNum  || pageInfo.totalRowNum;
				}
				if (respD[this.dataRoot] && Sigma.$invoke(this,'beforeDatasetUpdate',[respD[this.dataRoot]])!==false ) {
					//if (this.remotePaging===false){
						pageInfo.totalRowNum = pageInfo.totalRowNum ||  respD[this.dataRoot].length;
					//}
					this.refresh( respD[this.dataRoot] );
				}else{
					this.refresh();
				}
	},
	
	/**
     * @description {Method} showDialog To show a dialog.
     * @param {String} type Could be 'filter' or 'chart'.
     */
	showDialog : function(type){
		var grid=this;
		switch (type) {
			case 'filter':
				/*
				grid.filterDialog= grid.filterDialog || new Sigma.Dialog({
					gridId : grid.id , container : grid.gridMask ,
					id: "filterDialog" ,
					width: 300,height:200 ,
					autoRerender : true,
					title : "filterDialog",
					body : "filter dialogasdasdsad"
				} );
				*/
				grid.filterDialog= grid.filterDialog || Sigma.createFilterDialog( { title : grid.getMsg('DIAG_TITLE_FILTER')   ,gridId : grid.id});
				grid.filterDialog.show();

				break;
			case 'chart':

				var record= grid.activeCell?grid.getRecordByRow(grid.activeRow):grid.getRecord();
				if (!record){
					break;
				}
				var date=[];
				var caption='';
				var w=300,h=300;
				if (record) {
					grid.charDialog= grid.charDialog || new Sigma.Dialog({  gridId : grid.id , container : grid.gridMask ,
						id: "charDialog" ,
						width: w,height:h ,
						autoRerender : true,
						title : grid.getMsg('DIAG_TITLE_CHART')
					} );
					grid.charDialog.show();

					grid.chart= new Sigma.Chart( { swfPath : grid.SigmaGridPath+'/flashchart/fusioncharts/charts/',
						width:w-3,height:h-23,
						container : grid.charDialog.bodyDiv
					} );
					Sigma.$each(grid.columnList , function(colObj,i){
						if (colObj.chartCaption) {
							caption= colObj.chartCaption.replace('{@}', record[colObj.fieldIndex]);
						}
						if (colObj.inChart) {
							date.push( [colObj.header || colObj.title  , record[colObj.fieldIndex],colObj.chartColor || '66bbff' ] );
						}
					} );

					grid.chart.caption= grid.chartCaption;
					grid.chart.subCaption=caption;
					grid.chart.data=date;
					grid.chart.generateChart();

				}
				break;
		}

	},
	
	/**
     * @description {Method} showMask To show/hide a mask.
     * @param {Boolean} trp True for show, false for hide. 
     */ 
	showMask :function(trp){
		if ( trp || this.transparentMask){
			Sigma.U.addClass(this.gridMask,"gt-transparent");
		}else{
			Sigma.U.removeClass(this.gridMask,"gt-transparent");
		}
		this.gridMask && (this.gridMask.style.display="block");
		this.pageSizeSelect&&(this.pageSizeSelect.style.visibility = "hidden");
	},
	
	/**
     * @description {Method} showWaiting To show a waiting bar. 
     */
	showWaiting : function(){
		this.showMask();
		if (this.gridWaiting && !this.transparentMask ){
			this.gridWaiting.style.display="block";
		}
		this.isWaiting=true;
	},
    
	/**
     * @description {Method} hideWaiting To hide a waiting bar. 
     */
	hideWaiting : function(){
		if (this.gridWaiting) {
			this.gridWaiting.style.display="none";
		}
		this.hideMask();
		this.isWaiting=false;
	},
	
	/**
	 * @description {Method} getFilterInfo Get filter criteria info 
	 */
	getFilterInfo : function(){
		return this.filterInfo || [];
	},
	
	/**
	 * @description {Method} setFilterInfo To set filter info.
	 * @param {Object} fi Filter info object.
	 */
	setFilterInfo : function(fi){
		this.filterInfo = fi || [];
	},
	
	/**
     * @description {Method} applyFilter Apply filter to this grid. 
     * Only records meeting filter criteria will be displayed within grid. 
     * @param {Object} filterInfo. Filter criteria.
     */
	applyFilter : function(filterInfo){
		this.filterInfo = filterInfo || this.filterInfo||[];
		
		/* todo */
		if (this.remoteFilter){
			this.reload();
			return;
		}
		this.dataProxyBak=this.dataset.dataProxy;
		this.filterDataProxy =this.dataset.filterData(this.filterInfo);
		/* todo */

		if (!this.remoteFilter && this.justShowFiltered){
			this.dataset.dataProxy=this.filterDataProxy;
			this.refresh();
		}
		if (this.afterFilter){
			this.afterFilter(this.filterDataProxy);
		}
		
		if (this.tools.filterTool) {
			if (this.filterInfo.length>0){
				Sigma.U.addClass(this.tools.filterTool.itemIcon,"gt-tool-filtered");
		    }else{
			    Sigma.U.removeClass(this.tools.filterTool.itemIcon,"gt-tool-filtered");
		    }
		}
		
		return this.filterDataProxy;
	},
	
	/**
     * @description {Method} setDimension To set grid width and height.
     * @param {Number} newWidth New width.
     * @param {Number} newHeight New height.
     * @param {Boolean} isInit Indicate whether grid is initialized.
     */ 
	setDimension : function(newWidth,newHeight,isInit){
			var grid=this;
			var cWH= [this.gridDiv.offsetWidth,this.gridDiv.offsetHeight ]; //Sigma.U.getContentWidthHeight( this.container );
			newWidth=''+newWidth;
			newHeight=''+newHeight;
			this.width = newWidth;
			this.height = newHeight;

			if ( newWidth.toLowerCase()=='auto'){
				this.width= cWH[0] +'px';
			}else if (newWidth.indexOf('%')<1 && newWidth.indexOf('px')<1) {
				this.width = Sigma.U.parseInt(newWidth)+'px';
			}
			if (newHeight.toLowerCase()=='auto'){
				this.height= cWH[1] +'px';
			}else if (newHeight.indexOf('%')<1 && newHeight.indexOf('px')<1) {
				this.height = Sigma.U.parseInt(newHeight)+'px';
			}
			var _hideOverflow=false;
			if ( (newHeight.indexOf('%')>1 || newWidth.indexOf('%')>1) && this.monitorResize ){

				//if  (!this.hasResizeListener){
				//	Sigma.U.addEvent(window, 'resize' ,function(event){
				//		grid._onResize();
				//	});
				//}
				_hideOverflow=true;
			}

			if (_hideOverflow){
				if (Sigma.isIE ){
					this.gridDiv.style.overflowY="hidden";
				}else if (Sigma.isOpera ){
					this.gridDiv.style.overflow="hidden";
				}
			}

			//Sigma.$thread(function(){
				grid._onResize(isInit);
			//});

	},
	
	/**
     * @description {Method} getDimension To get grid width and height.
     * @return {Object} obj.width and obj.height
     */ 
	getDimension: function(){
		return {
			width: this.width,
			height: this.height
		};
	},
	
	/**
	 * @description {Method} getSkin To get skin name
	 * @return {String} skin Skin name. 
	 * Could be 'default', 'mac', 'vista' and 'pink' or self defined name. 
	 */
	getSkin: function(){
		return this.skin;
	},
	
	/**
	 * @description {Method} setSkin To change skin by name
	 * @param {String} skin Skin name. Could be 'default', 'mac', 'vista' and 'pink'. 
	 */
	setSkin: function(skin){
		if(this.skin == skin){
			return;
		} 
		this.skin = skin;
		Sigma.Grid.changeSkin(this, skin);
	},
	
	/**
	 * @description {Method} getPageInfo To get pagging info.
	 * @param {Object} refresh Indecate to refresh navigator tool bar or not.
	 * Return pagging info.
	 */
	getPageInfo : function(refresh){
		return refresh?this.navigator.refreshState():this.navigator.pageInfo;

	},

    /**
     * @description {Method} setPageInfo To update pagingo info.
     * @param {Object} pageInfo Page info object.
     */
	setPageInfo:function(pageInfo){
			Sigma.$extend(this.getPageInfo(),pageInfo);
	},
	
	/**
	 * @description {Method} gotoPage To go to page pageIdx 
	 * @param {Number} pageIdx Page index.
	 */
	gotoPage : function(pageIdx){
		Sigma.$chk(pageIdx) && (this.getPageInfo().pageNum=pageIdx);

		if (this.autoSaveOnNav){
			this.save(true);
		}else{
			this.load();
		}
	},
	
	/**
	 * @description {Method} forEachRow Excute function fn(row,record,i,grid) on each row.
	 * @param {Function} fn Function to be executed. It takes the following parameters.
	 * row: The object of the row. 
	 * record: The data record behine the row. 
	 * i: Loop counter. grid: Grid object.
	 */
	forEachRow : function( fn ){
		var rows=this.getRows();
		for (var i=0,len=rows.length; i<len; i++) {
			var row=rows[i];
			var record=this.getRecordByRow(row);
			fn(row,record,i,this);
		}
	},
	
	/**
	 * @description {Method} moveColumn To move column oldIndex to new Index.
	 * @param {Number} oldIndex Index of column to be moved.
	 * @param {Number} newIndex Index of new position.
	 * @param {Boolean} ifreeze Whether to freeze it.
	 */
	moveColumn : function(oldIndex,newIndex,ifreeze){
			if (oldIndex==newIndex)	{
				return;
			}
			var grid=this;
			var colNum=this.columnList.length;
			var minCol=ifreeze!==true&& this.freezeHeadDiv.style.display=="block"? this.frozenColumnList.length:0;
			newIndex=newIndex<minCol?minCol:newIndex;

			var after=false ,i;
			if (newIndex>= colNum){
				newIndex=colNum-1;
				newIndex^=oldIndex;
				oldIndex^=newIndex;
				newIndex^=oldIndex;
				after=true;

			}
			var rows=this.getAllRows();
			Sigma.U.insertNodeBefore(this.columnList[oldIndex].headCell, this.columnList[newIndex].headCell );
			for (i=0;i<rows.length ; i++)	{
				Sigma.U.insertNodeBefore(rows[i].cells[oldIndex], rows[i].cells[newIndex] );
			}

			Sigma.U.moveItem(this.columnList,oldIndex,newIndex);

			for (i=0;i<colNum ; i++){
				this.columnList[i].colIndex=i;
			}
	},
	
	/**
	 * @description {Method} sortGrid To sort grid by specifed rules.
	 * @param {Array} sortInfo. Array of sortInfo.
	 */
	sortGrid : function( sortInfo){
		this.sortInfo = sortInfo || this.sortInfo;
		if (this.remoteSort){
			this.reload();
		}else{
			this.refresh();			
		}
	},
	
	/**
	 * @description {Method} getCell To get td object via row index and col index/id.
	 * @param {Number} rowNo Row index.
	 * @param {Number or String} colIdxOrId Column index.
	 * @return {Object} cell Tr(DOM) of a cell.
	 */
	getCell : function(rowNo, colIdxOrId){
		var col = this.getColumn(colIdxOrId);
		var row = this.getRow(rowNo);
		return row.cells[col.getColumnIndex()];
	},
	
	/**
	 * @description {Method} focusCell To focus a cell.
	 * @param {Object} cell. Dom(td) of the cell to be focused.
	 */
	focusCell : function(cell){
		if (cell && !this.isGroupRow(cell) ) {
				this.closeGridMenu();
				var _change=cell!=this.activeCell;
				if (_change){
					Sigma.U.removeClass(this.activeCell,'gt-cell-actived'+( this.activeEditor?'-editable':''));
					this.syncTwinRowCell(null,this.activeCell);
				}

				this.syncActiveObj(cell);

				_change && Sigma.U.addClass(this.activeCell,'gt-cell-actived'+( this.activeEditor?'-editable':''));

				if (this.activeColumn && this.activeRecord){
					this.activeValue= this.activeRecord[this.activeColumn.fieldIndex];
				}

				this.scrollGrid();
				//this.activeCell.focus();
				this.syncTwinRowCell(null,this.activeCell);

			}else{
				this.cleanActiveObj();
			}

			return cell;
		
	},
    /**
	 * @description {Method} editCell To open a cell for editing.
	 * @param {Object} cell. Dom(td) of the cell to be open.
	 */
	editCell : function(cell){
		this.focusCell(cell);
		this.syncActiveObj(cell);
		this.startEdit();
	},
	/**
	 * @description {Method} refreshRow To refresh /update row.
	 * @param {Object} row Row Object.
	 * @param {Array or Object} record New record for update row. If not specified, just refresh the row. 
	 */
	refreshRow :function(row ,record ){
		row = this.getRow(row);
		record = record || this.getRecordByRow(row);
		var rowNo= row.getAttribute(Sigma.Const.DataSet.INDEX)/1; //row.rowIndex;
		this.dataset.initValues(record,rowNo,this.dataset);
		for (var i=0;i<row.cells.length;i++ ){
			var colObj = this.getColumn(i);
			if (colObj != this.activeColumn && colObj.syncRefresh ===false)	{
				continue;
			}
			var cell=row.cells[i];
			var changed=false;
			cell.firstChild.innerHTML= colObj.renderer(record[colObj.fieldIndex],record,colObj,this,i,rowNo);
			//if (changed){
			//	this.dirty(cell);
			//}
		}
	},
	/**
	 * @description {Method} checkRow To check/uncheck row.
	 * @param {Object} row Row to be checked/unchecked.
	 * @param {Boolean} checked True for checked, false for unchecked. 
	 */
	checkRow : function(row, checked){
		row = this.getTwinRows(row)[0];
		for (var i=0;i<row.cells.length;i++){
			var colObj = this.getColumn(i);
			if (colObj.isCheckColumn){
				var el=row.cells[i].firstChild.firstChild;
				if(Sigma.U.getTagName(el)=='INPUT' && el.type=='checkbox' && Sigma.U.hasClass(el,'gt-f-check')){
					if(el.checked !== checked){
						el.checked = checked;
		                if (this.selectRowByCheck){
			                this.selectRow(row, checked);
		                }
						checked? this.checkedRows[el.value]=true : delete this.checkedRows[el.value];
					}
					return;
				}
			}
		}	
	}
	
	
	
};



Sigma.Grid=Sigma.$class(Sigma.GridDefault);


/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////

Sigma.$extend( Sigma.Grid , {

	isSelectedRow :function(row){
		return Sigma.U.hasClass(row,"gt-row-selected");
	},

	handleOverRowCore : function(evt,grid,row){
		if(!row || grid.overRow==row){
			grid.changingStyle=false;
			return;
		}
		//Sigma.U.stopEvent(evt);
		grid.changingStyle=true;

		//Sigma.U.setXY(grid.lineLightLine,[Sigma.U.getXY(row)[0] ,Sigma.U.getXY(row)[1] + row.clientHeight]);
		if (grid.overRow){
			grid.overRow.className=grid.overRow.className.replace(" gt-row-over",'');
		}
		row.className+=" gt-row-over";
		grid.overRow=row;

		grid.changingStyle=false;
		return row;
	},

	handleOverRow : function(evt,grid,el){
		evt=evt||window.event;
		var row= Sigma.U.getParentByTagName("tr",null,evt);
		if (grid.isEmptyRow(row)){
			return;
		}
		if (el==grid.freezeBodyDiv && row){
			//if (grid.overRowF){
				//grid.overRowF.className=grid.overRowF.className.replace(" gt-row-over",'');
			//}
			var rowC =grid.getRow(row.rowIndex);
			Sigma.Grid.handleOverRowCore(evt,grid,rowC);
			//row.className+=" gt-row-over";

			grid.overRowF=row;
		}else{
			Sigma.Grid.handleOverRowCore(evt,grid,row);
		}
	},


	startGridResize : function(evt,grid) {
		evt=evt||window.event;
		grid=Sigma.$grid(grid);
		grid.closeGridMenu();
		grid.isGridResizing=true;
		grid.resizeButton.style.cursor = grid.gridGhost.style.cursor = "se-resize";
		/* todo */
		grid.syncLeftTop();
		grid.gridGhost.style.top=grid.top+Sigma.doc.body.scrollTop+"px";
		grid.gridGhost.style.left=grid.left+Sigma.doc.body.scrollLeft+"px";
		var ow=grid.gridDiv.offsetWidth ,oh = grid.gridDiv.offsetHeight ;
		grid.gridGhost.cx=evt.clientX- ow;
		grid.gridGhost.cy=evt.clientY- oh;
		grid.gridGhost.style.width=ow+'px';
		grid.gridGhost.style.height=oh+'px';
		grid.gridGhost.style.display="block";
	},
	endGridResize : function(evt,grid) {

			var newWidth= Sigma.U.parseInt(grid.gridGhost.style.width)+'px';
			var newHeight= Sigma.U.parseInt(grid.gridGhost.style.height)+'px';

			grid.gridGhost.style.cursor="auto";

			/* todo */
			grid.gridMask.style.display= grid.gridGhost.style.display ="none";

			grid.isGridResizing=false;

			grid.setDimension(newWidth,newHeight);

	},


	startColumnResize : function(evt,colObj){

		evt=evt||window.event;

		if (colObj.resizable===false) {
			return;
		}
		var grid= colObj.grid;
		grid.mouseDown=true;
		if (evt.ctrlKey ){ return; }
		grid.isColumnResizing=true;

		grid.closeGridMenu();
		grid.showMask(true);
		grid.headDiv.style.cursor = grid.gridMask.style.cursor = "col-resize";

		grid.resizeColumnId= colObj.id ;

		var mX=Sigma.U.getPageX(evt);

		colObj.oldRightX= mX- grid.viewportXY[0] ;
		grid.separateLineMinX= colObj.oldRightX + colObj.minWidth - colObj.headCell.offsetWidth; //colObj.width
		if (grid.separateLine) {
			grid.separateLine.style.left= colObj.oldRightX  +"px";
			grid.separateLine.style.height=grid.viewport.offsetHeight-2+"px";
			grid.separateLine.style.display="block";
		}

		if (grid.columnMoveS) {
			grid.columnMoveS.style.left = colObj.headCell.offsetLeft + ((Sigma.isFF2 || Sigma.isFF1)?0:grid.tableMarginLeft) +"px";
			grid.columnMoveS.style.display="block";
		}



	},

	startColumnMove : function(evt,colObj){
		evt=evt||window.event;
		var grid=colObj.grid;
		grid.mouseDown=true;

		if (!evt.ctrlKey || grid.frozenColumnList[colObj.getColumnIndex()] ){
			return;
		}
		grid.closeGridMenu();

		var mX=Sigma.U.getPageX(evt);
		var left=colObj.headCell.offsetLeft;
		grid.columnMoveS.setAttribute("newColIndex",null);
		var _hg = grid.headerGhost;
		_hg.setAttribute("colIndex",colObj.getColumnIndex());
		_hg.setAttribute("offsetX2",left-mX);
		_hg.style.left= left + ( (Sigma.isFF2 || Sigma.isFF1)?0:grid.tableMarginLeft)+"px";
		_hg.style.width=colObj.headCell.offsetWidth-1 +"px";
		_hg.style.display="block";
		_hg.innerHTML="<span style=\"padding-left:2px;\" >"+ Sigma.$getText(colObj.headCell)+"</span>";
		//window.setTimeout("Sigma.U.startColumnMoveT('"+grid.gridId+"')",grid.moveColumnDelay);

	},


	doDocGridHandler : function(evt,grid) {
		evt=evt||window.event;
		var mX=Sigma.U.getPageX(evt);
		if (grid.separateLine && grid.separateLine.style.display=="block"){
			var cX=mX- grid.viewportXY[0];
			cX=cX>grid.separateLineMinX?cX:grid.separateLineMinX;
			grid.separateLine.style.left= cX+"px";
		}else if ( !grid.customHead  && grid.headerGhost && grid.headerGhost.style.display=="block"){
			var dX= mX - grid.viewportXY[0] + grid.headDiv.scrollLeft;
			grid.headerGhost.style.left= mX  + ( (Sigma.isFF2 || Sigma.isFF1)?0:grid.tableMarginLeft) + grid.headerGhost.getAttribute("offsetX2")/1+"px";
			var sLeft=-1;
			var idx=-1;
			for (var i=0;i<grid.headFirstRow.cells.length ;i++ ){
				var cell=grid.headFirstRow.cells[i];
				if (dX>cell.offsetLeft && dX<cell.offsetLeft+cell.offsetWidth){
					sLeft=cell.offsetLeft;
					idx=i;
					break;
				}
			}
			if (dX<=cell.offsetLeft){
				i=0;
			}
			if (sLeft>=0){
				grid.columnMoveS.style.left=sLeft +( (Sigma.isFF2 || Sigma.isFF1)?0:grid.tableMarginLeft)+"px";
				grid.columnMoveS.style.display="block";
			}else{
				grid.columnMoveS.style.display="none";
			}
			grid.columnMoveS.setAttribute("newColIndex",idx);

		}else if ( grid.isGridResizing ){
				var newWidth= evt.clientX-grid.gridGhost.cx;
				var newHeight= evt.clientY-grid.gridGhost.cy;

				newWidth= newWidth< grid.minWidth ? grid.minWidth : newWidth;
				newHeight= newHeight< grid.minHeight ? grid.minHeight : newHeight;

				grid.gridGhost.style.width = newWidth +"px";
				grid.gridGhost.style.height = newHeight +"px";

				//Sigma.doc.body.style.cursor="se-resize";
		}
	},

	endDocGridHandler : function(evt,grid){
		evt=evt||window.event;
		grid=Sigma.$grid(grid);
		var mX=Sigma.U.getPageX(evt);
		grid.mouseDown=false;
		if (grid.separateLine && grid.separateLine.style.display=="block"){

			var colObj=grid.columnMap[grid.resizeColumnId];
			colObj.newRightX=mX- grid.viewportXY[0];

			var dwidth=colObj.newRightX-colObj.oldRightX; //- ( Sigma.isOpera||Sigma.isSafari?0:1);

			var newWidth= dwidth+ parseInt(colObj.width);
			colObj.setWidth(newWidth);

			//grid.isColResizing=false;
			grid.resizeColumnId=-1;
			grid.separateLine.style.display= grid.columnMoveS.style.display="none";
			grid.headDiv.style.cursor = "auto";

			grid.hideMask();

			grid.syncScroll();

			if ( !Sigma.isOpera ){
				grid.isColumnResizing=false;
			}
			Sigma.$invoke(grid,'afterColumnResize',[colObj,newWidth,grid]);
			

		}else if( !grid.customHead  && grid.headerGhost && grid.headerGhost.style.display=="block" ){
			var dX=Sigma.isIE?evt.x:evt.pageX;
			var newIndex=grid.columnMoveS.getAttribute("newColIndex");
			var oldIndex=grid.headerGhost.getAttribute("colIndex");

			if (newIndex!==null && (newIndex+'').length>0 && oldIndex!==null && (oldIndex+'').length>0){
				newIndex=newIndex/1;
				if (newIndex<0){
					newIndex=grid.columnList.length;
				}
				
				if(Sigma.$invoke(grid,'beforeColumnMove',[oldIndex/1,newIndex/1,grid]) !==false){
					grid.moveColumn(oldIndex/1,newIndex/1);
				    grid.syncScroll();
				}
			}

			grid.columnMoveS.style.display="none";
			grid.columnMoveS.setAttribute("newColIndex",null);

			grid.headerGhost.style.display="none";
			grid.headerGhost.setAttribute("colIndex",null);
			grid.headerGhost.style.cursor = "auto";
		}else if ( grid.isGridResizing ){
			Sigma.Grid.endGridResize(evt,grid);
		}

	},

	changeSkin : function(grid,skinName){
		grid=Sigma.$grid(grid);
		var classNames=grid.gridDiv.className.split(" ");

		for (var i=0;i<classNames.length ;i++ ){
			if (classNames[i].indexOf(Sigma.Const.Grid.SKIN_CLASSNAME_PREFIX)===0){
				classNames[i]='';
			}
		}
		classNames.push(Sigma.Const.Grid.SKIN_CLASSNAME_PREFIX+skinName);
		grid.gridDiv.className=classNames.join(" ");
	},


	createCheckColumn : function(grid,cfg){

			var id = cfg.id;
			grid=Sigma.$grid(grid);
			var gridId=grid.id;
			var checkValid = cfg.checkValid;
			var checkValue = cfg.checkValue;
			var checkType=cfg.checkType || 'checkbox';

			if (!checkValue){
				checkValue = Sigma.$chk(cfg.fieldIndex)?
						// checkValue/cfg.fieldIndex bug ?? 
				//'grid.getColumnValue("'+checkValue+'",record);'
				//'grid.getColumnValue("'+cfg.fieldIndex+'",record);' 
					'record["'+cfg.fieldIndex+'"];'	: 'grid.getUniqueField(record);';
			}
			if (typeof checkValue == 'string'){
				//
				checkValue = new Function( 'value' ,'record','col','grid','colNo','rowNo',
					[
						'return ', checkValue
					].join('')
					);
			}

			if (!checkValid){
				checkValid = function(cvalue ,value,record,colObj,_g,colNo,rowNo){
					//return record.isChecked;
					return _g.checkedRows[cvalue];
				};
			}

			cfg.header='';
			cfg.title= cfg.title || grid.getMsg('CHECK_ALL');
			cfg.width=30;
			cfg.resizable = false ;  
			cfg.printable = false ; 
			cfg.sortable = false ;
			var checkBoxName= 'gt_'+gridId+'_chk_'+id ;
			cfg.hdRenderer = function(h,c,_g){
						return '<input type="'+checkType+'" class="gt-f-totalcheck" name="'+checkBoxName+'" />';
					};
			cfg.renderer = function(value ,record,colObj,_g,colNo,rowNo){
						var cvalue= checkValue(value ,record,colObj,_g,colNo,rowNo);
						var checkFlag= checkValid(cvalue,value ,record,colObj,_g,colNo,rowNo)?'checked="checked"':'';
						return '<input type="'+checkType+'" class="gt-f-check" value="'+cvalue+'" '+checkFlag+' name="'+checkBoxName+'" />';
					};
			return cfg;
		}

} );

//////////////////////////////////////////////////////
Sigma.Grid.prototype.initGrid = Sigma.Grid.prototype.render;

Sigma.$extend(Sigma.Grid ,{

	render : function(grid){
		grid=Sigma.$grid(grid);
		return function(){
			grid.render();
		};
	},
	initColumnEvent  :function(grid,colObj,headCell,sortIcon){

			headCell = headCell || colObj.headCell ;
			if(!headCell) { return ;}
			sortIcon = sortIcon || Sigma.Grid.getSortIcon(colObj,headCell);
			var menuButton = Sigma.U.nextElement(sortIcon);
			var separator = Sigma.U.nextElement(menuButton);

			colObj.hdTool = colObj.hdTool || Sigma.Grid.getHdTool(colObj,headCell);
			colObj.sortIcon = colObj.sortIcon || sortIcon;
			colObj.menuButton = colObj.menuButton || menuButton;
			colObj.separator=  colObj.separator || separator;

			if (colObj.separator && colObj.resizable===false) {
				colObj.separator.style.display="none";
			}

			Sigma.U.addEvent(headCell,"mousedown",function(event){
				grid.activeMe();
				if (grid.endEdit()===false){
					return;
				}
				grid.closeGridMenu();
				if (!grid.customHead ){
					Sigma.U.stopEvent(event);
					Sigma.Grid.startColumnMove(event,colObj);
				}

			} );

			//oncontextmenu

			Sigma.U.addEvent(headCell,'click',function(event){
				// Sigma.U.stopEvent(event);
				var et =Sigma.U.getEventTarget(event);
				if ( !grid.isColumnResizing ){
					Sigma.$invoke(grid,'onHeadClick',[event,headCell,colObj,grid]);

					if (  Sigma.U.getTagName(et)=='INPUT' && et.type=='checkbox' && Sigma.U.hasClass(et,'gt-f-totalcheck')  ){
						Sigma.checkTotalBox(et,grid,colObj);
					}else if (colObj.sortable && et.className!='gt-hd-button' )	{
						grid.lastAction='sort';
						grid.sorting=true;
						var sortOrder = colObj.sortOrder=='asc'?'desc': (colObj.sortOrder=='desc'&& colObj.enableDefaultSort ?'defaultsort':'asc');
						var si=grid.createSortInfo(colObj);si.sortOrder=sortOrder;
						grid.addSortInfo(si);
						//grid.showWaiting();
						//todo : 
						//Sigma.$thread(function(){
							grid.sortGrid( );
						//} );

					}
				}
				if ( Sigma.isOpera ){
					grid.isColumnResizing=false;
				}


			} );

			if (colObj.resizable){
					separator.colID=colObj.id;
					separator.style.cursor='col-resize';
					Sigma.U.addEvent(separator,"mousedown",function(event){
						grid.activeMe();
						Sigma.U.stopEvent(event);
						Sigma.Grid.startColumnResize(event,colObj);
					});
			}

			if (!colObj.sortable && !colObj.resizable && colObj.hdTool){
				colObj.hdTool.style.display="none";
			}

	},

	getHdTool : function(colObj,headCell){
		var tempEl=Sigma.U.firstChildElement( headCell || colObj.headCell);
		return Sigma.U.lastChildElement(tempEl);
	},

	getSortIcon : function(colObj,headCell){
		var tempEl=Sigma.Grid.getHdTool(colObj,headCell);
		return Sigma.U.firstChildElement(tempEl);
	},


	mappingRenderer : function(mapping,defaultText){
		return function(cellValue){
			return mapping[cellValue] || (defaultText===undefined||defaultText===null ?cellValue : defaultText) ;
		};
	},


	findGridByElement : function(obj){

		var tagName = 'DIV';
		var className='gt-grid';
		var gridId='';
		while( (obj=obj.parentNode) ){
			if(Sigma.U.getTagName(obj)==tagName &&  Sigma.U.hasClass(obj, className) ){
				gridId=obj.id;
				break;
			}
		}
		if (gridId.indexOf('_div')===gridId.length-4) {
			gridId=gridId.substring(0,gridId.length-4);
		}
		return  Sigma.$grid(gridId);
	}

} );


/* todo */
var Ext = Ext || null;
(Ext && Ext.reg) && (Ext.reg('gtgrid',Sigma.Grid));

/* todo */
Sigma.checkOneBox=function(chkbox, grid,chk){
	grid=Sigma.$grid(grid);
	chkbox=Sigma.$(chkbox);
	if (chkbox.checked==chk){
		return chk;
	}
	
	var cell=Sigma.U.getParentByTagName('td',chkbox);
	var row=cell.parentNode;
	var mrow = grid.getTwinRows(row)[0];
	if (chk===true || chk===false){
		if(Sigma.$invoke(this,'onRowCheck',[mrow, chk, grid])===false){
		    return !!chkbox.checked;
	    }
		chkbox.checked=chk;
	}
	if (chkbox.checked){
		grid.checkedRows[chkbox.value]=true;
		if (grid.selectRowByCheck){
			grid.selectRow(mrow, true);
		}
	}else{
		delete grid.checkedRows[chkbox.value];
		if (grid.selectRowByCheck){
			grid.selectRow(mrow, false);
		}
	}
	return !!chkbox.checked;

};


Sigma.checkTotalBox=function(chkbox,grid,colObj,chk){
	grid=Sigma.$grid(grid);
	chkbox=Sigma.$(chkbox);
	if (chk!==null && chk!==undefined) {
		chkbox.checked=chk;
	}
	var htd=Sigma.U.getParentByTagName('td',chkbox);
	var cellIdx=Sigma.U.getCellIndex(htd);
	var checked=chkbox.checked;

	var rows= colObj.frozen? grid.getAllFreezeRows() : grid.getAllRows();
	
	for (var i=0,j=rows.length;i<j; i++ ){
		var row=rows[i];
		var cell=row.cells[cellIdx];
		if (cell){
			var _chk=cell.getElementsByTagName('input')[0];
			Sigma.checkOneBox(_chk,grid,checked);
		}
	}

};

Sigma.initGlobalEvent = function(){
		if (Sigma.initGlobalEvent.inited){
			return;
		}
//		
		var d = Sigma.isIE ? Sigma.doc.body : Sigma.doc ;

		Sigma.U.addEvent( d , "mousemove" ,function(event){  
			Sigma.activeGrid && Sigma.Grid.doDocGridHandler(event,Sigma.activeGrid);  
		});
		Sigma.U.addEvent( d , "mouseup" ,function(event){ 
			Sigma.activeGrid && Sigma.Grid.endDocGridHandler(event,Sigma.activeGrid);
		});
		Sigma.U.addEvent( d , 'click' ,function(event){
			Sigma.activeGrid && ( Sigma.activeGrid.endEdit() || Sigma.activeGrid.closeGridMenu() );
		});
		Sigma.U.addEvent( d, 'keydown' ,function(event){
			if(Sigma.activeGrid){
				if(Sigma.$invoke(Sigma.activeGrid,'onKeyDown',[event])!==false){
				    Sigma.activeGrid._onKeydown(event);	
				}
			} 
		});

		Sigma.initGlobalEvent.inited=true;
};

///////////////////////////////////////////////////////////////

