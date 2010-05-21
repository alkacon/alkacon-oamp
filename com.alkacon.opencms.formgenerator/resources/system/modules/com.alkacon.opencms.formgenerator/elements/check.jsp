<%@page buffer="none" session="false" import="org.opencms.i18n.*,org.opencms.jsp.*, com.alkacon.opencms.formgenerator.*, java.util.*" %><%

// Initialize JSP action element
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

CmsFormHandler formHandler = (CmsFormHandler)request.getAttribute("formhandler");

CmsMessages messages = formHandler.getMessages(); 

CmsCaptchaField captchaField = formHandler.getFormConfiguration().getCaptchaField();

%><%= formHandler.getFormCheckText() %><%

if (captchaField != null) {
%>
<script type="text/javascript">
<!--
function runConfirmValues() {
	document.getElementById("webformconfirmvalues").<%= captchaField.getName() %>.value = "" + document.getElementById("webformcaptcha").<%= captchaField.getName() %>.value;
	return true;
}
//-->
</script>
<form id="webformcaptcha" action="" method="post" enctype="multipart/form-data">
<%
}
%>

<table border="0" class="webform_check_table">
<%
List resultList = formHandler.getFormConfiguration().getAllFields(true, false, false);

for (int i = 0, n = resultList.size(); i < n; i++) {
	I_CmsField current = (I_CmsField)resultList.get(i);
	if ((!CmsDynamicField.class.isAssignableFrom(current.getClass()) && !CmsHiddenField.class.isAssignableFrom(current.getClass()) && !CmsCaptchaField.class.isAssignableFrom(current.getClass()) && !CmsHiddenDisplayField.class.isAssignableFrom(current.getClass())) || (CmsDisplayField.class.isAssignableFrom(current.getClass()))) {
		if (current instanceof CmsEmptyField || current instanceof CmsPagingField) {
	    	continue;
		}    
		String label = current.getLabel();
		if (current instanceof CmsTableField) {
		    label = ((CmsTableField)current).buildLabel(formHandler.getMessages(),false,false);
		}
		String value = current.toString();
	    if (current instanceof CmsTableField) {
	        value = ((CmsTableField)current).buildHtml(formHandler.getMessages(),false);
	    }else if (current instanceof CmsPasswordField) {
	        value = value.replaceAll(".", "*");
	    }else if (current instanceof CmsFileUploadField) {
	  		value = CmsFormHandler.getTruncatedFileItemName(value);
	  		value = formHandler.convertToHtmlValue(value);
	    }else {
	        value = formHandler.convertToHtmlValue(value);
	    }
		out.print("<tr>\n\t<td valign=\"top\">" + label + "</td>");
		out.print("\n\t<td valign=\"top\" style=\"font-weight: bold;\">" + value + "</td></tr>\n");
	}
}

if (captchaField != null) {

	CmsCaptchaSettings captchaSettings = captchaField.getCaptchaSettings();
	String fieldLabel = captchaField.getLabel();	
	String errorMessage = (String)formHandler.getErrors().get(captchaField.getName());	
	
	if (errorMessage != null) {
		// create the error message for the field
		if (CmsFormHandler.ERROR_MANDATORY.equals(errorMessage)) {
			errorMessage = messages.key("form.error.mandatory");
		} else {
			errorMessage = messages.key("form.error.validation");
		}
		errorMessage = messages.key("form.html.error.start") + errorMessage + messages.key("form.html.error.end");
		fieldLabel = messages.key("form.html.label.error.start") + fieldLabel + messages.key("form.html.label.error.end");
	} else {
		errorMessage = "";
	}	
	
	out.println("<tr>\n\t<td valign=\"middle\">" + fieldLabel + "</td>");
	out.println("\t<td valign=\"top\" style=\"font-weight: bold;\">");
	out.println("\t<img src=\"" + cms.link("/system/modules/com.alkacon.opencms.formgenerator/pages/captcha.jsp") + "?" + captchaSettings.toRequestParams(cms.getCmsObject()) + "\" width=\"" + captchaSettings.getImageWidth() + "\" height=\"" + captchaSettings.getImageHeight() + "\" alt=\"\" /><br />");
	out.println("\t<input type=\"text\" name=\"" + captchaField.getName() + "\" value=\"\" />" + errorMessage);
	out.println("\t</td>\n</tr>\n");
}

%></table><%

if (captchaField != null) {
%>
</form>
<%
}
%>

<table border="0" class="webform_check_table">
<tr><td>
<form id="webformconfirmvalues" method="post" enctype="multipart/form-data" action="<%= cms.link(cms.getRequestContext().getUri()) %>" onsubmit="return runConfirmValues();">
<div style="display: none;">
<input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_CONFIRMED %>" />
<input type="hidden" name="<%= CmsCaptchaField.C_PARAM_CAPTCHA_PHRASE %>" value="" />
<%= formHandler.createHiddenFields() %>
</div><div>
<input type="submit" value="<%= messages.key("form.button.checked") %>" class="formbutton" />&nbsp;&nbsp;&nbsp;&nbsp;</div>
</form>
</td>
<td>
<form id="webformdisplayvalues" method="post" enctype="multipart/form-data" action="<%= cms.link(cms.getRequestContext().getUri()) %>">
<div style="display: none;">
<input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_CORRECT_INPUT %>" />
<%= formHandler.createHiddenFields() %>
</div><div>
<input type="submit" value="<%= messages.key("form.button.correct") %>" class="formbutton" /></div>
</form>
</td></tr>
</table>