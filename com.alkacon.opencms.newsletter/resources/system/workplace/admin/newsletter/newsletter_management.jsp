<%@ page import="com.alkacon.opencms.newsletter.admin.*" %><%

    CmsNewsletterList wp = new CmsNewsletterList(pageContext, request, response);
    wp.displayDialog();
%>