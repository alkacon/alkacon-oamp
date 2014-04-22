<c:choose>
	<c:when test="${!alkaconCmt.config.moderated}">
		<c:set var="className" value="cmtCommentApproved" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${comment.state == 0}">
				<c:set var="className" value="cmtCommentNew" />
			</c:when>
			<c:when test="${comment.state == 1}">
				<c:set var="className" value="cmtCommentApproved" />
			</c:when>
			<c:otherwise>
				<c:set var="className" value="cmtCommentBlocked" />
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<div class="cmtCommentEntry" >
	<div class="${className} cmtActionsHeader">
		<div class="cmtActions">
			<c:if test="${alkaconCmt.config.moderated}">
				<c:if test="${comment.state != 1}">
					<a href="javascript:doAction('approve','${comment.entryId}');" ><img title="<fmt:message key="comment.manager.approve" />" alt="<fmt:message key="comment.manager.approve" />" src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/resources/approve.png:56649cab-15df-11e1-aeb4-9b778fa0dc42)</cms:link>' width='20' height='20'></a>
				</c:if>
				<c:if test="${comment.state != 2}">
					<a href="javascript:doAction('block','${comment.entryId}');" ><img title="<fmt:message key="comment.manager.block" />" alt="<fmt:message key="comment.manager.block" />" src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/resources/block.png:566bc89e-15df-11e1-aeb4-9b778fa0dc42)</cms:link>' width='20' height='20'></a>
				</c:if>
			</c:if>
			<a href="javascript:doAction('delete','${comment.entryId}');" ><img title="<fmt:message key="comment.manager.delete" />" alt="<fmt:message key="comment.manager.delete" />" src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/resources/delete.png:5672f493-15df-11e1-aeb4-9b778fa0dc42)</cms:link>' width='20' height='20'></a>
		</div>
		<div class="cmtCommentTitle">
			<c:out value="${comment.field['subject']}" escapeXml="true" />
		</div>
		<div class="cmtCommentSubtitle">
			<fmt:message key="comment.header.view.2" >
				<fmt:param value="${fn:escapeXml(comment.field['name'])}" />
				<fmt:param value="${fn:escapeXml(date)}" />
			</fmt:message>
		</div>
	</div>
	<div class="cmtCommentManager" >
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
	<div class="cmtCommentBody" >
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
