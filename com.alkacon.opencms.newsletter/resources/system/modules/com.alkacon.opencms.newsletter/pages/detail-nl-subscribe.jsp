<%@ page session="false" import="com.alkacon.opencms.newsletter.*, org.opencms.util.*" %><%

CmsNewsletterSubscriptionBean cms = new CmsNewsletterSubscriptionBean(pageContext, request, response);

String template = cms.property("template", "search", "");

if (CmsStringUtil.isNotEmpty(template)) {
	cms.include(template, "head");
} else {
	out.print("<html><head>\n<title>");
	out.print(cms.property("Title", "search", ""));
	out.print("</title>\n<style type=\"text/css\">\n");
	out.print(cms.getContent("/system/modules/com.alkacon.opencms.newsletter/resources/style.css"));
	out.print("</style>\n</head>\n<body>");
}

%>
<div class="element newslettersubscription-wrapper">
<%

switch (cms.getAction()) {

case CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE:
	// the subscription action
	%><h1><%= cms.getConfigText("Subscribe/Headline") %></h1><p><%
	out.print(cms.actionSubscribe());
	if (!cms.isConfirmationEnabled() && cms.isShowSendLastNewsletter()) { %>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.newsletter/pages/includes/form-sendlast.jsp:62c86790-6b9a-11de-b895-5fc9fa232cc8)" %>
	<%}
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE:
	// the unsubscription action
	%><h1><%= cms.getConfigText("Subscribe/Headline") %></h1><p><%
	out.print(cms.actionUnsubscribe());
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_CONFIRMSUBSCRIPTION:
	// the confirm action: subscribe
	%><h1><%= cms.getConfigText("Confirm/Subscribe/Headline") %></h1><p><%
	out.print(cms.actionConfirmSubscribe());
	if (cms.isShowSendLastNewsletter()) { %>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.newsletter/pages/includes/form-sendlast.jsp:62c86790-6b9a-11de-b895-5fc9fa232cc8)" %>
	<%}
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_CONFIRMUNSUBSCRIPTION:
	// the confirm action: unsubscribe
	%><h1><%= cms.getConfigText("Confirm/UnSubscribe/Headline") %></h1><p><%
	out.print(cms.actionConfirmUnsubscribe());
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_SENDLASTNEWSLETTER:
	// the action: send last newsletter
	%><h1><%= cms.getConfigText("Subscribe/SendLast/Headline") %></h1><p><%
	out.print(cms.actionSendLastNewsletter());
	%></p><%
break;

default:
	// the initial newsletter subscription / unsubscription page with the form
	%>
	<h1><%= cms.getConfigText("Subscribe/Headline") %></h1>
	<%= cms.getConfigText("Subscribe/Text") %>
	
	<%
	
	if (!cms.isValid()) {
		%>
		<p class="newslettersubscription-error"><%= cms.key("validation.alknewsletter.error.headline") %></p>
		<ul class="newslettersubscription-error">
		<%= cms.getValidationErrorsHtml("li") %>
		</ul>
		<%
	}

	%>
	<p>
	<form name="subscription" class="newslettersubscription-form" action="<%= cms.link(cms.getRequestContext().getUri()) %>" method="post">
		<div class="newslettersubscription-radio">
			<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE  %>"<% if (cms.getCheckedAction() <= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE) { %> checked="checked"<% } %> />
			<%= cms.key("form.alknewsletter.subscribe") %>
		</div>
		<div class="newslettersubscription-radio">
			<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE  %>"<% if (cms.getCheckedAction() == CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE) { %> checked="checked"<% } %> />
			<%= cms.key("form.alknewsletter.unsubscribe") %>
		</div>
		<div class="newslettersubscription-email">
			<%= cms.key("form.alknewsletter.email") %>
			<input type="text" name="email" class="newslettersubscription-email" value="<%= cms.getEmail() %>" />
		</div>
		<div class="newslettersubscription-buttons">
			<input type="submit" value=" <%= cms.key("form.alknewsletter.buttonok") %> " />&nbsp;&nbsp;<input type="reset" value=" <%= cms.key("form.alknewsletter.buttonreset") %> " />
		</div>
	</form>
	</p>
<%
}
%>
</div>
<%

if (CmsStringUtil.isNotEmpty(template)) {
	cms.include(template, "foot");
} else {
	out.print("</body>\n</html>");
}

%>