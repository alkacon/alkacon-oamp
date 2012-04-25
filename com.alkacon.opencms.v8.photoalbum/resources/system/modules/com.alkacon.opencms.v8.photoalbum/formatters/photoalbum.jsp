<%@ page session="false" import="com.alkacon.opencms.v8.photoalbum.CmsPhotoAlbumBean, org.opencms.workplace.*" taglibs="c,cms,fmt,fn"%><%
	CmsPhotoAlbumBean album = new CmsPhotoAlbumBean(pageContext, request, response);
	pageContext.setAttribute("album", album);
%>

<cms:formatter var="content">
<div class="album-wrapper">
<c:set var="albumid">${fn:replace(cms.element.id.stringValue,"-","_")}</c:set>
<fmt:setLocale value="${cms.locale}" />
<fmt:bundle basename="com.alkacon.opencms.v8.photoalbum.frontend">
<c:choose>
<c:when test="${cms.edited || cms.element.inMemoryOnly}">
		<p style="color:red; font-weight: bold;">
			<fmt:message key="photoalbum.javascript_disabled" />
		</p>
	</c:when>
<c:otherwise>

<c:set var="currentPage"><c:out value="${param.page}" default="1"/></c:set>
	<c:set var="thumbs" value="${content.value['Thumbs']}" />
	<c:set var="imageCount" value="${fn:length(album.readImages[content.value['VfsFolder'].stringValue])}" />
	<c:set var="paginationClass">album-pagination<c:if test="${content.value['ShowNavigationBorder'].exists && content.value['ShowNavigationBorder']=='true' }"> pagination-border</c:if></c:set>
	<c:set var="hasPagination" value="${!(empty thumbs.value['ItemsPerPage'].stringValue) && thumbs.value['ItemsPerPage'].stringValue > 0}" />

	<%-- Quality --%>
	<c:set var="quality" value="50" />
	<c:if test="${thumbs.value['HighQuality'] == 'true'}">
		<c:set var="quality" value="90" />
	</c:if>
	<%-- Title --%>
	<c:if test="${!content.value['Title'].isEmptyOrWhitespaceOnly}">
		<h1><c:out value="${content.value['Title']}" /></h1>
	</c:if>
	
	<%-- Pagination above top text --%>
	<c:if test="${hasPagination && content.value['NavigationPosition'] == 't_a'}">
		<div class="pagination_container" style="text-align: ${content.value['AlignNavigation']};">
			<div id="pagination_${albumid}" class="${paginationClass}"></div>
		</div>
	</c:if>

	<%-- Top Text --%>
	<c:if test="${!thumbs.value['TextTop'].isEmptyOrWhitespaceOnly}">
		<div><c:out value="${thumbs.value['TextTop']}" escapeXml="false"/></div>
	</c:if>	

	<%-- Pagination below top text --%>
	<c:if test="${hasPagination && content.value['NavigationPosition'] == 't_b'}">
		<div class="pagination_container" style="text-align: ${content.value['AlignNavigation']};">
			<div id="pagination_${albumid}" class="${paginationClass}"></div>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${empty content.value['VfsFolder'].stringValue}">
			<fmt:message key="photoalbum.no_gallery_folder" />
		</c:when>
		<c:otherwise>
			<div id="album_pages_${albumid}">
				<div id="album_page_${albumid}_1">
					<%-- Show the images in the given vfs path --%>
					<cms:include file="../elements/albumpage.jsp">
						<cms:param name="vfsFolder" value="${content.value['VfsFolder'].stringValue}" />
						<cms:param name="background" value="${thumbs.value['Background']}" />
						<cms:param name="size" value="${thumbs.value['Size']}" />
						<cms:param name="quality" value="${quality}" />
						<cms:param name="filter" value="${thumbs.value['Filter']}" />
						<cms:param name="alignTitle" value="${thumbs.value['AlignTitle']}" />
						<cms:param name="showTitle" value="${thumbs.value['ShowTitle']}" />
						<cms:param name="showResourceNameAsTitle" value="${content.value['ShowResourceNameAsTitle']}" />
						<cms:param name="page" value="1" />
						<cms:param name="itemsPerPage" value="${thumbs.value['ItemsPerPage']}" />
						<cms:param name="maxImageSize" value="${content.value['MaxImageSize']}" />
						<cms:param name="albumid" value="${albumid}" />
					</cms:include>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

	<%-- Pagination above bottom text --%>
	<c:if test="${hasPagination && content.value['NavigationPosition'] == 'b_a'}">
		<div class="pagination_container" style="text-align: ${content.value['AlignNavigation']};">
			<div id="pagination_${albumid}" class="${paginationClass}"></div>
		</div>
	</c:if>

	<%-- Bottom Text --%>
	<c:if test="${!thumbs.value['TextBottom'].isEmptyOrWhitespaceOnly}">
		<div><c:out value="${thumbs.value['TextBottom']}" escapeXml="false"/></div>
	</c:if>
	
	<%-- Pagination below bottom text --%>
	<c:if test="${hasPagination && content.value['NavigationPosition'] == 'b_b'}">
		<div class="pagination_container" style="text-align: ${content.value['AlignNavigation']};">
			<div id="pagination_${albumid}" class="${paginationClass}"></div>
		</div>
	</c:if>
	<script type='text/javascript'>
     // colorbox configuration
     var colorboxConfig_${albumid} = {
       close: '<fmt:message key="photoalbum.image.close" />',
       next: '<fmt:message key="photoalbum.image.next" />',
       previous: '<fmt:message key="photoalbum.image.prev" />',
       current: '<fmt:message key="photoalbum.image.count" />',
       maxWidth: '98%',
       maxHeight: '98%'
     };
     
     var currentPage_${albumid} = 1;
     var lastPage_${albumid} = 1;
     
     // execute on document ready
     $(document).ready(function(){
         // initialize the colorbox for this album
     	$("a.imagelink_${albumid}").colorbox(colorboxConfig_${albumid});
         // initialize the pagination for this album
     	$("#pagination_${albumid}").pagination(${imageCount},
     	    {
				num_edge_entries: 2,
				num_display_entries: 8,
				prev_text: '<fmt:message key="photoalbum.pagination.prev" />',
				next_text: '<fmt:message key="photoalbum.pagination.next" />',
				items_per_page: ${thumbs.value['ItemsPerPage']},
				callback: function(page_id, jq){
					var page=page_id+1;
					lastPage_${albumid} = currentPage_${albumid};
					currentPage_${albumid} = page;
					if ( $('#album_page_${albumid}_' + page).length == 0 ) {
					    // load the requested page
						$('<div/>').load("${cms:vfs(pageContext).link['/system/modules/com.alkacon.opencms.v8.photoalbum/elements/albumpage.jsp']}",
						    {
				    			vfsFolder: '${content.value['VfsFolder'].stringValue}',
				    			background: '${thumbs.value['Background']}',
				    			size: '${thumbs.value['Size']}',
				    			quality: '${quality}',
				    			filter: '${thumbs.value['Filter']}',
				    			alignTitle: '${thumbs.value['AlignTitle']}',
				    			showTitle: '${thumbs.value['ShowTitle']}',
				    			showResourceNameAsTitle: '${content.value['ShowResourceNameAsTitle']}',
				    			page: page,
				    			itemsPerPage: '${thumbs.value['ItemsPerPage']}',
				    			maxImageSize: '${content.value['MaxImageSize']}',
				    			albumid: '${albumid}'
				    		}, function(){
				    		    // initialize the colorbox for the new page
				    		    $("a.imagelink_${albumid}").colorbox(colorboxConfig_${albumid});
								// set path to loading image       
							    imgLoader = new Image();
							    imgLoader.src = '<%= CmsWorkplace.getSkinUri() %>jquery/css/thickbox/loading.gif';
				    		    $('#album_page_${albumid}_' + lastPage_${albumid}).hide();
						    	$('#album_page_${albumid}_' + currentPage_${albumid}).css('opacity', '0').show().animate({opacity: '1'}, 'slow');
				    		}).attr('id', 'album_page_${albumid}_' + page).css('display', 'none').appendTo('#album_pages_${albumid}');
					} else {
						$('#album_page_${albumid}_' + lastPage_${albumid}).hide();
						$('#album_page_${albumid}_' + currentPage_${albumid}).css('opacity', '0').show().animate({opacity: '1'}, 'slow');
					}
				}
		    });
     });
</script>
</c:otherwise></c:choose></div>
</fmt:bundle>
</cms:formatter>