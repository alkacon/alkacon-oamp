<%@ page buffer="none" import="com.alkacon.opencms.v8.formgenerator.dialog.*" %>
<%
	// initialize the workplace class
	CmsFormDataListDialog wp = new CmsFormDataListDialog(pageContext, request, response);
    wp.displayDialog();
%>