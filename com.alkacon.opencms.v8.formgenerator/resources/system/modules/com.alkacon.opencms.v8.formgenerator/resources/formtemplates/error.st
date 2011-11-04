<h1>$errorheadline$</h1>
<p>$errortext$</p>
$if(errortemplatenames)$
<p><strong>Existing templates:</strong></p>
<ul>
$errortemplatenames:{
	<li>$it$</li>
}$
</ul>
$endif$