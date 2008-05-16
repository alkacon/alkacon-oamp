<c:choose>
	<c:when test="${!cmt.config.moderated}">
		<c:set var="className" value="comment_header" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${comment.state == 0}">
				<c:set var="className" value="comment_new" />
			</c:when>
			<c:when test="${comment.state == 1}">
				<c:set var="className" value="comment_header" />
			</c:when>
			<c:otherwise>
				<c:set var="className" value="comment_blocked" />
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<div class="comment_entry" >
	<div class="${className}" style="position: relative; height: 20px;">
		<div style="position: absolute; right: 0px;">
			<c:if test="${cmt.config.moderated}">
				<c:if test="${comment.state != 1}">
					<a href="#" id="approve${comment.entryId}" class="action"><img title="<fmt:message key="comment.manager.approve" />" alt="<fmt:message key="comment.manager.approve" />" src='<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/resources/approve.png:ad840730-1dc0-11dd-9741-111d34530985)</cms:link>' width='20' height='20'></a>
				</c:if>
				<c:if test="${comment.state != 2}">
					<a href="#" id="block${comment.entryId}" class="action"><img title="<fmt:message key="comment.manager.block" />" alt="<fmt:message key="comment.manager.block" />" src='<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/resources/block.png:adb158c8-1dc0-11dd-9741-111d34530985)</cms:link>' width='20' height='20'></a>
				</c:if>
			</c:if>
			<a href="#" id="delete${comment.entryId}" class="action"><img title="<fmt:message key="comment.manager.delete" />" alt="<fmt:message key="comment.manager.delete" />" src='<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/resources/delete.png:ada571e4-1dc0-11dd-9741-111d34530985)</cms:link>' width='20' height='20'></a>
		</div>
		<div style="padding-top: 3px;">
			<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
			<fmt:message key="comment.header.view.2" >
				<fmt:param value="${comment.allFields['name']}" />
				<fmt:param value="${date}" />
			</fmt:message>
		</div>
	</div>
	<div class="comment_manager" >
		<fmt:message key="comment.manager.username.1" >
			<fmt:param value="${comment.allFields['username']}" />
		</fmt:message><br>
		<fmt:message key="comment.manager.email.1" >
			<fmt:param value="${comment.allFields['email']}" />
		</fmt:message><br>
		<fmt:message key="comment.manager.ipaddress.1" >
			<fmt:param value="${comment.allFields['ipaddress']}" />
		</fmt:message><br>
		<fmt:message key="comment.manager.count.1" >
			<fmt:param value="${cmt.countByAuthor[comment.allFields['name']]}" />
		</fmt:message>
	</div>
	<div class="comment_body" >
		<c:out value="${comment.allFields['comment']}"  escapeXml="false" />
	</div>
</div>
