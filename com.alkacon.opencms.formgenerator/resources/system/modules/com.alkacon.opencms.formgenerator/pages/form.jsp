<%@page buffer="none" session="false" import="com.alkacon.opencms.formgenerator.*"%><%

	// initialize the form handler
	CmsFormHandler cms = CmsFormHandlerFactory.create(pageContext, request, response);

	// in case of downloading the CSV file from database no template parts must be included
	boolean showTemplate = cms.showTemplate();

	String template = "";
	if (showTemplate) {
		// get the template to display
		template = cms.property("template", "search");
		// include the template head
		cms.include(template, "head");
	}
	
	cms.createForm();

	if (showTemplate) {
		// include the template foot
		cms.include(template, "foot");
	}
%>