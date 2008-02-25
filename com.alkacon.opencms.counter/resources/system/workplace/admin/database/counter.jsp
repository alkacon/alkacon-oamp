<%@ page import="com.alkacon.opencms.counter.*" %>
<%
	CmsCounterDialog wp = new CmsCounterDialog(pageContext, request, response);
	wp.displayDialog();
%>