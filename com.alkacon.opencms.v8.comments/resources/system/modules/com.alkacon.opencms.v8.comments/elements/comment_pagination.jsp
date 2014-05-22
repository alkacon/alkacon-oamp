<c:if test="${alkaconCmt.needPagination || alkaconCmt.needFilter}" >
	<div>
		<c:set var="state" value="${alkaconCmt.state}" />
		<c:if test="${state == null}"><c:set var="state" value="${-1}" /></c:if>
			<c:if test="${alkaconCmt.needPagination}" >
				<div id="cmtPagination"
					cmt-page="${param.cmtpage}" 
					cmt-count-comment="${alkaconCmt.countStateComments}" 
					cmt-item-per-page="${alkaconCmt.config.list}" 
					cmt-state="${state}"
					class="pull-right" ><ul></ul></div>
			</c:if>
			<c:if test="${alkaconCmt.needFilter}" >
				<ul class="pagination" >
					<li <c:if test='${state == -1}'>class="active"</c:if>><a href='#' id="paginationAll"><fmt:message key="pagination.all" /></a></li>
					<li <c:if test='${state == 0}'>class="active"</c:if>><a href='#' id="paginationNew"><fmt:message key="pagination.new" /></a></li>
					<li <c:if test='${state == 2}'>class="active"</c:if>><a href='#' id="paginationBlocked"><fmt:message key="pagination.blocked" /></a></li>
					<li <c:if test='${state == 1}'>class="active"</c:if>><a href='#' id="paginationApproved"><fmt:message key="pagination.approved" /></a></li>
				</ul>
			</c:if>
	</div>	
</c:if>

