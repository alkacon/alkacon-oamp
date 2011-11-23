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
	<div class="cmtCommentBody">
		<c:set var="commentcontent" value="${comment.field['comment']}" />
		<c:set var="testcontent" value="${fn:toLowerCase(fn:replace(comment.field['comment'], ' ', ''))}" />
		<c:if test="${fn:indexOf(testcontent, '<script') != - 1 || fn:indexOf(testcontent, '<form') != - 1 || fn:indexOf(testcontent, '<input') != - 1}">
			<c:set var="commentcontent">${cms:stripHtml(commentcontent)}</c:set>
		</c:if>
		<c:out value="${commentcontent}" escapeXml="false" />
	</div>
</div>