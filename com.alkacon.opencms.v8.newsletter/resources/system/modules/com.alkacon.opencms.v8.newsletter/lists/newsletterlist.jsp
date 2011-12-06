<%@ page taglibs="c,cms" %>

<c:choose>
	<c:when test="${cms.container.width < 300}">
		<cms:include file="%(link.weak:/system/modules/com.alkacon.opencms.v8.newsletter/lists/newsletterlist-side.jsp)" />
	</c:when>
	<c:otherwise>
		<cms:include file="%(link.weak:/system/modules/com.alkacon.opencms.v8.newsletter/lists/newsletterlist-center.jsp)" />
	</c:otherwise>
</c:choose>