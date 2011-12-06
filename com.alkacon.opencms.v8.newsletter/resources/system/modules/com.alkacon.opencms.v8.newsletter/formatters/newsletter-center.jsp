<%@ page session="false" taglibs="c,cms,fmt" import="com.alkacon.opencms.v8.commons.*,com.alkacon.opencms.v8.newsletter.*" %>

<fmt:setLocale value="${cms.locale}" />
<c:choose>
<c:when test="${empty param.uri}">
	<fmt:bundle basename="com.alkacon.opencms.v8.newsletter.workplace">
	
	<div class="paragraph">
	<cms:formatter var="content" val="value">
	
	<title>${value.Subject}</title>
	
	<div class="newsletter">
	
	<h1><fmt:message key="preview.alknewsletter.headline" /></h1>
	
	<div class="field">
		<div class="fieldlabel"><fmt:message key="label.AlkaconV8NewsletterMail.From" /></div>
		<div class="fieldtext">${value.From}</div>
	</div>
	
	<c:if test="${value.BCC.isSet}">
		<div class="field">
			<div class="fieldlabel"><fmt:message key="label.AlkaconV8NewsletterMail.BCC" /></div>
			<div class="fieldtext">${value.BCC}</div>
		</div>
	</c:if>
	
	<div class="field">
		<div class="fieldlabel"><fmt:message key="label.AlkaconV8NewsletterMail.Subject" /></div>
		<div class="fieldtext"><strong>${value.Subject}</strong></div>
	</div>
	
	<c:choose>
		<c:when test="${cms.element.inMemoryOnly}">
			<div style="border: 2px solid red; padding: 10px;">
				<fmt:message key="v8.newsletter.edited" />
			</div>
		</c:when>
		<c:otherwise>
			<iframe name="email" src="<cms:link>newsletter-center-detail.jsp?nluri=${content.filename}&__locale=${cms.locale}</cms:link>" frameborder="0" ></iframe>
		</c:otherwise>
	</c:choose>
	
	</div>
	
	</cms:formatter>
	
	</div>
	
	</fmt:bundle>
</c:when>

<c:otherwise>
	<fmt:bundle basename="org/opencms/frontend/templateone/modules/workplace">
	
	<c:set var="detailFile" value="${param.uri}" />
	
	<div class="paragraph">
	<%
		CmsTemplateBase cms = new CmsTemplateBase(pageContext, request, response);
		I_CmsNewsletterMailData mailData = CmsNewsletterManager.getMailData(cms, cms.getCmsObject().readGroup(org.opencms.main.OpenCms.getDefaultUsers().getGroupGuests()), (String)pageContext.getAttribute("detailFile"));
		out.print(mailData.getEmailContentPreview(true));
	%>
	</div>
	</fmt:bundle>
</c:otherwise>
</c:choose>