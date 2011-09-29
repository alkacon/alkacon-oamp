<%@page session="false" taglibs="c,cms,fmt" %>

<fmt:setLocale value="${cms.locale}" />

<c:set var="listeditable" value="true" />
<c:if test="${param.pageIndex > 1}">
	<c:set var="listeditable" value="false" />
</c:if>
	
<%-- Entries of the list box --%>
<cms:contentload collector="%(param.collectorName)" param="%(param.collectorParam)" editable="${listeditable}" pageSize="%(param.itemsPerPage)" pageIndex="%(param.pageIndex)" pageNavLength="5" >
	<cms:contentaccess var="content" />			
	<cms:contentinfo var="info" />

	<div class="boxbody_listentry">
		<h5><a href="<cms:link baseUri="${param.pageUri}">${content.filename}?uri=${content.filename}</cms:link>">${content.value.Title}</a></h5>
		
		<c:choose>
			<c:when test="${info.resultSize > 0}">
				<p>
					<a href="<cms:link baseUri="${param.pageUri}">${content.filename}?uri=${content.filename}</cms:link>"><b>${content.value.Subject}</b></a><br/>
					${cms:trimToSize(cms:stripHtml(content.value.Text), 150)}<br/>
					<small><a href="<cms:link baseUri="${param.pageUri}">${content.filename}?uri=${content.filename}</cms:link>">Mehr...</a></small>
				</p>
			</c:when>
			<c:otherwise>
				<!-- Keine Newsletter gefunden -->
			</c:otherwise>
		</c:choose>
	</div>

</cms:contentload>