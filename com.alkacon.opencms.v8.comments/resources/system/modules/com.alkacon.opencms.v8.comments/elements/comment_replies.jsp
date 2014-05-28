<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map, com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean" %>
<%@ page taglibs="c,cms,fn,fmt" %>
<%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
	CmsCommentStringTemplateHandler templateHandler = new CmsCommentStringTemplateHandler(alkaconCmt);
%>
<c:set var="boxColor"><c:out value="${param.cmtcolor}" default="default" /></c:set>

<jsp:useBean id="alkaconReplies" class="com.alkacon.opencms.v8.comments.CmsRepliesAccessBean" />
<c:choose>
	<c:when test="${param.userCanManage == 'true'}">
		<c:forEach var="comment" items="${alkaconReplies.replies[param.entryId]}" >
			<%  CmsFormDataBean comment = (CmsFormDataBean) pageContext.getAttribute("comment");
				String boxColor = (String)pageContext.getAttribute("boxColor");
			%>
			<%= templateHandler.buildReplyManagerHtml(comment,boxColor) %>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:forEach var="comment" items="${alkaconReplies.replies[param.entryId]}" >
			<%  CmsFormDataBean comment = (CmsFormDataBean) pageContext.getAttribute("comment");
				String boxColor = (String)pageContext.getAttribute("boxColor");
			%>
			<%= templateHandler.buildReplyViewHtml(comment,boxColor) %>
		</c:forEach>
	</c:otherwise>
</c:choose>