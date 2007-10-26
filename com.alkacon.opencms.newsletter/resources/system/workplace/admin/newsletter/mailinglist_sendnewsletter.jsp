<%@ page import="com.alkacon.opencms.newsletter.admin.*" %><%

    CmsNewsletterListSend wp = new CmsNewsletterListSend(pageContext, request, response);
    wp.displayDialog();
%>