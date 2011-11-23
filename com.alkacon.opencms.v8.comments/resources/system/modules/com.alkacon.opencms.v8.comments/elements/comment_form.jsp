<%@page buffer="none" session="false" import="com.alkacon.opencms.v8.comments.*, com.alkacon.opencms.v8.formgenerator.*" %><%

// initialize the form handler
CmsCommentFormHandler cms = (CmsCommentFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsCommentFormHandler.class.getName(), request.getParameter("configUri"));
// create the form
cms.createForm();

%>