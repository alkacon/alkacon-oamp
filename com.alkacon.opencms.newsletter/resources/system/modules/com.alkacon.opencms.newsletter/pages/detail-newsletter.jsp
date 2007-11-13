<%@ page session="false" import="java.util.*,org.opencms.jsp.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

Locale locale = cms.getRequestContext().getLocale();

%>
<fmt:setLocale value="<%= locale %>" />
<fmt:bundle basename="com.alkacon.opencms.newsletter.workplace">


<cms:contentload collector="singleFile" param="%(opencms.uri)" editable="true">

<html>
<head>
	<title><cms:contentshow element="Subject" /></title>
	<style type="text/css">
		body {
			font-family: Verdana, Arial, Helvetica, sans-serif;  font-size: 12px; color: #000000;
		}
		h1 {
			font-size: 14px; color: #000000;
		}
		div.field {
			height: 20px; margin: 6px 0 6px 0; padding: 4px; clear: both; background-color: #EEEEEE; border: 1px solid #CCCCCC;
		}
		div.fieldlabel {
			float: left; width: 150px;
		}
		div.fieldtext {
			float: left; 
		}
		iframe {
			width: 100%; height: 300px; border: 1px solid #CCCCCC; padding: 0; margin: 0; 
		}
	</style>
</head>
<body>

<h1><fmt:message key="preview.alknewsletter.headline" /></h1>

<div class="field">
	<div class="fieldlabel"><fmt:message key="label.AlkaconNewsletterMail.From" /></div>
	<div class="fieldtext"><cms:contentshow element="From" /></div>
</div>

<cms:contentcheck ifexists="BCC">
<div class="field">
	<div class="fieldlabel"><fmt:message key="label.AlkaconNewsletterMail.BCC" /></div>
	<div class="fieldtext"><cms:contentshow element="BCC" /></div>
</div>
</cms:contentcheck>

<div class="field">
	<div class="fieldlabel"><fmt:message key="label.AlkaconNewsletterMail.Subject" /></div>
	<div class="fieldtext"><strong><cms:contentshow element="Subject" /></strong></div>
</div>
<iframe name="email" src="<cms:link>detail-newsletter-iframe.jsp?uri=<cms:contentshow element="%(opencms.filename)" /></cms:link>" frameborder="0" ></iframe>

</body>
</html>
</cms:contentload>

</fmt:bundle>