<%@page taglibs="cms" buffer="none" session="false" import="com.alkacon.opencms.v8.comments.*, com.alkacon.opencms.v8.formgenerator.*, java.util.Map" %><cms:secureparams /><%

//adjust configuration to comment or not
String sParentId = request.getParameter("cmtparentid");
int parentId;
Map<String, String> dynamicConfig;
try {
	parentId = Integer.valueOf(sParentId);
	dynamicConfig = CmsCommentsAccess.generateDynamicConfig(CmsCommentsAccess.REPLIES_FORMID);
} catch (NumberFormatException e) {
	parentId = -1;
 	dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
}

// initialize the form handler
CmsCommentFormHandler cms = (CmsCommentFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsCommentFormHandler.class.getName(), request.getParameter("configUri"), dynamicConfig);
// create the form
cms.createForm(parentId);

%>