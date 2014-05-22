<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
%><%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
    if ("login".equals(request.getParameter("action"))) {
        alkaconCmt.login(request.getParameter("name"), request.getParameter("password"), "Offline");
        if (alkaconCmt.getLoginException() == null) {
            out.print("ok");
        } else {
            out.print(alkaconCmt.getLoginException().getLocalizedMessage());
        }
    	return; 
    }
    pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>
<fmt:setLocale value="${cms.locale}" />
<cms:bundle basename="${alkaconCmt.resourceBundle}">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4 class="modal-title"><fmt:message key="login.message.title" /></h4>
	</div>
	<div class="modal-body">
		<form role="form" class="form-horizontal cmtLoginForm" id="fid" cmt-login-error="<fmt:message key="login.message.failed" />" method="post">
		<div id="errmsg" ><fmt:message key="login.message.enterdata" /><br>&nbsp;</div>
			<div class="form-group">
				<label class="control-label" for="name"><fmt:message key="login.label.username" />:</label>
				<input class="form-control first" type="text" name="name" autofocus>
			</div>
			<div class="form-group">
				<label class="control-label" for="password"><fmt:message key="login.label.password" />:</label>
				<input class="form-control" type="password" name="password">
			</div>
			<input type="hidden" name="action" value="login" />
			<input type="hidden" name="cmturi" value="${param.cmturi}" />
			<input type="hidden" name="cmtminimized" value="${param.cmtminimized}" />
			<input type="hidden" name="cmtlist" value="${param.cmtlist}" />
			<input type="hidden" name="cmtsecurity" value="${param.cmtsecurity}" />
			<input type="hidden" name="cmtformid" value="${param.cmtformid}" />
			<input type="hidden" name="cmtallowreplies" value="${param.cmtallowreplies}" />
			<input type="hidden" name="__locale" value="${param.__locale}" />
		</form>
	</div>
	<div class="modal-footer">
		<button id="cmtLoginLoginButton" type="button" class="btn btn-primary" data-dissmiss="modal"><fmt:message key="login.label.login" /></button>
		<button id="cmtLoginCancelButton" type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="login.label.cancel" /></button>
	</div>
	<script type="text/javascript">
		function bindEnter(e,button) {
			var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
			if (key == 13 && button.css('display') != 'none') {
				button.click();
				return false;
			} else {
				return true;
			}
		}
		$("body").keypress(function (e) { 
	bindEnter(e, \$( "#cmtLoginLoginButton" ) );
});
	</script>
</cms:bundle>
