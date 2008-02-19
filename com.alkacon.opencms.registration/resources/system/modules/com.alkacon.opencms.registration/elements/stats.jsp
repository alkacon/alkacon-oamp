<%@ page session="false" import="org.opencms.jsp.*, com.alkacon.opencms.registration.*" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %><%--
--%><jsp:useBean id="regInfo" class="com.alkacon.opencms.registration.CmsRegistrationInfo" scope="page" /><%--
--%><jsp:setProperty name="regInfo" property="ou" value="/test/" />
<table border="0" cellpadding="2" cellspacing="0">
<tr><td colspan="2">Statistics</td><tr>
<tr><td>Online Users:</td><td><jsp:getProperty name="regInfo" property="numOnlineUsers" /></td></tr>
<tr><td>Registered Users:</td><td><jsp:getProperty name="regInfo" property="numUsers" /></td></tr>
</table>