//

if (!window.Sigma){
	window.Sigma={};
}
Sigma.loaded=false;

Sigma.init=function(win){
	win = win || window;

	Sigma.doc=document;

	win.undefined = win.undefined;

	var ua = win.navigator.userAgent.toLowerCase();

	Sigma.isIE = ua.indexOf("msie") > -1;
	Sigma.isIE7 = ua.indexOf("msie 7") > -1;
	Sigma.isIE8 = ua.indexOf("msie 8") > -1;
	Sigma.isIE9 = ua.indexOf("msie 9") > -1;

	Sigma.isFF = ua.indexOf("firefox") > -1 ;
	Sigma.isFF1 = ua.indexOf("firefox/1") > -1 ;
	Sigma.isFF2 = ua.indexOf("firefox/2") > -1 ;
	Sigma.isFF3 = ua.indexOf("firefox/3") > -1 ;

	Sigma.isOpera = ua.indexOf("opera") > -1;

	Sigma.isWebkit = (/webkit|khtml/).test(ua);
	Sigma.isSafari = ua.indexOf("safari") > -1 || Sigma.isWebkit ;
	Sigma.isChrome = ua.indexOf("chrome") > -1 || Sigma.isWebkit ;
	Sigma.isGecko = Sigma.isMoz =!Sigma.isSafari && ua.indexOf("gecko") > -1;

	Sigma.isStrict = Sigma.doc.compatMode == "CSS1Compat" || Sigma.isSafari ;
	Sigma.isBoxModel = Sigma.isIE && !Sigma.isIE8 && !Sigma.isIE9 && !Sigma.isStrict ;

	Sigma.isNotStrictIE = Sigma.isBoxModel;

	Sigma.isSecure = win.location.href.toLowerCase().indexOf("https") === 0;


	Sigma.isWindows = (ua.indexOf("windows") != -1 || ua.indexOf("win32") != -1);
	Sigma.isMac = (ua.indexOf("macintosh") != -1 || ua.indexOf("mac os x") != -1);
	Sigma.isLinux = (ua.indexOf("linux") != -1);

};

Sigma.init();

Sigma.$extend = function(original,extended, isDeep){
	if (arguments.length<2){
		extended = original;
		original = this;
	}
	for (var property in extended) {
		var v=extended[property];
		if (isDeep && v && Sigma.$type(v,'object','array') ){
			v=Sigma.$clone(v, isDeep);
		}
		if (v!==undefined) {
			original[property] = v;
		}

	}
	return original;
};

Sigma.$extend(Sigma , {

	$empty : function(){},

	$chk:function (obj){
		return !!(obj || obj === 0 || obj==='' );
	},

//'string', 'array', 'object', 'arguments', 'collection','number'
	$type : function (obj){
		var argNum=arguments.length;
		if (argNum>1){
			for (var i=1;i<argNum;i++){
				if(Sigma.$type(obj)==arguments[i]) { return true; }
			}
			return false;
		}
		var type = typeof obj;
		if (obj === null) { return 'object'; }
		if (type == 'undefined') { return 'undefined'; }
		if (obj.htmlElement) { return 'element'; }
		if (type == 'object'  && obj.nodeType && obj.nodeName){
			switch(obj.nodeType){
				case 1: return 'element';
				case 3: return (/\S/).test(obj.nodeValue) ? 'textnode' : 'whitespace';
			}
		}
		if (Sigma.U.isArray(obj)) {
			return 'array';
		}
		/*
		if (type == 'object' || type == 'function'){
			switch(obj.constructor){
				case RegExp: return 'regexp';
				case Sigma.Class: return 'class';
			}
		}
		*/
		if (type == 'object' && typeof obj.length == 'number'){
			return (obj.callee) ? 'arguments' : 'collection';
		}else if (type == 'function' && typeof obj.length == 'number' && obj[0]!==undefined ){
			return  'collection';
		}

		return type;
	},


	$merge: function (){
		var mix = {};
		for (var i = 0; i < arguments.length; i++){
			for (var property in arguments[i]){
				var ap = arguments[i][property];
				var mp = mix[property];
				if (mp && Sigma.$type(ap,'object') && Sigma.$type(mp,'object') ) {
					mix[property] = Sigma.$merge(mp, ap);
				}else {
					 mix[property] = ap;
				}
			}
		}
		return mix;
	},

	$indexOf : function(arr,item,start){
		if (arr){
			start = start || 0;
			for (var i=start,j=arr.length; i<j; i++) {
				if (arr[i]===item) {
					return i;
				}
			}
		}
		return -1;
	},

	$array : function(iterable,start, end ,isDeep) {
		var results = [];
		if (iterable) {
			if (!Sigma.$chk(start)){
				start=0;
			}
			if (!Sigma.$chk(end)){
				end=iterable.length;
			}
			if (Sigma.$type(iterable,'arguments', 'collection') ||  Sigma.$type(iterable,'array')  &&  (start>0 || end<iterable.length) ){
				for (var i = start; i < end; i++) {
					results.push(iterable[i]);
				}
			}else if (Sigma.$type(iterable,'array') ){
				results=results.concat(iterable);
			}else{
				for (var k in iterable ){
					if (iterable.hasOwnProperty(k)){
						results.push( iterable[k] );
					}
				}
			}
		}
		return results;
	},
	
	$clone : function(obj,isDeep){
		var newObj;
		if (!obj){
			newObj=obj;
		}else if (Sigma.$type(obj,'array','arguments', 'collection')){
			newObj=Sigma.$array(obj,0,obj.length,isDeep);
		}else{
			newObj= Sigma.$extend({},obj,isDeep);
		}
		return newObj;
	},

	$msg : function(msgTemplate, msgs){
		for (var i=1;i<arguments.length ;i++ ){
			msgTemplate=Sigma.U.replaceAll(msgTemplate,"#{"+i+"}",arguments[i]);
		}
		return msgTemplate;
	},

	$clear : function (timer){
		window.clearTimeout(timer);
		window.clearInterval(timer);
		if ( CollectGarbage ){
			CollectGarbage();
		}
		return null;
	},
		
	$thread : function(fn,timeout){
		//fn();
		//return;
		var nfn=fn;
		window.setTimeout(nfn ,timeout || 20);	
	},


	$each : function(iterable, fn, bind,arg){
		var resultList=[];
		if ( Sigma.$type(iterable,'array','arguments','collection') || iterable&&!Sigma.$type(iterable,'string')&&Sigma.$type(iterable.length,'number') ) {
			for (var i = 0, j = iterable.length; i < j; i++) {
				resultList.push( fn.call(bind || iterable, iterable[i], i, iterable,arg) );
			}
		} else {
			 for (var name in iterable) {
				 resultList.push( fn.call(bind || iterable, iterable[name], name,iterable,arg) );
			 }
		}
		return resultList;
	},

	$getText : function(el){
		return el.innerText===undefined?el.textContent : el.innerText;
	},	


	$element : function(el,props){
		if (Sigma.$type(el,'string') ){
			if (Sigma.isIE && props && (props.name || props.type)){
				var name = (props.name) ? ' name="' + props.name + '"' : '';
				var type = (props.type) ? ' type="' + props.type + '"' : '';
				delete props.name;
				delete props.type;
				el = '<' + el + name + type + '>';
			}
			el = Sigma.doc.createElement(el);
		}
		if (props){
			if (props.style){
				Sigma.$extend(el.style,props.style);
				delete props.style;
			}
			Sigma.$extend(el,props);
		}
		return el;
	}

} );


Sigma.Class=function(properties){

	properties = properties || {};

	var klass = function(){

			var prop=this.properties;
			if (Sigma.$type(prop, 'function')){
				prop=prop.apply(this, arguments);
			}

			if (Sigma.$type(prop, 'object')){
				Sigma.$extend(this, prop);
			}
			var methds=this.abstractMethods ;
			Sigma.$each(this.abstractMethods , function(_item){
				this[_item]=Sigma.$empty;
			},this);

			return (arguments[0]!==Sigma.$empty 
					&& Sigma.$type(this.initialize, 'function') ) ? 
						this.initialize.apply(this, arguments) : this;
		};
		
		Sigma.$extend(klass, this);
		klass.constructor = Sigma.Class;
		klass.prototype = properties;

		return klass;
};

Sigma.Class.prototype={

	extend:function(){
		var proto = new this(Sigma.$empty);
		for (var i = 0, l = arguments.length; i < l; i++) {
			var properties=arguments[i];
			for (var property in properties){
				var pp = proto[property];
				proto[property] = Sigma.Class.merge(pp, properties[property]);
			}
		}
		return new Sigma.Class(proto);
	}

};

Sigma.Class.merge = function(previous, current){
	if (previous && previous != current){
		var type = Sigma.$type(current);
		if ( !Sigma.$type(previous,type) ) { return current; }
		switch(type){
			case 'function':
				var merged = function(){
					this._parent = arguments.callee._parent;
					return current.apply(this, arguments);
				};
				merged._parent = previous;
				return merged;
			case 'object': return Sigma.$merge(previous, current);
		}
	}
	return current;
};





//////////////////////////////////////////


Sigma.$class = function(properties){
	return new Sigma.Class(properties);
};


Sigma.$e=Sigma.$element;
Sigma.$A=Sigma.$array;


Sigma.$byId=function(el,pros){
	if ( !Sigma.$chk(el) ) { return null; }
	var type = Sigma.$type(el);
	if (type == 'element'){
		return Sigma.$e(el,pros);
	}
	if (type == 'string' || type == 'number' ){
		el = Sigma.doc.getElementById(''+el);
	}
	if ( !el ) { return null; }

	if (  Sigma.U.contains(['object', 'embed'],!el.tagName?el.tagName.toLowerCase():'')) { return el; }
	return Sigma.$e(el);
};

Sigma.getDom = function(el){
	if(!el || !document){
		return null;
	}
	return el.dom ? el.dom : (typeof el == 'string' ? document.getElementById(el) : el);
};

Sigma.$byName=function(el){
	var elList=[];
	if ( !Sigma.$chk(el) ) { return elList; }
	var elColl=Sigma.doc.getElementsByName(''+el);

	if (!elColl || elColl.length<1 ){
		return elList;
	}
	for (var i=0;i<elColl.length;i++){
		el=elColl[i];
		elList.push( Sigma.U.contains(['object', 'embed'],el.tagName.toLowerCase())?el:Sigma.$e(el));
	}
	return elList;
};

Sigma.$=function(el){
	var tEl=Sigma.$byName(el);
	//if ( tEl && tEl.length===1 ){
	if ( tEl && tEl.length>0 ){
		return tEl[0];
	}
	return (!tEl||tEl.length<1)?Sigma.$byId(el):tEl;
};




/////////////////////////////////////




Sigma.Utils={
	P_START : '@{',
	P_END : '}',
	P_VAR_NAME : 'obj_in',
	parseExpression : function(ex,pName,argNames,pStart,pEnd){
			pStart =  pStart || Sigma.U.P_START;
			pEnd = pEnd || Sigma.U.P_END;
			pName = pName || Sigma.U.P_VAR_NAME;
			argNames = argNames || pName;
			var startLength= pStart.length;
			var endLength= pEnd.length;

			var templateC=[];

			var current=0;
			while(true){
				var start= ex.indexOf( pStart ,current);
				var sBegin=start+ startLength;
				var sEnd=ex.indexOf( pEnd ,sBegin);
				var str=null;
				var val=null;

				if (sBegin>= startLength  && sEnd>sBegin){
					str=ex.substring(current,start);
					val=ex.substring(sBegin,sEnd);
				}else{
					str=ex.substring(current);
				}
	
				//if (!Sigma.U.isNumber(str)){
					str=Sigma.U.escapeString(str);
				//}
				templateC.push( str );

				if (val===null){
					break;
				}
				if (!Sigma.U.isNumber(val)){
					val=(pName?(pName+'.'):'')+ val ;
				}else{
					val=(pName?(pName+'['):'')+ val +(pName?']':'');
				}
				templateC.push( val );
				current=sEnd+ endLength;

			}
		var t="function(" + argNames + "){ return "+ templateC.join('+')+" }";
		eval("t="+t);
		return t;
		//return new Function(pName, 'return '+templateC.join(''));
				
	},
	
	isArray : function(a){
		return Object.prototype.toString.apply(a) === '[object Array]' ;
	},
	isNumber : function(n ,strict){
		return n===0 || (strict?Sigma.$type(n, 'number'):(n && !isNaN(n)));
	},
	parseInt : function(num,defaultNum){
		var t=parseInt(num);
		return isNaN(parseInt(num))? defaultNum || 0:t;
	},
	add2Map : function(key,value,map){
		map=map || {};
		if (map[key]===undefined) {
			map[key]=value;
		}else{
			map[key]=[].concat(map[key]);
			map[key].push(value);
		}
		return map;
	},

	moveItem : function (arr,fromIdx, toIdx){
		//fromIdx=fromIdx<0 ? 0:( fromIdx>arr.length-1?arr.length-1:fromIdx);
		//toIdx=toIdx<0 ? 0:( toIdx>arr.length-1?arr.length-1:toIdx);
		if (fromIdx==toIdx) {
			return arr;
		}

		var moveObj =arr[fromIdx];
		var dObj =arr[toIdx];
		arr.splice(toIdx,1,moveObj,dObj);
		if (fromIdx<toIdx) {
			arr.splice(fromIdx,1);
		}else{
			arr.splice(fromIdx+1,1);
		}	
		return arr;
	},


	 convert: function (sValue, sDataType) {

					switch(sDataType) {
						case "int":
							return parseInt(sValue);
						case "float":
							return parseFloat(sValue);
						case "date":
							return sValue; //(new Date(Date.parse(sValue))).getTime();
						default:
							return sValue;
					
					}
			return sValue;
	},
	
	getTagName : function(node){
		return node && node.tagName ? String(node.tagName).toUpperCase(): null;
	},

	getParentByTagName : function(tagName,node,event,deep){
		if (!node){
			event=Sigma.$event(event);
			node=Sigma.U.getEventTarget(event);
		}
		deep= deep || 6;
		if (!node){ return null; }
		tagName = tagName.toLowerCase();

		while( node && (deep--)>0 ) {
			if(node.tagName && node.tagName.toLowerCase()==tagName){
				return node;
			}
			/* todo */
			if ( Sigma.U.hasClass(node.className,'gt-grid') && tagName!="div" ){
				break;
			}
			node=node.parentNode;
		}
		return null;
	},

	focus : function(el){
		if (el) {
			try{
				el.focus();
				el.select && el.select();
			}catch(e){}
		}
	},

	hasClass: function(el,className){
		return el?Sigma.U.hasSubString(el.className,className, ' '):false;
	},

	addClass: function(el,className){
		if (el && !Sigma.U.hasClass(el,className)) {
			el.className = Sigma.U.clean(el.className + ' ' + className) ;
		}
		return el;
	},

	removeClass: function(el,className){
		if (el){
			el.className = Sigma.U.clean(el.className.replace(new RegExp('(^|\\s)' + className + '(?:\\s|$)'), '$1') );
		}
		return el;
	},

	toggleClass: function(el,className){
		return Sigma.U.hasClass(el,className) ? Sigma.U.removeClass(el,className) : Sigma.U.addClass(el,className);
	},

	hasSubString: function(str,string, s){
		return (s) ? (s + str + s).indexOf(s + string + s) > -1 : str.indexOf(string) > -1;
	},


	childElement : function(p, index){

		var i = 0;
		var n = p?p.firstChild:null;
		while(n){
			if(n.nodeType == 1){
			   if(++i == index){
				   return n;
			   }
			}
			n = n.nextSibling;
		}
		return null;
	},

	firstChildElement : function(el){
		return Sigma.U.childElement(el,1);
	},

	lastChildElement : function(el){
		var tEl=el.childNodes[el.childNodes.length-1];
		return tEl.nodeType == 1?tEl:Sigma.U.prevElement(tEl);
	},

	nextElement : function(n){
			while((n = n.nextSibling) && n.nodeType != 1){}
			return n;
	},

	prevElement : function(n){
			while((n = n.previousSibling) && n.nodeType != 1){}
			return n;
	},

	getCellIndex : function(td){
		if (Sigma.isIE){
			var cells=td.parentNode.cells;
			for (var i=0,j=cells.length;i<j;i++ ){
				if (cells[i]===td){
					return i;
				}
			}
		}
		return td.cellIndex;
	},

	insertNodeBefore : function(elA,elB){
		if (!elA || !elB || !elB.parentNode){
			return null;
		}
        elB.parentNode.insertBefore(elA, elB);
        return elA;
    },

	insertNodeAfter : function(elA,elB){
        elB.parentNode.insertBefore(elA, elB.nextSibling);
        return elA;
    },

	listToMap : function (list){
						var map={};
						for (var i=0;i<list.length;i++ ){
							map[list[i]]=list[i];
						}
						return map;
					},

	createSelect : function(map, defaultValue,opt, selectEl){
			selectEl=selectEl || Sigma.$e("select",opt||{});
			var sTemp=Sigma.doc.createDocumentFragment();
			Sigma.$each(map,
				function(text,value){
					var op=Sigma.$e("option",{'value' : value ,'text':''+text, innerHTML :text});
					if (Sigma.$chk(defaultValue) && value==defaultValue){
						op.selected=true;
					}
					sTemp.appendChild(op);
				}
			);
			selectEl.appendChild(sTemp);
			return selectEl;
	},

	createSelectHTML : function(map,defaultValue,opt){
		opt=opt||{};
		var id=opt.id?(' id="'+opt.id+'" '):' ' ,
			cls=opt.className || '' ,
			st =opt.style? (' style="'+opt.style+'" '):' ' ;
		var selectH= ['<select'+ id + st+'class="gt-input-select '+cls+'">'];
		for (var k in map ){
			var s='';
			if ( (defaultValue||defaultValue===0) && k==defaultValue){
					s=' selected="selected" ';
			}
			selectH.push('<option value="'+k+'" '+s+'>'+map[k]+'</option>');
		}
		selectH.push('</select>');
		return selectH.join('');
	},

	getEventTarget : function(evt){

		var targetEl=null;
		try{
			targetEl=evt.target || evt.srcElement;
		}catch(e){ return null; }
		return !targetEl?null:(targetEl.nodeType == 3 ? targetEl.parentNode: targetEl);
	},


	stopEvent : function(event) {  
		event = event || window.event;
		if (event){
		//event = event || window.event;
			if (event.stopPropagation){
				event.stopPropagation();
				event.preventDefault();
			} else {
				event.cancelBubble = true;
				event.returnValue = false;
			}
		}
		//return event;
	},

	addEvent : function(el,type, fn,bind,args){
		if ( !fn || !el || !type ){ return false; }
		if (arguments.length>3)	{
			fn=Sigma.U.bindAsEventListener(fn,bind,args);
		}
		if (el.addEventListener) {
			el.addEventListener(type,fn, false);
		}else{
			var _type= type=='selectstart'?type: 'on' + type;
			 el.attachEvent(_type, fn);
		}

		Sigma.EventCache.add(el, type, fn, false);
		return el;

	},


	removeEvent: function(el,type, fn,bind,args){
		if ( !fn || !el || !type ){ return false; }
		if (arguments.length>3)	{
			fn=Sigma.U.bindAsEventListener(fn,bind,args);
		}
			if (el.addEventListener) {
				 el.removeEventListener(type,fn, false);
			}else {
				var _type= type=='selectstart'?type: 'on' + type;
				el.detachEvent(_type, fn);
			}

		Sigma.EventCache.remove(el, type, fn, false);
		return el;

	},

	onLoadFuncList : [] ,
		
	onLoadFuncCaller : function(){
		for (var i=0;i< Sigma.U.onLoadFuncList.length;i++ ){
			var func= Sigma.U.onLoadFuncList[i];
			//try{
				func.apply(this, arguments);
			//}catch(e){}
		}
		Sigma.loaded=true;
	},

	onLoad : function(fn, win){
		win = win || window;
		Sigma.U.onLoadFuncList.push(fn);
		if (!Sigma.U.onLoadFuncCaller.hasAdd){
			Sigma.U.addEvent(win, "load", Sigma.U.onLoadFuncCaller);
			Sigma.U.onLoadFuncCaller.hasAdd=true;
		}
	},

	orphanDiv : function(){ 
		var div=Sigma.doc.createElement('div'); 
		div.className="gt-orphan-div";
		return div; }(),


	createElementFromHTML : function(html,parentEl){
		Sigma.U.orphanDiv.innerHTML=html;
		var el=Sigma.U.firstChildElement(Sigma.U.orphanDiv);
		parentEl.appendChild(el);
		Sigma.U.orphanDiv.innerHTML='';
		return el;
	},

	createTrFromHTML : function(html,parentEl){
		 Sigma.U.orphanDiv.innerHTML='<table><tbody>'+html+'</tbody></table>';
		var tr= Sigma.U.orphanDiv.getElementsByTagName('tr')[0];
		parentEl.appendChild(tr);
		 Sigma.U.orphanDiv.innerHTML='';
		return tr;
	},

	removeNode : function(els){
		for (var i = 0; i < arguments.length; i++){
			var el=arguments[i];
			if (!el || !el.parentNode || el.tagName == 'BODY'){ return null;}
			Sigma.EventCache.remove(el);
			if (Sigma.isIE) {
				   Sigma.U.orphanDiv.appendChild(el);
				   Sigma.U.orphanDiv.innerHTML = '';
			}else{
				el.parentNode.removeChild(el);
			}
		}
	},

	removeNodeTree : function(el){
		if (el) {
			var els=el.getElementsByTagName("*");
			for (var i = 0; i < els.length; i++){
				//Sigma.U.removeNode(els[i]);
				Sigma.U.removeNodeTree(els[i]);
			}
			Sigma.U.removeNode(el);
		}
		

	},

	getLastChild : function(el){
		return el.childNodes[el.childNodes.length-1];
	},


	getPosLeftTop:function(elm,pEl) {
			pEl=pEl||window;

		var top = elm.offsetTop;
		var left = elm.offsetLeft;
		elm = elm.offsetParent;
		while(elm && elm != pEl)	{
			top += (elm.offsetTop-elm.scrollTop);
			left += (elm.offsetLeft-elm.scrollLeft);
			elm = elm.offsetParent;
		}
		return [left,top];
	},


	getPosRight:function(elm){
		return Sigma.U.getPosLeftTop(elm)[0]+elm.offsetWidth;
	},

	getPosBottom:function(elm){
		return Sigma.U.getPosLeftTop(elm)[1]+elm.offsetHeight;
	},

	getHeight : function(el,content){
			var h = el.offsetHeight || 0;
			if (content !== true){
				return h;
			}
			var bws=Sigma.U.getBorderWidths(el);
			var pws=Sigma.U.getPaddings(el);
			return h-bws[0]-bws[2]-pws[0]-pws[2];
	 },

	getWidth : function(el,content){
			var w = el.offsetWidth || 0;
			if (content !== true){
				return w;
			}
			var bws=Sigma.U.getBorderWidths(el);
			var pws=Sigma.U.getPaddings(el);
			return w-bws[1]-bws[3]-pws[1]-pws[3];
		},

	getBorderWidths : function(el){
		return [ Sigma.U.parseInt(el.style.borderTopWidth),
			Sigma.U.parseInt(el.style.borderRightWidth),
			Sigma.U.parseInt(el.style.borderBottomWidth),
			Sigma.U.parseInt(el.style.borderLeftWidth) ];
	},

	getPaddings : function(el){
		return [ Sigma.U.parseInt(el.style.paddingTop),
			Sigma.U.parseInt(el.style.paddingRight),
			Sigma.U.parseInt(el.style.paddingBottom),
			Sigma.U.parseInt(el.style.paddingLeft) ];
	},


	getPageX: function(ev) {
		ev = ev || window.event;
		var x = ev.pageX;
		if (!x && 0 !== x) {
			x = ev.clientX || 0;

			if (Sigma.isIE) {
				x += Sigma.U.getPageScroll()[0];
			}
		}

		return x;
	},


	getPageY: function(ev) {
		ev = ev || window.event;
		var y = ev.pageY;
		if (!y && 0 !== y) {
			y = ev.clientY || 0;
			if (Sigma.isIE) {
				y += Sigma.U.getPageScroll()[1];
			}
		}
		return y;
	},

	getPageScroll: function() {
			var dd = Sigma.doc.documentElement, db = Sigma.doc.body;
			if (dd && (dd.scrollLeft || dd.scrollTop )) {
				return [dd.scrollLeft,dd.scrollTop];
			} else if (db) {
				return [db.scrollLeft,dd.scrollTop];
			} else {
				return [0, 0];
			}
		},

    getScroll : function(el){
        var d = el, doc = Sigma.doc;
        if(d == doc || d == doc.body){
            var l = window.pageXOffset || doc.documentElement.scrollLeft || doc.body.scrollLeft || 0;
            var t = window.pageYOffset || doc.documentElement.scrollTop || doc.body.scrollTop || 0;
            return [ l, t];
        }else{
            return [ d.scrollLeft,d.scrollTop];
        }
    },

	getXY : function(el,pEl) {
		var p, pe, b, scroll, bd =  Sigma.doc.body;
		
		if (el.getBoundingClientRect) {
			b = el.getBoundingClientRect();
			scroll = Sigma.U.getScroll(Sigma.doc);
			return [b.left + scroll[0], b.top + scroll[1]];
		}

		var x = 0, y = 0;
		p = el;
		pEl=pEl||bd;
		var hasAbsolute =el.style.position == "absolute";
		while (p) {
			x += p.offsetLeft;
			y += p.offsetTop;
			if (!hasAbsolute && p.style.position == "absolute") {
				hasAbsolute = true;
			}
			if (Sigma.isGecko) {
					pe = p;
				var bt = parseInt(pe.style.borderTopWidth, 10) || 0;
				var bl = parseInt(pe.style.borderLeftWidth, 10) || 0;
				x += bl;
				y += bt;
				if (p != el && pe.style.overflow!= 'visible') {
					x += bl;
					y += bt;
				}
			}
			p = p.offsetParent;
		}

		if (Sigma.isSafari && hasAbsolute) {
			x -= bd.offsetLeft;
			y -= bd.offsetTop;
		}
		if (Sigma.isGecko && !hasAbsolute) {
			var dbd = bd;
			x += parseInt(dbd.style.borderTopWidth, 10) || 0;
			y += parseInt(dbd.style.borderTopWidth, 10) || 0;
		}

		p = el.parentNode;
		while (p && p != bd) {
			if (!Sigma.isOpera || (p.tagName.toUpperCase() != 'TR' && p.style.display != "inline")) {
				x -= p.scrollLeft;
				y -= p.scrollTop;
			}
			p = p.parentNode;
		}
		return [x, y];
	},
	setXY : function(el, xy) {
		if(el.style.position == 'static'){
		   el.style.position='relative';
	   }
		var pts = Sigma.U.translatePoints(el,xy);
		if (xy[0] !== false) {
			el.style.left = pts.left + "px";
		}
		if (xy[1] !== false) {
			el.style.top = pts.top + "px";
		}
	},

    translatePoints : function(el,x, y){
        if(typeof x == 'object' || x instanceof Array){
            y = x[1]; x = x[0];
        }
        var p = el.style.position;
        var o = Sigma.U.getXY(el);

        var l = parseInt(el.style.left, 10);
        var t = parseInt(el.style.top, 10);

        if(isNaN(l)){
            l = (p == "relative") ? 0 : el.offsetLeft;
        }
        if(isNaN(t)){
            t = (p == "relative") ? 0 : el.offsetTop;
        }

        return {left: (x - o[0] + l), top: (y - o[1] + t)};
    },
		
	getContentWidthHeight : function(node){
		var mL=Sigma.U.parseInt(node.style.marginLeft);
		var mR=Sigma.U.parseInt(node.style.marginRight);

		var pL=Sigma.U.parseInt(node.style.paddingLeft);
		var pR=Sigma.U.parseInt(node.style.paddingRight);

		var w= node.clientWidth-pL-pR;
		var h= node.clientHeight;
		return [w,h];
	},

	getPixelValue : function(inval,parentVal){
		if (Sigma.$type(inval,'number')){
			return inval;
		}
		inval=''+inval;
		var nVal=Sigma.U.parseInt(inval);
		if ( inval.indexOf("%")>1 )	{
			return parentVal*nVal/100;
		}
		return nVal;
		
	},

 
	setValue: function(el,value){
		el=Sigma.$(el);
		if (!el){
			return;
		}
		var tag=el.tagName;
		tag=(''+tag).toUpperCase();
		switch( tag ){
			case 'SELECT':
				var values = [].concat(value);
				var firstOption=null;
				Sigma.$each(el.options, function(option,idx){
					if (idx===0) {
						firstOption = option;
					}
					option.selected=false;
					if (el.multiple){
						Sigma.$each(values,function(val){
							option.selected=option.value==val;
						});
					}else if(option.value==values[0]){
						option.selected=true;
						firstOption=false;
					}
				});

				if (!el.multiple && firstOption){
					firstOption.selected=true;
				}

				return (el.multiple) ? values : values[0];
			case 'INPUT': 
				if (el.type=='checkbox' || el.type=='radio'  ){
					el.checked= el.value==value;
					break;
				}
			case 'TEXTAREA': 
				el.value=value;
		}

		return null;
	},

	getValue: function(el){
		el=Sigma.$(el);
		if (!el){
			return;
		}
		var tag=el.tagName;
		switch( tag ){
			case 'SELECT':
				var values = [];
				Sigma.$each(el.options, function(option){
					if (option.selected) { values.push(option.value); }
				});
				values = (el.multiple) ? values : values[0];
				if ( (values===null || values===undefined) && el.options[0] ) {
					values=el.options[0].value;
				}
				return values;
			case 'INPUT': 
				if ( (el.type=='checkbox' || el.type=='radio' ) && !el.checked ){
					break;
				}	
			case 'TEXTAREA': return el.value;
		}
		return null;
	},


	setOpacity: function(el,opacity){
		opacity=opacity>1?1:(opacity<0?0:opacity);
		if (!el.currentStyle || !el.currentStyle.hasLayout) { el.style.zoom = 1; }
		if (Sigma.isIE) {
			 el.style.filter = (opacity == 1) ? '' : "alpha(opacity=" + opacity * 100 + ")";
		}
		el.style.opacity =  opacity;
		if (opacity === 0){
			if (el.style.visibility != "hidden") { el.style.visibility = "hidden";}
		} else {
			if (el.style.visibility != "visible") { el.style.visibility = "visible";}
		}
		return el;
	},

	replaceAll: function(exstr,ov,value){
		var gc=Sigma.U.escapeRegExp(ov);
		if ( !Sigma.$chk(gc) || gc===''){
			return exstr;
		}
		var rep="/"+gc+"/gm";
		var r=null;
		var cmd="r=exstr.replace("+rep+","+Sigma.U.escapeString(value)+")";
		eval(cmd);
		return r;
	},

	trim: function(str, wh){
				if( !str || !str.replace || !str.length ){ return str; }
				var re = (wh > 0) ? (/^\s+/) : (wh < 0) ? (/\s+$/) : (/^\s+|\s+$/g);
				return str.replace(re, '');
	},

	escapeRegExp: function(str) {
		return !str?''+str:(''+str).replace(/\\/gm, "\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm, "\\$1");
	},

	escapeString: function(str){ 
		return str===''?'""':( !str?''+str:
			(
				'"' + (''+str).replace(/(["\\])/g, '\\$1') + '"'
			).replace(/[\f]/g, "\\f"
			).replace(/[\b]/g, "\\b"
			).replace(/[\n]/g, "\\n"
			).replace(/[\t]/g, "\\t"
			).replace(/[\r]/g, "\\r") );
	},

	bind: function(fn,bindObj,args){
		args=[].concat(args);
		return function(){
				return fn.apply(bindObj||fn, Sigma.U.merge(Sigma.$A(arguments),args) );
		};
	},
	
	bindAsEventListener: function(fn,bindObj,args){
		return function(event){
			event= event||window.event;
				return fn.apply(bindObj||fn, [ Sigma.$event(event) ].concat(args));
		};
	},


	clean: function(str){
		return Sigma.U.trim(str.replace(/\s{2,}/g, ' '));
	},

	contains: function(arr,item, from){
		return Sigma.U.indexOf(arr,item, from) != -1;
	},

	merge:function(arr,coll,isOverride){
		var minEnd= arr.length<coll.length?arr.length:coll.length;
		var i,j;
		if (isOverride){
			for (i = 0, j = minEnd; i < j; i++) {
				arr[i]=coll[i];
			}
		}
		for (i = minEnd, j = coll.length; i < j; i++) {
			arr[i]=coll[i];
		}
		return arr;
	},

	each: function(arr,fn, bind){
		return Sigma.$each(arr,fn,bind);
	},


	indexOf:  function(arr,item, from){
		var len = arr.length;
		for (var i = (from < 0) ? Math.max(0, len + from) : from || 0; i < len; i++){
			if (arr[i] === item) { return i; }
		}
		return -1;
	},
	remove:  function(arr,item,all){
		var i = 0;
		var len = arr.length;
		while (i < len){
			if (arr[i] === item){
				arr.splice(i, 1);
				if (!all){ return arr ;}
				len--;
			} else {
				i++;
			}
		}
		return arr;
	},
	next :   function(arr,item){
		var t = Sigma.U.indexOf(arr,item);
		if (t<0){
			return null;
		}
		return arr[t+1];
	},
	previous :   function(arr,item){
		var t = Sigma.U.indexOf(arr,item);
		if (t<1){
			return null;
		}
		return arr[t-1];
	},


	createStyleSheet : function(id,doc){
		doc = doc||Sigma.doc;
		var styleS = doc.createElement("style");
		styleS.id=id;
		
		var head=doc.getElementsByTagName('head')[0];
		head && head.appendChild(styleS);
		
		return styleS;
	},

	getCheckboxState : function(inputs,name){

		var rs={};
		for (var i=0;i<inputs.length ;i++ ){
			if (inputs[i].name==name && inputs[i].checked ){
				rs[inputs[i].value]= inputs[i].checked;
			}
		}
		return rs;
	}

};
Sigma.Util = Sigma.Utils;
Sigma.U = Sigma.Utils;

//=============================================

Sigma.Utils.CSS = function(){
	var rules = null;

   return {
		createStyleSheet : function(cssText, id, docT){
		   var ss;
		   docT = docT || Sigma.doc;
		   var heads=docT.getElementsByTagName("head");
			
			if (!heads || heads.length<1){
				heads=docT.createElement('head');
				if (docT.documentElement) {
					docT.documentElement.insertBefore(heads,docT.body);
				}else{
					docT.appendChild(heads);
				}
				heads=docT.getElementsByTagName("head");
			}
			var head=heads[0];
		   
		   var rules = docT.createElement("style");
		   rules.setAttribute("type", "text/css");
		   if(id){
			   rules.setAttribute("id", id);
		   }
		   if(Sigma.isIE){
			   head.appendChild(rules);
			   ss = rules.styleSheet;
			   ss.cssText = cssText;
		   }else{
			   try{
					rules.appendChild(docT.createTextNode(cssText));
			   }catch(e){
				   rules.cssText = cssText; 
			   }
			   head.appendChild(rules);
			   ss = rules.styleSheet ? rules.styleSheet : (rules.sheet || docT.styleSheets[docT.styleSheets.length-1]);
		   }
		   this.cacheStyleSheet(ss);
		   return ss;
	   },

	   
	   getRules : function(refreshCache,docT){
		   docT = docT || Sigma.doc;
			if( !rules  || refreshCache){
				rules = {};
				var ds = docT.styleSheets;
				for(var i =0, len = ds.length; i < len; i++){
					this.cacheStyleSheet(ds[i]);
				}
			}
			return rules;
		},
		
	   getRule : function(selector, refreshCache){
			var rs = this.getRules(refreshCache);
			return rs[selector.toLowerCase()];
		},
	
		updateRule : function(selector,property, value){
			var rule = this.getRule(selector);
			if(rule){
				rule.style[property] = value ;
			}
		},

	   // private
	   cacheStyleSheet : function(ss){
		   rules = rules || {};
		   try{// try catch for cross domain access issue
			   var ssRules = ss.cssRules || ss.rules;
			   for(var j = ssRules.length-1; j >= 0; --j){
				   rules[ssRules[j].selectorText.toLowerCase()] = ssRules[j];
			   }
		   }catch(e){}
	   }
   };	
}();

////////////////////////////////////////////



Sigma.$event= function(event){
		event = event || window.event;
		//event.fromElement=Sigma.U.getEventTarget(event);
		return event;
};


Sigma.EventCache  = (function (){
     var listEvents = [];
	 var nodeList =[];
	 var eventList ={};
	 function getKey(n){
		return ''+n+'_'+n.id;

	 }
      return {
        add : function (node, type, fn){
			if(!node) { return; }
			if (! Sigma.U.contains(listEvents,arguments)){
				listEvents.push(arguments);
			}
			var idx= Sigma.U.indexOf(nodeList,node);
			var key=idx+'_'+ node +'_'+ node.id;
			if(idx<0){
				key=nodeList.length+'_'+ node +'_'+ node.id;
				nodeList.push(node);
				eventList[key]={};
			}
			eventList[key][type]= eventList[key][type] || [];
			if ( ! Sigma.U.contains(eventList[key][type],fn)) {
				eventList[key][type].push(fn);
			}
        },
		remove : function (node, type, fn){
			if(!node) { return; }
           	var idx=Sigma.U.indexOf(nodeList,node);
			var key=idx+'_'+ node +'_'+ node.id;
			if(idx<0 || !eventList[key]) {return;}
			if (!type){
				eventList[key]=null;
				nodeList[idx]=null;
				return ;
			}
			if (!fn && eventList[key][type]){
				eventList[key][type]=null;
				delete eventList[key][type];
			}
			if (eventList[key][type]){
				eventList[key][type].remove(fn);
			}

        },

        clearUp : function (){

             var  i, item;
             for (i  =  listEvents.length - 1;i>=0;i=i-1){
                item  =  listEvents[i];
                Sigma.EventCache.remove(item[0]);
                if (item[0].removeEventListener){
                    item[0].removeEventListener(item[1], item[2], item[3]);
                }
                if (item[1].substring( 0 ,  2 )  !=   "on" ){
                    item[1]  =  "on"   +  item[1];
                }
                if (item[0].detachEvent){
                    item[0].detachEvent(item[1], item[2]);
                }
                
                item[0][item[1]]  =   null ;

				delete listEvents[i];
            }
			Sigma.destroyGrids && Sigma.destroyGrids();
			Sigma.destroyWidgets && Sigma.destroyWidgets();
			window.CollectGarbage && CollectGarbage();
        }
    };
})(); 



Sigma.toQueryString = function(source){
	if ( !source || Sigma.$type(source,'string','number') ) {
		return source;
	}
	var queryString = [];
	for (var property in source) {
		var value=source[property];
		if (value!==undefined) {
			value=[].concat(value);
		}
		for (var i=0;i<value.length;i++) {
			var val =value[i];
			if (Sigma.$type(val,'object')){
				val=Sigma.$json(val);
			}
			queryString.push(encodeURIComponent(property) + '=' + encodeURIComponent(val) );
		}
	}
	return queryString.join('&');
};

Sigma.toJSONString = function(source,format){
	return Sigma.JSON.encode(source,'__gt_',format);
};
Sigma.$json = Sigma.toJSONString;

////////////////////////

Sigma.FunctionCache={};


Sigma.$invoke = function(obj, funcName , argsList){
		obj=obj||window;
		var func= obj[funcName] || Sigma.$getFunction(funcName) ;
		if (typeof(func)=='function'){
			return func.apply(obj,argsList||[] );
		}
};


Sigma.$getFunction = function(funName){
	return Sigma.FunctionCache[funName];
};

Sigma.$callFunction = function(funName ,argsList ){
	Sigma.$invoke(null , funName ,argsList );
};


Sigma.$putFunction = function(funName ,func ){
	Sigma.FunctionCache[funName]=func;
};


Sigma.$removeFunction = function(funName){
	Sigma.FunctionCache[funName]=null;
	delete Sigma.FunctionCache[funName];
};

Sigma.U.onLoad(function(){
	Sigma.U.addEvent(window,"unload",Sigma.EventCache.clearUp);
});

//