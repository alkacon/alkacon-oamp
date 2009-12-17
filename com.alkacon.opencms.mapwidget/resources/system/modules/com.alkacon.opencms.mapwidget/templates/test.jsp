<%@ page import="org.opencms.jsp.*" %><%--
--%><%@ page session="false" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %><%
   CmsJspActionElement jsp = new CmsJspActionElement(pageContext, request, response);

   String gmapsKey = !jsp.getCmsObject().getRequestContext().currentProject().isOnlineProject() ? 
     "ABQIAAAAa_ldt8Egmcyz-vH8TV6m3hS4_EiOQ7VavV1fQimZ5Fhi2laDTBTOwCKHsTVlmBe82tZBT3jUZLa8Rg" : // offline
     "ABQIAAAAa_ldt8Egmcyz-vH8TV6m3hTr3VSYZWoEpAgOMD46_cvZXZIkkBQqf-_rNzg4jMtAEbD_aM1A0Fz0xQ";  // online
%><cms:template element="head"><%--
--%><!doctype html public "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>Map Test</title>
    <meta name="generator" content="<%=org.opencms.main.OpenCms.getSystemInfo().getVersion()%>" />
    <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=<%=gmapsKey%>" type="text/javascript"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type="text/javascript"></script>
    <script src="<cms:link>../resources/jquery.metadata.js</cms:link>" type="text/javascript"></script>
    <script src="<cms:link>../resources/displaymap.js</cms:link>" type="text/javascript"></script>
  </head>
<body>
  <h1>Map Test<h1>
</cms:template><%--


--%><cms:template element="body">
  <cms:include element="body" editable="true" />
</cms:template><%--


--%><cms:template element="foot">
</body>
</html>
</cms:template>