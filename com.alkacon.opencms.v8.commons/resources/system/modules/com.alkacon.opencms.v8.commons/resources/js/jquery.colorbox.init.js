if (!colorboxConfig) {
	var colorboxConfig = {
		close: 'Close',
		next: 'Next >',
		previous: '< Prev',
		current: 'Image {current} of {total}'
	};
}


$(document).ready(function() {
	$("a.thickbox").colorbox(
		colorboxConfig
	);
});