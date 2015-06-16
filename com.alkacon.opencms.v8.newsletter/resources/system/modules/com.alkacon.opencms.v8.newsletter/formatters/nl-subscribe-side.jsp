<%@page buffer="none" session="false" taglibs="c,cms" import="com.alkacon.opencms.v8.newsletter.*, org.opencms.util.*" %>
<c:set var="uri" value="${cms.element.sitePath}" />
<c:set var="formUri" value="${cms:vfs(pageContext).context.uri}" />
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>

<%
CmsNewsletterSubscriptionBean cmsBean = new CmsNewsletterSubscriptionBean(pageContext, request, response, (String)pageContext.getAttribute("uri"));
%>


<cms:formatter var="content" val="value">
<div class="box ${cms.element.settings.boxschema}">
	<%-- Title of the article --%>
<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
			<h4><%= cmsBean.key("v8.newsletter.newNewsletterSubscription") %></h4>
	</c:when>
	<c:otherwise>
		<c:if test="${cms.element.settings.hidetitle ne 'true'}">
			<h4>${value.Title}</h4>
		</c:if>	
	</c:otherwise>
</c:choose>
	
	<div class="boxbody">

<%
switch (cmsBean.getAction()) {

case CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE:
	%><div class="headline"><h2><%= cmsBean.getConfigText("Subscribe/Headline") %></h2></div><div class="alert alert-info" role="alert"><%
	out.print(cmsBean.actionSubscribe());

	if (!cmsBean.isConfirmationEnabled() && cmsBean.isShowSendLastNewsletter()) { %>
		</div><div>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.newsletter/pages/includes/form-sendlast.jsp:7fd76711-1f60-11e1-818e-9b778fa0dc42)" %>
	<%}
	%></div><%
break;

case CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE:
	// the unsubscription action
	%><div class="headline"><h2><%= cmsBean.getConfigText("Subscribe/Headline") %></h2></div><div class="alert alert-info" role="alert"><%
	out.print(cmsBean.actionUnsubscribe());
	%></div><%
break;

case CmsNewsletterSubscriptionBean.ACTION_CONFIRMSUBSCRIPTION:
	// the confirm action: subscribe
	%><div class="headline"><h2><%= cmsBean.getConfigText("Confirm/Subscribe/Headline") %></h2></div><div class="alert alert-success" role="alert"><%
	out.print(cmsBean.actionConfirmSubscribe());
	if (cmsBean.isShowSendLastNewsletter()) { %>
		</div><div>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.newsletter/pages/includes/form-sendlast.jsp:7fd76711-1f60-11e1-818e-9b778fa0dc42)" %>
	<%}
	%></div><%
break;

case CmsNewsletterSubscriptionBean.ACTION_CONFIRMUNSUBSCRIPTION:
	// the confirm action: unsubscribe
	%><div class="headline"><h2><%= cmsBean.getConfigText("Confirm/UnSubscribe/Headline") %></h2></div><div class="alert alert-success" role="alert"><%
	out.print(cmsBean.actionConfirmUnsubscribe());
	%></div><%
break;

case CmsNewsletterSubscriptionBean.ACTION_SENDLASTNEWSLETTER:
	// the action: send last newsletter
	%><div class="headline"><h2><%= cmsBean.getConfigText("Subscribe/SendLast/Headline") %></h2></div><div class="alert alert-success" role="alert"><%
	out.print(cmsBean.actionSendLastNewsletter());
	%></div><%
break;

default:
	// the initial newsletter subscription / unsubscription page with the form
	%>
<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<div class="headline"><h2><%= cmsBean.key("v8.newsletter.newNewsletterSubscription") %></h2></div>
	</c:when>
	<c:otherwise>
		<div class="headline"><h2><%= cmsBean.getConfigText("Subscribe/Headline") %></h2></div>
		<%= cmsBean.getConfigText("Subscribe/Text") %>
	</c:otherwise>
</c:choose>

	<%

	if (!cmsBean.isValid()) {
		%>
		<div class="alert alert-danger newslettersubscription-error" role="alert"><strong><%= cmsBean.key("validation.alknewsletter.error.headline") %></strong>
		<ul class="newslettersubscription-error">
		<%= cmsBean.getValidationErrorsHtml("li") %>
		</ul>
		</div>
		<%
	}

	%>

	<p>
	<form name="subscription" class="newslettersubscription-form" action="<%= cmsBean.link(cmsBean.getRequestContext().getUri()) %>" method="post">
		<div class="radio newslettersubscription-radio">
			<label>
				<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE  %>"<% if (cmsBean.getCheckedAction() <= CmsNewsletterSubscriptionBean.ACTION_SUBSCRIBE) { %> checked="checked"<% } %> />
				<%= cmsBean.key("form.alknewsletter.subscribe") %>
			</label>
		</div>
		<div class="radio newslettersubscription-radio">
			<label>
				<input type="radio" name="action" class="newslettersubscription-radio" value="<%= CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE  %>"<% if (cmsBean.getCheckedAction() == CmsNewsletterSubscriptionBean.ACTION_UNSUBSCRIBE) { %> checked="checked"<% } %> />
				<%= cmsBean.key("form.alknewsletter.unsubscribe") %>
			</label>
		</div>
		<div class="form-group newslettersubscription-email">
			<label for="nlSubscriptionEmail"><%= cmsBean.key("form.alknewsletter.email") %></label>
			<input type="text" name="email" id="nlSubscriptionEmail" class="form-control newslettersubscription-email" value="<%= cmsBean.getEmail() %>" />
		</div>
		<div class="newslettersubscription-buttons">
			<button type="submit" class="btn btn-default"><%= cmsBean.key("form.alknewsletter.buttonok") %></button>
		</div>
	</form>
	</p>

<%
}
%>

	</div>
</div>

</cms:formatter>	