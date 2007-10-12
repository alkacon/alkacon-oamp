<%@ page import="com.alkacon.opencms.newsletter.admin.*, org.opencms.workplace.administration.CmsAdminDialog" %><%

    CmsDummyList dl = new CmsDummyList(pageContext, request, response);

    CmsAdminDialog wp = new CmsAdminDialog(pageContext, request, response);
    wp.displayDialog();
%>