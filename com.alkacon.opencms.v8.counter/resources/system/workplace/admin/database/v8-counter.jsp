<%@ page import="com.alkacon.opencms.v8.counter.*" %>
<%
	CmsCounterDialog wp = new CmsCounterDialog(pageContext, request, response);
	wp.displayDialog();
%>