<%@ page buffer="none" import="com.alkacon.opencms.formgenerator.dialog.*" %>
<%
	// initialize the workplace class
	CmsFormDataListDialog wp = new CmsFormDataListDialog(pageContext, request, response);
    wp.displayDialog();
%>