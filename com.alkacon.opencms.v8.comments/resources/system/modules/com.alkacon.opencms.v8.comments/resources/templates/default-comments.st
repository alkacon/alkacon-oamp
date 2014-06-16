group comment;

/* Displayed on minimized start 
 * Arguments are:              
 *   messageType: What can be done on minimized start depends on the configuration and the rights of the user.
 *     Possibilities are: isManageModerated   - user can manage comments and comments are moderated
 *                        isManageUnmoderated - user can manage comments, but they are not moderated
 *                        isPost              - user can post comments
 *                        isView              - user can only view comments
 *                        isLogin             - a login facility can be provided (node that this can be true in several cases)
 *   countApprovedComments: number of approved comments
 *   countNewComments: number of new comments
 *   countBlockedComments: number of blocked comments
 *   countComments: total number of comments
 *   messages: The configured messagebundle, where messages are accessed as demonstrated.
 */ 
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

/* Displayed at top whenever comments are shown (minimized or not)
 * Arguments are:
 *   title: The headline element node of the "Comments" content
 */
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

/* Displayed in the non-minimized view on top of the comments list
 * Arguments are:
 *   canPostOrOfferLogin: flag indicating if the user can post or login should be offered
 *   canPost:             flag indicating if the user can post
 *   messages:            the configured message bundle
 */
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

/* Displays the login form.
 * Arguments are:
 *   hiddenFields: HTML code for the fields that contain data that necessary, but hidden to the user. These fields must be part of the form.
 *   messages:     the configured message bundle
 */
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

/* Displays the pagination when needed and also the filter for new, blocked etc. comments in moderation mode.
 * Arguments are:
 *   state: Object to check the filter state
 *   stateNum: id of the state currently choosen
 *   commentCount: number of comments to display
 *   pageNum: number of the page to display (starting at 1)
 *   list: number of comments to display on a single page
 *   needFilter: flag, indicating if the filter should be shown (i.e. if comments are moderated and managed)
 *   needPagination: flag, indicating if pagination is needed
 *   messages: the configured message bundle
 */
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

/* Classes to convert the boxColor that is configured in the element setting to a panel attribute */

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

/* Displays a single comment in management view. 
 * Arguments are:
 *   commentId: id of the comment to display
 *   commentCreationDate: date when the comment was created
 *   commentFields: further fields of the comment which depend on the configuration of the webform for comments
 *   messages: the configured message bundle
 *   state: Obect indicating the current state of the comment to display
 *   stateNum: id of the state of the comment to display
 *   isModerated: flag, indicating if the comment has to be displayed as moderated
 *   colorClass: the class generated by the above helper templates
 *   repliesOption: HTML code generated by the repliesOption stringtemplate
 *   countPostsByAuther: number of posts already posted by the author of the comment to display.
 */
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

/* Helper template */
disable(doDisable) ::= <<
  $if(doDisable)$
    disabled='disabled'
  $endif$
>>


/* Displays the option to reply to a comment and to view already existing comments 
 * Arguments are:
 *   noReplies: flag indicating that no replies are present
 *   exactlyOneReply: flag indicating that exactly one reply exists
 *   countReplies: number of replies present
 *   linkPost: link used to post new replies
 *   commentId: id of the comment where the reply option is for
 *   userCanManage: flag indicating if the current user can manage comments/replies
 *   userCanPost: flag indicating if the current user can post comments/replies
 *   messages: the configured message bundle
 */
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

/* Display a single reply in management view. 
 * See the manager template for details on the arguments.
 */
reply_manager(commentId,commentCreationDate,commentFields,messages,state,stateNum,isModerated,colorClass,repliesOption,countPostsByAuthor) ::= <<
	$manager(commentId=commentId,commentCreationDate=commentCreationDate,commentFields=commentFields,messages=messages,state=state,stateNum=stateNum,isModerated=isModerated,colorClass=colorClass,repliesOption=repliesOption,countPostsByAuthor=countPostsByAuthor)$
>>

/* Displays a single comment in non-management view.
 * Arguments are:
 *   colorClass: the color class generated by the above helper template.
 *   commentFields: the comments field, depending on the webform configuration
 *   commentCreationDate: creation date of the comment to display.
 *   messages: the configured message bundle
 *   repliesOption: flag indicating if replies are allowed 
 */
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

/* Displays a single reply in non-management view.
 * See the view template for details on the arguments. But note that there is no reply option for a reply.
 */
reply_view(colorClass,commentFields,commentCreationDate,messages) ::= <<
	$view(colorClass=colorClass,commentFields=commentFields,commentCreationDate=commentCreationDate,messages=messages)$
>>
