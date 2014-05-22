<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><cms:secureparams /><%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
    pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<cms:bundle basename="${alkaconCmt.resourceBundle}" >
	<div class="cmtHeader">
		<c:choose>
			<c:when test="${alkaconCmt.userCanManage}">
				<button class="btn btn-primary cmtLoadComments">
					<c:choose>
						<c:when test="${alkaconCmt.config.moderated}">
							<fmt:message key="header.user.manage.2" >
								<fmt:param value="${fn:escapeXml(alkaconCmt.countApprovedComments)}" />
								<fmt:param value="${fn:escapeXml(alkaconCmt.countNewComments)}" />
							</fmt:message>
						</c:when>
						<c:otherwise>
							<fmt:message key="header.user.manage.1" >
								<fmt:param value="${fn:escapeXml(alkaconCmt.countComments)}" />
							</fmt:message>
						</c:otherwise>
					</c:choose>
				</button>
			</c:when>
			<c:when test="${alkaconCmt.userCanPost}">
				<button class="btn btn-primary cmtLoadComments">
					<fmt:message key="header.user.post.1" >
						<fmt:param value="${fn:escapeXml(alkaconCmt.countComments)}" />
					</fmt:message>
				</button>
			</c:when>
			<c:when test="${alkaconCmt.userCanView}">
				<button class="btn btn-primary cmtLoadComments">
					<fmt:message key="header.user.read.1" >
						<fmt:param value="${fn:escapeXml(alkaconCmt.countComments)}" />
					</fmt:message>
				</button>
			</c:when>
			<c:otherwise>
				<c:if test="${alkaconCmt.guestUser && alkaconCmt.config.offerLogin}">
					<button 
						title="<fmt:message key="login.message.title" />" 
						class="btn btn-primary showLoginModal" 
						data-toggle="modal" 
						data-target="#cmtLoginModal"
						>
						<fmt:message key="header.user.login.1" >
							<fmt:param value="${fn:escapeXml(alkaconCmt.countComments)}" />
						</fmt:message>
					</button>
				</c:if>
			</c:otherwise>
		</c:choose>
	</div>
</cms:bundle>
