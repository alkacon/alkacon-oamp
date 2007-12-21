<%@page buffer="none" session="false"
	import="org.opencms.i18n.*,ch.corner.card.frontend.form.*,ch.corner.card.frontend.base.*,java.util.*,org.opencms.util.*"%><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%

// initialize the form handler
CmsFormHandler cms = new CmsFormHandler(pageContext, request, response);
boolean isOffline = !cms.getRequestContext().currentProject().isOnlineProject();

// get the localized messages to create the form
CmsMessages messages = cms.getMessages();

boolean showTemplate = cms.showTemplate();
if(showTemplate) {
%>
<cms:include property="template" element="head"/>
<cms:contentload collector="singleFile" param="${opencms.uri}" editable="true">
<h1 class="flashtext"><cms:contentshow element="Title"/></h1>
</cms:contentload>
<%
}
boolean showForm = cms.showForm();

if (! showForm) {
	// form has been submitted with correct values, decide further actions
	if (cms.showCheck()) {
		// show optional check page
		request.setAttribute("formhandler", cms);
		cms.include("../elements/check.jsp");
	} else if(cms.showDownloadData()) {
	    cms.include("../elements/datadownload.jsp");
	} else if (cms.getFormConfiguration().hasTargetUri()) {
		response.sendRedirect(cms.link(cms.getFormConfiguration().getTargetUri()));
	} else {
		// try to send a notification email with the submitted form field values
		if (cms.sendData()) {
			// successfully sent mail, show confirmation end page
			request.setAttribute("formhandler", cms);
			cms.include("../elements/confirmation.jsp");
		} else {
			// failure sending mail, show error output %>


<h2><%= messages.key("form.error.mail.headline") %></h2>
<p><%= messages.key("form.error.mail.text") %></p>

<!--
			Error description: <%= (String)cms.getErrors().get("sendmail") %>
			//-->
<%
		}
	}	
} else {

	// get the configured form elements
	CmsForm formConfiguration = cms.getFormConfiguration();
	List<List<I_CmsField>> fieldGroups = formConfiguration.getFieldGroups();
	// create the form head 
	%>
<form name="emailform" action="<%= cms.link(cms.getRequestContext().getUri()) %>" method="post" class="formarea"><%
	// show form text
  out.print(formConfiguration.getFormText());
	// show global error message if validation failed
  if (cms.hasValidationErrors()) {%>
  <div class="formerrorbox">
    <p><%= messages.key("form.html.label.error.message")%></p>
  </div><% 	
  }
  // create the html output to display the form fields
  int counter = 0;
  for(List<I_CmsField> fieldGroup: fieldGroups) {
    // loop through all form input field groups
    if(counter == 0) { %>
  <div class="formgroup bgdgrey linetop linewhite"><%
    } else {%>
  <div class="formgroup bgdgrey linewhite"><%	        
	}
    for(I_CmsField field:fieldGroup) {
      // loop through all fields of a group
      String fieldType = field.getType();	
      String fieldLabel = field.getLabel();	
      String errorMessage = (String)cms.getErrors().get(field.getName());
      if(!CornerCardStringUtil.isEmptyOrUnfound(errorMessage)) {%>
    <div class="formdescription formerror">
      <label for="<%= field.getName() %>">&raquo;&nbsp;<%= field.getLabel() %></label>(<%=messages.key("form.error."+errorMessage)%>)
    </div>
    <div class="formelements formerror"><%
      } else {%>
    <div class="formdescription">	
      <label for="<%= field.getName() %>"><%= field.isMandatory() ? "*":"" %><%= field.getLabel() %></label>
    </div>
    <div class="formelements"><%
      }		
      if(field instanceof CmsTextField) {%>
      <input type="text" id="<%= field.getName() %>" name="<%= field.getName() %>"  value="<%= field.getValue() %>" class="ip_text" /><% 
      } else if(field instanceof CornercardCounterField) { 
        ((CornercardCounterField)field).setFormHandler(cms);%>
        <input type="hidden" name="<%= field.getName() %>" class="ip_text" value="<%= field.getValue() %>" /><%= field.getValue()%><%
      } else if(field instanceof CornercardCounterResetField) { 
        CmsFieldItem item = (CmsFieldItem )field.getItems().get(0);
        String checked ="";
        if (item.isSelected()) {
	   checked =" checked=\"checked\"";
        } else {
           checked = "";
        }
	%>
      <input type="checkbox" class="ip_radio" id="<%= field.getName() %>" name="<%= field.getName() %>" value="<%= item.getValue() %>"<%= checked %> /> <% 

      } else if(field instanceof CmsTextareaField) { %> 
      <textarea id="<%= field.getName() %>" name="<%= field.getName() %>" rows="10" cols="40"><%= field.getValue() %></textarea><% 
      } else if( field instanceof CmsCheckboxField) { 
CmsCheckboxField checkField = (CmsCheckboxField) field;
List<CmsFieldItem> items = checkField.getItems();
        for(CmsFieldItem item : items) { 
        String checked ="";
        if (item.isSelected()) {
	   checked =" checked=\"checked\"";
        } else {
           checked = "";
        }

	%>
      <input type="checkbox" class="ip_radio" id="<%= field.getName() %>" name="<%= field.getName() %>" value="<%= item.getValue() %>"<%= checked %> /> <%= item.getLabel() %><br/><% 
}
      } else if(field instanceof CmsEmailField) { %> 
       <input type="text" id="<%= field.getName() %>" name="<%= field.getName() %>" value="<%= field.getValue() %>" class="ip_text" /><%
      } else if(field instanceof CmsCaptchaField) { 
        CmsCaptchaField captchaField = (CmsCaptchaField)field;
        CmsCaptchaSettings captchaSettings = captchaField.getCaptchaSettings();
        String captchaLink = new StringBuffer("/system/modules/ch.corner.card.frontend.form/pages/captcha.jsp?").append(captchaSettings.toRequestParams(cms.getCmsObject())).toString();
        captchaLink = cms.link(captchaLink);  %>
      <img src="<%= captchaLink %>" alt="captcha" style="margin-bottom: 8px;" /><br/>
      <input type="text" name="captchaphrase" id="captcha" class="ip_text" /><%
      } else if(field instanceof CmsHiddenField) { %>
      <input type="hidden" name="<%= field.getName() %>" id="<%= field.getName() %>" value="<%= field.getValue() %>" /> <%
      } else if(field instanceof CmsRadioButtonField) {
        CmsRadioButtonField radioField = (CmsRadioButtonField) field;
        List<CmsFieldItem> items = radioField.getItems();
        int fieldCount = 0;
        for(CmsFieldItem item : items) {      
          fieldCount++;   
          String checked ="";
          if (item.getValue().equals(request.getParameter(field.getName()))) {
	     checked ="checked=\"checked\"";
          } else {
             checked = "";
          }%>
      <nobr><label for="<%= item.getLabel() %>" class="radioitem"><input type="radio" name="<%= radioField.getName()%>" <%=checked%> class="ip_radio"  value="<%= item.getValue() %>"/> <strong><%= item.getLabel() %></strong></label></nobr><% 
          if(fieldCount % 2 == 0) {%>
      <br/><%
          } 
        } 
      } else if(field instanceof CmsEmailField) { %>
          <input type="text" id="<%= field.getName() %>" name="<%= field.getName() %>" value="<%= field.getValue() %>" class="ip_text" /><% 
      } else if(field instanceof CornercardCreditcardValidationField) { 
          String value = field.getValue();
          String[] values;
          if(CmsStringUtil.isNotEmptyOrWhitespaceOnly(value)) { 
            values = CmsStringUtil.splitAsArray(value, ',');
          } else { 
            values = new String[4];
          }
          for (int i = 0; i < 4; i++) { %>
          <input type="text" name="<%= field.getName() %>" class="ip_text_cardfield" value="<% 
            if (values.length == 4) {%><%= (values[i] != null) ? values[i].trim() : "" %><%}%>" size="4" maxlength="4" width="40" /><span width="20px" /><%
        }
      } 
      counter++;%>
    </div>
    <div class="clearer"></div><% 
    } %>
  </div><%
  }
  // create the form foot 
  if (formConfiguration.hasMandatoryFields()) {%>
  <div class="formgroup bgdgrey linewhite">
    <div class="formelements">
   <%= messages.key("form.message.mandatory") %>
    </div>
    <div class="clearer">
   </div></div><%
	} %>
   <!-- Hidden form fields:  --!>
   <input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>"  id="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_SUBMIT %>"/>
   <div class="formbuttons bgdbluebright linegreydark">
     <input type="submit" value="<%= messages.key("form.button.submit") %>" class="ip_submit" />
     <input type="reset" value="<%= messages.key("form.button.reset") %>" class="ip_submit">
   </div><%
if(isOffline) { 
%>   <div class="formbuttons bgdbluebright linegreydark">
     <input type="submit" onClick="javascript:document.getElementById('<%=CmsFormHandler.PARAM_FORMACTION %>').value='<%=CmsFormHandler.ACTION_DOWNLOAD_DATA_1 %>'"  value="<%= messages.key("form.button.downloaddata") %>" class="ip_submit" />
   </div><%
}
%>  </div>
</form>
<%
	
}
if(showTemplate) {
%><cms:include property="template" element="foot"/><%
}%>
