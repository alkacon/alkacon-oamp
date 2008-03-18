<%@ page buffer="none" import="com.alkacon.opencms.formgenerator.dialog.*" %>
<%
	// initialize the workplace class
	CmsFormListDialog wp = new CmsFormListDialog(pageContext, request, response);
    wp.displayDialog();
%>