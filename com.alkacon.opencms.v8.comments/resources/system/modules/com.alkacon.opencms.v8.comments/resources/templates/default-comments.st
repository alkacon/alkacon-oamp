group comment;


header(messageType,countApprovedComments,countNewComments,countBlockedComments,countComments,messages) ::= <<
	<div class="cmtHeader">
	  	$if(messageType.isManageModerated)$
			<button class="btn btn-primary cmtLoadComments">
				$countApprovedComments,countNewComments:{p0,p1 | $messages.("header.user.manage.2").stringTemplate$}$
			</button>
		$elseif(messageType.isManageUnmoderated)$
			<button class="btn btn-primary cmtLoadComments">
				$countComments:{p0 | $messages.("header.user.manage.1").stringTemplate$}$
			</button>
		$elseif(messageType.isPost)$
			<button class="btn btn-primary cmtLoadComments">
				$countComments:{p0 | $messages.("header.user.post.1").stringTemplate$}$
			</button>
		$elseif(messageType.isView)$
			<button class="btn btn-primary cmtLoadComments">
				$countComments:{p0 | $messages.("header.user.read.1").stringTemplate$}$
			</button>
		$elseif(messageType.isLogin)$
		<button 
			title='$messages.("login.message.title")$' 
			class="btn btn-primary showLoginModal" 
			data-toggle="modal" 
			data-target="#cmtLoginModal"
		>
			$countComments:{p0 | $messages.("header.user.login.1").stringTemplate$}$
		</button>
		$endif$
	</div>
>>


headline(title) ::= <<
	$! Modal for the login dialog !$
	<div class="modal fade" id="cmtLoginModal" tabindex="-1" role="dialog" aria-labelledby="cmtLoginModalLabel" aria-hidden="true">
		<div class="modal-dialog" style="top: 5%;">
			<div class="modal-content" style="width:800px;"></div>
		</div>
	</div>
	$! The real headline !$
	<div class="headline">
		<h3>$title$</h3>
	</div>
>>

post_options(canPostOrOfferLogin,canPost,messages) ::= <<
	$if(canPostOrOfferLogin)$
		<div class="cmtPostOptions">
			$if(canPost)$
				<button  
					class="showFormModal btn btn-default pull-right"
					data-toggle="modal"
					data-target="#cmtFormModal"
					title="$messages.("form.message.post")$" 
				>
						$messages.("post.0")$
				</button>
			$else$
				<button 
					class="btn btn-default pull-right showLoginModal"
					title='$messages.("login.message.title")$' 
					data-toggle="modal" 
					data-target="#cmtLoginModal"
				>
					$messages.("post.user.login.0")$
				</button>
			$endif$
			<div class="clearfix"></div>
		</div>
	$endif$
	$! include form-modal !$
	<div class="modal fade" id="cmtFormModal" tabindex="-1" role="dialog" aria-labelledby="cmtFormModalLabel" aria-hidden="true">
		<div class="cmtFormModalDialog modal-dialog">
			<div class="cmtFormModalContent modal-content"></div>
		</div>
	</div>
>>

login(hiddenFields,messages) ::= <<
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	<h4 class="modal-title">$messages.("login.message.title")$</h4>
	</div>
	<div class="modal-body">
		<form role="form" class="form-horizontal cmtLoginForm" id="fid" cmt-login-error='$messages.("login.message.failed")$' method="post">
		<div id="errmsg" >$messages.("login.message.enterdata")$<br>&nbsp;</div>
			<div class="form-group">
				<label class="control-label" for="name">$messages.("login.label.username")$:</label>
				<input class="form-control first" type="text" name="name" autofocus>
			</div>
			<div class="form-group">
				<label class="control-label" for="password">$messages.("login.label.password")$:</label>
				<input class="form-control" type="password" name="password">
			</div>
			$hiddenFields$
		</form>
	</div>
	<div class="modal-footer">
		<button id="cmtLoginLoginButton" type="button" class="btn btn-primary" data-dissmiss="modal">$messages.("login.label.login")$</button>
		<button id="cmtLoginCancelButton" type="button" class="btn btn-default" data-dismiss="modal">$messages.("login.label.cancel")$</button>
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
		\$("body").keypress(function (e) { 
			bindEnter(e, \$( "#cmtLoginLoginButton" ) );
		});
	</script>
>>

pagination(state,stateNum,commentCount,pageNum,list,needFilter,needPagination,messages) ::= <<
	$! The template is only rendered if needFilter or needPagination is true !$
	<div>
		$if(needPagination)$
			<div 
				id="cmtPagination"
				cmt-page="$pageNum$" 
					cmt-count-comment="$commentCount$" 
					cmt-item-per-page="$list$" 
					cmt-state="$stateNum$"
					class="pull-right" 
			><ul></ul></div>
		$endif$
		$if(needFilter)$
			<ul class="pagination" >
				<li $if(state.isAll)$class="active"$endif$><a href='#' id="paginationAll">$messages.("pagination.all")$</a></li>
				<li $if(state.isNew)$class="active"$endif$><a href='#' id="paginationNew">$messages.("pagination.new")$</a></li>
				<li $if(state.isBlocked)$class="active"$endif$><a href='#' id="paginationBlocked">$messages.("pagination.blocked")$</a></li>
				<li $if(state.isApproved)$class="active"$endif$><a href='#' id="paginationApproved">$messages.("pagination.approved")$</a></li>
			</ul>
		$endif$
	</div>
	<div class="clearfix"></div>
>>

colorClassAll(boxColor) ::= <<
  panel-$boxColor$
>>

colorClassNew(boxColor) ::= <<
  panel-info
>>

colorClassBlocked(boxColor) ::= <<
  panel-danger
>>

colorClassApproved(boxColor) ::= <<
  panel-success
>>

colorClassReply(boxColor) ::= <<
  panel-$boxColor$
>>

manager(commentId,commentCreationDate,commentFields,messages,state,stateNum,isModerated,colorClass,repliesOption,countPostsByAuthor) ::= <<
	<div class="panel $colorClass$" >
		<div class="panel-heading">
			<h5>
				$commentFields.subject$
				<span class="pull-right">
					$if(isModerated)$
						$if(!state.isApproved)$
							<a class="cmtAction" cmt-action="approve" cmt-comment-entry="$commentId$" cmt-state="$stateNum$" href="#" title='$messages.("comment.manager.approve")$'><span class="glyphicon glyphicon-ok text-success"></span></a>
						$endif$
						$if(!state.isBlocked)$
							<a class="cmtAction" cmt-action="block" cmt-comment-entry="$commentId$" cmt-state="$stateNum$" href="#" title='$messages.("comment.manager.block")$'><span class="glyphicon glyphicon-ban-circle text-warning"></span></a>
						$endif$
					$endif$
					<a class="cmtAction" cmt-action="delete" cmt-comment-entry="$commentId$" cmt-state="$stateNum$" cmt-confirmation-message='$messages.("comment.manager.delete.conf")$' href="#" title='$messages.("comment.manager.delete")$'><span class="glyphicon glyphicon-remove text-danger"></span></a>
				</span>
			</h5>
			<h6>
				$commentFields.name.escapeXml,commentCreationDate.dateLongTimeShort:{p0,p1 | $messages.("comment.header.view.2").stringTemplate$}$
			</h6>
		</div>
		<div class="panel-body" >
			<div>
				$commentFields.username.escapeXml:{p0 | $messages.("comment.manager.username.1").stringTemplate$}$<br>
				$commentFields.email.escapeXml:{p0 | $messages.("comment.manager.email.1").stringTemplate$}$<br>
				$commentFields.ipaddress.escapeXml:{p0 | $messages.("comment.manager.ipaddress.1").stringTemplate$}$<br>
				$countPostsByAuthor:{p0 | $messages.("comment.manager.count.1").stringTemplate$}$
			</div>
			<div>
				$commentFields.comment$				
				$repliesOption$
			</div>
		</div>	
	</div>
>>

disable(doDisable) ::= <<
  $if(doDisable)$
    disabled='disabled'
  $endif$
>>

repliesOption (noReplies,exactlyOneReply,countReplies,linkPost,commentId,userCanManage,userCanPost,messages) ::= <<
	<div class="cmtCommentReplyOptions">
		<div class="cmtCommentCountReplies">
			<button class="btn btn-primary btn-xs cmtShowRepliesButton" $disable(doDisable=noReplies)$ cmt-comment-entry='$commentId$' cmt-user-can-manage='$userCanManage$'>
				$if(exactlyOneReply)$
					1 $messages.("oneReply")$
				$else$
					$countReplies$ $messages.("manyReplies")$
				$endif$
			</button>
			$if(userCanPost)$
				<div class="cmtCommentReply">
					<button 
						class="btn btn-primary btn-xs showFormModal"
						data-toggle="modal"
						data-target="#cmtFormModal"
						title='$messages.("form.message.post")$' 
						cmt-parent-id="$commentId$"
						href="$linkPost$" 
					>
						$messages.("doReply")$
					</button>
				</div>
				<div class="clear"></div>
			$endif$
		</div>
		<div class="cmtCommentShowReplies" id="cmtCommentShowReplies-$commentId$"></div>
	</div>
>>

reply_manager(commentId,commentCreationDate,commentFields,messages,state,stateNum,isModerated,colorClass,repliesOption,countPostsByAuthor) ::= <<
	$manager(commentId=commentId,commentCreationDate=commentCreationDate,commentFields=commentFields,messages=messages,state=state,stateNum=stateNum,isModerated=isModerated,colorClass=colorClass,repliesOption=repliesOption,countPostsByAuthor=countPostsByAuthor)$
>>

view(colorClass,commentFields,commentCreationDate,messages,repliesOption) ::= <<
<div class="panel $colorClass$" >
	<div class="panel-heading" >
		<div class="h5">
			$commentFields.subject$
		</div>
		<div class="h6">
			$commentFields.name.escapeXml,commentCreationDate.dateLongTimeShort:{p0,p1 | $messages.("comment.header.view.2").stringTemplate$}$
		</div>
	</div>
	<div class="panel-body">
		$commentFields.comment$
		$repliesOption$
	</div>
</div>
>>

reply_view(colorClass,commentFields,commentCreationDate,messages) ::= <<
	$view(colorClass=colorClass,commentFields=commentFields,commentCreationDate=commentCreationDate,messages=messages)$
>>
