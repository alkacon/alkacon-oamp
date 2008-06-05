<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<div class="cmtCommentEntry" >
	<div class="cmtCommentHeader" >
		<div class="cmtCommentTitle">
			<c:out value="${comment.field['subject']}"  />
		</div>
		<div class="cmtCommentSubtitle">
			<fmt:message key="comment.header.view.2" >
				<fmt:param value="${comment.field['name']}" />
				<fmt:param value="${date}" />
			</fmt:message>
		</div>
	</div>
	<div class="cmtCommentBody" >
		<c:out value="${comment.field['comment']}"  escapeXml="false" />
	</div>
</div>