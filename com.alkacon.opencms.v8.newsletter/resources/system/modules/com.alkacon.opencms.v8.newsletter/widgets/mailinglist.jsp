<%@ page import="com.alkacon.opencms.v8.newsletter.*" %>
<%
	// initialize the list dialog
	CmsMailinglistSelectionList wpList = new CmsMailinglistSelectionList (pageContext, request, response);
	// perform the list actions 
	wpList.displayDialog();
%>
