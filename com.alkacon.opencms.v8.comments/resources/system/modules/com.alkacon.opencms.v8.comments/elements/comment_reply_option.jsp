<div class="cmtCommentReplyOptions">
	<div class="cmtCommentCountReplies">
		<c:set var="countReplies" value="${alkaconReplies.countReplies[comment.entryId]}" />
		<a href="javascript:showReplies(${comment.entryId}, ${alkaconCmt.userCanManage})">
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
		</a>			
		<c:if test="${alkaconCmt.userCanPost}">
			<div class="cmtCommentReply">
				<a 
					title="<fmt:message key="form.message.post" />" 
					href="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_form.jsp:562b63fd-15df-11e1-aeb4-9b778fa0dc42)?cmtparentid=${comment.entryId}&cmturi=${param.cmturi}&cmtminimized=${param.cmtminimized}&cmtlist=${param.cmtlist}&cmtsecurity=${param.cmtsecurity}&configUri=${param.configUri}&cmtformid=${param.cmtformid}&__locale=${cms:vfs(pageContext).requestContext.locale}&width=800&height=530</cms:link>" 
					class="cmt_thickbox" >
					<fmt:message key="doReply" />
				</a>
			</div>
			<div class="clear"></div>
		</c:if>
	</div>
	<div class="cmtCommentShowReplies" id="cmtCommentShowReplies-${comment.entryId}"></div>
</div>
