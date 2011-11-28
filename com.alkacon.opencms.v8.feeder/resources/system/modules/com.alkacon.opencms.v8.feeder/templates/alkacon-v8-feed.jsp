<%@ page session="false" import="org.opencms.jsp.*, com.alkacon.opencms.v8.feeder.*" %><%

// Create a JSP action element
CmsJspActionElement bean = new CmsJspActionElement(pageContext, request, response);

CmsFeed feed = new CmsFeed(bean.getCmsObject());
feed.write(out);

%>