<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><cms:secureparams />
<%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
pageContext.setAttribute("alkaconCmt", alkaconCmt);
if (!alkaconCmt.isUserCanView() && !alkaconCmt.isUserCanManage() && !alkaconCmt.isUserCanPost() && !alkaconCmt.getConfig().isOfferLogin()) {
	return;
}
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<cms:bundle basename="${alkaconCmt.resourceBundle}">
	<!-- start: post form link -->
	<div class="cmtPostOptions">
		<c:choose>
			<c:when test="${alkaconCmt.userCanPost}">
				<button  class="showFormModal btn btn-default pull-right"
					data-toggle="modal"
					data-target="#cmtFormModal"
					title="<fmt:message key='form.message.post' />" 
				>
					<fmt:message key="post.0" />
				</button>
			</c:when>
			<c:when test="${alkaconCmt.guestUser && alkaconCmt.config.offerLogin}">
				<button 
				    class="btn btn-default pull-right showLoginModal"
					title="<fmt:message key="login.message.title" />" 
	 			    data-toggle="modal" 
				    data-target="#cmtLoginModal"
				>
					<fmt:message key="post.user.login.0" />
				</button>
			</c:when>
		</c:choose>
		<div class="clearfix"></div>
	</div>
	<!-- include form-modal -->
	<div class="modal fade" id="cmtFormModal" tabindex="-1" role="dialog" aria-labelledby="cmtFormModalLabel" aria-hidden="true">
		<div class="cmtFormModalDialog modal-dialog">
			<div class="cmtFormModalContent modal-content"></div>
		</div>
	</div>
	<!-- end: post form link -->
	<c:if test="${alkaconCmt.userCanView}">
		<!-- start: comments list -->
		<div id="comments">
			<%-- include of comment_innerlist.jsp happens via JavaScript --%>
		</div>
		<!-- end: comments list -->
	</c:if>
</cms:bundle>