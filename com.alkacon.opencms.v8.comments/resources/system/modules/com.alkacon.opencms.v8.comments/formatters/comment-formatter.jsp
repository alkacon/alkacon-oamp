<%@ page import="com.alkacon.opencms.v8.comments.*,org.opencms.jsp.util.*,org.opencms.main.*, java.util.Map"%>
<%@ page import="org.opencms.workplace.CmsWorkplace"%>
<%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%@ taglib
	prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<cms:formatter var="content">
	<fmt:setLocale value="${cms.workplaceLocale}" />
	<c:set var="isOnline"
		value="${cms.requestContext.currentProject.onlineProject}" />
	<fmt:bundle basename="com.alkacon.opencms.v8.comments.workplace">
	<c:set var="notVisibleMessage">
		<c:choose>
			<c:when test="${empty content.value.ConfigUri.stringValue}"><fmt:message key="warning.no_config" /></c:when>
			<c:when test="${cms.edited}">
				<fmt:message key="warning.being_edited" />
				<c:choose>
					<c:when test="${cms.element.settings.visibility=='directview'}">
						<br />
						<fmt:message key="warning.direct_view_only" />
					</c:when>
					<c:when	test="${cms.element.settings.visibility=='detailview'}">
						<br />
						<fmt:message key="warning.detail_view_only" />
					</c:when>
				</c:choose>
			</c:when>
			<c:when	test="${cms.detailRequest && cms.element.settings.visibility=='directview'}">
				<fmt:message key="warning.direct_view_only" />
			</c:when>
			<c:when	test="${not cms.detailRequest && cms.element.settings.visibility=='detailview'}">
				<fmt:message key="warning.detail_view_only" />
			</c:when>
		</c:choose>
	</c:set>
	</fmt:bundle>
	<c:choose>
		<c:when test="${not empty notVisibleMessage}">
			<c:if test="${not isOnline}">
				<div style="border: 1px solid red;">
					<c:if test="${not empty content.value.Title.stringValue}"><h3>${content.value.Title.stringValue}</h3></c:if>
					<p>${notVisibleMessage}</p>
				</div>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:set var="configUri" value="${content.value.ConfigUri.stringValue}" />
			<c:set var="cmturi" scope="request">
				<c:choose>
					<c:when test="${cms.detailRequest}">${cms.detailContentSitePath}</c:when>
					<c:otherwise>${cms.requestContext.uri}</c:otherwise>
				</c:choose>
			</c:set>
			<c:set var="formid">
				<c:if test="${!content.value.FormId.isEmptyOrWhitespaceOnly}">${content.value.FormId}</c:if>
			</c:set><%
				Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(pageContext.getAttribute("formid").toString());
			    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(
											pageContext, request, response,
											(String) pageContext.getAttribute("configUri"), dynamicConfig);
									pageContext.setAttribute("alkaconCmt", alkaconCmt);
			%><div><div id="commentbox" class="commentbox">
				<fmt:setLocale value="${cms.requestContext.locale}"/>
				<cms:bundle basename="com.alkacon.opencms.v8.comments.formatters">
					<c:set var="formid">${alkaconCmt.config.formId}</c:set>
					<c:set var="minimized"><cms:elementsetting name="minimized" default="${content.hasValue.Minimized ? content.value.Minimized.stringValue : ''}"/></c:set>
					<c:set var="list"><cms:elementsetting name="list" default="${content.hasValue.List ? content.value.List.stringValue : ''}"/></c:set>
					<c:set var="security"><cms:elementsetting name="security" default="${content.hasValue.Security ? content.value.Security.stringValue : ''}"/></c:set>
					<c:set var="allowreplies"><cms:elementsetting name="allowreplies" default="${content.hasValue.AllowReplies ? content.value.AllowReplies : alkaconCmt.config.allowReplies}"/></c:set>
					<div class="cmtLoading"></div>
					<c:set var="url">
						<c:choose>
							<c:when test="${!minimized == 'false' && (minimized == 'true' || (content.hasValue.Minimized && content.value.Minimized.stringValue=='true') || not alkaconCmt.maximized)}">%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_header.jsp:56328ff3-15df-11e1-aeb4-9b778fa0dc42)</c:when>
							<c:otherwise>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_list.jsp:5639bbed-15df-11e1-aeb4-9b778fa0dc42)</c:otherwise>
						</c:choose>
					</c:set>
					<script type='text/javascript'>
						<c:choose>
						<c:when test="${!empty alkaconCmt.config.styleSheet}" >
						load_script('<cms:link>${alkaconCmt.config.styleSheet}</cms:link>', 'css');
						</c:when>
						<c:otherwise>
						load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/resources/comments.css:56708391-15df-11e1-aeb4-9b778fa0dc42)</cms:link>', 'css');
						</c:otherwise>
						</c:choose>
					     /**
					      * Insert translated strings as inline javascript code          
					      **/
					     var colorboxConfig_comments = {
					             close: '<fmt:message key="comment.image.close" />',
					             next: '<fmt:message key="comment.image.next" />',
					             previous: '<fmt:message key="comment.image.prev" />',
					             current: '<fmt:message key="comment.image.count" />'
					           };
					 	$.ajax({
						    type: "POST", 
						    url: "<cms:link>${url}</cms:link>", 
						    data: { 
						        cmturi:"${alkaconCmt.uri}", 
						        configUri: "${configUri}", 
						        title:"${content.hasValue.Headline ? content.value.Headline.stringValue : ''}",
						        cmtminimized:"${minimized}",
						        cmtlist:"${list}",
						        cmtsecurity:"${security}",
						        cmtformid: "${formid}",
								cmtallowreplies: "${allowreplies}",
						        __locale: "${cms.requestContext.locale}"
						    },
						    success: function(html){ 
								$("#commentbox").html(html);
								$('a.cmt_thickbox').colorbox(colorboxConfig_comments); //pass where to apply thickbox
								imgLoader = new Image(); // preload image
								imgLoader.src = '<%=CmsWorkplace.getSkinUri()%>jquery/css/thickbox/loading.gif';
							},
						    error: function(){ $("#commentbox").html(""); }
						});
						$(document).bind('cbox_closed', function() {
							$('a.cmt_thickbox').colorbox(colorboxConfig_comments); //pass where to apply thickbox
							imgLoader = new Image(); // preload image
							imgLoader.src = '<%=CmsWorkplace.getSkinUri()%>jquery/css/thickbox/loading.gif';
						});
					</script>
				</cms:bundle>
			</div></div>
		</c:otherwise>
	</c:choose>
</cms:formatter>