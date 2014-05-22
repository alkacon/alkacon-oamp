<div class="cmtCommentReplyOptions">
	<div class="cmtCommentCountReplies">
		<c:set var="countReplies" value="${alkaconReplies.countReplies[comment.entryId]}" />
		<c:set var="disabled"><c:if test="${countReplies == 0}">disabled='disabled'</c:if></c:set>
		<button class="btn btn-primary btn-xs cmtShowRepliesButton" ${disabled} cmt-comment-entry='${comment.entryId}' cmt-user-can-manage='${alkaconCmt.userCanManage}'>
			<c:choose>
				<c:when test="${countReplies == 0}">
					0 <fmt:message key="manyReplies" />
				</c:when>
				<c:when test="${countReplies == 1}">	
					1 <fmt:message key="oneReply" />
				</c:when>
				<c:otherwise>
					${countReplies} <fmt:message key="manyReplies" />
				</c:otherwise>
			</c:choose>
		</button>			
		<c:if test="${alkaconCmt.userCanPost}">
			<div class="cmtCommentReply">
				<button 
				    class="btn btn-primary btn-xs showFormModal"
					data-toggle="modal"
					data-target="#cmtFormModal"
					title="<fmt:message key="form.message.post" />" 
					cmt-parent-id="${comment.entryId}"
					href="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_form.jsp:562b63fd-15df-11e1-aeb4-9b778fa0dc42)?cmtparentid=${comment.entryId}&cmturi=${param.cmturi}&cmtminimized=${param.cmtminimized}&cmtlist=${param.cmtlist}&cmtsecurity=${param.cmtsecurity}&configUri=${param.configUri}&cmtformid=${param.cmtformid}&__locale=${cms.locale}&width=800&height=530</cms:link>" 
				>
					<fmt:message key="doReply" />
				</button>
			</div>
			<div class="clear"></div>
		</c:if>
	</div>
	<div class="cmtCommentShowReplies" id="cmtCommentShowReplies-${comment.entryId}"></div>
</div>
