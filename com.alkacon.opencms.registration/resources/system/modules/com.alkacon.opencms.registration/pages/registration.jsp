<%@page buffer="none" session="false" import="org.opencms.i18n.*,com.alkacon.opencms.formgenerator.*, com.alkacon.opencms.registration.*, java.util.*, org.opencms.util.*" %><%

// initialize the form handler
CmsRegistrationFormHandler cms = new CmsRegistrationFormHandler(pageContext, request, response);


// get the localized messages to create the form
CmsMessages messages = cms.getMessages();

// get the template to display
String template = cms.property("template", "search");
 // include the template head
 cms.include(template, "head");

String actcode = (String)request.getParameter(CmsRegistrationFormHandler.PARAM_ACTCODE);
if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(actcode)) {

		// show user activation page
		request.setAttribute("formhandler", cms);
		cms.include("%(link.strong:/system/modules/com.alkacon.opencms.registration/elements/activation.jsp:7628cd2b-dbd3-11dc-9fc7-9b273b3a1499)");

} else {
boolean showForm = cms.showForm();

if (! showForm) {
	// form has been submitted with correct values, decide further actions
	if (cms.showCheck()) {
		// show optional check page
		request.setAttribute("formhandler", cms);
		cms.include("%(link.strong:/system/modules/com.alkacon.opencms.registration/elements/check.jsp:2d117413-da41-11dc-af2b-9b273b3a1499)");
	} else {
		// try to send a notification email with the submitted form field values
		if (cms.sendData()) {
			// successfully sent mail, show confirmation end page
			request.setAttribute("formhandler", cms);
			cms.include("%(link.strong:/system/modules/com.alkacon.opencms.registration/elements/confirmation.jsp:2d1878f6-da41-11dc-af2b-9b273b3a1499)");
		} else {
			// failure sending mail, show error output %>
			<h3><%= messages.key("form.error.mail.headline") %></h3>
			<p><%= messages.key("form.error.mail.text") %></p>

			<!--
			Error description: <%= (String)cms.getErrors().get("sendmail") %>
			//--><%
		}
	}
	
} else {

	// get the configured form elements
	CmsForm formConfiguration = cms.getFormConfiguration();
	List fields = formConfiguration.getFields();
	
	// show form text
	out.print(formConfiguration.getFormText());
	
	// show global error message if validation failed
	if (cms.hasValidationErrors()) {
		out.print("<p>");
		out.print(messages.key("form.html.label.error.start"));
		out.print(messages.key("form.error.message"));
		out.print(messages.key("form.html.label.error.end"));
		out.println("</p>");
	}

	// create the form head 
	%>
	<form name="emailform" action="<%= cms.link(cms.getRequestContext().getUri()) %>" method="post" enctype="multipart/form-data">
	<!-- Hidden form fields:  -->
        <input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>"  id="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_SUBMIT %>"/>
	<%= messages.key("form.html.start") %><%= formConfiguration.getFormAttributes() %>
		<%
	// create the html output to display the form fields
	int pos=0;
	int place=0;
	for( int i = 0, n = fields.size(); i < n; i++) {
		
		// loop through all form input fields 
		I_CmsField field = (I_CmsField)fields.get(i);
		
		if(i==n-1)place=1; //the last one must close the tr
		field.setPlaceholder(place);
		field.setPosition(pos);
		String errorMessage = (String)cms.getErrors().get(field.getName());
		
		out.println(field.buildHtml(cms, messages, errorMessage, formConfiguration.isShowMandatory()));
		pos=field.getPosition();
		place=field.getPlaceholder();
	}
	
	// create the form foot 
	if (formConfiguration.hasMandatoryFields() && formConfiguration.isShowMandatory()) {
		%><%= messages.key("form.html.row.start") %>
			<%= messages.key("form.html.button.start") %><%= messages.key("form.message.mandatory") %><%= messages.key("form.html.button.end") %>
		<%= messages.key("form.html.row.end") %>
		<%
	}
		%><%= messages.key("form.html.row.start") %>
			<%= messages.key("form.html.button.start") %><input type="submit" value="<%= messages.key("form.button.submit") %>"  class="formbutton submitbutton"/><% if (formConfiguration.isShowReset()) { %>&nbsp;<input type="reset" value="<%= messages.key("form.button.reset") %>" class="formbutton resetbutton"/><% } %><%= messages.key("form.html.button.end") %>
		<%= messages.key("form.html.row.end") %>
	<%= messages.key("form.html.end") %>
       	</form><%
	// show form footer text
	out.print(formConfiguration.getFormFooterText());

}
}
 // include the template foot
 cms.include(template, "foot");
%>
