<%@page buffer="none" session="false" import="org.opencms.jsp.*, com.alkacon.opencms.formgenerator.*, java.util.*" %><%

// Initialize JSP action element
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

CmsFormHandler formHandler = (CmsFormHandler)request.getAttribute("formhandler"); 

%><%= formHandler.getFormConfirmationText() %>
<table border="0" class="webform_confirm_table">
<%
List resultList = formHandler.getFormConfiguration().getAllFields(true, false, true);

for (int i = 0, n = resultList.size(); i < n; i++) {
	I_CmsField current = (I_CmsField)resultList.get(i);
	if (CmsHiddenField.class.isAssignableFrom(current.getClass()) || CmsPrivacyField.class.isAssignableFrom(current.getClass())
			|| CmsCaptchaField.class.isAssignableFrom(current.getClass()) || CmsPagingField.class.isAssignableFrom(current.getClass())) {
		continue;
	}
	String label = current.getLabel();
	if (current instanceof CmsTableField) {
	    label = ((CmsTableField)current).buildLabel(formHandler.getMessages(),false,false);
	} else if (current instanceof CmsEmptyField) {
    	    label = "";
    }
	String value = current.toString();
    if ((current instanceof CmsDisplayField)) {
    	value = formHandler.convertToHtmlValue(value);
    } else if ((current instanceof CmsHiddenDisplayField)) {
    	continue;
    } else if ((current instanceof CmsDynamicField)) {
        if (!current.isMandatory()) {
            // show dynamic fields only if they are marked as mandatory
            continue;
        }
        // compute the value for the dynamic field
        value = formHandler.getFormConfiguration().getFieldStringValueByName(current.getName());
        value = formHandler.convertToHtmlValue(value);
    }else if (current instanceof CmsTableField) {
        value = ((CmsTableField)current).buildHtml(formHandler.getMessages(),false);
    }else if (current instanceof CmsPasswordField) {
        value = value.replaceAll(".", "*");
    }else if (current instanceof CmsFileUploadField) {
	value = CmsFormHandler.getTruncatedFileItemName(value);
	value = formHandler.convertToHtmlValue(value);
    }else if (current instanceof CmsEmptyField) {
        // do nothing
    }else {
        value = formHandler.convertToHtmlValue(value);
    }

	out.print("<tr>\n\t<td valign=\"top\">" + label + "</td>");
	out.print("\n\t<td valign=\"top\" style=\"font-weight: bold;\">" + value + "</td></tr>\n");
}

%>
</table>