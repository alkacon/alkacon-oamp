//

Sigma.Navigator=Sigma.$class( {
	
	properties : function(){return {
		pageInfo : {
			pageSize : 20,
			pageNum : 1,
			totalRowNum :0,
			totalPageNum : 1,
			startRowNum :0,
			endRowNum :0
		}

	};},
	
	inited : false,

	initialize : function(options){
		var pageInfo = options.pageInfo || {};
		delete options.pageInfo;
		Sigma.$extend(this,options);
		Sigma.$extend(this.pageInfo,pageInfo);
	},

	destroy : function(){
		var nav=this;
		var dList=['firstPageButton','prevPageButton','nextPageButton','lastPageButton','gotoPageButton'];
		Sigma.$each(dList,function(k,i){
			if (nav[k] && nav[k].destroy) {
				nav[k].destroy();
			}
			nav[k]=null;
		});
		Sigma.U.removeNode(this.pageInput);
		this.pageInput=null;

	},

	buildNavTools : function(){
		var grid=Sigma.$grid(this.gridId);

		this.firstPageButton=new Sigma.Button({
				container : grid.toolBar,cls:"gt-first-page", 
				onclick:this.gotoFirstPage,onclickArgs:[this]
			} );

		this.prevPageButton=new Sigma.Button({
					container : grid.toolBar,cls:"gt-prev-page", 
					onclick:this.gotoPrevPage,onclickArgs:[this]
			} );

		this.nextPageButton=new Sigma.Button({
					container : grid.toolBar,cls:"gt-next-page", 
					onclick:this.gotoNextPage,onclickArgs:[this]
			} );


		this.lastPageButton=new Sigma.Button({
					container : grid.toolBar,cls:"gt-last-page", 
					onclick:this.gotoLastPage,onclickArgs:[this]
			} );

/*
this.pageSizeSelect=Sigma.U.createSelect({1:1,2:2,3:3} );
grid.toolBar.appendChild(Sigma.Button.createToolComp(  this.pageSizeSelect ));
Sigma.Button.createSeparator(grid.toolBar);
*/

		this.inited=true;
		if (!grid.loading){
			this.refreshState();
		} 


	},
	createGotoPage : function(){
		var grid=Sigma.$grid(this.gridId);
		this.gotoPageButton=new Sigma.Button({
					container : grid.toolBar,cls:"gt-goto-page", 
					onclick:this.gotoInputPage,onclickArgs:[this],
					text:grid.getMsg('GOTOPAGE_BUTTON_TEXT')
			} );
		if (grid.toolBar) {
			var text1,text2;
			this.pageInput = Sigma.$e("input",{ type:'text', className:'gt-page-input' });
			
			var Me=this;
			Sigma.U.addEvent(this.pageInput,'keydown',function(event){  
				var kcode=event.keyCode;
				if (kcode ==  Sigma.Const.Key.ENTER) {
					Sigma.U.stopEvent(event);
					Me.gotoInputPage(event,Me);
				}
			});

			text1= Sigma.$e("div",{ innerHTML:grid.getMsg('PAGE_BEFORE') , className:'gt-toolbar-text'});
			text2= Sigma.$e("div",{ innerHTML:grid.getMsg('PAGE_AFTER') , className:'gt-toolbar-text'});
			grid.toolBar.appendChild(text1);
			grid.toolBar.appendChild(Sigma.Button.createToolComp( this.pageInput ) );
			grid.toolBar.appendChild(text2);
		}

	},

	/* todo */
	refreshState : function(pageInfo,doCount){
		this.pageInfo = (pageInfo || this.pageInfo);
		var pInfo = this.pageInfo;
		if (doCount!==false) {
			if (pInfo.totalRowNum<1){
				var grid=Sigma.$grid(this.gridId);
				pInfo.totalRowNum=grid.dataset.getSize();
			}

			pInfo.totalPageNum=Math.ceil(pInfo.totalRowNum/pInfo.pageSize);
			pInfo.pageNum= pInfo.pageNum>pInfo.totalPageNum?pInfo.totalPageNum:pInfo.pageNum;
			pInfo.pageNum= pInfo.pageNum <1?1:pInfo.pageNum;

			pInfo.startRowNum= (pInfo.pageNum-1) * pInfo.pageSize +1 ;
			pInfo.startRowNum= isNaN(pInfo.startRowNum)?1:pInfo.startRowNum;
			
			pInfo.endRowNum=pInfo.startRowNum/1+pInfo.pageSize/1-1;
			pInfo.endRowNum=pInfo.endRowNum>pInfo.totalRowNum?pInfo.totalRowNum:pInfo.endRowNum;
		}
		return pInfo;

	},

	refreshNavBar : function(pageInfo){
			this.pageInfo = (pageInfo || this.pageInfo);
			var pInfo = this.pageInfo;
			var grid=Sigma.$grid(this.gridId);
			if (this.inited ){
				if (this.pageInput)	{
					this.pageInput.value= pInfo.pageNum;
					this.pageInput.maxLength=(''+pInfo.totalPageNum).length;
				}
				if (pInfo.pageNum==1){
					this.firstPageButton.disable();
					this.prevPageButton.disable();
				}else{
					this.firstPageButton.enable();
					this.prevPageButton.enable();
				}

				if (pInfo.pageNum==pInfo.totalPageNum){
					this.nextPageButton.disable();
					this.lastPageButton.disable();
				}else{
					this.nextPageButton.enable();
					this.lastPageButton.enable();
				}
			}
		if (grid && grid.pageSizeSelect){
			grid.pageSizeList = !grid.pageSizeList||grid.pageSizeList.length<1? [grid.pageSize]:grid.pageSizeList;
			grid.pageSizeSelect.innerHTML="";		
			Sigma.U.createSelect( Sigma.U.listToMap(grid.pageSizeList) ,this.pageInfo.pageSize,{},grid.pageSizeSelect);
		}


	},

	gotoPage : function(navObj,pageNum,lastAction){
		navObj = navObj ||this;
		var oldPageNum=navObj.pageInfo.pageNum;
		var grid=Sigma.$grid(navObj.gridId);

		pageNum=(!pageNum||pageNum<1)?1:(pageNum>navObj.pageInfo.totalPageNum?navObj.pageInfo.totalPageNum:pageNum);

		if (Sigma.$invoke(grid,'beforeGotoPage',[pageNum,oldPageNum,navObj,grid])!==false){
			/* todo */
			grid.lastAction=lastAction;
			grid.gotoPage(pageNum,oldPageNum);
		}
		
	},

	gotoInputPage:function(event ,navObj){
		navObj.gotoPage(navObj,Sigma.U.parseInt(navObj.pageInput.value,navObj.pageInfo.pageNum),'gotoPage');
	},


	gotoFirstPage:function(event ,navObj){
		navObj.gotoPage(navObj,1,'firstPage');
	},

	gotoPrevPage:function(event ,navObj){
		navObj.gotoPage(navObj, navObj.pageInfo.pageNum-1,'prevPage');

	},
	gotoNextPage:function(event ,navObj){
		navObj.gotoPage( navObj, navObj.pageInfo.pageNum+1,'nextPage');
	},

	gotoLastPage:function(event ,navObj){
		 navObj.gotoPage( navObj, navObj.pageInfo.totalPageNum,'lastPage');
	},

	refreshPage : function(event ,navObj){
		navObj.gotoPage(navObj, navObj.pageInfo.pageNum,'refreshPage');
	}

} );


///////////////////////////////////////////////////////////////////

/*

Sigma.BaseItem
	applyTo : string  ---->  toolbar, menu
	icon : string 
	text  : string
	toggle : boolean
	clickShowChildren : boolean
	handler : function
	addChild : function
	setChildren : function

Sigma.BaseItemGroup
	type : string  ---->  common radio check
	addItem : function
	setItems : function


Sigma.Menu

Sigma.MenuItem

Sigma.Button

Sigma.Separator  V/H

Sigma.Toolbar

Sigma.Navigator


Sigma.Dialog
	setSize
	icon
	title
	moveable
	resizable
	body
	beforeClose


*/

Sigma.BaseMenuItem = Sigma.$class( {

	id				: null ,

	gridId			: null ,
	cls	: null ,

	type			: null ,

	onclickArgs		: null ,
	parentItem		: null ,
	reference		: null ,
	container		: null ,

	text			: null ,
	toolTip			: null ,
	itemBox			: null ,
	itemIcon		: null ,
	itemText		: null ,
	itemAfterIcon	: null ,

	subMenu			: null,

	initialize : function( options){
	
		this.disabled=false;
		this.withSeparator=false;
		this.overShowSubMenu=true;
	
		this.onclick=Sigma.$empty;
	
		Sigma.$extend(this,options);
	
		this.toolTip = this.toolTip || this.text || '';
	
	},

	destroy : function(){
		if (this.subMenu) {
			this.subMenu.destroy();
		}
		this.container=null;
		this.parentItem=null;
		if (this.separator) {
			Sigma.U.removeNode(this.separator);
			this.separator=null;
		}
		Sigma.U.removeNode(this.itemIcon);
		this.itemIcon=null;
		Sigma.U.removeNode(this.itemText);
		this.itemText=null;
		Sigma.U.removeNode(this.itemAfterIcon);
		this.itemAfterIcon=null;
		Sigma.U.removeNode(this.itemBox);
		this.itemBox=null;
	},

	onclickAction : function(event,itemT){ 
			
			Sigma.activeGrid &&  Sigma.activeGrid.endEdit() ;

			var hidden=itemT.subMenu?itemT.subMenu.hidden:false;

			if (itemT.parentItem){
				(itemT.parentItem.closeSubMenu) && itemT.parentItem.closeSubMenu(event);
				if (itemT.parentItem.currenItem){
					Sigma.U.removeClass(itemT.parentItem.currenItem.itemBox,'gt-menu-activemenu');
				}
					itemT.parentItem.currenItem=itemT;
					Sigma.U.addClass(itemT.itemBox,'gt-menu-activemenu');
				
			}

			if (itemT.disabled || itemT.onclick.apply(itemT, [event].concat(itemT.onclickArgs|| itemT) )===false){
				Sigma.U.stopEvent(event);
				return;
			}

			Sigma.U.stopEvent(event);
			if (itemT.type=='checkbox'){
				itemT.toggleCheck();
			}else if(itemT.type=='radiobox'){
				var others= itemT.parentItem.itemList;
				for (var i=0;i<others.length ;i++ ){
					if (others[i].type=='radiobox' && others[i]!=itemT ){others[i].uncheckMe();}
				}
				itemT.checkMe();

			}

			if (itemT.subMenu){
				if (hidden){
					itemT.showMenu(event);
				}else{
					itemT.closeMenu(event);
				}
			}

		},


	closeSubMenu : Sigma.$empty,

	checkMe:function(){
				Sigma.U.removeClass(this.itemIcon,'gt-icon-unchecked');
				Sigma.U.addClass(this.itemIcon,'gt-icon-'+this.type);
				this.checked=true;
	},

	uncheckMe:function(){
				Sigma.U.removeClass(this.itemIcon,'gt-icon-'+this.type);
				Sigma.U.addClass(this.itemIcon,'gt-icon-unchecked');
				this.checked=false;
	},

	toggleCheck:function (){
			if (this.checked===true){
				this.uncheckMe();
			}else{
				this.checkMe();
			}
	},

	disable :function(){
			Sigma.U.addClass(this.itemBox,"gt-button-disable");
			this.disabled=true;
		},
	
	enable :function(){
			Sigma.U.removeClass(this.itemBox,"gt-button-disable");
			this.disabled=false;
		},
	getMenuPosition :function(){
			if (this.subMenu){
				return this.subMenu.position;
			}
			return null;
	},
	setMenuPosition :function(position){
			if (this.subMenu && position){
				this.subMenu.position=position;
			}
	},
	showMenu:function(event){
		if (this.subMenu){
			if (!this.getMenuPosition()){
				this.setMenuPosition('R');
			}
			this.subMenu.show(event);
		}

	},
	toggleMenu:function(event){
		if (this.subMenu){
			if (!this.getMenuPosition()){
				this.setMenuPosition('R');
			}
			this.subMenu.toggle(event);
		}

	},

	closeMenu :function(event){
		var menu=this;
		while( (menu=menu.subMenu) ){
			menu.close(event);
		}

	},

	addMenuItems : function(menuItem,position){
		if (menuItem){
			if (!this.subMenu){
				this.subMenu=new Sigma.GridMenu({gridId:this.gridId,parentItem:this, reference: this.itemBox  });
				this.itemAfterIcon && Sigma.U.addClass(this.itemAfterIcon,'gt-menu-parent');
			}	
			menuItem.gridId=this.gridId;
			this.setMenuPosition(position);
			this.subMenu.addMenuItems(menuItem);
		}

		return this;
	}
} );


Sigma.Button=Sigma.BaseMenuItem.extend( {

	initialize : function( options){
		
		this.className='gt-image-button';
		this.clickClassName='gt-image-button-down';

		this._parent(options);
	
		if (!this.container ){
			return;
		}
		this.itemBox=Sigma.$e("a",{ href:'javascript:void(0);return false;', className:this.className,title:this.toolTip });

		this.itemIcon=Sigma.$e("div",{ className:this.cls });


		this.itemBox.appendChild(this.itemIcon);
		
		this.container.appendChild(this.itemBox);

		if(this.withSeparator){
			Sigma.Button.createSeparator(this.container);
		}

		var Me=this;


			Sigma.U.addEvent(Me.itemBox,"mousedown",function(event){ 
				if (!Me.disabled){
					Sigma.U.addClass(Me.itemBox,Me.clickClassName); 
				} } );
			Sigma.U.addEvent(Me.itemBox,"mouseup",function(event){ 
				if (!Me.disabled){
					Sigma.U.removeClass(Me.itemBox,Me.clickClassName); 
			} } );

			Sigma.U.addEvent(Me.itemBox,"click",function(event){ Me.onclickAction(event,Me); });
			Sigma.U.addEvent(Me.itemBox,"dblclick",function(event){ Me.onclickAction(event,Me); });

			//if ( Me.overShowSubMenu){
			//	Sigma.U.addEvent(Me.itemBox,"mouseover",function(event){ Me.onclickAction(event,Me); });
			//}else{
			//
			//}
	}


});



Sigma.$extend( Sigma.Button , {

	createSeparator : function(bContainer){
		var buttonObj=Sigma.$e("div",{ className:'gt-image-button gt-button-split' });
		if (bContainer ){
			bContainer.appendChild(buttonObj);
		}
		return buttonObj;
	},

	createCommonButton : function(buttonId,cls,clickFn,clickFnArgs,container, withSeparator){
		return new Sigma.Button({
				id : buttonId,
				container : container,cls:cls, 
				onclick:clickFn,onclickArgs:clickFnArgs,
				withSeparator : withSeparator
		} );
	},
	createToolComp: function(obj){
		var div=Sigma.$e("div",{className:'gt-toolbar-comp' });
		if (obj){
			if (Sigma.$type(obj,'string','number')) {
				div.innerHTML=obj;
			}else{
				div.appendChild(obj);
			}
		}
		return div;
	}

}

);


Sigma.MenuItem=Sigma.BaseMenuItem.extend( {


	initialize : function( options){

		this._parent(options);

			if(this.type=="checkbox" || this.type=="radiobox"){
				this.cls= this.checked ?('gt-icon-'+this.type):'gt-icon-unchecked';
			}
			this.itemBox=Sigma.$e("a", { href:'javascript:void(0);return false;', className : 'gt-menuitem'}  );

			this.itemIcon=Sigma.$e("div", {  className : 'gt-menu-icon '+this.cls}  );
			this.itemText=Sigma.$e("div", {  className : 'gt-checkboxtext',innerHTML: this.text,title:this.toolTip } );
			this.itemAfterIcon=Sigma.$e("div", {  className : 'gt-aftericon '+this.afterIconClassName}  );

			this.itemBox.appendChild(this.itemIcon);
			this.itemBox.appendChild(this.itemText);
			this.itemBox.appendChild(this.itemAfterIcon);

			var Me=this;

			Sigma.U.addEvent(Me.itemBox,'click',function(event){ Me.onclickAction(event,Me); });

	}


});



Sigma.$extend( Sigma.MenuItem , {

	createSeparator : function(bContainer){
		var buttonObj=Sigma.$e("div",{ className:'gt-image-button gt-button-split' });
		if (bContainer ){
			bContainer.appendChild(buttonObj);
		}
		return buttonObj;
	}
} );



Sigma.GridMenu=Sigma.$class( {

		gridId		: null,			
		parentItem	: null,	
		container	: null,	
		fixX		: null,	
		fixY		: null,							
		
		destroy : function(){
			this.container=null;
			this.parentItem=null;
			Sigma.$each(this.itemList,function(k,i,list){
				Sigma.U.removeNode(k);
				list[i]=null;
			});
			this.itemList=null;

		},

		initialize : function(options){

			this.itemList=[];
			this.refreshOnShow=false;
			this.currenItem=null;
		
			this.hidden=true;
			this.className='gt-popmenu';
		
			this.position=''; // L T R B LT RT RB LB M

			Sigma.$extend(this,options);
			this.menuBox=Sigma.$e("div", {  className : this.className,style : {display:"none",left:"10px",top:"10px"} }  );

			var grid=Sigma.$grid(this.gridId) || {};

			this.container= this.container || grid.gridDiv || Sigma.doc.body;
			this.container.appendChild(this.menuBox);

		},

		refresh :function(){

		},

		onshow:function(){

		},

		clearUp : function(){

		},
		addMenuItems : function(menuItems){
			menuItems=[].concat(menuItems);
			for (var i=0;i<menuItems.length;i++ ){
				if (menuItems[i]){
					menuItems[i].gridId=this.gridId;
					menuItems[i].parentItem=this;
					menuItems[i].container=this.menuBox;

					this.itemList.push(menuItems[i]);
					this.menuBox.appendChild(menuItems[i].itemBox);
				}
			}

			return this;
		},
		show : function(event){
			if (this.container &&this.container.parentNode && this.container.parentNode.className.indexOf('menu')>1){
				//this.menuBox.style.zIndex=this.parentItem.parentNode.style.zIndex+1;
			}
				
			this.menuBox.style.display="block";
			//Sigma.U.fadeIn(	this.menuBox);


			var x,y;
			//	var x=Sigma.U.getPosLeft(this.parentItem,this.container);
			//	var y=Sigma.U.getPosTop(this.parentItem,this.container);

			var xy=Sigma.U.getXY(this.reference,this.container);
			x=xy[0];
			y=xy[1];
			this.fixX=this.fixX||0;
			this.fixY=this.fixY||0;
				switch (this.position.toUpperCase()){
					case 'L' : 
						x-=this.menuBox.offsetWidth;
						break;
					case 'T' : 
						y-=this.menuBox.offsetHeight;
						break;				
					case 'R' : 
						x+=this.reference.offsetWidth;
						break;
					case 'B' : 
						y+=this.reference.offsetHeight;
						break;
					case 'LT' : 
						x-=this.menuBox.offsetWidth;
						y-=this.menuBox.offsetHeight-this.reference.offsetHeight;
						break;
					case 'RT' : 
						x+=this.reference.offsetWidth;
						y-=this.menuBox.offsetHeight-this.reference.offsetHeight;
						break;
					case 'RB' : 
						x+=this.reference.offsetWidth;
						y+=this.reference.offsetHeight;
						break;
					case 'LB' : 
						x-=this.reference.offsetWidth;
						y+=this.menuBox.offsetHeight;
						break;
					case 'M' : 
						x=event.pageX||(event.clientX - event.x);
						y=event.pageY||(event.clientY - event.y);
						break;
					default:
						y+=this.reference.offsetHeight;				
				}

				Sigma.U.setXY(this.menuBox,[ x+this.fixX,y+this.fixY]);

				//this.menuBox.style.left=  x+this.fixX+'px';
				//this.menuBox.style.top= y+this.fixY+ "px";
				this.hidden=false;
		},
		close : function(event){
				this.closeSubMenu(event);
				this.menuBox.style.display='none';
				this.hidden=true;
		},
		closeSubMenu : function(event){
				for (var i=0;i<this.itemList.length;i++){
					this.itemList[i].closeMenu(event);
				}
		},
		toggle : function(event){

			Sigma.U.stopEvent(event);
			var grid=Sigma.$grid(this.gridId);

			if (this.hidden===true){
				this.show(event);
			}else{
				this.close(event);
			}
		}
} );


////////////////////////////////////////
/**
  * @description {Class} ToolFactroy
  * This class is for managing tool buttons. 
  * @inherit Object
  * @namespace Sigma
  */
 
/**
  * @description {Method} register   
  * The following sample shows how to add your own button on tool bar.
  * @sample
  * Sigma.ToolFactroy.register(
  *        'mybutton',  //id of the button. Lower case accepted only.
  *        {
  *            // css style of the button
  *            // .mybutton-cls { 
  *            //    background : url(./mybutton.png) no-repeat center center; 
  *            // }
  *            cls : 'mybutton-cls',  
  *            // tip of the button
  *            toolTip : 'Press this button to get grid id',        
  *            //function fired when button pressed
  *            action : function(event,grid) {  alert( 'The id of this grid is  '+grid.id)  }
  *        }
  * );
  * //show your own button 
  * toolbarContent : 'nav | mybutton | state' ,
  */
Sigma.ToolFactroy =  {
	
	register : function(key , fn ){
		Sigma.ToolFactroy.tools[key]=fn;
	},

	create : function(grid,type, doIt){
		if (doIt===false){
			return false;
		}
		grid=Sigma.$grid(grid);
		var Me = grid ;
		if (type=='info' || type=='pagestate'){
			type='state';
		}
		var tf=Sigma.ToolFactroy.tools[type];
		if (tf && Sigma.$type(tf,'function') ){
			tf=tf(grid,type,doIt);
		}else if (tf){
			var name=tf.name || type ;
			var action=tf.onclick || tf.action;
			tf=new Sigma.Button({
						container : tf.container || grid.toolBar,
						cls : tf.cls || "gt-tool-"+name,  
						toolTip : tf.toolTip || grid.getMsg('TOOL_'+name.toUpperCase()) ,
						onclick: function(event){
							action(event,grid);
						}
				} );

		}
		return tf;

	},
	tools : {
		'goto' : function(grid){
			return grid.navigator.createGotoPage();
		},

		'pagesize' : function(grid){
			//var pageSizeSelect = Sigma.U.createSelect( listToMap(grid.pageSizeList) ,grid.pageInfo.pageSize);
			var pageSizeSelect = Sigma.U.createSelect({});
			pageSizeSelect.className='gt-pagesize-select';
			grid.pageSizeSelect=pageSizeSelect;
			function changePageSize(event){
				grid.setPageInfo( {pageSize : pageSizeSelect.value/1});
				grid.navigator.gotoFirstPage(event,grid.navigator);
				grid.pageSizeSelect.blur();
				try{ grid.bodyDiv.focus(); }catch(e){}
			}

			Sigma.U.addEvent(grid.pageSizeSelect, 'change',changePageSize );

			var text1= Sigma.$e("div",{ innerHTML:grid.getMsg('PAGESIZE_BEFORE') , className:'gt-toolbar-text'});
			var text2= Sigma.$e("div",{ innerHTML:grid.getMsg('PAGESIZE_AFTER') , className:'gt-toolbar-text'});
			grid.toolBar.appendChild(text1);
			grid.toolBar.appendChild(Sigma.Button.createToolComp( grid.pageSizeSelect ) );
			grid.toolBar.appendChild(text2);
			return pageSizeSelect;

		},

		'add' : { onclick : function(event,grid){ grid.addRow(); }  },

		'del' : { onclick : function(event,grid){ 
		        if (grid.readOnly || grid.endEdit()===false){
			        return;
		        }
		        grid.deleteRow(); 
			}	
		},

		'save' : { onclick : function(event,grid){ grid.lastAction='save'; grid.save(); } 	},

		'reload' : { onclick : function(event,grid){ grid.lastAction='reload'; grid.reload(); } 	},

		'print' : { onclick : function(event,grid){ grid.lastAction='print'; grid.printGrid(); } 	},

		'xls' : { onclick : function(event,grid){ grid.lastAction='export'; grid.exportGrid('xls'); } 	},

		'pdf' : { onclick : function(event,grid){ grid.lastAction='export'; grid.exportGrid('pdf'); } 	},

		'csv' : { onclick : function(event,grid){ grid.lastAction='export'; grid.exportGrid('csv'); } 	},

		'xml' : { onclick : function(event,grid){ grid.lastAction='export'; grid.exportGrid('xml'); } 	},
			
		'filter' : { onclick : function(event,grid){ grid.lastAction='filter'; grid.showDialog("filter"); } 	},

		'chart' : { onclick : function(event,grid){ grid.showDialog("chart"); } 	},

		'state' : function(grid){
			var button= Sigma.$e("div",{ innerHTML:'&#160;', className:'gt-page-state' });
			grid.toolBar.appendChild(button);
			return button;
		},

		'separator' : function(grid){
			var button=Sigma.Button.createSeparator(grid.toolBar);
			return button;
		},

		'fill' : function(grid){
			var button='';
			return button;
		}
	}

} ;



Sigma.Widget = Sigma.$class( {

	id : null,
	dom : null,
	setDom : function(dom){
		this.dom=dom;
	},

	getDom : function(){
		return this.dom;
	},

	show : function(){
		this.dom && (this.dom.style.display="block" );
	},

	hide : function(){
		this.dom && (this.dom.style.display="none");
	},
	close : function(){
		this.hide();
	},
	
	setPosition : function(x,y){
		if (x||x===0){
			this.left=x;
			this.dom && (this.dom.style.left= this.left +'px');
		}
		if (y||y===0){
			this.top=y;
			this.dom && (this.dom.style.top= this.top + 'px');
		}

	},
	setSize : function(w,h){
		this.width=w||this.width;
		this.height=h||this.height;
		if (!this.dom){ return;	}
		if (this.width/1 && this.width>0) {
			this.dom.style.width= ( this.width-1)+'px';
		}
		if (this.height/1 && this.height>0) {
			this.dom.style.height= ( this.height-1)+'px';
		}
	},

	destroy : function(){
		Sigma.$invoke(this,'beforeDestroy');
		Sigma.U.removeNode(this.dom);
		this.dom=null;
	}


});


//

