var oldTab = null;

function setSerialTab(tabType) {
	document.getElementById("serialtab." + tabType).style.display = "block";
	if (oldTab != null && oldTab != tabType) {
		document.getElementById("serialtab." + oldTab).style.display = "none";
	}
	oldTab = tabType;
}

function initSerialTab() {
	for (var i=1; i<5; i++) {
		var elem = document.getElementById("serialtype." + i);
		if (elem == null) {
			setTimeout("initSerialTab()", 200);
			return;
		}
		if (elem.checked) {
			setSerialTab(elem.value);
			i = 5;
		}
	}

}