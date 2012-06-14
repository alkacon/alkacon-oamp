		<h2><%= cmsBean.getConfigText("Subscribe/SendLast/Headline") %></h2>
		<p>
	  	<form name="sendlastnewsletter" class="newslettersubscription-form" action="<%= cmsBean.link(cmsBean.getRequestContext().getUri()) %>" method="post">
	  	  <input type="hidden" name="email" value="<%= cmsBean.getEmail() %>" / >
	  	  <input type="hidden" name="action" value="<%= CmsNewsletterSubscriptionBean.ACTION_SENDLASTNEWSLETTER %>" / >
	  		<div class="newslettersubscription-check">
	  			<input type="checkbox" name="send" class="newslettersubscription-check" value="<%= CmsStringUtil.TRUE %>" checked="checked" />
	  			<%= cmsBean.key("form.alknewsletter.sendlastnewsletter") %>
	  		</div>
	  		<div class="newslettersubscription-buttons">
	  			<input type="submit" value=" <%= cmsBean.key("form.alknewsletter.buttonok.short") %> " />
	  		</div>
	  	</form>
	  	</p>