<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<div class="comment_entry" >
	<div class="comment_header" >
		<div class="comment_title">
			<c:out value="${comment.field['subject']}"  />
		</div>
		<div class="comment_subtitle">
			<fmt:message key="comment.header.view.2" >
				<fmt:param value="${comment.field['name']}" />
				<fmt:param value="${date}" />
			</fmt:message>
		</div>
	</div>
	<div class="comment_body" >
		<c:out value="${comment.field['comment']}"  escapeXml="false" />
	</div>
</div>