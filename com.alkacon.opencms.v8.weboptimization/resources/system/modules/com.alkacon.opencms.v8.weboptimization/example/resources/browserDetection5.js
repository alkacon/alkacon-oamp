/**
* Setzen eines Cookies
*
* @var string name Name des Cookies
* @var string value Wert des Cookies
* @var int expires Ablaufsdatum in Tagen von jetzt an gerechnet
* @var string path Pfad f√Rr das das Cookie g√Rltig ist
* @var string domain Domain f√Rr die das Cookie gilt
* @var boolean secure Zeigt an ob das Cookie nur √Rber https gesendet werden soll
*/
function setCookie( name, value, expires, path, domain, secure )
{
	// set time, it's in milliseconds
	var today = new Date();
	today.setTime( today.getTime() );

	// expires in days
	if ( expires )
	{
		expires = expires * 1000 * 60 * 60 * 24;
	}
	var expiresDate = new Date( today.getTime() + (expires) );
	
	document.cookie = name + "=" +escape( value ) +
	( ( expires ) ? ";expires=" + expiresDate.toGMTString() : "" ) + 
	( ( path ) ? ";path=" + path : "" ) + 
	( ( domain ) ? ";domain=" + domain : "" ) +
	( ( secure ) ? ";secure" : "" );
}

/**
* Liest ein Cookie aus.
*
* @var string name Name des zu lesenden Cookies
* @return string Wert des Cookies
*/
function getCookie(name)
{
	var start = document.cookie.indexOf( name + "=" );
	var len = start + name.length + 1;
	if ( ( !start ) && ( name != document.cookie.substring( 0, name.length ) ) ) 
	{
		return null;
	}
	if ( start == -1 ) return null;
	var end = document.cookie.indexOf( ';', len );
	if ( end == -1 ) end = document.cookie.length;
	return unescape( document.cookie.substring( len, end ) );
}

//<!--
// Ultimate client-side JavaScript client sniff. Version 3.04
// (C) Netscape Communications 1999-2001.  Permission granted to reuse and distribute.
// Revised 17 May 99 to add is_nav5up and is_ie5up (see below).
// Revised 20 Dec 00 to add is_gecko and change is_nav5up to is_nav6up
//                      also added support for IE5.5 Opera4&5 HotJava3 AOLTV
// Revised 22 Feb 01 to correct Javascript Detection for IE 5.x, Opera 4,
//                      correct Opera 5 detection
//                      add support for winME and win2k
//                      synch with browser-type-oo.js
// Revised 26 Mar 01 to correct Opera detection
// Revised 02 Oct 01 to add IE6 detection
// Revised 06 Jan 04 to add Opera6 detection
// Revised xx xxx 05 to add Opera7 detection
// Revised xx xxx 05 to add Konquerer5 detection
// Revised 02 Sept 06 to add JavaScript Version det

	// convert all characters to lowercase to simplify testing
	var agt=navigator.userAgent.toLowerCase();

	// *** BROWSER VERSION ***
	// Note: On IE5, these return 4, so use is_ie5up to detect IE5.
	var is_major = parseInt(navigator.appVersion);
	var is_minor = parseFloat(navigator.appVersion);

	// Note: Opera and WebTV spoof Navigator.  We do strict client detection.
	// If you want to allow spoofing, take out the tests for opera and webtv.
	var is_nav  = ((agt.indexOf('mozilla')!=-1) && (agt.indexOf('spoofer')==-1) && (agt.indexOf('compatible') == -1) && (agt.indexOf('opera')==-1) && (agt.indexOf('webtv')==-1) && (agt.indexOf('hotjava')==-1));
	var is_nav2 = (is_nav && (is_major == 2));
	var is_nav3 = (is_nav && (is_major == 3));
	var is_nav4 = (is_nav && (is_major == 4));
	var is_nav4up = (is_nav && (is_major >= 4));
	var is_navonly = (is_nav && ((agt.indexOf(";nav") != -1) || (agt.indexOf("; nav") != -1)) );
	var is_nav6 = (is_nav && (is_major == 5));
	var is_nav6up = (is_nav && (is_major >= 5));
	var is_gecko = (agt.indexOf('gecko') != -1);

	// IE
	var is_ie     = ((agt.indexOf("msie") != -1) && (agt.indexOf("opera") == -1));
	var is_ie3    = (is_ie && (is_major < 4));
	var is_ie4    = (is_ie && (is_major == 4) && (agt.indexOf("msie 4")!=-1) );
	var is_ie4up  = (is_ie && (is_major >= 4));
	var is_ie5    = (is_ie && (is_major == 4) && (agt.indexOf("msie 5.0") !=-1));
	var is_ie5_5  = (is_ie && (is_major == 4) && (agt.indexOf("msie 5.5") !=-1));
	var is_ie5up  = (is_ie && !is_ie3 && !is_ie4);
	var is_ie5_5up =(is_ie && !is_ie3 && !is_ie4 && !is_ie5);
	var is_ie6    = (is_ie && (is_major == 4) && (agt.indexOf("msie 6.")!=-1) );
	var is_ie6up  = (is_ie && !is_ie3 && !is_ie4 && !is_ie5 && !is_ie5_5);

	// KNOWN BUG: On AOL4, returns false if IE3 is embedded browser
	// or if this is the first browser window opened.  Thus the
	// variables is_aol, is_aol3, and is_aol4 aren't 100% reliable.
	var is_aol   = (agt.indexOf("aol") != -1);
	var is_aol3  = (is_aol && is_ie3);
	var is_aol4  = (is_aol && is_ie4);
	var is_aol5  = (agt.indexOf("aol 5") != -1);
	var is_aol6  = (agt.indexOf("aol 6") != -1);

	// Opera
	var is_opera = (agt.indexOf("opera") != -1);
	var is_opera2 = (agt.indexOf("opera 2") != -1 || agt.indexOf("opera/2") != -1);
	var is_opera3 = (agt.indexOf("opera 3") != -1 || agt.indexOf("opera/3") != -1);
	var is_opera4 = (agt.indexOf("opera 4") != -1 || agt.indexOf("opera/4") != -1);
	var is_opera5 = (agt.indexOf("opera 5") != -1 || agt.indexOf("opera/5") != -1);
	var is_opera5up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4);
	var is_opera6 = (agt.indexOf("opera 6") != -1 || agt.indexOf("opera/6"));
	var is_opera6up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4 && !is_opera5);
	var is_opera7 = (agt.indexOf("opera 7") != -1 || agt.indexOf("opera/7"))
	var is_opera7up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4 && !is_opera5 && !is_opera6);
	var is_opera8 = (agt.indexOf("opera 8") != -1 || agt.indexOf("opera/8"))
	var is_opera8up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4 && !is_opera5 && !is_opera6 && !is_opera7);
	var is_opera9 = (agt.indexOf("opera 9") != -1 || agt.indexOf("opera/9"))
	var is_opera9up = (is_opera && !is_opera2 && !is_opera3 && !is_opera4 && !is_opera5 && !is_opera6 && !is_opera7 && !is_opera8);
	
	// Konquerer
	var is_konqueror = (agt.indexOf("konqueror") != -1);
	var is_konqueror1 = (agt.indexOf("konqueror 1") != -1 || agt.indexOf("konqueror/1") != -1);
	var is_konqueror2 = (agt.indexOf("konqueror 2") != -1 || agt.indexOf("konqueror/2") != -1);
	var is_konqueror3 = (agt.indexOf("konqueror 3") != -1 || agt.indexOf("konqueror/3") != -1);
	var is_konqueror3up = (is_konqueror && !is_konqueror2 && !is_konqueror1);
	/*var is_konqueror4 = (agt.indexOf("konqueror 4") != -1 || agt.indexOf("konqueror/4"));
	var is_konqueror5 = (agt.indexOf("konqueror 5") != -1 || agt.indexOf("konqueror/5"));
	var is_konqueror5up = (is_konqueror && !is_konqueror5 && !is_konqueror4 && !is_konqueror3 && !is_konqueror2 && !is_konqueror1);*/
	
	// Safarie
	var is_safari = (agt.indexOf("safari") != -1);
	
	
	// Sonstige
	var is_webtv = (agt.indexOf("webtv") != -1);

	var is_TVNavigator = ((agt.indexOf("navio") != -1) || (agt.indexOf("navio_aoltv") != -1));
	var is_AOLTV = is_TVNavigator;

	var is_hotjava = (agt.indexOf("hotjava") != -1);
	var is_hotjava3 = (is_hotjava && (is_major == 3));
	var is_hotjava3up = (is_hotjava && (is_major >= 3));

	// *** JAVASCRIPT VERSION CHECK ***
	var is_js;
	if (is_nav2 || is_ie3) is_js = 1.0;
	else if (is_nav3) is_js = 1.1;
	else if (is_opera5up) is_js = 1.3;
	else if (is_opera) is_js = 1.1;
	else if ((is_nav4 && (is_minor <= 4.05)) || is_ie4) is_js = 1.2;
	else if ((is_nav4 && (is_minor > 4.05)) || is_ie5) is_js = 1.3;
	else if (is_hotjava3up) is_js = 1.4;
	else if (is_nav6 || is_gecko) is_js = 1.5;
	// NOTE: In the future, update this code when newer versions of JS
	// are released. For now, we try to provide some upward compatibility
	// so that future versions of Nav and IE will show they are at
	// *least* JS 1.x capable. Always check for JS version compatibility
	// with > or >=.
	else if (is_nav6up) is_js = 1.5;
	// NOTE: ie5up on mac is 1.4
	else if (is_ie5up) is_js = 1.3

	// HACK: no idea for other browsers; always check for JS version with > or >=
	else is_js = 0.0;


	/**
	* OS Check
	*
	*/
	var is_win  = (agt.indexOf('win')!=-1);
	var is_mac  = (agt.indexOf('mac')!=-1);
	
	
	/**
	* Ermittelt die benutzer Browser Engine
	*/
	var browserEngine;
	function getBrowserEngine()
	{
		if (is_safari)
		{
			browserEngine = "khtml";
		}
		else if (is_konqueror)
		{
			browserEngine = "khtml";
		}
		else if (is_opera)
		{
			browserEngine = "presto";
		}
		else if (is_gecko)
		{
			browserEngine = "gecko";
		}
		else if (is_nav)
		{
			browserEngine = "gecko";
		}
		else if (is_ie)
		{
			browserEngine = "trident";
		}
		else 
		{
			browserEngine = "unknown";
		}
		
		var tmp = getCookie("browserEngine");
		if (!(tmp && tmp != "" && tmp != "unknown"))
		{
			setCookie("browserEngine", browserEngine);
		}
	}
	getBrowserEngine();
	
	/**
	* F√Rllt die versteckten Felder des Anmeldeformular aus.
	*/
	function fillLoginFormWithData()
	{
		/*document.getElementsByName("value[browser][0][name][0]")[0].value = browserEngine;
		document.getElementsByName("value[browser][0][number][0]")[0].value = navigator.appVersion;
		document.getElementsByName("value[browser][0][version][0]")[0].value = is_js;*/
		//document.getElementsByName("value[browser][0][flash][]")[0].value = agt;
	}

	/**
	* Browserabh√¨ngige CSS werden eingebunden
	*/
	if (is_ie)
	{
		document.write('<link rel="stylesheet" type="text/css" href="/css/ie.css">');
	}
	if (is_safari)
	{
		document.write('<link rel="stylesheet" type="text/css" href="/css/safari.css">');
	}
	if (is_opera)
	{
		document.write('<link rel="stylesheet" type="text/css" href="/css/opera.css">');
	}
	if (is_gecko && is_win)
	{
		document.write('<link rel="stylesheet" type="text/css" href="/css/geckoWin.css">');
	}
	
	/**
	* Browserabh√¨ngige JS werden eingebunden
	*/
	//XSLTProcessor = undefined;
	//document.evaluate = undefined;
	if (typeof XSLTProcessor == 'undefined' || typeof document.evaluate  == 'undefined' || is_safari)
	{
		try
		{
			// Test ob der IE alle ben√∂tigten ActiveXObject erstellen kann.
			var tmp = new ActiveXObject("MSXML2.FreeThreadedDOMDocument");
			tmp = new ActiveXObject("MSXML2.XSLTemplate");
			tmp = new ActiveXObject("Microsoft.XMLDOM");
		}
		catch(e)
		{
			// Browser unterst√Rtzt kein XSLT oder XPath
			document.write('<script language="javascript" type="text/javascript" src="/trainer/js/util.js" type="text/javascript"></script>');
			document.write('<script language="javascript" type="text/javascript" src="/trainer/js/xmltoken.js" type="text/javascript"></script>');
			document.write('<script language="javascript" type="text/javascript" src="/trainer/js/dom.js" type="text/javascript"></script>');
			document.write('<script language="javascript" type="text/javascript" src="/trainer/js/xpath.js" type="text/javascript"></script>');
			document.write('<script language="javascript" type="text/javascript" src="/trainer/js/xslt.js" type="text/javascript"></script>');
		}
	}

	/**
	* Dynamische CSS Anpassung
	*/
	var innerHeight = 0;
	var innerWidth = 0;
	function setCss()
	{
		if (document.getElementById("testResult") && typeof startTest != 'undefined')
		{
			startTest();
		}
		var diff = 380;
		//var diff = 300;

		var innerHeight = 0;
		if (window.innerHeight)
		{
			// alle Browser auser IE
			innerHeight = window.innerHeight;
			innerWidth = window.innerWidth;
		}
		else if (document.documentElement && document.documentElement.clientHeight)
		{
			// IE 6 Strict Mode
			innerHeight = document.documentElement.clientHeight;
			innerWidth = document.documentElement.clientWidth;
		}
		else if (document.body)
		{
			// andere IEs
			innerHeight = document.body.clientHeight;
			innerWidth = document.body.clientWidth;
		}
		var height = innerHeight -  diff;
		if (height < 380)
		{
			height = 380;
		}
		if (height > 540)
		{
			height = 540;
		}
		// F√Rr alle Browser
		if ( document.styleSheets[0].cssRules)
		{
			for (var i = 0; i < document.styleSheets[0].cssRules.length; i++)
			{
				if (document.styleSheets[0].cssRules[i].selectorText == ".tabContentContainer .content")
				{
					document.styleSheets[0].cssRules[i].style.height = height + "px";
				}
				if (document.styleSheets[0].cssRules[i].selectorText == ".content tbody.overflow")
				{
					document.styleSheets[0].cssRules[i].style.height = (height-40) + "px";
				}
			}
			for (var i = 0; i < document.styleSheets[2].cssRules.length; i++)
			{
				if (document.styleSheets[2].cssRules[i].selectorText == ".contentContainer")
				{
					document.styleSheets[2].cssRules[i].style.height = (height) + "px";
				}
			}
		}
		else
		{
			// IE Hack
			if ( document.styleSheets[0].rules)
			{
				for (var i = 0; i < document.styleSheets[0].rules.length; i++)
				{
					if (document.styleSheets[0].rules[i].selectorText == ".tabContentContainer .content")
					{
						document.styleSheets[0].rules[i].style.height = height + "px";
					}
				}
			}
		}
	}

	//setCss();

// -->

