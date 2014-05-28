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

colorClassLastComments(boxColor) ::= <<
	panel-$boxColor$
>>

last_comments_show_meta(messageCreated,creationDate,messageCreationDate,locale,messageLocale,ipAddress,messageIpAddress) ::= <<
		$messageCreated$<br>
		$if(creationDate)$
			$messageCreationDate$&nbsp;$creationDate$<br>
		$endif$
		$if(locale)$
			$messageLocale$&nbsp;$locale$<br>
		$endif$
		$if(ipAddress)$
			$messageIpAddress$&nbsp;$ipAddress$
		$endif$
>>

last_comments_show_user(name,email,username) ::= <<
	$if(name)$
		$if(email)$
			<a href='mailto:$email$'>$name$</a>
		$else$
			$name$
		$endif$
		$if(username)$
			($username$)
		$endif$
	$elseif(username)$
		$if(email)$
			<a href='mailto:$email$'>$username$</a>
		$else$
			$username$
		$endif$
	$else$ $! if the template is called at least one of name, email, username must be defined !$
		<a href='mailto:$email$'>$email$</a>
>>

last_comments_entry(colorClass,showUser,name,email,username,messageCommentOn1,messageCommentOn2,link,commentTitle,messageCommentSubject,commentSubject,messageCommentContent,commentContent,messageCreated,creationDate,messageCreationDate,locale,messageLocale,ipAddress,messageIpAddress,showCommentDetails, showCommentMeta) ::= <<
	<div class="panel $colorClass$">											
		<div class="panel-heading cmtCommentEntry">
			<div class="cmtCommentHeader">
				$if(showUser)$
					<h5 class="panel-title cmtCommentTitle">
						$last_comments_show_user(name=name,email=email,username=username)$
					</h5>
					$messageCommentOn1$
				$endif$
				<h6 class="panel-title cmtCommentTitle">
					<a class="panel-title" href="$link$">$commentTitle$</a>
				</h6>
				$if(showUser)$
					$messageCommentOn2$
				$endif$
			</div>
		</div>												
		$if(showCommentDetails)$
			<div class="panel-body">
			   		$if(commentSubject)$
						<strong>$messageCommentSubject$: $commentSubject$</strong><br>
					$endif$
					$if(commentContent)$
						<strong>$messageCommentContent$</strong>: $commentContent$
					$endif$
			   	$if(showCommentMeta)$
					<hr />
			   		$last_comments_show_meta(messageCreated=messageCreated,creationDate=creationDate,messageCreationDate=messageCreationDate,locale=locale,messageLocale=messageLocale,ipAddress=ipAddress,messageIpAddress=messageIpAddress)$
				$endif$
			</div>
		$elseif(showCommentMeta)$
			<div class="panel-body">
				$last_comments_show_meta(messageCreated=messageCreated,creationDate=creationDate,messageCreationDate=messageCreationDate,locale=locale,messageLocale=messageLocale,ipAddress=ipAddress,messageIpAddress=messageIpAddress)$
			</div>
		$endif$
	</div>
>>

last_comments_no_entry(message) ::= <<
	<div>
		$message$
	</div>
>>
