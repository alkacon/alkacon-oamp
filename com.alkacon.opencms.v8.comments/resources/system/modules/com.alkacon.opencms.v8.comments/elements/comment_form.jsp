<%@page taglibs="cms" buffer="none" session="false" import="com.alkacon.opencms.v8.comments.*, com.alkacon.opencms.v8.formgenerator.*, java.util.Map" %><cms:secureparams /><%

// initialize the form handler
Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
CmsCommentFormHandler cms = (CmsCommentFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsCommentFormHandler.class.getName(), request.getParameter("configUri"), dynamicConfig);
// create the form
cms.createForm();

%>