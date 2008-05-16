<div class="comment_entry" >
<div class="comment_header" >
<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<fmt:message key="comment.header.view.2" >
	<fmt:param value="${comment.allFields['name']}" />
	<fmt:param value="${date}" />
</fmt:message>
</div>
<div class="comment_body" >
<c:out value="${comment.allFields['comment']}"  escapeXml="false" />
</div>
</div>