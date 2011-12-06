<%@ page import="com.alkacon.opencms.v8.newsletter.admin.*" %><%

    CmsNewsletterList wp = new CmsNewsletterList(pageContext, request, response);
    wp.displayDialog();
%>