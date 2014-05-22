<c:set var="date"><fmt:formatDate value='${cms:convertDate(comment.dateCreated)}' dateStyle='long' timeStyle='short' type='both' /></c:set>
<c:set var="boxColor"><c:out value="${param.cmtcolor}" default="default" /></c:set>
<div class="panel panel-${boxColor}" >
	<div class="panel-heading" >
		<div class="h5">
			<c:out value="${comment.field['subject']}" escapeXml="true" />
		</div>
		<div class="h6">
			<fmt:message key="comment.header.view.2" >
				<fmt:param value="${fn:escapeXml(comment.field['name'])}" />
				<fmt:param value="${fn:escapeXml(date)}" />
			</fmt:message>
		</div>
	</div>
	<div class="panel-body">
		<c:set var="commentcontent" value="${comment.field['comment']}" />
		<c:set var="testcontent" value="${fn:toLowerCase(fn:replace(comment.field['comment'], ' ', ''))}" />
		<c:if test="${fn:indexOf(testcontent, '<script') != - 1 || fn:indexOf(testcontent, '<form') != - 1 || fn:indexOf(testcontent, '<input') != - 1}">
			<c:set var="commentcontent">${cms:stripHtml(commentcontent)}</c:set>
		</c:if>
		<c:out value="${commentcontent}" escapeXml="false" />
		
		<c:if test="${param.cmtallowreplies == 'true'}">
		   <%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_reply_option.jsp:8ecfda3a-c49e-11e3-befc-6306da683c37)" %>
		</c:if>
	</div>
</div>