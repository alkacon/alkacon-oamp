<%@ page session="false" import="com.alkacon.opencms.newsletter.*, org.opencms.util.*" %><%

// This element can be included on sides of other templates.
// Be sure to pass a request parameter "file" with the absolute site path of 
// a subscription configuration content as value, e.g. "file=/mypath/newsletter-subscription.html".

CmsNewsletterSubscriptionBean cms = new CmsNewsletterSubscriptionBean(pageContext, request, response);

if (CmsStringUtil.isNotEmpty(request.getParameter(CmsNewsletterSubscriptionBean.PARAM_FILE))) {

%>
<form name="subscription" class="newslettersubscription-form-sm" action="<%= cms.link(request.getParameter(CmsNewsletterSubscriptionBean.PARAM_FILE)) %>" method="post">
	<div class="newslettersubscription-radio-sm">
		<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE  %>"<% if (cms.getCheckedAction() <= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE) { %> checked="checked"<% } %> />
		<%= cms.key("form.alknewsletter.subscribe.short") %>
	</div>
	<div class="newslettersubscription-radio-sm">
		<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE  %>"<% if (cms.getCheckedAction() == CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE) { %> checked="checked"<% } %> />
		<%= cms.key("form.alknewsletter.unsubscribe.short") %>
	</div>
	<div class="newslettersubscription-email-sm">
		<%= cms.key("form.alknewsletter.email.short") %>
		<input type="text" name="email" class="newslettersubscription-email-sm" value="<%= cms.getEmail() %>" />
	</div>
	<div class="newslettersubscription-buttons-sm">
		<input type="submit" value=" <%= cms.key("form.alknewsletter.buttonok.short") %> " />
	</div>
</form>
<%

} else { %>

<h4>Error including newsletter subscription side element</h4>
<p>No valid request parameter "file" has been passed.</p>
<%
}

%>
