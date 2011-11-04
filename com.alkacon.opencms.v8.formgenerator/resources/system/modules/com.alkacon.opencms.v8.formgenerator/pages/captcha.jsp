<%@page session="true" import="org.opencms.file.*,org.opencms.util.*,org.opencms.jsp.*,org.opencms.xml.content.*,com.alkacon.opencms.v8.formgenerator.*" %><%

	CmsJspActionElement jsp = new CmsJspActionElement(pageContext, request, response);
    CmsCaptchaSettings settings = CmsCaptchaSettings.getInstance(jsp);
	CmsCaptchaField captcha = new CmsCaptchaField(settings, null, null);
	captcha.writeCaptchaImage(jsp);

%>