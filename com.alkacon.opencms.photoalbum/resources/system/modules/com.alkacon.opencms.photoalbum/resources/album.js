var currentPage = 1;
var lastPage = 1;

function pageselectCallback(page_id, jq) {
	loadAlbumPage(page_id+1);
	return false;
}
		
$(document).ready(function() {
	// Create pagination element
	$("#Pagination").pagination(${imageCount}, {
		num_edge_entries: 2,
		num_display_entries: 8,
		prev_text: '<fmt:message key="photoalbum.pagination.prev" />',
		next_text: '<fmt:message key="photoalbum.pagination.next" />',
		items_per_page: ${thumbs.value['ItemsPerPage']},
		callback: pageselectCallback
    });
});
               
function loadAlbumPage(page) {
	lastPage = currentPage;
	currentPage = page;
	if ( $('#album_page_' + page).length == 0 ) {
		$('<div/>').load("${cms:vfs(pageContext).link['/system/modules/com.alkacon.opencms.photoalbum/elements/albumpage.jsp']}", {
			vfsFolder: '${album.value['VfsFolder'].stringValue}',
			background: '${thumbs.value['Background']}',
			size: '${thumbs.value['Size']}',
			quality: '${quality}',
			filter: '${thumbs.value['Filter']}',
			alignTitle: '${thumbs.value['AlignTitle']}',
			showTitle: '${thumbs.value['ShowTitle']}',
			showResourceNameAsTitle: '${album.value['ShowResourceNameAsTitle']}',
			page: page,
			itemsPerPage: '${thumbs.value['ItemsPerPage']}',
			maxImageSize: '${album.value['MaxImageSize']}'
		}, onPageLoad).attr('id', 'album_page_' + page).css('display', 'none').appendTo('#album_pages');
	} else {
		switchPage();
	}
}

function onPageLoad() {
	tb_init('#album_page_' + currentPage + ' a.thickbox');
	switchPage();
}

function afterFadeOut() {
	$('#album_page_' + lastPage).hide();
	$('#album_page_' + currentPage).css('opacity', '0').show().animate({opacity: '1'}, 'slow');
	fixAlbum();
}

function switchPage() {
	$('#album_page_' + lastPage).animate({opacity: '0'}, 'slow', null, afterFadeOut);
}

$(window).resize(fixAlbum);
$(document).ready(fixAlbumDelayed);

function fixAlbumDelayed() {
	setTimeout("fixAlbum();", 200);
}

function fixAlbum() {
	lastParent = 0;
	first = true;
	galleryitems = $(".album_box");
	galleryitems.each(function(i) {
		if (this.parentNode != lastParent) {
			lastTop = 0;
			rowHeight = 0;
			rowStart = i;
			lastParent = this.parentNode;
		}
		this.style.height = "auto";
		this.style.clear = "none";
		if (this.offsetTop != lastTop) {
			if (first == false) {
				this.style.clear = "left";
			}
		    rowHeight = this.offsetTop - lastTop - (parseInt($(this).css('marginTop')) + parseInt($(this).css('marginBottom')) + parseInt($(this).css('paddingBottom')) + parseInt($(this).css('paddingTop')));
			for (j=rowStart; j<i; j++) {
				galleryitems.get(j).style.height = rowHeight + "px";
		    }
			lastTop = this.offsetTop;
			rowStart = i;
			first = false;
		}
	});
}