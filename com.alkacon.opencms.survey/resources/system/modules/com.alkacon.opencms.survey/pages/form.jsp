<%@page buffer="none" session="false" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="addMessage"><cms:property name="webformMessage" default="/com/alkacon/opencms/survey/webform" /></c:set>
<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.formgenerator/pages/form.jsp)" %>
