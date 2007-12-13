<%@ page session="false" import="org.opencms.jsp.*, com.alkacon.opencms.feeder.*" %><%

// Create a JSP action element
CmsJspActionElement bean = new CmsJspActionElement(pageContext, request, response);

CmsFeed feed = new CmsFeed(bean.getCmsObject());
feed.write(out);

%>