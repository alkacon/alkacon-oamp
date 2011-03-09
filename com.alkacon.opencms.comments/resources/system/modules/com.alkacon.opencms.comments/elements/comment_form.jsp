<%@page buffer="none" session="false" import="com.alkacon.opencms.comments.*, com.alkacon.opencms.formgenerator.*" %><%

// initialize the form handler
CmsCommentFormHandler cms = (CmsCommentFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsCommentFormHandler.class.getName(), null);
// create the form
cms.createForm();

%>