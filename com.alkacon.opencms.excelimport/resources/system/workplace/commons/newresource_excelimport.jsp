<%@ page import="java.util.*,com.alkacon.opencms.excelimport.*, org.opencms.main.OpenCms, org.opencms.jsp.CmsJspActionElement" %><%	

	// initialize the workplace class
	CmsJspActionElement cmsAction = new CmsJspActionElement(pageContext, request, response);
	CmsResourceExcelImport wp = new CmsResourceExcelImport(pageContext, request, response);

//////////////////// start of switch statement 
	
switch (wp.getAction()) {
   
case CmsResourceExcelImport.ACTION_CANCEL:
//////////////////// ACTION: cancel button pressed
	wp.actionCloseDialog();
	break;

case CmsResourceExcelImport.ACTION_OK:
//////////////////// ACTION: upload name specified and form submitted
	wp.setParamAction(CmsResourceExcelImport.DIALOG_CONFIRMED);
	// read parameters from session
	if (wp.useConfirmationDialog()) {
		wp.readParasFromSession();
	}
	wp.actionUpload();
	break;

//////////////////// ACTION: other actions handled outside of this JSP
case CmsResourceExcelImport.ACTION_CONFIRMED:
case CmsResourceExcelImport.ACTION_REPORT_BEGIN:
case CmsResourceExcelImport.ACTION_REPORT_UPDATE:
case CmsResourceExcelImport.ACTION_REPORT_END:
	wp.actionReport();
	break;

case CmsResourceExcelImport.ACTION_SUBMITFORM2:
//////////////////// ACTION: upload name specified and form submitted
	// write parameters to session
	if (wp.useConfirmationDialog()) {
		wp.writeParasToSession();
	}
	wp.setParamAction(CmsResourceExcelImport.DIALOG_OK);
%>
<%= wp.htmlStart("help.explorer.new.file") %>
<%= wp.bodyStart("dialog") %>
<%= wp.dialogStart() %>
<%= wp.dialogContentStart(wp.getParamTitle()) %>

<form name="main" action="<%= wp.getDialogUri() %>" method="post" class="nomargin" onsubmit="return submitAction('<%= wp.DIALOG_OK %>', null, 'main');">
<%= wp.paramsAsHidden() %>
<%= wp.getDialogText() %>

<%= wp.dialogContentEnd() %>
<% if (wp.isValid()) { %>
	<%= wp.dialogButtonsOkCancel("id=\"okButton\"", null) %>
<% } else { %>
	<%= wp.dialogButtonsOkCancel("id=\"okButton\" disabled=\"disabled\"", null) %>
<% } %>

</form>

<%= wp.dialogEnd() %>
<%= wp.bodyEnd() %>
<%= wp.htmlEnd() %>

<% break;

case CmsResourceExcelImport.ACTION_DEFAULT:
default:
//////////////////// ACTION: show the form to specify the upload file and the unzip option
	if (wp.useConfirmationDialog()) {
		wp.setParamAction(wp.DIALOG_SUBMITFORM2);
		wp.writeParasToSession();
	} else {
		wp.setParamAction(wp.DIALOG_OK);
	}

%><%= wp.htmlStart("help.explorer.new.file") %>
<script type="text/javascript">
<!--
	var labelFinish = "<%= wp.key(Messages.GUI_BUTTON_ENDWIZARD_0) %>";
	var labelNext = "<%= wp.key(Messages.GUI_BUTTON_CONTINUE_0) %>";

	function checkValue() {
		var resName = document.getElementById("excelfile").value;
		var theButton = null;
		if(document.getElementById("nextButton")) {
			theButton = document.getElementById("nextButton");
		} else {
			theButton = document.getElementById("okButton");
		}
		if (resName.length == 0) { 
			if (theButton.disabled == false) {
				theButton.disabled =true;
			}
		} else {
			if (theButton.disabled == true) {
				theButton.disabled = false;
			}
		}
	}
	
	function startTimeOut() {
		// this is required for Mozilla since the onChange event doesn't work there for <input type="file">
		window.setTimeout("checkValue();startTimeOut();", 500);
	}
	
	startTimeOut();	
//-->
</script>
<%= wp.bodyStart("dialog") %>
<%= wp.dialogStart() %>
<%= wp.dialogContentStart(wp.getParamTitle()) %>

<form name="main" action="<%= wp.getDialogUri() %>" method="post" class="nomargin" onsubmit="return submitAction('<%= wp.DIALOG_OK %>', null, 'main');" enctype="multipart/form-data">
<%= wp.paramsAsHidden() %>
<input type="hidden" name="<%= wp.PARAM_FRAMENAME %>" value="">

<table border="0" width="100%">
<tr>
	<td style="white-space: nowrap;" unselectable="on"><%= wp.key(Messages.GUI_RESOURCE_NAME_0) %></td>
	<td class="maxwidth"><input name="<%= wp.PARAM_EXCEL_FILE %>" id="excelfile" type="file" value="" size="60" class="maxwidth" onchange="checkValue();"></td>
</tr> 
<tr>
	<td>&nbsp;</td>
	<td style="white-space: nowrap;" unselectable="on" class="maxwidth"><input name="<%= wp.PARAM_PUBLISH %>" id="publish" type="checkbox" value="true">&nbsp;<%= wp.key(Messages.GUI_BUTTON_NEWRESOURCE_EXCELIMPORT_PUBLISH_0) %></td>    
</tr> 
</table>


<%= wp.dialogContentEnd() %>

<%
if (wp.useConfirmationDialog()) { %>
	<%= wp.dialogButtonsNextCancel("id=\"nextButton\" disabled=\"disabled\"", null) %>
<% } else { %>
	<%= wp.dialogButtonsOkCancel("id=\"okButton\" disabled=\"disabled\"", null) %>
<% } %>

</form>

<%= wp.dialogEnd() %>

<%= wp.bodyEnd() %>
<%= wp.htmlEnd() %>
<%
} 
//////////////////// end of switch statement 
%>