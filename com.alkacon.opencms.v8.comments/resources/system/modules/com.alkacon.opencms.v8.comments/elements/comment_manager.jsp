<c:set var="boxColor"><c:out value="${param.cmtcolor}" default="default" /></c:set>
<%-- Use choice to have different box-colors dependent on the state of the comment --%>
<c:choose>
	<c:when test="${!alkaconCmt.config.moderated}">
		<c:set var="className" value="panel-${boxColor}" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${comment.state == 0}">
				<c:set var="className" value="panel-info" />
			</c:when>
			<c:when test="${comment.state == 1}">
				<c:set var="className" value="panel-success" />
			</c:when>
			<c:when test="${comment.state == 2}">
				<c:set var="className" value="panel-danger" />
			</c:when>
			<c:otherwise>
				<c:set var="className" value="panel-default" />
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<div class="panel ${className}" >
	<div class="panel-heading">
		<h5>
			<c:out value="${comment.field['subject']}" escapeXml="true" />
			<span class="pull-right">
				<c:if test="${alkaconCmt.config.moderated}">
					<c:if test="${comment.state != 1}">
						<a class="cmtAction" cmt-action="approve" cmt-comment-entry="${comment.entryId}" cmt-state="${alkaconCmt.state}" href="#" title="<fmt:message key="comment.manager.approve" />"><span class="glyphicon glyphicon-ok text-success"></span></a>
					</c:if>
					<c:if test="${comment.state != 2}">
						<a class="cmtAction" cmt-action="block" cmt-comment-entry="${comment.entryId}" cmt-state="${alkaconCmt.state}" href="#" title="<fmt:message key="comment.manager.block" />"><span class="glyphicon glyphicon-ban-circle text-warning"></span></a>
					</c:if>
				</c:if>
				<a class="cmtAction" cmt-action="delete" cmt-comment-entry="${comment.entryId}" cmt-state="${alkaconCmt.state}" cmt-confirmation-message="<fmt:message key="comment.manager.delete.conf" />" href="#" title="<fmt:message key="comment.manager.delete" />"><span class="glyphicon glyphicon-remove text-danger"></span></a>
			</span>
		</h5>
		<h6>
			<fmt:message key="comment.header.view.2" >
				<fmt:param value="${fn:escapeXml(comment.field['name'])}" />
				<fmt:param value="${fn:escapeXml(date)}" />
			</fmt:message>
		</h6>
	</div>
	<div class="panel-body" >
		<div>
			<fmt:message key="comment.manager.username.1" >
				<fmt:param value="${fn:escapeXml(comment.field['username'])}" />
			</fmt:message><br>
			<fmt:message key="comment.manager.email.1" >
				<fmt:param value="${fn:escapeXml(comment.field['email'])}" />
			</fmt:message><br>
			<fmt:message key="comment.manager.ipaddress.1" >
				<fmt:param value="${fn:escapeXml(comment.field['ipaddress'])}" />
			</fmt:message><br>
			<fmt:message key="comment.manager.count.1" >
				<fmt:param value="${fn:escapeXml(alkaconCmt.countByAuthor[comment.field['username']])}" />
			</fmt:message>
		</div>
		<div>
			<c:set var="commentcontent" value="${comment.field['comment']}" />
			<c:set var="testcontent" value="${fn:toLowerCase(fn:replace(comment.field['comment'], ' ', ''))}" />
			<c:if test="${fn:indexOf(testcontent, '<script') != - 1 || fn:indexOf(testcontent, '<form') != - 1 || fn:indexOf(testcontent, '<input') != - 1}">
				<c:set var="commentcontent">${cms:stripHtml(commentcontent)}</c:set>
			</c:if>
			<c:out value="${commentcontent}" escapeXml="false" />
			<c:if test="${param.cmtallowreplies}">
				<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_reply_option.jsp:8ecfda3a-c49e-11e3-befc-6306da683c37)" %>
			</c:if>
		</div>
	</div>	
</div>
