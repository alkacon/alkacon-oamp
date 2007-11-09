<%@ page session="false" import="org.opencms.file.*, org.opencms.jsp.*, com.alkacon.opencms.newsletter.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

String uri = request.getParameter("uri");

if (uri != null  && !CmsResource.isFolder(uri)) {
	I_CmsNewsletterMailData mailData = CmsNewsletterManager.getMailData(cms.getCmsObject(), null, uri);
	out.print(mailData .getEmailContentPreview());
}

%>

