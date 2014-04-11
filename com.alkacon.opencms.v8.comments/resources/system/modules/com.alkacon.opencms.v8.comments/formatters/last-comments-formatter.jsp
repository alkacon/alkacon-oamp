<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<%@page import="java.util.List, java.util.Arrays" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="com.alkacon.opencms.v8.comments.formatters">
	<cms:formatter var="content">
		<div>
			<c:choose>
				<c:when test="${cms.element.inMemoryOnly}"><fmt:message key="commentlist.memoryonly" /></c:when>
				<c:otherwise>
					<c:set var="formId">${content.value.FormId}</c:set>
					<c:set var="maxComments">
						<c:choose>
							<c:when test="${content.value.MaxNumber.exists}">${content.value.MaxNumber}</c:when>
							<c:otherwise>-1</c:otherwise>
						</c:choose>	
					</c:set>
					<jsp:useBean id="commentCollector" class="com.alkacon.opencms.v8.formgenerator.collector.CmsFormCollectorBean">
						<% commentCollector.init(pageContext, request, response); %>
						<jsp:setProperty name="commentCollector" property="formId" value="${formId}" />
						<jsp:setProperty name="commentCollector" property="numForms" value="${maxComments}" />
						<c:set var="comments" value="${commentCollector.formDataSets}" />
						<div id="commentbox" class="commentbox">
							<div class="cmtHeader">
								${content.value.Title}
							</div>
							<c:set var="commentNum" value="${fn:length(comments)}" />
							<c:choose>
								<c:when test="${commentNum < 1}">
									<div>
										<fmt:message key="commentlist.nocomment" />No comments available.
									</div>
								</c:when>
								<c:otherwise>
									<c:set var="fields" value="${fn:split(content.value.Fields, ',')}" />
									<% List<String> fields = Arrays.asList((String[])pageContext.getAttribute("fields")); %>
									<c:set var="showUser" value='<%= fields.contains("name") || fields.contains("username") || fields.contains("email") %>' />
									<c:set var="showCommentDetails" value='<%= fields.contains("subject") || fields.contains("comment") %>' />
									<c:set var="showCommentMeta" value='<%= fields.contains("locale") || fields.contains("ipaddress") || fields.contains("creationdate") %>' />
									<c:forEach var="comment" items="${comments}">
										<div class="cmtCommentEntry">
											<div class="cmtCommentHeader">
												<c:if test="${showUser}">
													<div class="cmtCommentTitle">
														<c:choose>
															<c:when test='<%= fields.contains("name") %>'>
																<c:choose>
																	<c:when test='<%= fields.contains("email") %>'>
																		<a href='mailto:${comment.fields["email"]}'>${comment.fields["name"]}</a>
																	</c:when>
																	<c:otherwise>
																		${comment.fields["name"]}
																	</c:otherwise>
																</c:choose>
																<c:if test='<%= fields.contains("username") %>'> (${comment.fields["username"]})</c:if>
															</c:when>
															<c:when test='<%= fields.contains("username") %>'>
																<c:choose>
																	<c:when test='<%= fields.contains("email") %>'>
																		<a href='mailto:${comment.fields["email"]}'>${comment.fields["username"]}</a>
																	</c:when>
																	<c:otherwise>
																		${comment.fields["username"]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<a href='mailto:${comment.fields["email"]}'>${comment.fields["email"]}</a>
															</c:otherwise>
														</c:choose>											
													</div>
													<fmt:message key="commentlist.madecommenton1" />							
												</c:if>
												<div class="cmtCommentTitle">
													<a href="${comment.uri}">${comment.title}</a>
												</div>
												<c:if test="${showUser}"><fmt:message key="commentlist.madecommenton2" /></c:if>
											</div>
											<c:if test="${showCommentDetails}">
												<div>
													<c:if test='<%= fields.contains("subject") %>'>
														<strong><fmt:message key="commentlist.subject" />: ${comment.fields["subject"]}</strong><br>
													</c:if>
													<c:if test='<%= fields.contains("comment") %>'>
														<fmt:message key="commentlist.comment" />: ${comment.fields["comment"]}
													</c:if>
												</div>
											</c:if>
											<c:if test="${showCommentMeta}">
												<div class="cmtCommentManager">
													<fmt:message key="commentlist.created" />&nbsp;
													<c:if test='<%= fields.contains("creationdate") %>'>
														<fmt:message key="commentlist.at" />&nbsp;<fmt:formatDate value="${cms:convertDate(comment.dateCreated)}" dateStyle="SHORT" timeStyle="SHORT" type="both" />&nbsp;
													</c:if>
													<c:if test='<%= fields.contains("locale") %>'>
														<fmt:message key="commentlist.in" />&nbsp;${comment.fields["locale"]}&nbsp;
													</c:if>
													<c:if test='<%= fields.contains("ipaddress") %>'>
														<fmt:message key="commentlist.fromip" />&nbsp;${comment.fields["ipaddress"]}
													</c:if>
												</div>
											</c:if>
										</div>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</div>
					</jsp:useBean>						
				</c:otherwise>
			</c:choose>
		</div>
	</cms:formatter>
</cms:bundle>