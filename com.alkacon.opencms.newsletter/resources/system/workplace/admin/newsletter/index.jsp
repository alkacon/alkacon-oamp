<%@ page import="com.alkacon.opencms.newsletter.admin.*,
                 org.opencms.jsp.CmsJspActionElement" %><% 

  CmsJspActionElement actionElement = new CmsJspActionElement(pageContext, request, response);
  
  CmsOrgUnitsAdminList wpOrgUnitsAdmin = new CmsOrgUnitsAdminList(actionElement);
  // perform the list actions   
  wpOrgUnitsAdmin.displayDialog(true);

  if(!wpOrgUnitsAdmin.hasMoreAdminOUs()){
    wpOrgUnitsAdmin.forwardToSingleAdminOU();
  }

  // write the content of list dialogs
  wpOrgUnitsAdmin.writeDialog();
%>