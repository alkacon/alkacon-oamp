<%@ page session="false" import="org.opencms.jsp.*, com.alkacon.opencms.v8.usagereport.*" %><%

// Create a JSP action element
CmsJspActionElement bean = new CmsJspActionElement(pageContext, request, response);

CmsUpdatefeed feed = new CmsUpdatefeed(bean.getCmsObject());
feed.write(out);

%>