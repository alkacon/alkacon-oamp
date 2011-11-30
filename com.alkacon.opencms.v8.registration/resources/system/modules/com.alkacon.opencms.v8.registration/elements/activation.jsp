<%@page buffer="none" session="false" import="org.opencms.i18n.*, org.opencms.jsp.*, com.alkacon.opencms.v8.formgenerator.*, com.alkacon.opencms.v8.registration.*, java.util.*" %><%

CmsRegistrationFormHandler formHandler = (CmsRegistrationFormHandler)request.getAttribute("formhandler"); 

// get the localized messages to create the form
CmsMessages messages = formHandler.getMessages();

// the user already exists
if (formHandler.existUser()) {
   // the user is already activated
   if (formHandler.isUserActivated()) {
      // 'already activated' error message
      out.println(messages.key("activation.error.alreadyactivated"));
   } else {
      // activate 
      formHandler.activateUser();
      out.println(formHandler.getFormActivatedText());
   }
} else {
   out.println(messages.key("activation.error.invalidcode"));
}
%>
<!-- Login element starts here --><%--
--%><cms:include file="login.jsp" /><%--
--%><!-- Login element ends here -->
