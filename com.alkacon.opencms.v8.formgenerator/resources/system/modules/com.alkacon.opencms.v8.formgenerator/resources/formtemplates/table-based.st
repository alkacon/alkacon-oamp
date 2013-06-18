group webform;

form_twocolumns() ::= "false"


form(formuri,enctype,errormessage,mandatorymessage,formconfig,fields,downloadbutton,submitbutton,resetbutton,hiddenfields,prevbutton,subfieldjs) ::= <<
$! ###### Main input form ###### !$
$formconfig.formText$
$! --- show validation errors -- !$
$if(errormessage)$
	<p><span class="webform_msg_error">
	$errormessage$
	</span></p>
$endif$
<form name="emailform$formconfig.configId$" id="emailform$formconfig.configId$" action="$formuri$"
	method="post"$enctype$ $formconfig.formAttributes$>
<div style="display: none;"><input type="hidden" name="formaction$formconfig.configId$" id="formaction$formconfig.configId$" value="submit" /></div>
<table class="webform_table">
$fields$

$! --- show form middle text --- !$
<tbody><tr><td colspan="2" class="webform_middletext"><br/> 
	$formconfig.formMiddleText$
</td></tr></tbody>

$! --- show mandatory note --- !$
$if(mandatorymessage)$
	<tbody><tr><td>&nbsp;</td><td class="webform_mandatory">$mandatorymessage$</td></tr></tbody>
$endif$

$! --- show submit, reset and previous button --- !$
$if(submitbutton)$
	<tbody><tr><td>&nbsp;</td><td class="webform_button">
		$hiddenfields$
		$if(prevbutton)$
			<input type="submit" value="$prevbutton$" name="back" class="formbutton btn prevbutton" />
		$endif$
		<input type="submit" value="$submitbutton$" class="formbutton btn submitbutton" />  
		$if(resetbutton)$
			&nbsp;<input type="reset" value="$resetbutton$" class="formbutton btn resetbutton" />
		$endif$
	</td></tr></tbody>
$endif$

$! --- show download button -- !$
$if(downloadbutton)$
	<tbody><tr><td>&nbsp;</td><td class="webform_button">
	<input type="submit"
		onclick="javascript:document.getElementById('formaction$formconfig.configId$').value='export1';"
		value="$downloadbutton$"
		class="formbutton btn downloadbutton" />
	</td></tr></tbody>
$endif$
</table></form>

$! --- JavaScript for sub fields --- !$
$if(subfieldjs)$
	<script type="text/javascript">$subfieldjs$</script>
$endif$

$! --- show form footer text --- !$
$formconfig.formFooterText$
>>


form_js(formconfig,sessionjs,sessionuri,subfieldjs) ::= <<
$! ###### JAVASCRIPT for Main input form ###### !$
$if(sessionjs)$
<script type="text/javascript" src="$sessionjs$"></script>
<script type="text/javascript">
	var formgenRefreshSessionTimeout = $formconfig.refreshSessionInterval$;var formgenKeepSessionURI = "$sessionuri$";
	setTimeout("formgenKeepSession();", $formconfig.refreshSessionInterval$);
</script>
$endif$
<script type="text/javascript" src="$subfieldjs$"></script>
>>


field_captcha(field,formconfig,attributes,errormessage,mandatory,captcha) ::= <<
$! ###### CAPTCHA field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	$captcha$
	<input type="text" name="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_checkbox(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### Checkbox field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	$field.items:{
		$if(it.showInRow)$
			<table border="0" cellpadding="0" cellspacing="0" class="webform_checkbox_row"><tr><td class="webform_field_checkbox_row">
		$else$
			<table border="0" cellpadding="0" cellspacing="0" class="webform_checkbox"><tr><td class="webform_field_checkbox">
		$endif$
		<input type="checkbox" name="$field.name$" value="$it.value$" class="check"$if(it.selected)$ checked="checked"$endif$/>
		$if(it.showInRow)$
			</td><td class="webform_label_checkbox_row">
			$it.label$
			</td></tr></table>
		$else$
			</td><td class="webform_label_checkbox">
			$it.label$
			</td></tr></table>
		$endif$
	}$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_display(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### DISPLAY field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	$if(field.valueEscaped)$
		$field.valueEscaped$
	$else$
		&nbsp;
	$endif$
	<input type="hidden" name="$field.name$" value="$field.valueEscaped$"/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_email(field,formconfig,errormessage,mandatory) ::= <<
$! ###### EMAIL field ###### !$
$field_text(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
>>


field_empty(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### EMPTY field ###### !$
$row_start(field=field)$
<td class="webform_label">&nbsp;</td>
<td class="webform_field">
	$if(field.valueEscaped)$
		$field.valueEscaped$
	$else$
		&nbsp;
	$endif$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_file(field,formconfig,attributes,errormessage,mandatory,infomessage) ::= <<
$! ###### FILE UPLOAD field ###### !$
$row_start(field=field)$
<td class="webform_label">
	$if(errormessage)$
		<span class="webform_msg_error">
			$field.label$$mandatory$
		</span>
	$elseif(infomessage)$
		<span class="webform_msg_info">
			$field.label$$mandatory$
		</span>
	$else$
		$field.label$$mandatory$
	$endif$
</td>
<td class="webform_field">
	<input type="file" name="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
	$if(infomessage)$
	<br/><span class="webform_label_info">$infomessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_hidden(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### HIDDEN field ###### !$
$if(field.subField)$
	<tr style="display: none;"><td colspan="2">
$else$
	<tbody><tr style="display: none;"><td colspan="2">
$endif$
	<input type="hidden" name="$field.name$" value="$field.valueEscaped$"/>
$if(field.subField)$
	</td></tr>
$else$
	</td></tr></tbody>
$endif$
$subtext(field=field,formconfig=formconfig)$
>>


field_hiddendisplay(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### HIDDEN DISPLAY field ###### !$
$row_start(field=field)$
<td class="webform_label">&nbsp;</td>
<td class="webform_field">
	<input type="hidden" name="$field.name$" value="$field.valueEscaped$"/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_paging(field,formconfig,attributes,errormessage,mandatory,hiddenfields,prevbutton,nextbutton,page) ::= <<
$! ###### PAGING field ###### !$
$row_start(field=field)$
<td class="webform_label">&nbsp;</td>
<td class="webform_field">
	$hiddenfields$
	$if(prevbutton)$
	<input type="submit" value="$prevbutton$" name="back$formconfig.configId$" class="formbutton btn prevbutton" />&nbsp;
	$endif$
	<input type="submit" value="$nextbutton$" class="formbutton btn nextbutton" />
	<input type="hidden" name="page$formconfig.configId$" value="$page$" />
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_password(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### PASSWORD field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	<input type="password" name="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_privacy(field,formconfig,attributes,errormessage,mandatory,label,link,linktext) ::= <<
$! ###### PRIVACY field ###### !$
$row_start(field=field)$
<td class="webform_label">
	$if(errormessage)$
		<span class="webform_msg_error">
			$label$
		</span>
	$else$
		$label$
	$endif$
</td>
<td class="webform_field">
	$field.items:{
		<input type="checkbox" name="$field.name$" value="$it.value$" class="check"$if(it.selected)$ checked="checked"$endif$/>
	}$
	<a href="$link$" rel="_blank">$linktext$</a>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_radio(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### RADIO BUTTONS field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	$field.items:{
		$if(it.showInRow)$
			<table border="0" cellpadding="0" cellspacing="0" class="webform_radio_row"><tr><td class="webform_field_radio_row">
		$else$
			<table border="0" cellpadding="0" cellspacing="0" class="webform_radio"><tr><td class="webform_field_radio">
		$endif$
		<input type="radio" name="$field.name$" value="$it.value$" class="radio"$if(it.selected)$ checked="checked"$endif$$if(field.hasSubFields)$ onchange="toggleWebformSubFields(this);"$endif$/>
		$if(it.showInRow)$
			</td><td class="webform_label_radio_row">
			$it.label$
			</td></tr></table>
		$else$
			</td><td class="webform_label_radio">
			$it.label$
			</td></tr></table>
		$endif$
	}$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_select(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### SELECT field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	<select name="$field.name$" $if(formconfig.formFieldAttributes)$ $formconfig.formFieldAttributes$$endif$$if(field.hasSubFields)$ onchange="toggleWebformSubFields(this);"$endif$>
	$field.items:{
		<option value="$it.value$" $if(it.selected)$ selected="selected"$endif$>$it.label$</option>
	}$
	</select>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_table(field,formconfig,attributes,errormessage,mandatory,cols,rows,rowswithitems,editable) ::= <<
$! ###### TABLE field ###### !$
$if(!field.showRowStart)$
	$if(field.subField)$
		</tr>
	$else$
		</tr></tbody>
	$endif$
$endif$
$if(field.subField)$
	<tr>
$else$
	<tbody><tr>
$endif$
<td class="webform_label_table">
	$field_table_labels(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory,rows=rows)$
</td>
<td class="webform_field_table">
	$field_table_fields(formconfig=formconfig,cols=cols,rowswithitems=rowswithitems,editable=editable)$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$if(field.subField)$
	</tr>
$else$
	</tr></tbody>
$endif$
$subtext(field=field,formconfig=formconfig)$
>>


field_table_labels(field,formconfig,errormessage,mandatory,rows) ::= <<
$! ###### Labels for the TABLE field, separate sub template because also used for email generation ###### !$
<table cellpadding="1" cellspacing="1" class="webform_label_table" width="100%"><thead>
	<tr style="height: 20px;">
		<th align='left' valign='top' style='font-weight: normal; '>
			$if(errormessage)$
				<span class="webform_msg_error">
					$field.label$$mandatory$
				</span>
			$else$
				$field.label$$mandatory$
			$endif$
		</th>
	</tr>
	$! --- iterate the row names --- !$
	$rows:{
	<tr style="height: 20px;">
		<th align='right' >$it$</th>
	</tr>
	}$
</thead><tbody><tr><td></td></tr></tbody></table>
>>


field_table_fields(formconfig,cols,rowswithitems,editable) ::= <<
$! ###### Rows for the TABLE field, separate sub template because also used for email generation ###### !$
<table cellpadding="1" cellspacing="1" class="webform_field_table" width="100%"><thead>
	<tr style="height: 20px;">
	$! --- iterate the column names --- !$
	$cols:{
		<th align='center' >$it$</th>
	}$
	</tr>
	</thead>
	<tbody>
	$! --- iterate the rows --- !$
	$rowswithitems:{
	<tr style="height: 20px;">
		$! --- iterate the items of the row --- !$
		$it:{
		<td>
			$if(editable)$
			<input type="text" name="$it.name$" value="$it.valueEscaped$" class="table"/>
			$else$
			<span class="table">$it.valueEscaped$</span>
			$endif$
		</td>
		}$
	</tr>
	}$
</tbody></table>
>>


field_text(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXT field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<td class="webform_field">
	<input type="text" name="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


field_textarea(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXTAREA field ###### !$
$row_start(field=field)$
<td class="webform_label_multi">
	$if(errormessage)$
		<span class="webform_msg_error">
			$field.label$$mandatory$
		</span>
	$else$
		$field.label$$mandatory$
	$endif$
</td>
<td class="webform_field_multi">
	<textarea cols="5" rows="5" name="$field.name$" $formconfig.formFieldAttributes$$attributes$>$field.valueEscaped$</textarea>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</td>
$row_end(field=field,formconfig=formconfig)$
>>


label(field,formconfig,errormessage,mandatory) ::= <<
$! ###### Standard LABEL for a field ###### !$
<td class="webform_label">
	$if(errormessage)$
		<span class="webform_msg_error">
			$field.label$$mandatory$
		</span>
	$else$
		$field.label$$mandatory$
	$endif$
</td>
>>


row_start(field) ::= <<
$! ###### Standard start row for a field ###### !$
$if(field.showRowStart)$
	$if(field.subField)$
		<tr>
	$else$
		<tbody><tr>
	$endif$
$endif$
>>


row_end(field,formconfig) ::= <<
$! ###### Standard end row for a field ###### !$
$if(field.showRowEnd)$
	$if(field.subField)$
		</tr>
	$else$
		</tr></tbody>
	$endif$
$endif$
$subtext(field=field,formconfig=formconfig)$
>>


subfieldwrapper(subfields,style,id) ::= <<
$! ###### Sub field wrapper ###### !$
<tbody class="webform_subfields" id="$id$" style="$style$">
$subfields$
</tbody>
>>


subtext(field,formconfig) ::= <<
$! ###### Intermediate texts for the fields ###### !$
$if(field.hasText)$
	$if(field.showRowStart)$
		<tbody><tr>
	$endif$
	$if(field.text.columnLeft)$
		<td class="webform_text">
		$field.text.text$
		</td>
		<td class="webform_field"></td>
	$elseif(field.text.columnRight)$
		<td class="webform_label"></td>
		<td class="webform_text">
		$field.text.text$
		</td>
	$else$
		<td colspan="2" class="webform_text">
		$field.text.text$
		</td>
	$endif$
	$if(field.showRowEnd)$
		</tr></tbody>
	$endif$
$endif$
>>


checkpage(formuri,formconfig,checktext,captchafield,captchaerror,captchaimagelink,checkfields,hiddenfields,checkbutton,correctbutton) ::= <<
$! ###### CHECK page of the form ###### !$
$checktext$
$if(captchafield)$
	<script type="text/javascript">
		function runConfirmValues() {
			document.getElementById("webformconfirmvalues").$captchafield.name$.value = "" + document.getElementById("webformcaptcha").$captchafield.name$.value;
			return true;
		}
	</script>
	<form id="webformcaptcha$formconfig.configId$" action="" method="post" enctype="multipart/form-data">
$endif$
<table border="0" class="webform_check_table">
$checkfields:{
	<tr><td valign="top">$it.label$</td><td valign="top" style="font-weight: bold;">$it.value$</td></tr>
}$

$if(captchafield)$
	<tr><td  valign="middle">
		$if(captchaerror)$
			<span class="webform_msg_error">
				$captchafield.label$
			</span>
		$else$
			$captchafield.label$
		$endif$
	</td>
	<td valign="top" style="font-weight: bold;">
		<img src="$captchaimagelink$" width="$captchafield.captchaSettings.imageWidth$" height="$captchafield.captchaSettings.imageHeight$" alt="" /><br />
		<input type="text" name="$captchafield.name$" value="" />
		$if(captchaerror)$
			<br/><span class="webform_label_error">
			$captchaerror$
			</span>
		$endif$
	</td></tr>
$endif$
</table>
$if(captchafield)$
	</form>
$endif$

<table border="0" class="webform_check_table">
<tr><td>
<form id="webformconfirmvalues$formconfig.configId$" method="post" enctype="multipart/form-data" action="$formuri$" onsubmit="return runConfirmValues();">
<div style="display: none;">
<input type="hidden" name="formaction$formconfig.configId$" value="confirmed" />
<input type="hidden" name="captchaphrase" value="" />
$hiddenfields$
</div><div>
<input type="submit" value="$checkbutton$" class="formbutton" />&nbsp;&nbsp;&nbsp;&nbsp;</div>
</form>
</td>
<td>
<form id="webformdisplayvalues$formconfig.configId$" method="post" enctype="multipart/form-data" action="$formuri$">
<div style="display: none;">
<input type="hidden" name="formaction$formconfig.configId$" value="correct" />
$hiddenfields$
</div><div>
<input type="submit" value="$correctbutton$" class="formbutton" /></div>
</form>
</td></tr>
</table>
>>


confirmationpage(formconfig,confirmtext,confirmfields) ::= <<
$! ###### CONFIRMATION page of the form ###### !$
$confirmtext$
<table border="0" class="webform_confirm_table">
$confirmfields:{
	<tr><td valign="top">$it.label$</td><td valign="top" style="font-weight: bold;">$it.value$</td></tr>
}$
</table>
>>


datadownloadpage(formuri,formconfig,locale,skinuri,labelfrom,datefrom,labelto,dateto,calendaralttext,submitbutton,resetbutton) ::= <<
$! ###### DATA DOWNLOAD page of the form ###### !$
<style type="text/css">
.calendarinput {
	padding: 1px 2px 1px 2px; border-top: 1px solid #868686; border-right: 1px solid #ccc; border-bottom: 1px solid #ccc; border-left: 1px solid #868686; background-color: #fff; width: 140px;
}

span.norm{
	display: block; border: 1px solid ThreeDFace;
}
span.push {
	display: block; border-top: 1px solid ThreeDShadow; border-left: 1px solid ThreeDShadow; border-bottom: 1px solid ThreeDHighlight; border-right: 1px solid ThreeDHighlight;
}
span.over {
	display: block; border-top: 1px solid ThreeDHighlight; border-left: 1px solid ThreeDHighlight; border-bottom: 1px solid ThreeDShadow; border-right: 1px solid ThreeDShadow;
}
.calendarbutton {
	height: 20px; width: 20px; border: 0px none; vertical-align: middle;
}
a.button {
	color: ButtonText; text-decoration: none; cursor: pointer;
}


/* - internet explorer 6 specific definitions (hacks) - */
* html .ip_text {
	padding: 2px;
}
</style>
<form name="emailform$formconfig.configId$" action="$formuri$" method="post" class="formarea">
<table class="webform_table">
<tbody><tr>
 <td class="webform_label">
   <label for="starttime">$labelfrom$</label>
 </td>
 <td class="webform_field">
  <table border="0">
   <tr>
    <td>
     <input id="starttime" type="text" value="$datefrom$" name="starttime" class="calendarinput" /> 
    </td>
    <td>  
     <a href="#" class="button" title="$calendaralttext$" id="starttime.calendar"><span unselectable="on" class="norm" onmouseover="className='over'" onmouseout="className='norm'" onmousedown="className='push'" onmouseup="className='over'"><img class="calendarbutton" src="$skinuri$buttons/calendar.png" alt="$calendaralttext$"></span></a>
    </td>
   </tr>
  </table>
 </td>
</tr></tbody>
<tbody><tr>
 <td class="webform_label">
  <label for="endtime">$labelto$</label>
 </td>
 <td class="webform_field">
  <table border="0">
   <tr>
    <td>
     <input id="endtime" type="text" value="$dateto$" name="endtime" class="calendarinput" /> 
    </td>
    <td>  
     <a href="#" class="button" title="$calendaralttext$" id="endtime.calendar"><span unselectable="on" class="norm" onmouseover="className='over'" onmouseout="className='norm'" onmousedown="className='push'" onmouseup="className='over'"><img class="calendarbutton" src="$skinuri$buttons/calendar.png" alt="$calendaralttext$"></span></a>
    </td>
   </tr>
  </table>
 </td>
</tr></tbody>
<tbody><tr>
 <td>&nbsp;</td><td class="webform_button">
  <input type="submit" value="$submitbutton$" class="formbutton" /> <input type="reset" value="$resetbutton$" class="formbutton"/>
 </td>
</tr></tbody>
</table>
 <input type="hidden" name="formaction$formconfig.configId$" value="export2" />
 <input type="hidden" name="__locale" value="$locale$" />
</form>
>>


htmlemail(mailcss,mailtext,errorheadline,errors) ::= <<
$! ###### HTML email for the form ###### !$
<html><head>
<style type="text/css">
$if(mailcss)$
	$mailcss$
$else$
* { font-family: Verdana, Arial, Helvetica, sans-serif;  font-size: 12px; color: #000; }
h1 { font-size: 16px; font-weight: bold; }
p.foot { font-size: 9px; color: #555; }
table.dataform { border-collapse: collapse; border: 1px solid #333; }
table.dataform tr td { border-top: 1px solid #ccc; vertical-align:top; padding: 8px; }
table.dataform tr td.first { border-top: none; }
td.data { font-weight: bold; }
table.webform_label_table { vertical-align: top; }
table.webform_label_table tr th, table.webform_label_table tr td, table.webform_field_table tr th, table.webform_field_table tr td { white-space: nowrap; }
td.webform_label_table { vertical-align: top; }
table.webform_field_table thead, table.webform_field_table tbody, table.webform_label_table thead { margin: 0; padding: 0; }
table.webform_label_table tr th, table.webform_field_table tr td, table.webform_field_table tr th { height: 25px; }
table.webform_field_table tr td { padding-top: 0 !important; padding-bottom: 0 !important; }
table.webform_label_table tr th:first-child { vertical-align: middle; }
$endif$
</style>
</head><body>
$mailtext$
$if(errorheadline)$
	<h1>$errorheadline$</h1>
	<p>
	$errors:{
		$it$<br/>
	}$
	</p>
$endif$
</body></html>
>>


htmlemailfields(mailcss,fields) ::= <<
$! ###### FIELDS for the HTML email ###### !$
<table border="0" class="dataform$if(!mailcss)$ fields$endif$">
$first(fields):{
	<tr>
		<td class="first$if(!mailcss)$ fieldlabel$endif$"> $it.label$</td>
		<td class="first$if(mailcss)$ data$else$ fieldvalue$endif$">$it.value$</td>
	</tr>
}$
$rest(fields):{
	<tr>
		<td class="$if(!mailcss)$fieldlabel$endif$"> $it.label$</td>
		<td class="$if(mailcss)$data$else$fieldvalue$endif$">$it.value$</td>
	</tr>
}$
</table>
>>


emailerror(headline,text,error) ::= <<
$! ###### EMAIL error message ###### !$
<h3 class="webform_error_start">$headline$</h3>
<p class="webform_error_text_start">$text$</p>
<!--
        Error description: $error$
//-->
>>


initerror(headline,text) ::= <<
$! ###### Initialization error message ###### !$
<h1 class="webform_error_start">$headline$</h1>
<p class="webform_error_text_start">$text$</p>
>>