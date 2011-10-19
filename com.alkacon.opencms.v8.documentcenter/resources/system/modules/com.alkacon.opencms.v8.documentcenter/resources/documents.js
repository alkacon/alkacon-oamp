
function parseSearchQuery(theForm, message) {
	var queryValue = theForm.elements["query2"].value;
	var testValue = queryValue.replace(/ /g, "");
	if (testValue.length < 3) {
		alert(message);
		return (false);
	}
	queryValue = queryValue.replace(/\+/g, "%2b");
	queryValue = queryValue.replace(/\-/g, "%2d");
	theForm.elements["query"].value = queryValue;
	return (true);
}


function initCategories(size) {
	var form = document.forms.searchnew;

	form.all.checked = true;
	toggleCategories(size);
}
	
function toggleCategories(size) {
	var theForm = document.forms.searchnew;
	var activate = false;
	if (theForm.all.checked == true) {
		activate = true;
	}
	for (var i=0; i<size; i++) {
		var curElem =  document.getElementById("cat"+i);
		curElem.disabled = activate;
		curElem.checked = activate;
	}
}


function checkIntraTel()
{
	if (document.intratel.p_Person.value=="")
	{	
		alert("Für die Suche im IntraTel\nbitte mindestens 1 Zeichen eingeben!");
		return false;
	}
	else
	{
		return true;
	}
}

function openPW(url){ 
    top.name = "main_window"; 
    var popup = window.open(url,"neuesfenster",'toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=0,width=550,height=450'); 
} 

function toggleTree(categoryCount, category, mode) {
	
	var selectedCategories = "";
	var form = document.forms.searchnew;
	
	for (var i=0; i<categoryCount; i++) {
		var checkbox = document.getElementById("cat"+i);
		
		if (checkbox.checked == true) {
		
			if (selectedCategories != "") {
				selectedCategories += ",";
			}
		
			selectedCategories += checkbox.value;
		}
	}	
 
 	form.categorylist.value = "" + selectedCategories;
 	//alert("categorylist: " + form.categorylist.value);
 	//alert("categorylist length: " + selectedCategories.length); 	
 	
 	form.toggleMode.value = "" + mode;
 	//alert("mode: " + form.toggleMode.value);
 	
 	form.toggleCategory.value = "" + category;	
 	//alert("category: " + form.toggleCategory.value);
 	
	form.action.value = "toggleTree";
	//alert("action: " + form.action.value);
	
	form.submit();		
}

