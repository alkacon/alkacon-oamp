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
						<c:if test="${not empty formId}">
							<jsp:setProperty name="commentCollector" property="formId" value="${formId}" />
						</c:if>
						<jsp:setProperty name="commentCollector" property="numForms" value="${maxComments}" />
						<c:set var="comments" value="${commentCollector.formDataSets}" />
						<div id="commentbox" class="commentbox">
							<div class="header red">
								<h1><span class="icon normal white -icon-dialog"></span>${content.value.Title}</h1>
							</div>
							<c:set var="commentNum" value="${fn:length(comments)}" />
							<c:choose>
								<c:when test="${commentNum < 1}">
									<div id="cmtNocomment">
										<fmt:message key="commentlist.nocomment" />
									</div>
								</c:when>
								<c:otherwise>
									<c:set var="fields" value="${fn:split(content.value.Fields, ',')}" />
									<% List<String> fields = Arrays.asList((String[])pageContext.getAttribute("fields")); %>
									<c:set var="showUser" value='<%= fields.contains("name") || fields.contains("username") || fields.contains("email") %>' />
									<c:set var="showCommentDetails" value='<%= fields.contains("subject") || fields.contains("comment") %>' />
									<c:set var="showCommentMeta" value='<%= fields.contains("locale") || fields.contains("ipaddress") || fields.contains("creationdate") %>' />
									<c:forEach var="comment" items="${comments}">
										<div class="item">
										 <div class="comment">
											<p class="commentDate">
												<fmt:formatDate value="${cms:convertDate(comment.dateCreated)}" pattern="MMM" type="date"/>
												<span>
												<fmt:formatDate value="${cms:convertDate(comment.dateCreated)}" pattern="d" type="date"/> 
											</span></p>
											<p class="commentUser">		
												<c:if test="${showUser}">
													<span>
														<c:choose>
															<c:when test='<%= fields.contains("name") %>'>
																<c:choose>
																	<c:when test='<%= fields.contains("email") %>'>
																		<a href='mailto:${comment.fields["email"]}'>${comment.fields["name"]}</a>
																	</c:when>
																	<c:otherwise>
																		<c:set var="name1">${fn:escapeXml(comment.fields["name"])}</c:set>
																		<c:choose>
																			<c:when test='${ fn:contains(name1,"--missing") }'>
																				<c:set var="username1">${fn:escapeXml(comment.fields['username'])}</c:set>
																				<c:set var="path4prf">/shared/.content/wmgprofile/${fn:replace(username1,'@', '_at_')}.prf</c:set>
																				<c:choose>
																				<c:when test="${cms:vfs(pageContext).exists[path4prf]}" >	    
																				    <a href="<cms:link>${path4prf}</cms:link>">
																					<cms:resourceload collector="singleFile" param="${path4prf}">
																						<cms:resourceaccess var="prf" />
																						${prf.property['wmgcontact.firstname']}&nbsp;${prf.property['wmgcontact.lastname']}
																					</cms:resourceload>
																					</a>
																				</c:when>
																				<c:otherwise>
																					${username1}
																				</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:set var="username1">${fn:escapeXml(comment.fields['username'])}</c:set>
																				<c:set var="path4prf">/shared/.content/wmgprofile/${fn:replace(username1,'@', '_at_')}.prf</c:set>
																				<a href="<cms:link>${path4prf}</cms:link>">${comment.fields["name"]}</a>
																			</c:otherwise>
																		</c:choose>
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
													</span>
													<fmt:message key="commentlist.madecommenton1" />							
												</c:if>
												
											</p>
											</div>
												<p>
													<a href="${comment.uri}">${comment.title}</a>
												</p>												
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