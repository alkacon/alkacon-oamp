<%@ page buffer="none" import="com.alkacon.opencms.v8.formgenerator.dialog.*" %>
<%
	// initialize the workplace class
	CmsFormListDialog wp = new CmsFormListDialog(pageContext, request, response);
    wp.displayDialog();
%>