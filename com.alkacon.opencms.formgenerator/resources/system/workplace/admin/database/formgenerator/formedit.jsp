<%@ page buffer="none" import="com.alkacon.opencms.formgenerator.dialog.*" %>
<%
	// initialize the workplace class
	CmsFormEditDialog wp = new CmsFormEditDialog(pageContext, request, response);
    wp.displayDialog();
%>