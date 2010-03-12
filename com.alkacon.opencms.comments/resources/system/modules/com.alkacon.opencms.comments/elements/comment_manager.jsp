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
					<a href="javascript:doAction('approve','${comment.entryId}');" ><img title="<fmt:message key="comment.manager.approve" />" alt="<fmt:message key="comment.manager.approve" />" src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/approve.png:ad840730-1dc0-11dd-9741-111d34530985)</cms:link>' width='20' height='20'></a>
				</c:if>
				<c:if test="${comment.state != 2}">
					<a href="javascript:doAction('block','${comment.entryId}');" ><img title="<fmt:message key="comment.manager.block" />" alt="<fmt:message key="comment.manager.block" />" src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/block.png:adb158c8-1dc0-11dd-9741-111d34530985)</cms:link>' width='20' height='20'></a>
				</c:if>
			</c:if>
			<a href="javascript:doAction('delete','${comment.entryId}');" ><img title="<fmt:message key="comment.manager.delete" />" alt="<fmt:message key="comment.manager.delete" />" src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/delete.png:ada571e4-1dc0-11dd-9741-111d34530985)</cms:link>' width='20' height='20'></a>
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
		<c:out value="${comment.field['comment']}"  escapeXml="true" />
	</div>
</div>
