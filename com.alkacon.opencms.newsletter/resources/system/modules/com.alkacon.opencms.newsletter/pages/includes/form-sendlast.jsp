		<h2><%= cms.getConfigText("Subscribe/SendLast/Headline") %></h2>
		<p>
	  	<form name="sendlastnewsletter" class="newslettersubscription-form" action="<%= cms.link(cms.getRequestContext().getUri()) %>" method="post">
	  	  <input type="hidden" name="email" value="<%= cms.getEmail() %>" / >
	  	  <input type="hidden" name="action" value="<%= CmsNewsletterSubscriptionBean.ACTION_SENDLASTNEWSLETTER %>" / >
	  		<div class="newslettersubscription-check">
	  			<input type="checkbox" name="send" class="newslettersubscription-check" value="<%= CmsStringUtil.TRUE %>" checked="checked" />
	  			<%= cms.key("form.alknewsletter.sendlastnewsletter") %>
	  		</div>
	  		<div class="newslettersubscription-buttons">
	  			<input type="submit" value=" <%= cms.key("form.alknewsletter.buttonok.short") %> " />
	  		</div>
	  	</form>
	  	</p>