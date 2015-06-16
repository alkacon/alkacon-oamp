		<h3><%= cmsBean.getConfigText("Subscribe/SendLast/Headline") %></h3>
		<p>
	  	<form name="sendlastnewsletter" class="newslettersubscription-form" action="<%= cmsBean.link(cmsBean.getRequestContext().getUri()) %>" method="post">
	  	  <input type="hidden" name="email" value="<%= cmsBean.getEmail() %>" / >
	  	  <input type="hidden" name="action" value="<%= CmsNewsletterSubscriptionBean.ACTION_SENDLASTNEWSLETTER %>" / >
	  		<div class="checkbox newslettersubscription-check">
	  			<label>
					<input type="checkbox" name="send" class="newslettersubscription-check" value="<%= CmsStringUtil.TRUE %>" checked="checked" />
					<%= cmsBean.key("form.alknewsletter.sendlastnewsletter") %>
				</label>
	  		</div>
	  		<div class="newslettersubscription-buttons">
	  			<input type="submit" class="btn btn-default" value=" <%= cmsBean.key("form.alknewsletter.buttonok.short") %> " />
	  		</div>
	  	</form>
	  	</p>