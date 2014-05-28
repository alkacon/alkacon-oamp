group comment;


header(buttonTitle, message, messageType) ::= <<
	<div class="cmtHeader">
		$if(messageType.isLogin)$
			<button 
				title="$buttonTitle$" 
				class="btn btn-primary showLoginModal" 
				data-toggle="modal" 
				data-target="#cmtLoginModal"
			>
				$message$
			</button>
		$else$
			<button class="btn btn-primary cmtLoadComments">
				$message$
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

post_options(canPostOrOfferLogin,canPost,postButtonTitle,postButtonMessage,loginButtonTitle,loginButtonMessage) ::= <<
	$if(canPostOrOfferLogin)$
		<div class="cmtPostOptions">
			$if(canPost)$
				<button  
					class="showFormModal btn btn-default pull-right"
					data-toggle="modal"
					data-target="#cmtFormModal"
					title="$postButtonTitle$" 
				>
						$postButtonMessage$
				</button>
			$else$
				<button 
					class="btn btn-default pull-right showLoginModal"
					title="$loginButtonTitle$" 
					data-toggle="modal" 
					data-target="#cmtLoginModal"
				>
					$loginButtonMessage$
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

login(hiddenFields,messageTitle,messageEnterData,messageFailed,userNameLabel,passwordLabel,loginButtonMessage,cancelButtonMessage) ::= <<
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	<h4 class="modal-title">$messageTitle$</h4>
	</div>
	<div class="modal-body">
		<form role="form" class="form-horizontal cmtLoginForm" id="fid" cmt-login-error="$messageFailed$" method="post">
		<div id="errmsg" >$messageEnterData$<br>&nbsp;</div>
			<div class="form-group">
				<label class="control-label" for="name">$userNameLabel$:</label>
				<input class="form-control first" type="text" name="name" autofocus>
			</div>
			<div class="form-group">
				<label class="control-label" for="password">$passwordLabel$:</label>
				<input class="form-control" type="password" name="password">
			</div>
			$hiddenFields$
		</form>
	</div>
	<div class="modal-footer">
		<button id="cmtLoginLoginButton" type="button" class="btn btn-primary" data-dissmiss="modal">$loginButtonMessage$</button>
		<button id="cmtLoginCancelButton" type="button" class="btn btn-default" data-dismiss="modal">$cancelButtonMessage$</button>
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

pagination(state,stateNum,commentCount,pageNum,list,needFilter,needPagination,messageAll,messageNew,messageBlocked,messageApproved) ::= <<
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
				<li $if(state.isAll)$class="active"$endif$><a href='#' id="paginationAll">$messageAll$</a></li>
				<li $if(state.isNew)$class="active"$endif$><a href='#' id="paginationNew">$messageNew$</a></li>
				<li $if(state.isBlocked)$class="active"$endif$><a href='#' id="paginationBlocked">$messageBlocked$</a></li>
				<li $if(state.isApproved)$class="active"$endif$><a href='#' id="paginationApproved">$messageApproved$</a></li>
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

manager(commentId,commentSubject,commentContent,state,stateNum,isModerated,colorClass,messageApprove,messageBlock,messageDelete,messageDeleteConf,messageHeader,messageUsername,messageEmail,messageIpAddress,messageCount,repliesOption) ::= <<
	<div class="panel $colorClass$" >
		<div class="panel-heading">
			<h5>
				$commentSubject$
				<span class="pull-right">
					$if(isModerated)$
						$if(!state.isApproved)$
							<a class="cmtAction" cmt-action="approve" cmt-comment-entry="$commentId$" cmt-state="$stateNum$" href="#" title="$messageApprove$"><span class="glyphicon glyphicon-ok text-success"></span></a>
						$endif$
						$if(!state.isBlocked)$
							<a class="cmtAction" cmt-action="block" cmt-comment-entry="$commentId$" cmt-state="$stateNum$" href="#" title="$messageBlock$"><span class="glyphicon glyphicon-ban-circle text-warning"></span></a>
						$endif$
					$endif$
					<a class="cmtAction" cmt-action="delete" cmt-comment-entry="$commentId$" cmt-state="$stateNum$" cmt-confirmation-message="$messageDeleteConf$" href="#" title="$messageDelete$"><span class="glyphicon glyphicon-remove text-danger"></span></a>
				</span>
			</h5>
			<h6>
				$messageHeader$
			</h6>
		</div>
		<div class="panel-body" >
			<div>
				$messageUsername$<br>
				$messageEmail$<br>
				$messageIpAddress$<br>
				$messageCount$
			</div>
			<div>
				$commentContent$				
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

repliesOption (noReplies,messageShowRepliesButton,messageReplyButton,messagePost,linkPost,commentId,userCanManage,userCanPost) ::= <<
	<div class="cmtCommentReplyOptions">
		<div class="cmtCommentCountReplies">
			<button class="btn btn-primary btn-xs cmtShowRepliesButton" $disable(doDisable=noReplies)$ cmt-comment-entry='$commentId$' cmt-user-can-manage='$userCanManage$'>
				$messageShowRepliesButton$
			</button>			
			$if(userCanPost)$
				<div class="cmtCommentReply">
					<button 
						class="btn btn-primary btn-xs showFormModal"
						data-toggle="modal"
						data-target="#cmtFormModal"
						title="$messagePost$" 
						cmt-parent-id="$commentId$"
						href="$linkPost$" 
					>
						$messageReplyButton$
					</button>
				</div>
				<div class="clear"></div>
			$endif$
		</div>
		<div class="cmtCommentShowReplies" id="cmtCommentShowReplies-$commentId$"></div>
	</div>
>>

reply_manager(commentId,commentSubject,commentContent,stateNum,colorClass,messageDelete,messageDeleteConf,messageHeader,messageUsername,messageEmail,messageIpAddress,messageCount) ::= <<
	$manager(commentId=commentId,commentSubject=commentSubject,commentContent=commentContent,stateNum=stateNum,colorClass=colorClass,messageDelete=messageDelete,messageDeleteConf=messageDeleteConf,messageHeader=messageHeader,messageUsername=messageUsername,messageEmail=messageEmail,messageIpAddress=messageIpAddress,messageCount=messageCount)$
>>

view(colorClass,commentSubject,header,commentContent,repliesOption) ::= <<
<div class="panel $colorClass$" >
	<div class="panel-heading" >
		<div class="h5">
			$commentSubject$
		</div>
		<div class="h6">
			$header$
		</div>
	</div>
	<div class="panel-body">
		$commentContent$
		$repliesOption$
	</div>
</div>
>>

reply_view(colorClass,commentSubject,header,commentContent) ::= <<
	$view(colorClass=colorClass,commentSubject=commentSubject,header=header,commentContent=commentContent)$
>>
