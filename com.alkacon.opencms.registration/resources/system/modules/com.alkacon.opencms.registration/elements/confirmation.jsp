<%@page buffer="none" session="false" import="org.opencms.jsp.*, com.alkacon.opencms.formgenerator.*, com.alkacon.opencms.registration.*, java.util.*" %><%

CmsRegistrationFormHandler formHandler = (CmsRegistrationFormHandler)request.getAttribute("formhandler"); 

%><%= formHandler.getFormConfirmationText() %>
<table border="0" style="margin-top: 14px;">
<%
List resultList = formHandler.getFormConfiguration().getAllFields();

for (int i = 0, n = resultList.size(); i < n; i++) {
	I_CmsField current = (I_CmsField)resultList.get(i);
	if (CmsPasswordField.class.isAssignableFrom(current.getClass()) || CmsHiddenField.class.isAssignableFrom(current.getClass()) || CmsPrivacyField.class.isAssignableFrom(current.getClass()) || CmsCaptchaField.class.isAssignableFrom(current.getClass())) {
		continue;
	}
	String value = current.toString();
    if ((current instanceof CmsDynamicField)) {
        if (!current.isMandatory()) {
            // show dynamic fields only if they are marked as mandatory
            continue;
        }
        // compute the value for the dynamic field
        value = formHandler.getFormConfiguration().getFieldStringValueByName(current.getName());
    }

	out.print("<tr>\n\t<td valign=\"top\">" + current.getLabel() + "</td>");
	out.print("\n\t<td valign=\"top\" style=\"font-weight: bold;\">" + formHandler.convertToHtmlValue(value) + "</td></tr>\n");
}

%>
</table>