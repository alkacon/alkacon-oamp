<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
$.get('<cms:link>test.html</cms:link>', function(html) {
  $('#id').html(html);
});