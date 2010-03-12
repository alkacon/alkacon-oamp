<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<div class="cmtCommentEntry" >
	<div class="cmtCommentHeader" >
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
	<div class="cmtCommentBody" >
		<c:out value="${comment.field['comment']}"  escapeXml="true" />
	</div>
</div>