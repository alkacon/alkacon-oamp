<%@ page import="org.opencms.jsp.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);


%><html>

<head>
<title>Calendar example</title>
<link href="<%= cms.link("../resources/calendar.css") %>" rel="stylesheet" type="text/css">
</head>

<body>
<center>
<div style="padding-top: 20px; text-align: center; width: 230px;">
	<% cms.include("../elements/calendar_month.jsp"); %>
</div>
</center>
</body>
</html>