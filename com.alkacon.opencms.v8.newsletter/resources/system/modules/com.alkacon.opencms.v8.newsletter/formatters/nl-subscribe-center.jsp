<%@page buffer="none" session="false" taglibs="c,cms" import="com.alkacon.opencms.v8.newsletter.*, org.opencms.util.*" %>
<c:set var="uri" value="${cms.element.sitePath}" />
<c:set var="formUri" value="${cms:vfs(pageContext).context.uri}" />
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>

<%
CmsNewsletterSubscriptionBean cmsBean = new CmsNewsletterSubscriptionBean(pageContext, request, response, (String)pageContext.getAttribute("uri"));
%>






<cms:formatter var="content" val="value">
<div>
	<%-- Title of the article --%>
	<div>

<%
switch (cmsBean.getAction()) {

case CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE:
	%><h1><%= cmsBean.getConfigText("Subscribe/Headline") %></h1><p><%
	out.print(cmsBean.actionSubscribe());

	if (!cmsBean.isConfirmationEnabled() && cmsBean.isShowSendLastNewsletter()) { %>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.newsletter/pages/includes/form-sendlast.jsp:7fd76711-1f60-11e1-818e-9b778fa0dc42)" %>
	<%}
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE:
	// the unsubscription action
	%><h1><%= cmsBean.getConfigText("Subscribe/Headline") %></h1><p><%
	out.print(cmsBean.actionUnsubscribe());
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_CONFIRMSUBSCRIPTION:
	// the confirm action: subscribe
	%><h1><%= cmsBean.getConfigText("Confirm/Subscribe/Headline") %></h1><p><%
	out.print(cmsBean.actionConfirmSubscribe());
	if (cmsBean.isShowSendLastNewsletter()) { %>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.newsletter/pages/includes/form-sendlast.jsp:7fd76711-1f60-11e1-818e-9b778fa0dc42)" %>
	<%}
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_CONFIRMUNSUBSCRIPTION:
	// the confirm action: unsubscribe
	%><h1><%= cmsBean.getConfigText("Confirm/UnSubscribe/Headline") %></h1><p><%
	out.print(cmsBean.actionConfirmUnsubscribe());
	%></p><%
break;

case CmsNewsletterSubscriptionBean.ACTION_SENDLASTNEWSLETTER:
	// the action: send last newsletter
	%><h1><%= cmsBean.getConfigText("Subscribe/SendLast/Headline") %></h1><p><%
	out.print(cmsBean.actionSendLastNewsletter());
	%></p><%
break;

default:
	// the initial newsletter subscription / unsubscription page with the form
	%>
<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<h3><%= cmsBean.key("v8.newsletter.newNewsletterSubscription") %></h3>
	</c:when>
	<c:otherwise>
		<h1><%= cmsBean.getConfigText("Subscribe/Headline") %></h1>
		<%= cmsBean.getConfigText("Subscribe/Text") %>
	</c:otherwise>
</c:choose>
	
	<%
	
	if (!cmsBean.isValid()) {
		%>
		<p class="newslettersubscription-error"><%= cmsBean.key("validation.alknewsletter.error.headline") %></p>
		<ul class="newslettersubscription-error">
		<%= cmsBean.getValidationErrorsHtml("li") %>
		</ul>
		<%
	}

	%>

	<p>
	<form name="subscription" class="newslettersubscription-form" action="<%= cmsBean.link(cmsBean.getRequestContext().getUri()) %>" method="post">
		<div class="newslettersubscription-radio">
			<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE  %>"<% if (cmsBean.getCheckedAction() <= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE) { %> checked="checked"<% } %> />
			<%= cmsBean.key("form.alknewsletter.subscribe") %>
		</div>
		<div class="newslettersubscription-radio">
			<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE  %>"<% if (cmsBean.getCheckedAction() == CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE) { %> checked="checked"<% } %> />
			<%= cmsBean.key("form.alknewsletter.unsubscribe") %>
		</div>
		<div class="newslettersubscription-email">
			<%= cmsBean.key("form.alknewsletter.email") %>
			<input type="text" name="email" class="newslettersubscription-email" value="<%= cmsBean.getEmail() %>" />
		</div>
		<div class="newslettersubscription-buttons">
			<input type="submit" value=" <%= cmsBean.key("form.alknewsletter.buttonok") %> " />&nbsp;&nbsp;<input type="reset" value=" <%= cmsBean.key("form.alknewsletter.buttonreset") %> " />
		</div>
	</form>
	</p>
	
	
<%
}
%>


	</div>
</div>

</cms:formatter>	