<div class="pagination_header">
	<c:if test="${alkaconCmt.config.moderated && alkaconCmt.userCanManage && alkaconCmt.countComments > 0}" >
	   <div class="moderation_state" >
	   	<a id="statenull" class="state" href="#" ><fmt:message key="pagination.all" /></a> |
	   	<a id="state0" class="state" href="#" ><fmt:message key="pagination.new" /></a> |
	   	<a id="state2" class="state" href="#" ><fmt:message key="pagination.blocked" /></a> |
	   	<a id="state1" class="state" href="#" ><fmt:message key="pagination.approved" /></a>
	   </div>
	</c:if>
	<c:if test="${alkaconCmt.needPagination}" >
	   <div id="cmt-pagination" class="pagination pagination_box" />
	</c:if>
</div>
<script>
$("a.state").each(
	function(intIndex) {
		var getClickHandler = function(actionId) {
			return function(evt) { 			     
			    reloadComments(actionId.substring(5));
				if (evt.stopPropagation) {
					evt.stopPropagation();
				} else {
					evt.cancelBubble = true;
				}
				return false;
			};
		};
		$(this).bind("click", getClickHandler($(this).attr("id")));
	}
);	
    var newPage = ${param.cmtpage};
<c:if test="${alkaconCmt.needPagination}" >
    var oldPage = ${param.cmtpage};
	$("#cmt-pagination").pagination(${alkaconCmt.countComments}, {
		items_per_page: ${alkaconCmt.config.list},
		current_page: ${param.cmtpage},
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
					{ cmturi: '${param.cmturi}', cmtpage: newPage, __locale: '<cms:info property="opencms.request.locale" />', cmtstate: '${alkaconCmt.state}' },
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
</c:if>
</script>
