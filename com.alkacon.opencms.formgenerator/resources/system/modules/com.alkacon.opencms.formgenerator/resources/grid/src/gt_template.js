
/////////////////////////////////////////////////

if (!Sigma.Template){
	Sigma.Template={};
}

Sigma.$extend( Sigma.Template , {
	Grid : {
		main :function(grid){
					var gid=grid.id;

					var ghtml=[

						grid.toolbarPosition=='top'||grid.toolbarPosition=='t'?'<div id="'+gid+'_toolBarBox" class="gt-toolbar-box gt-toolbar-box-top" ></div>':'',

						'<div id="'+gid+'_viewport" class="gt-viewport'+(grid.simpleScrollbar?' gt-simple-scrollbar':'')+'" >',
							'<div id="'+gid+'_headDiv" class="gt-head-div"><div class="gt-head-wrap" ></div>',
								'<div id="'+gid+'_columnMoveS" class="gt-column-moveflag"></div>',
								'<div id="'+gid+'_headerGhost" class="gt-head-ghost"></div>',
							'</div>',
							'<div id="'+gid+'_bodyDiv" class="gt-body-div"></div>',
							//'<div id="'+gid+'_freezeView" class="gt-freeze-view">',
							'<div id="'+gid+'_freeze_headDiv" class="gt-freeze-div" ></div>' ,
							'<div id="'+gid+'_freeze_bodyDiv" class="gt-freeze-div" ></div>' ,
							//'</div>',

							/* todo */
							// '<div id="'+gid+'_footDiv"></div>',
							//'<div id="'+gid+'_separateLine" class="gt-split-line"></div>',
						'</div>',

						grid.toolbarPosition=='bottom'||grid.toolbarPosition=='b'?'<div id="'+gid+'_toolBarBox" class="gt-toolbar-box" ></div>':'',
						'<div id="'+gid+'_separateLine" class="gt-split-line"></div>',

						'<div id="'+gid+'_mask" class="gt-grid-mask" >',
							'<div  id="'+gid+'_waiting" class="gt-grid-waiting">',
								'<div class="gt-grid-waiting-icon"></div><div class="gt-grid-waiting-text">'+ grid.getMsg('WAITING_MSG')+'</div>',
							'</div>',
							'<div class="gt-grid-mask-bg">','</div>',
							//'<div id="'+gid+'_editor_cache" class="gt-editor-cache">',
							//	'<div id="'+gid+'_editor_container" class="gt-editor-container">gt-editor-container</div>',
							//'</div>',
						'</div>'
					  ];
					return ghtml.join('\n');
				},
		formIFrame : function(grid){
				var gid=grid.id;
				var ghtml=[
					 '<div class="gt-hidden" >',
						'<form id="'+gid+'_export_form" target="'+gid+'_export_iframe" style="width:0px;height:0px;margin:0px;padding:0xp" method="post" width="0" height="0" >',
							'<input type="hidden" id="'+gid+'_export_filename" name="exportFileName"  value="" />',
							'<input type="hidden" id="'+gid+'_export_exporttype" name="exportType" value="" />',
							'<textarea id="'+gid+'_export_form_textarea" name="" style="width:0px;height:0px;display:none;" ></textarea>',
						'</form>',
						'<iframe id="'+gid+'_export_iframe"  name="'+gid+'_export_iframe" scrolling="no" style="width:0px;height:0px;" width="0" height="0" border="0" frameborder="0" >',
						'</iframe>',
					 '</div>'
				];
				return ghtml.join('\n');
		},
		createHeaderCell : function(grid, col,hidden){

			var cell= Sigma.$e("td", {className :col.styleClass, columnId :col.id } );
			var header=col.hdRenderer(col.header,col,grid);
			col.title= col.title|| col.header || '';
			header=(!header||Sigma.U.trim(header)==='')?'&#160;':header;
			if (hidden) {
				cell.style.height="0px";
			}
			cell.innerHTML=[
			"<div class=\"gt-inner"+(col.headAlign?' gt-inner-'+col.headAlign:'')+"\" ",
			hidden?'style="padding-top:0px;padding-bottom:0px;height:1px;" ':'',
			"unselectable=\"on\" title=\""+ col.title +"\" >",
			'<span>',
			header,
			'</span>',
			hidden?'':Sigma.T_G.hdToolHTML,
			"</div>"
			].join('');
			return cell;
		},


		hdToolHTML : '<div class="gt-hd-tool" ><span class="gt-hd-icon"></span><span class="gt-hd-button"></span><span class="gt-hd-split"></span></div>',

		bodyTableStart : function(id,withTbody){
			return ["<table ",	id?"id=\""+id+"\" ":"",
				"class=\"gt-table\" cellspacing=\"0\"  cellpadding=\"0\" border=\"0\" >",
				withTbody===false?"":"<tbody>"].join('');
		},

		tableStartHTML : '<table class="gt-table" style="margin-left:0px" cellspacing="0"  cellpadding="0" border="0" ><tbody>',
		tableEndHTML :  "</tbody></table>",

		rowStart : function(grid, rowNo,rowId){
			//rowAttributes
			return Sigma.T_G.rowStartS(grid, rowNo)+'>\n';

		},

		rowStartS : function(grid, rowNo , rowAttribute){
			//rowAttributes
			return [
				'<tr class="gt-row',(rowNo%2===0? grid.evenRowCss :''),'" ',  
				Sigma.Const.DataSet.INDEX,'="',rowNo, '" ',rowAttribute
			].join('');

		},

		rowEndHTML : '</tr>\n',

		innerStart : function(col){
			return 	["<div class=\"gt-inner "+(col.align?' gt-inner-'+col.align+' ':'')+ '' /*(col.styleClass?(" "+col.styleClass):'')*/ ,
					"\" >"].join('');
		},
		cellStartHTML : "<td ><div class=\"gt-inner\" >",
		cellEndHTML : "</div></td>",


		cell : function(col,cellHTML,cellAttributes){
			return [
			"<td ", cellAttributes||col.cellAttributes ,
			" class=\""+col.styleClass+"\" >",
			col.innerStartHTML,
			cellHTML ,
			"</div></td>"
			].join('');
		},
		
		getColStyleClass : function(grid,colId){
			return (Sigma.Const.Grid.COL_T_CLASSNAME + grid.id +'-'+colId).toLowerCase();
		},

		freezeBodyCell : function (grid,columnWidth,text,isHead){

			var indexCellWidth = columnWidth + grid.cellWidthFix;
			var indexInnerWidth = columnWidth + grid.innerWidthFix;

			var divWidthStyle='style="width:'+ indexInnerWidth +'px;"';

			text= text || '&#160;';

			var tdObj=Sigma.$e("td",{
				style : { width :indexCellWidth +'px' },
				innerHTML:'<div class="'+ (isHead?'gt-hd-inner':'gt-inner')+'" '+divWidthStyle +'>'+text+'</div>'
				} );


			return tdObj;
		},

		freezeHeadCell : function(grid,columnWidth,text){
			return this.freezeBodyCell(grid,columnWidth,text,true);

		}
	},
		
		Dialog : {
			create : function(options){
					var id=options.domId;
					var gid=options.gridId;
					var hasCloseButton=Sigma.$chk(options.hasCloseButton)?options.hasCloseButton:true;
					var title=options.title||'Dialog';
					return ['<div class="gt-dialog-head" >',

								'<div class="gt-dialog-head-icon">&#160;</div>',

								'<div id="'+id+'_dialog_title"  class="gt-dialog-head-text" >'+ title +'</div>',

								'<div class="gt-dialog-head-button"  >',
									hasCloseButton?'<a href="#" onclick="Sigma.$grid(\''+gid+'\').closeDialog();return false;">&#160;</a>':'',
								'</div>',

							'</div><div id="'+id+'_dialog_body" class="gt-dialog-body"></div>'
						].join('');
				},
			filterCondSelect : [ 
					'<select class="gt-input-select">',
						'<option value="equal">=</option>',
						'<option value="notEqual">!=</option>',
						'<option value="less">&lt;</option>',
						'<option value="great">></option>',
						'<option value="lessEqual">&lt;=</option>',
						'<option value="greatEqual" >>=</option>',
						'<option value="like" >like</option>',
						'<option value="startWith">startWith</option>',
						'<option value="endWith">endWith</option>',
					'</select>'
				].join('')
			}
} );

Sigma.T_G = Sigma.Template.Grid;
Sigma.T_C = Sigma.Template.Column;
Sigma.T_D = Sigma.Template.Dialog;


//