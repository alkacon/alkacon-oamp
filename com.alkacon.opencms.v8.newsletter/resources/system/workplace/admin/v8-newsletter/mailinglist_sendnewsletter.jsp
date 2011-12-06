<%@ page import="com.alkacon.opencms.v8.newsletter.admin.*" %><%

    CmsNewsletterListSend wp = new CmsNewsletterListSend(pageContext, request, response);
    wp.displayDialog();
%>