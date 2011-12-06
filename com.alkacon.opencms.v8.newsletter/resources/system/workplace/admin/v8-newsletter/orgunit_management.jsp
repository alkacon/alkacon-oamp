<%@ page import="com.alkacon.opencms.v8.newsletter.admin.*, org.opencms.workplace.administration.CmsAdminDialog" %><%

    CmsDummyList dl = new CmsDummyList(pageContext, request, response);

    CmsAdminDialog wp = new CmsAdminDialog(pageContext, request, response);
    wp.displayDialog();
%>