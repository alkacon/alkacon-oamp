<%@page buffer="none" session="false" import="org.opencms.i18n.*,com.alkacon.opencms.formgenerator.*, com.alkacon.opencms.registration.*, java.util.*, org.opencms.util.*" %><%

// initialize the form handler
CmsRegistrationFormHandler cms = (CmsRegistrationFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsRegistrationFormHandler.class.getName(), null);
// set flag that we are on the profile page
cms.setProfilePage(true);

// get the template to display
String template = cms.property("template", "search");
// include the template head
cms.include(template, "head");

cms.createForm();

// include the template foot
cms.include(template, "foot");
%>
