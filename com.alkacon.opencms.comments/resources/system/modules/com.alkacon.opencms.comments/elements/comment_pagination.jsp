<div style="position: relative; height: 18px;">
   <div id="cmt-pagination" class="pagination" style="position: absolute; right: 0px;"/>
</div>
<script>
    var oldPage = 0;
    var newPage = 0;
	$("#cmt-pagination").pagination(${cmt.countComments}, {
		items_per_page: ${cmt.config.list},
		prev_text: '<fmt:message key="pagination.prev" />',
		next_text: '<fmt:message key="pagination.next" />',
		num_display_entries: 6,
		num_edge_entries: 1,
		callback: function(page) {
	        if (oldPage == page) return;
	        newPage = page;
	        if ($("#comments_page_" + newPage).length == 0) {
                $("<div/>").attr("id", "comments_page_" + newPage).css("display", "none").appendTo("#comments");
               	$.post(
					"<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_page.jsp:34fca4a6-1da3-11dd-be62-111d34530985)</cms:link>",
					{ cmturi: '${param.cmturi}', cmtpage: newPage, __locale: '<cms:info property="opencms.request.locale" />' },
					function(html) {
						$("#comments_page_" + newPage).html(html);
						onPagination();
					}
				);
			} else {
				onPagination();
			}
			return false;
		}
	});
	function onPagination() {
	
		$('body').css("cursor", "wait");
		$("#comments_page_" + oldPage).fadeOut(
			'slow', 
			function() {
				$("#comments_page_" + newPage).fadeIn('slow');
				$('body').css("cursor", "auto");
			}
		);
		oldPage = newPage;
	}
</script>
