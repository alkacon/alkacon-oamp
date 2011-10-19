if (!colorboxConfig) {
	var colorboxConfig = {
		close: 'Close',
		next: 'Next >',
		previous: '< Prev',
		current: 'Image {current} of {total}',
		maxWidth: '98%',
   		maxHeight: '98%'
	};
}


$(document).ready(function() {
	$("a.thickbox").colorbox(
		colorboxConfig
	);
});