<%@ page session="false" import="org.opencms.file.*, org.opencms.jsp.*, com.alkacon.opencms.newsletter.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

String uri = request.getParameter("nluri");

if (uri != null  && !CmsResource.isFolder(uri)) {
	I_CmsNewsletterMailData mailData = CmsNewsletterManager.getMailData(cms, cms.getCmsObject().readGroup(org.opencms.main.OpenCms.getDefaultUsers().getGroupGuests()), uri);
	out.print(mailData.getEmailContentPreview());
}

%>