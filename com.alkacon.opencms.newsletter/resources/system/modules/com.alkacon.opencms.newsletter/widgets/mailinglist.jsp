<%@ page import="com.alkacon.opencms.newsletter.*" %>
<%
	// initialize the list dialog
	CmsMailinglistSelectionList wpList = new CmsMailinglistSelectionList (pageContext, request, response);
	// perform the list actions 
	wpList.displayDialog();
%>
