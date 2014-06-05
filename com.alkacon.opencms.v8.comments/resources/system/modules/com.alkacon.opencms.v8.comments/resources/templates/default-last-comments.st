group last-comment;

last_comments_list(title,entries) ::= <<
	<div id="lastcommentbox" class="commentbox">
		<div class="headline cmtLastCommentsHeader">
			<h3>$title$</h3>
		</div>
		<div>
			$entries$
		</div>
	</div>
>>

last_comments_entry(boxColor,commentFields,commentCreationDate,commentPageLink,commentPageTitle,fieldsToShow,messages) ::= <<
	<div class="panel $colorClassLastComments(boxColor)$">											
		<div class="panel-heading cmtCommentEntry">
			$last_comments_show_header(commentFields=commentFields,fieldsToShow=fieldsToShow,title=commentPageTitle,link=commentPageLink,messages=messages)$
		</div>
		$showMoreInformation(commentFields=commentFields,fieldsToShow=fieldsToShow,commentCreationDate=commentCreationDate,messages=messages)$
	</div>
>>

last_comments_no_entry(message) ::= <<
	<div>
		$message$
	</div>
>>

colorClassLastComments(boxColor) ::= <<
	panel-$boxColor$
>>

titlewrapper(title,link) ::= <<
	<h6 class="panel-title cmtCommentTitle">
		<a class="panel-title" href="$link$">$title$</a>
	</h6>
>>

usertitlewrapper(usertitle,title,link,messages) ::= <<
	<h5 class="panel-title cmtCommentTitle">
		$usertitle$
	</h5>
	$messages.("commentlist.madecommenton1")$
	$titlewrapper(title=title,link=link)$
	$messages.("commentlist.madecommenton2")$
>>


last_comments_show_header(commentFields,fieldsToShow,usertitle,title,link,messages) ::= <<
	$if(fieldsToShow.name)$
		$if(fieldsToShow.email)$
			$if(fieldsToShow.username)$
				$usertitlewrapper(usertitle=nameAndMail(email=commentFields.email,name=nameAndUsername(name=commentFields.name,username=commentFields.username)),title=title,link=link,messages=messages)$
			$else$
				$usertitlewrapper(usertitle=nameAndMail(email=commentFields.email,name=commentFields.name),title=title,link=link,messages=messages)$
			$endif$
		$else$
			$if(fieldsToShow.username)$
				$usertitlewrapper(usertitle=nameAndMail(email=commentFields.email,name=commentFields.username),title=title,link=link,messages=messages)$
			$else$
				$usertitlewrapper(usertitle=commentFields.name,title=title,link=link,messages=messages)$
			$endif$
		$endif$
	$elseif(fieldsToShow.username)$
		$if(fieldsToShow.email)$
			$usertitlewrapper(usertitle=nameAndMail(email=commentFields.email,name=commentFields.username),title=title,link=link,messages=messages)$
		$else$
			$usertitlewrapper(usertitle=commentFields.username,title=title,link=link,messages=messages)$
		$endif$
	$elseif(fieldsToShow.email)$
		$usertitlewrapper(usertitle=nameAndMail(email=commentFields.email,name=commentFields.email),title=title,link=link,messages=messages)$
	$else$
		$titlewrapper(title=title,link=link)$
	$endif$
>>

nameAndMail(email,name) ::= <<
	<a href='mailto:$email$'>$name$</a>
>>

nameAndUsername(name, username) ::= <<
	$name$ ($username$)
>>

showMoreInformation(commentFields,fieldsToShow,commentCreationDate,messages) ::= <<
	$if(fieldsToShow.subject)$
		<div class="panel-body">
			<strong>$messages.("commentlist.subject")$:<br /> $commentFields.subject$</strong><br />
			$if(fieldsToShow.comment)$
				<strong>$messages.("commentlist.comment")$</strong>:<br /> $commentFields.comment$
			$endif$
		</div>
	$elseif(fieldsToShow.comment)$
		<div class="panel-body">
			<strong>$messages.("commentlist.comment")$</strong>:<br /> $commentFields.comment$
		</div>
	$endif$
>>
