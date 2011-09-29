<%@page session="false" taglibs="c,cms,fmt" %><%

%>

<fmt:setLocale value="${cms.locale}" />
<fmt:bundle basename="com.alkacon.opencms.v8.commons.workplace">

<cms:formatter var="listbox">
	<%-- Set the image position --%>
	<c:set var="imgpos"><cms:elementsetting name="imgalign" default="${listbox.value['PositionImage']}" /></c:set>
</cms:formatter>

<c:choose>
	<c:when test="${cms.element.settings.showbox == 'true'}">
		<div class="box ${cms.element.settings.boxschema}">
			<%-- Title of the list box --%>
			<c:if test="${cms.element.settings.showtitle == 'true'}"><h4><c:out value="${listbox.value['Title']}" escapeXml="false" /></h4></c:if>
			<div class="boxbody">
	</c:when>
	<c:otherwise>
		<div class="paragraph">
			<%-- Title of the list box --%>
			<c:if test="${cms.element.settings.showtitle == 'true'}"><h1><c:out value="${listbox.value['Title']}" escapeXml="false" /></h1></c:if>
			<div>
	</c:otherwise>
</c:choose>

		<%-- Text of the list box --%>
		<c:if test="${listbox.value['Text'].isSet}">
			<div class="boxbody_listentry">
				<c:out value="${listbox.value['Text']}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:choose>
		
			<c:when test="${cms.edited || cms.element.inMemoryOnly}">
				<div style="border: 2px solid red; padding: 10px;">
					<fmt:message key="v8.list.edited" />
				</div>
			</c:when>
			<c:otherwise>
				<%-- Entries of the list box --%>
				<cms:contentload collector="singleFile" param="${listbox.value['Type']}" >
					<cms:contentaccess var="content" />
					<c:choose>
						<c:when test="${content.value.FunctionProvider.isSet}">
							<cms:include file="${content.value.FunctionProvider}">
								<c:forEach var="parameter" items="${content.valueList.Parameter}">
									<cms:param name="${parameter.value.Key}" value="${parameter.value.Value}" />
								</c:forEach>
								<c:if test="${listbox.value['Count'].isSet}">
									<cms:param name="count" value="${listbox.value['Count']}" />
								</c:if>
								<c:if test="${listbox.value['Folder'].isSet}">
									<cms:param name="folder" value="${listbox.value['Folder']}" />
								</c:if>
							</cms:include>
						</c:when>
						<c:otherwise>
							<div style="border: 2px solid red; padding: 10px;">
								<fmt:message key="v8.list.jsp" />
							</div>
						</c:otherwise>
					</c:choose>
					
				</cms:contentload>
			</c:otherwise>
		</c:choose>

	</div>

</div>
</fmt:bundle>