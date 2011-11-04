function formgenKeepSession() {
	var keepFormSession;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		keepFormSession = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		keepFormSession = new ActiveXObject("Microsoft.XMLHTTP");
	}
	keepFormSession.onreadystatechange = function() {
		if (keepFormSession.readyState == 4 && keepFormSession.status == 200) {
			// alert(keepFormSession.responseText);
			setTimeout("formgenKeepSession();", formgenRefreshSessionTimeout);
		}
	}
	keepFormSession.open("GET", formgenKeepSessionURI, true);
	keepFormSession.send("");
}