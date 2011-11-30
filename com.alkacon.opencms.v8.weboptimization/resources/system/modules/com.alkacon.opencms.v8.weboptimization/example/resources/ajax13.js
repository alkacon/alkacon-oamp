/*
* Xslt Processor (Browser unabhÃ¬ngig)
* Funktioniert nicht im Konqueror
* Transformiert ein XML String mit Hilfe eines XSL Files
*/
function Xslt()
{
	var processor;
	//var cache;
	/*
	* LÃ¬dt das XSL file und iniziert den XSLTProcessor
	*/
	this.init = function(xslFile)
	{
		xslFile = xslFile + "?lang=" + lang + "&lp=" + lp + "&PHPSESSID=" + getCookie("PHPSESSID");
		
		if (typeof XSLTProcessor != 'undefined'  && !is_safari)
		{
			// Browser mit nativem XSLTProcessor Gecko, Opera
			var xslt = document.implementation.createDocument("http://www.w3.org/1999/XSL/Transform", "xsl", null);
			xslt.async = false;
			xslt.load(server + trainerPath +xslFile);
			
			this.processor = new XSLTProcessor();
			this.processor.importStylesheet(xslt);
		}
		else if (typeof xsltProcess != 'undefined')
		{
			// Browser die die Google XSLT-Lib verwendnen
			try
			{
				var xslt = document.implementation.createDocument("http://www.w3.org/1999/XSL/Transform", "xsl", null);
				xslt.async = false;
				xslt.load(server + trainerPath +xslFile);
				this.processor = xslt;
			}
			catch(e)
			{
				// Safari bis Version ??
				var xslt = new Ajax();
				xslt.asyncron = false;
				xslt.url = (server + trainerPath +xslFile);
				xslt.startPostRequest("");
				this.processor = xslt.xmlHttpReq.responseXML;
			}

			this.cache = {};
		}
		else if (typeof ActiveXObject != 'undefined')
		{
			// IE mit geeigneten ActiveXObjecten
			var xslDom = new ActiveXObject("MSXML2.FreeThreadedDOMDocument");
			xslDom.async = false;
			xslDom.load(server + trainerPath +xslFile);

			if (xslDom.parseError.errorCode != 0)
			{
				var strErrMsg = "Problem Parsing Style Sheet:\n" +
					" Error #: " + xslDom.parseError.errorCode + "\n" +
					" Description: " + xslDom.parseError.reason + "\n" +
					" In file: " + xslDom.parseError.url + "\n" +
					" Line #: " + xslDom.parseError.line + "\n" +
					" Character # in line: " + xslDom.parseError.linepos + "\n" +
					" Character # in file: " + xslDom.parseError.filepos + "\n" +
					" Source line: " + xslDom.parseError.srcText;
					alert(strErrMsg);
				return false;
			}
			var xslTemplate = new ActiveXObject("MSXML2.XSLTemplate");
			xslTemplate.stylesheet = xslDom;
			this.processor = xslTemplate.createProcessor();
		}
		else
		{
			/*
			* Es gibt keinen XsltProcessor
			* Nicht alle Funktionen in Leo stehen zur VerfÃRgung
			*/
			alert("The browser does not support XSLT");
		}
	}

	/*
	* Setzt einen Parameter fÃRr den XSLTProcessor
	*/
	this.setParameter = function(name, value)
	{
		if (typeof XSLTProcessor != 'undefined' && !is_safari)
		{
			this.processor.setParameter(null, name, value);
		}
		else if (typeof xsltProcess != 'undefined')
		{
			if (this.cache[name])
			{
				this.cache[name].setAttribute("select", "'" + value +"'");
			}
			else
			{
				var xPathExpr = "//xsl:param[@name='"+name+"']";
				var xPath = new XPath();
				xPath.evaluate(xPathExpr, this.processor);
				if (param = xPath.iterateNext())
				{
					this.cache[name] = param;
					param.setAttribute("select", "'" + value +"'");
				}
			}
		}
		else if (typeof ActiveXObject != 'undefined')
		{
			// IE mit geeigneten ActiveXObjecten
			this.processor.addParameter(name, value);
		}
		else
		{
			/*
			* Es gibt keinen XsltProcessor
			* Nicht alle Funktionen in Leo stehen zur VerfÃRgung
			*/
			alert("The browser does not support XSLT");
		}
	}

	this.transformToDocument = function(node)
	{
		if (node)
		{
			if (typeof XSLTProcessor != 'undefined' && !is_safari)
			{
				/**
				* Browser mit nativem XSLTProcessor
				* es wird ein XMLDocument zurÃRckgegeben
				*/
				return this.processor.transformToDocument(node);
			}
			else if (typeof xsltProcess != 'undefined')
			{
				/**
				* Browser die die google XSLT-Lib verwednen
				*/
				if (node.ownerDocument)
				{
					return xsltProcess(node, this.processor.documentElement);
				}
				else if (node.documentElement)
				{
					return xsltProcess(node.documentElement, this.processor.documentElement);
				}
			}
			else if (typeof ActiveXObject != 'undefined')
			{
				// Der IE scheint kein XMLDocument zurÃRckgegeben zu kÃ¶nnen,
				// wenn man die XSLTransformation mit Parameter aufrufen will ...
				this.processor.input = node;
				this.processor.transform();
				var xml = this.processor.output;
				try
				{
					var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
					xmlDoc.async = false;
					xmlDoc.loadXML(xml);
					if (xmlDoc.parseError.errorCode != 0)
					{
						//alert("Warning: String created instead of XmlNode or XmlDocument.");
						return xml;
					}
					return xmlDoc;
				}
				catch(e)
				{
					//alert("Warning: String created instead of XmlNode or XmlDocument.");
					return xml;
				}
			}
			else
			{
				alert("The browser does not support XSLT");
			}
		}
	}
}

/*
* XPath (Browser unabhÃ¬ngig)
* Funktioniert nicht im Konqueror
*/
function XPath()
{
	var xPathResult;
	var iterator = 0;

	this.evaluate = function(xPathExpr, xmlDoc, node)
	{
		//debugger;
		if (typeof document.evaluate != 'undefined' && !is_safari)
		{
			// Browser mit nativem XSLTProcessor
			if (node) {
				this.xPathResult = xmlDoc.evaluate(xPathExpr, node, null, XPathResult.ANY_TYPE, null);
			}
			else {
				this.xPathResult = xmlDoc.evaluate(xPathExpr, xmlDoc, null, XPathResult.ANY_TYPE, null);
			}
		}
		else if (typeof xsltProcess != 'undefined')
		{
			// Browser die die Google XSLT-Lib verwendnen
			this.iterator = 0;
			var tmp = xpathEval(xPathExpr, new ExprContext(xmlDoc));
			this.xPathResult = xpathEval(xPathExpr, new ExprContext(xmlDoc)).nodeSetValue();
		}
		else if (typeof ActiveXObject != 'undefined')
		{
			// IE mit geeigneten ActiveXObjecten
			this.iterator = 0;
			this.xPathResult = xmlDoc.selectNodes(xPathExpr);
		}
		else
		{
			alert("The browser does not support XPath");
		}
	}

	this.iterateNext = function()
	{
		if (typeof document.evaluate != 'undefined' &&  !is_safari)
		{
			// Browser mit nativem XSLTProcessor
			return this.xPathResult.iterateNext();
		}
		else if (typeof xsltProcess != 'undefined')
		{
			// Browser die die Google XSLT-Lib verwendnen
			if (this.xPathResult.constructor == Array && this.xPathResult.length > 0)
			{
				return this.xPathResult.shift();
			}
			if (typeof this.xPathResult == "object" && this.xPathResult.length > this.iterator)
			{
				this.iterator++;
				return this.xPathResult[this.iterator-1];
			}
		}
		else if (typeof ActiveXObject != 'undefined')
		{
			// IE mit geeigneten ActiveXObjecten
			if (this.xPathResult.constructor == Array && this.xPathResult.length > 0)
			{
				return this.xPathResult.pop();
			}
			if (typeof this.xPathResult == "object" && this.xPathResult.length > this.iterator)
			{
				this.iterator++;
				return this.xPathResult[this.iterator-1];
			}
		}
		else
		{
			alert("The browser does not support XPath");
		}
		return null;
	}
}


/*
* Ajaxrequest Klasse
* Abstracte Klasse von der alle anderen Ajax-Requests abgeleitet sind
*/
function Ajax()
{
	this.asyncron = true;
	this.xmlHttpReq = false;
	/*@cc_on @*/
	/*@if (@_jscript_version >= 5)
	try
	{
		this.xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
	}
	catch (e)
	{
		try
		{
			this.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
		}
		catch (e2)
		{
			this.xmlHttpReq = false;
		}
	}
	@end @*/

	if (!this.xmlHttpReq && typeof XMLHttpRequest != 'undefined')
	{
		this.xmlHttpReq = new XMLHttpRequest();
	}


	/*
	* Ajax-Get-Request wird durchgefÃRhrt
	*/
	this.startGetRequest =  function(querystring)
	{
		if (this.xmlHttpReq)
		{
			this.showLoader();
			this.xmlHttpReq.open("GET", this.url + "&" + querystring , true);
			this.xmlHttpReq.onreadystatechange = this.update;
			this.xmlHttpReq.send(null);
		}
		else
		{
			/*
			* Browser unterstÃRtzt keine AJAX-Requests,
			* Pop-Up  wird stadt dessen geÃ¶ffnet bzw. neue Seite angezeigt
			*/
			document.open(this.url +  "&" + querystring + "&lang=" + document.dict.lang.value + "&lp=" + document.dict.lp.value + "&ajax=false", "PopUpInfo", "width=500,height=400,left=0,top=0, menubar=yes, resizable=yes, scrollbars=yes, location=yes, toolbar=yes, status=yes");
		}
	}
	
	this.showLoader = function() {
		if (this.resultObject && !is_ie)
		{
			this.resultObject.innerHTML = "<table class='loading'><tr><td><img src='/trainer/img/ajax_loader_32x32.gif' alt='loading ...'/></td></tr></table>";
		}
	}

	/*
	* Ajax-Post-Request wird durchgefÃRhrt
	*/
	this.startPostRequest =  function(querystring)
	{
		if (this.xmlHttpReq)
		{
			this.showLoader();
			this.xmlHttpReq.open("POST", this.url, this.asyncron);
			this.xmlHttpReq.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			if (this.asyncron)
			{
				this.xmlHttpReq.onreadystatechange = this.update;
				// Der querystring wird encodeURI() erwartet und ist in UTF8
				this.xmlHttpReq.send(querystring);
			}
			else
			{
				this.xmlHttpReq.send(querystring);
				this.update();
			}
		}
		else
		{
			/*
			* Browser unterstÃRtzt keine AJAX-Requests,
			* Pop-Up  wird stadt dessen geÃ¶ffnet bzw. neue Seite angezeigt
			*/
			document.open(this.url +  "&" + querystring + "&lang=" + document.dict.lang.value + "&lp=" + document.dict.lp.value + "&ajax=false", "PopUpInfo", "width=500,height=400,left=0,top=0, menubar=yes, resizable=yes, scrollbars=yes, location=yes, toolbar=yes, status=yes");
		}
	}

	this.update = function(){};
}

/*
* Vocabeln werden geladen
*/
function AjaxGetSelectedVocables()
{
	this.url = "?ajax=info";
	this.xslt = new Xslt();
	this.xslt.init("Xsl/AJAX/displayVocables.php");

	/*
	* Ergebniss des Ajax-Requests wird behandelt
	* Statische Funktion
	*/
	this.update = function()
	{
		ajaxGetSelectedVocables.resultObject = document.getElementById('contentDisplayVocvables');
		if(ajaxGetSelectedVocables.xmlHttpReq.readyState == 4)
		{
			//alert(ajaxGetSelectedVocables.xmlHttpReq.responseText);
			var xmlDoc = ajaxGetSelectedVocables.xmlHttpReq.responseXML;
			// wird das auch von Irgendwo aufgerufen wo man beide Seiten sehen will? ja statistik
			ajaxGetSelectedVocables.xslt.setParameter("mode", "preview");
			ajaxGetSelectedVocables.xslt.setParameter("side", "2");
			if (document.getElementsByName("value[editcycleforlearning][0][cycle][0][direction][0]").length > 0)
			{
				if (!(document.getElementsByName("value[editcycleforlearning][0][cycle][0][direction][0]")[0].checked))
				{
					ajaxGetSelectedVocables.xslt.setParameter("side", "1");
				}
			}
			if (document.getElementsByName("value[editcycleforquery][0][cycle][0][direction][0]").length > 0)
			{
				if (!(document.getElementsByName("value[editcycleforquery][0][cycle][0][direction][0]")[0].checked))
				{
					ajaxGetSelectedVocables.xslt.setParameter("side", "1");
				}
			}
			result = ajaxGetSelectedVocables.xslt.transformToDocument(xmlDoc.documentElement);
			ajaxGetSelectedVocables.xslt.setParameter("side", "2");
			ajaxGetSelectedVocables.xslt.setParameter("mode", "");
			
			ajaxGetSelectedVocables.resultObject.innerHTML = "";
			if (xmlDoc.documentElement.firstChild.childNodes.length == 0) {
				document.getElementById("contentContainerHint").style.display = "block";
				
			}
			else {
				document.getElementById("contentContainerHint").style.display = "none";
				appendXSLTResult(ajaxGetSelectedVocables.resultObject, result);
			}
			//ajaxGetSelectedVocables.resultObject.appendXSLTResult(result);
			//.appendXSLTResult();
		}
	}

	/*
	* Ajaxrequest wird gestartet
	*/
	this.request = function(event, formName)
	{
		this.startPostRequest("ajax=info&"+postBackString(event, formName));
	}

	/*
	* Ajaxrequest wird gestartet
	*/
	this.loadLearningLection = function(id)
	{
		var str = "ajax=info&postBack=managecontent&value[savecontent][0][idLearningLection]="+id;
		this.startPostRequest(str);
	}
}
AjaxGetSelectedVocables.prototype = new Ajax();

/*
* Schreibt die Abfrageergebnisse zum Server zurÃRck
*/
function AjaxSendTrainerResults()
{
	this.resultObject = null;

	/*
	* Ergebniss des Ajax-Requests wird behandelt
	* Statische Funktion
	*/
	this.update = function()
	{
		if(ajaxSendTrainerResults.xmlHttpReq.readyState == 4)
		{
			// Es erfolgt kein visuelles Feedback
		}
	}

	/*
	* Ajaxrequest wird gestartet
	*/
	this.request = function(post)
	{
		this.startPostRequest(post);
	}
}
AjaxSendTrainerResults.prototype = new Ajax();
var ajaxSendTrainerResults = new AjaxSendTrainerResults();

/*
* I-Link wird angezeigt
*/
function AjaxGetMoreInfo()
{
	this.url = server + forumPath + "ajax.php?ajax=info";

	/*
	* Ergebniss des Ajax-Requests wird behandelt
	* Statische Funktion
	*/
	this.update = function()
	{
		if(ajaxGetMoreInfo.xmlHttpReq.readyState == 4)
		{
			ajaxGetMoreInfo.resultObject.innerHTML = ajaxGetMoreInfo.xmlHttpReq.responseText;
			
			this.xPath = new XPath();
			this.xPath.evaluate("//td[@id='pron']/a", ajaxGetMoreInfo.xmlHttpReq.responseXML);
			if ((obj = this.xPath.iterateNext()) && document.getElementById("player"))
			{
				document.getElementById("player").style.display = "block";
				var s3 = new SWFObject('/trainer/img/mp3player.swf', 'line', '130', '20', '7');
				s3.addVariable('file', obj.getAttribute("href"));
				s3.addVariable('repeat','false');
				s3.addVariable('showdigits','false');
				s3.addVariable('showdownload','false');
				s3.write('player');
				
			}
			this.xPath.evaluate("//td[@id='linguatec']", ajaxGetMoreInfo.xmlHttpReq.responseXML);
			if ((obj = this.xPath.iterateNext())) {
				this.player = new LinguatecPlayer("linguatec", obj.getAttribute("lang"));
				this.player.getTextFromNode(obj);
				this.player.speed = 75;
				this.player.write();
			}
		}
	}

	/*
	* Ajaxrequest wird gestartet
	*/
	this.request = function(event, offset)
	{
		var idWord = null;
		if (offset.search(/_b/) != -1) {
			document.getElementById('divForumInfo').className = "divForumInfo";
			this.activateForumInfo(event);
		}
		else
		{
			if (event.target && event.target.previousSibling)
			{
				idWord = event.target.previousSibling.value;
			}
			this.resultObject = document.getElementById('divMoreInfo');
			this.resultObject.style.display = "block";
			this.resultObject.style.visibility = "visible";
			if (offset.search(/_en/) != -1)
			{
				// Contextmenue wird links plaziert
				positionLeft(event, this.resultObject);
			}
			if (offset.search(/_de/) != -1)
			{
				// Contextmenue wird rechts plaziert
				positionRight(event, this.resultObject);
			}
		}
		this.startGetRequest("lp="+document.dict.lp.value+"&lang="+document.dict.lang.value+"&offset="+offset+"&idWord="+idWord);
	}
	
	/**
	* Das divForumInfo wird aktiviert und als resultObject gesetzt 
	*/
	this.activateForumInfo = function(event) {
		this.resultObject = document.getElementById('divForumInfo');
		positionRight(event, this.resultObject);
		this.resultObject.style.left = "150px";
		try {
			this.resultObject.style.top = (parseInt(this.resultObject.style.top.replace(/\s*px/,"")) - 100) +"px";
		}
		catch(e) {
		}
		
		this.resultObject.style.visibility = 'visible';
		this.resultObject.style.display = 'block';
		this.resultObject = document.getElementById('divForumInfoContent');
	}
	
	/*
	* Zeigt die Chinesischen Animierten Zeichereihenfolge an
	*/
	this.showAnimatedStroke = function(event, filename) {
		document.getElementById('divForumInfo').className = "popup";
		this.activateForumInfo(event);
		this.resultObject.innerHTML = "<div>"
				+ "<h1 onmousedown=\"makeDraggable(document.getElementById('divForumInfo'));\" class=\"drag\">" 
				+ "&#160;<img src=\"/trainer/img/fileclose.png\" alt=\"Close\" onclick=\"document.getElementById('divForumInfo').style.display='none';\"/>"
				+ "</h1>"
				+ "<div style='padding:3px;'>"
				+ "Radikale sind blau dargestellt, zusÃ¬tzliche Striche rot. Wird mehr "
				+ "als eine Pinyin-Silbe angezeigt, so besitzt das Zeichen die durch "
				+ "die Silben angezeigten Aussprachealternativen."
				+ "</div>"
				+ "<div style='padding:3px;'>"
				+ "çýaèýýèý²ä¹`åýýçýýïRýè¡aç¬ºè¯¥æ±ýå­ýçýýéýaé`ýãýýçýaçº¢èý²ä¹`åýýçýýïRýè¡aç¬ºè¯¥æ±ýå­ýçýýåý©äSýç¬ýçý»ãýý"
				+ "å`ýæýýè¯¥æ±ýå­ýä~ºå¬ýéý³å­ýïRýå°ýäRýæýxç¬ºè¯¥å­ýæýýæýýè¯»éý³çýýæýRéý³éý³èýýãýý"
				+ "</div>"
				+ "<iframe src='"+filename+"' style='border: 0px solid black; width: 778px; height:345px;'></iframe>"
				+ "Die Darstellungen basiert auf der <i>eStroke Online</i>-Software "
				+ " <a href='http://www.eon.com.hk/'>EON Media Limited</a>."
				+ "<br/>"
				+ "æ±ýå­ýç¬ýé¡ºè§ýèýýæRýç¬ºæý¯åýa"
				+ "<a href='http://www.eon.com.hk/'>EON Media Limited</a>"
				+ "åý¬åý~ç ýåýýçýýèS¯ä»¶<i>eStroke Online</i>-Softwareåýºç¡ýä~ýåý¶äSýçýýãýý"
				+ "<br/>"
				+ "<br/>"
				+ "</div>";
	}
	
	/*
	* Zeigt die Chinesischen Animierten Zeichereihenfolge an
	*/
	this.showExtendedStroke = function(event, filename) {
		document.getElementById('divForumInfo').className = "popup";
		this.activateForumInfo(event);
		this.resultObject.innerHTML = "<div>"
				+ "<h1 onmousedown=\"makeDraggable(document.getElementById('divForumInfo'));\" class=\"drag\">" 
				+ "&#160;<img src=\"/trainer/img/fileclose.png\" alt=\"Close\" onclick=\"document.getElementById('divForumInfo').style.display='none';\"/>"
				+ "</h1>"
				+ "<div style='padding:3px;'>"
				+ "Radikale sind blau dargestellt, zusÃ¬tzliche Striche rot. Wird mehr "
				+ "als eine Pinyin-Silbe angezeigt, so besitzt das Zeichen die durch "
				+ "die Silben angezeigten Aussprachealternativen."
				+ "</div>"
				+ "<div style='padding:3px;'>"
				+ "çýaèýýèý²ä¹`åýýçýýïRýè¡aç¬ºè¯¥æ±ýå­ýçýýéýaé`ýãýýçýaçº¢èý²ä¹`åýýçýýïRýè¡aç¬ºè¯¥æ±ýå­ýçýýåý©äSýç¬ýçý»ãýý"
				+ "å`ýæýýè¯¥æ±ýå­ýä~ºå¬ýéý³å­ýïRýå°ýäRýæýxç¬ºè¯¥å­ýæýýæýýè¯»éý³çýýæýRéý³éý³èýýãýý"
				+ "</div>"
				+ "<iframe src='"+filename+"' style='border: 0px solid black; width: 778px; height:345px;'></iframe>"
				+ "Die Darstellungen basiert auf der <i>eStroke Online</i>-Software "
				+ " <a href='http://www.eon.com.hk/'>EON Media Limited</a>."
				+ "<br/>"
				+ "æ±ýå­ýç¬ýé¡ºè§ýèýýæRýç¬ºæý¯åýa"
				+ "<a href='http://www.eon.com.hk/'>EON Media Limited</a>"
				+ "åý¬åý~ç ýåýýçýýèS¯ä»¶<i>eStroke Online</i>-Softwareåýºç¡ýä~ýåý¶äSýçýýãýý"
				+ "<br/>"
				+ "<br/>"
				+ "</div>";
	}
}
AjaxGetMoreInfo.prototype = new Ajax();
var ajaxGetMoreInfo = new AjaxGetMoreInfo();


/*
* Form soll ausgetauscht werden
*/


function AjaxGetForm()
{
	this.resultObject = null;
	this.url = "?ajax=form";

	/*
	* Ergebniss des Ajax-Requests wird behandelt
	* Statische Funktion
	*/
	this.update = function()
	{
		if(ajaxGetForm.xmlHttpReq.readyState == 4)
		{
			var xml = ajaxGetForm.xmlHttpReq.responseXML;

			xml = xml.documentElement;

			var targetNode = null;
			for(i=0;i<xml.childNodes.length;i++)
			{
				if(xml.childNodes[i].nodeName=="url")
				{
					ajaxGetForm.url = xml.childNodes[i].firstChild.nodeValue+"?ajax=form";
				}
				if(xml.childNodes[i].nodeName=="remove")
				{
					node = document.getElementById(xml.childNodes[i].firstChild.nodeValue);
					targetNode = node.parentNode;
					if(node && node.parentNode && node.parentNode.removeChild)
					{
						node.parentNode.removeChild(node);
					}
				}
				if(xml.childNodes[i].nodeName=="append")
				{
					//debugger;
					if (targetNode)
					{
						node = targetNode;
					}
					else
					{
						node = document.getElementById(xml.childNodes[i].firstChild.firstChild.nodeValue);
					}
					//alert(xml.childNodes[i].firstChild.nextSibling.firstChild.nodeValue);
					node.innerHTML += xml.childNodes[i].firstChild.nextSibling.firstChild.nodeValue;
				}
				if(xml.childNodes[i].nodeName=="eval")
				{
					eval(xml.childNodes[i].firstChild.firstChild.nodeValue);
				}
				if (xml.childNodes[i].nodeName=="error")
				{
					alert(xml.childNodes[i].firstChild.nodeValue);
				}
			}
		}
	}

	/*
	* Ajaxrequest wird gestartet
	*/
	this.request = function(event, formName)
	{
		this.startPostRequest(postBackString(event, formName));
	}
}
AjaxGetForm.prototype = new Ajax();
var ajaxGetForm = new AjaxGetForm();


/*
* Form soll ausgetauscht werden
*/
function AjaxGetPopUp()
{
	/*
	* Ergebniss des Ajax-Requests wird behandelt
	* Statische Funktion
	*/
	this.update = function()
	{
		if(ajaxGetPopUp.xmlHttpReq.readyState == 4)
		{
			var xml = ajaxGetPopUp.xmlHttpReq.responseXML;
			xml = xml.documentElement;

			for(var i=0;i<xml.childNodes.length;i++)
			{
				if(xml.childNodes[i].nodeName=="remove")
				{
					var node = document.getElementById(xml.childNodes[i].firstChild.nodeValue);
					if (node)
					{
						targetNode = node.parentNode;
						if(node && node.parentNode && node.parentNode.removeChild)
						{
							node.parentNode.removeChild(node);
						}
						document.getElementById('popup').style.display = "none";
					}
				}
				if(xml.childNodes[i].nodeName=="url")
				{
					ajaxGetPopUp.url = xml.childNodes[i].firstChild.nodeValue+"?ajax=form";
				}
				if(xml.childNodes[i].nodeName=="append")
				{
					var node = document.getElementById(xml.childNodes[i].firstChild.firstChild.nodeValue);
					/*while (node.firstChild) {
						node.removeChild(node.firstChild);
					}*/
					node.innerHTML = "";
					// Der neue Childnode wird geladen
					var childNode = importNode(loadXMLString(xml.childNodes[i].firstChild.nextSibling.firstChild.nodeValue).documentElement, document, true);
					//node.appendChild(childNode);
					node.innerHTML = xml.childNodes[i].firstChild.nextSibling.firstChild.nodeValue;
					document.getElementById('popup').style.display = "block";
				}
				if(xml.childNodes[i].nodeName=="replace")
				{
					node = document.getElementById(xml.childNodes[i].firstChild.firstChild.nodeValue);
					var newNode = document.createElement("div");
					newNode.innerHTML = xml.childNodes[i].firstChild.nextSibling.firstChild.nodeValue;
					node.parentNode.replaceChild(newNode, node);
				}
				if(xml.childNodes[i].nodeName=="eval")
				{
					eval(xml.childNodes[i].firstChild.firstChild.nodeValue);
				}
				if (xml.childNodes[i].nodeName=="error")
				{
					alert(xml.childNodes[i].firstChild.nodeValue);
				}
			}
		}
	}
}
AjaxGetPopUp.prototype = new AjaxGetForm();
var ajaxGetPopUp = new AjaxGetPopUp();

/**
* ÃýberprÃRft auf gÃRltiges Login/Nick
*/
function AjaxRegister() {
	this.url = "?ajax=info";
	/*
	* Ergebniss des Ajax-Requests wird behandelt
	* Statische Funktion
	*/
	this.update = function() {
		if(ajaxRegister.xmlHttpReq.readyState == 4) {
			var xml = ajaxRegister.xmlHttpReq.responseXML;
			xml = xml.documentElement;

			for(var i=0;i<xml.childNodes.length;i++) {
				if(xml.childNodes[i].nodeName=="loginack") {
					//document.getElementById("loginHelperContent").innerHTML = "This is a valid Login";
					ajaxRegister.login.style.borderColor = "green";
					document.getElementById("loginHelper").style.display = "none";
				}
				if(xml.childNodes[i].nodeName=="loginnoack") {
					ajaxRegister.login.style.borderColor = "red";
					document.getElementById("loginHelperContent").innerHTML = ajaxRegister.xmlHttpReq.responseText;
					document.getElementById("loginHelper").style.display = "block";
				}
				if(xml.childNodes[i].nodeName=="nickack") {
					ajaxRegister.nick.style.borderColor = "green";
					document.getElementById("nickHelper").style.display = "none";
				}
				if(xml.childNodes[i].nodeName=="nicknoack") {
					ajaxRegister.nick.style.borderColor = "red";
					document.getElementById("nickHelperContent").innerHTML = ajaxRegister.xmlHttpReq.responseText;
					document.getElementById("nickHelper").style.display = "block";
				}
			}
		}
	}
}
AjaxRegister.prototype = new Ajax();
var ajaxRegister;

AjaxRegister.prototype.checkLogin = function(node) {
	this.login = node;
	this.startPostRequest("postBack="+node.form.name+"&value[action]=login&value[object]=login&value[value]="+encodeURIComponent(node.value));
}

AjaxRegister.prototype.setLogin = function(node) {
	this.login.value = node.textContent;
	this.login.style.borderColor = "green";
	document.getElementById("loginHelper").style.display = "none";
}

AjaxRegister.prototype.checkNick = function(node) {
	this.nick = node;
	this.startPostRequest("postBack="+node.form.name+"&value[action]=login&value[object]=nick&value[value]="+encodeURIComponent(node.value));
}

AjaxRegister.prototype.setNick = function(node) {
	this.nick.value = node.textContent;
	this.nick.style.borderColor = "green";
	document.getElementById("nickHelper").style.display = "none";
}


/**
* Anklicken und ÃRbersetzen.
*/
function Translate() {
	this.url = server;
	this.xPath = new XPath();
	
	this.resultObject = document.getElementById('contentholder');
	
	document.getElementById("iframe").src="/forum/translator.php?" + Math.random();
	this.field = document.getElementById("iframe").contentDocument;
	this.win = document.getElementById("iframe").contentWindow;
}
Translate.prototype = new Ajax();

/**
* Ergebniss des Ajax-Requests wird behandelt
* Statische Funktion
*/
Translate.prototype.update = function() {
	if(translate.xmlHttpReq.readyState == 4)
	{
		if (translate.resultObject.innerHTML == "<notloggedin>Not logged in.</notloggedin>") {
			var relogin = new AjaxGetForm();
			relogin.url = "/forum/relogin.php?ajax=form";
			relogin.startPostRequest();
		}
		else {
			translate.resultObject.innerHTML = translate.xmlHttpReq.responseText.replace(/<div id="multiword">.*/,"").replace(/<td id="contentholder">/,"");
			translate.insertLinks();
		}
	}
}

Translate.prototype.insertLinks = function() {
	var nodes = document.getElementById("results").getElementsByTagName("tr");
	for (i = 0; i < nodes.length; i++){
		if (nodes[i].vAlign == 'top') {
			if (eval("document.inserttext.direction[1].checked")) {
				var langA = nodes[i].getElementsByTagName("td")[1].textContent;
				nodes[i].getElementsByTagName("td")[0].innerHTML = "<img src='/trainer/img/pfeil_l.gif' onclick='translate.replace(\""+langA+"\");'/>" + nodes[i].getElementsByTagName("td")[0].innerHTML;
			}
			else {
				var langB = nodes[i].getElementsByTagName("td")[3].textContent;
				nodes[i].getElementsByTagName("td")[4].innerHTML += "<img src='/trainer/img/pfeil_r.gif' onclick='translate.replace(\""+langB+"\");'/>";
			}
		}
	}
}

Translate.prototype.replace = function(insText) {
	var insertNode = this.field.createElement("span");
	insertNode.lang = "de";
	insertNode.appendChild(this.field.createTextNode(insText));
	// get current selection
	var sel = this.win.getSelection();
	
	// get the first range of the selection
	// (there's almost always only one range)
	var range = sel.getRangeAt(0);
	
	// deselect everything
	sel.removeAllRanges();
	
	// remove content of current selection from document
	range.deleteContents();
	
	// get location of current selection
	var container = range.startContainer;
	var pos = range.startOffset;
	
	// make a new range for the new selection
	range=document.createRange();
	
	if (container.nodeType==3 && insertNode.nodeType==3) {
	
	// if we insert text in a textnode, do optimized insertion
	container.insertData(pos, insertNode.nodeValue);
	
	// put cursor after inserted text
	range.setEnd(container, pos+insertNode.length);
	range.setStart(container, pos+insertNode.length);
	
	} else {
	
	
	var afterNode;
	if (container.nodeType==3) {
	
		// when inserting into a textnode
		// we create 2 new textnodes
		// and put the insertNode in between
	
		var textNode = container;
		container = textNode.parentNode;
		var text = textNode.nodeValue;
	
		// text before the split
		var textBefore = text.substr(0,pos);
		// text after the split
		var textAfter = text.substr(pos);
	
		var beforeNode = document.createTextNode(textBefore);
		afterNode = document.createTextNode(textAfter);
	
		// insert the 3 new nodes before the old one
		container.insertBefore(afterNode, textNode);
		container.insertBefore(insertNode, afterNode);
		container.insertBefore(beforeNode, insertNode);
	
		// remove the old node
		container.removeChild(textNode);
	
	} else {
	
		// else simply insert the node
		afterNode = container.childNodes[pos];
		container.insertBefore(insertNode, afterNode);
	}
	
	range.setEnd(afterNode, 0);
	range.setStart(afterNode, 0);
	}
	
	sel.addRange(range);

};


/**
* Anfrage ans WÃ¶rterbuch wird gestellt
*/
Translate.prototype.queryDict = function(word) {
	
	this.resultObject = document.getElementById('queryResult');
	var where = -1;
	this.field.body.lang = "en";
	if (eval("document.inserttext.direction[1].checked")) {
		where = 1;
		this.field.body.lang = "de";
	}
	
	var str = "onlyLoc=center&search=" + encodeURIComponent(word) + "&searchLoc=" + where + "&max=30";
	this.startPostRequest(str);
	document.inserttext.search.value = word;
}

/**
* Ergebniss wird angezeigt
*/
Translate.prototype.displaySearchResults = function(xml) {
	if (xml && xml.documentElement) {
		this.resultXml = xml;
		
		this.xslt.setParameter("mode", "bigresult");
		var result = this.xslt.transformToDocument(xml.documentElement);
		this.xslt.setParameter("mode", "");

		document.getElementById('queryResult').innerHTML = "";
		appendXSLTResult(document.getElementById('queryResult'), result.documentElement);
	}
	else {
		document.getElementById('queryResult').innerHTML = "";
		alert("Fehler bei der Anfrage");
	}
}

/**
* Multiwordsuche wird (de)aktiviert
*/
Translate.prototype.display = function() {
	if (document.getElementById("singleword").style.display.toLowerCase() == "block" || document.getElementById("singleword").style.display.toLowerCase() == "") {
		document.dict.style.display = "none";
		document.dict.parentNode.style.width = "100%";
		document.dict.parentNode.style.marginRight = "5px";
		document.inserttext.style.display = "block";
		document.getElementById("singleword").style.display = "none";
		document.getElementById("multiword").style.display = "block";
	}
	else {
		document.inserttext.style.display = "none";
		document.dict.parentNode.style.width = "250px";
		document.dict.parentNode.style.marginRight = "0px";
		document.dict.style.display = "block";
		document.getElementById("singleword").style.display = "block";
		document.getElementById("multiword").style.display = "none";
	}
}

Translate.prototype.showLoader = function() {
	if (this.resultObject && !is_ie)
	{
		this.resultObject.innerHTML = "<div style='width: 100%;height: 300px;'><table class='loading' style='width: 100%;height: 300px;'><tr><td>&#160;<img src='/trainer/img/ajax_loader_32x32.gif' alt='loading ...'/>&#160;</td></tr></table></div>";
	}
}


/**
* Ein dummer Browser kann keine internen Objecte Prototypen ...
*/
function appendXSLTResult(obj, result)
{
	if (typeof result == 'object') {
		try
		{
			if (is_opera) {
				var serializer = new XMLSerializer();
				var html = serializer.serializeToString(result);
				obj.innerHTML += html;
			}
			else {
				// node wird importiert falls nÃ¶tig
				if (result.ownerDocument == obj.ownerDocument)
				{
					node = result.documentElement;
				}
				else
				{
					node = importNode(result.documentElement, obj.ownerDocument, true);
				}
				obj.appendChild(node);
			}
		}
		catch(e)
		{
			// Hack fÃRr den IE ...
			if (result.xml)
			{
				obj.innerHTML += result.xml;
			}
			else if(result.innerHTML)
			{
				obj.innerHTML += result.innerHTML;
			}
		}
	}
	else if (typeof result == 'string')
	{
		obj.innerHTML += result;
	}
	else
	{
		alert("Unknown result type:" + typeof result);
	}
}
/**
* Importiert einen Node (und seine childNodes) in das ÃRbergebene Document
*/
function importNode(node, xmlDoc, childNodes)
{
	try
	{
		return xmlDoc.importNode(node, childNodes);
	}
	catch(e)
	{
		return node;
	}
}

/**
* 
*/
function loadXMLString(str) 
{
	try
	{
		 //Firefox, Mozilla, Opera, etc.
		var parser=new DOMParser();
		xmlDoc=parser.parseFromString(htmlEntityDecode(str.trim()),"text/xml");
		return(xmlDoc);
	}
	catch(e) 
	{
		try 
		{
			//Internet Explorer
			var xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async="false";
			xmlDoc.loadXML(htmlEntityDecode(str.trim()));
			return(xmlDoc); 
		}
		catch(e)
		{
		}
	}
	
	return(null);
}

function htmlEntityDecode(str) 
{
	var ta=document.createElement("textarea");
	ta.innerHTML=str.replace(/</g,"&lt;").replace(/>/g,"&gt;");
	return ta.value;
}
/*function appendXSLTResult(result)
{
	if (typeof result == 'object')
	{
		try
		{
			this.appendChild(result.documentElement);
		}
		catch(e)
		{
			// Hack fÃRr den IE ...
			this.innerHTML = result.xml;
		}
	}
	else if (typeof result == 'string')
	{
		this.innerHTML = result;
	}
	else
	{
		alert("Unknown result type");
	}
}

if (Node && Node.prototype)
{
	Node.prototype.appendXSLTResult = appendXSLTResult;
}
else if (Objecte && Objecte.prototype)
{
	Objecte.prototype.appendXSLTResult = appendXSLTResult;
}
else
{
	// Dummer IE
}*/
