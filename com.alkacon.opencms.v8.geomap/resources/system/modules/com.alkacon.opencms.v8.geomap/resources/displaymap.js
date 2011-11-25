// When leaving clean the map
$(document).unload(function() {GUnload();});
// Activate maps onload
$(document).ready(function() {
  $('div.map').each(function() {
    var $map = $(this);
    var opts = $map.metadata();
    $map.css('width', opts.width);
    $map.css('height', opts.height);
    if (opts.mode == 'dynamic') {
      var gMap = new GMap2($map.get(0));
      gMap.setCenter(new GLatLng(opts.lat, opts.lng), parseInt(opts.zoom));
      gMap.addControl(new GLargeMapControl());
      gMap.addControl(new GMapTypeControl());
      gMap.addOverlay(new GMarker(gMap.getCenter()));
      // set map type
      $.each(gMap.getMapTypes(), function(/**int*/i, /**GMapType*/type) {
        if (opts.type == type.getName().toLowerCase()) {
          gMap.setMapType(type);
          return false;
        }
      });
    } else {
      var url="http://maps.google.com/staticmap?";
      url += "center=" + opts.lat + "," + opts.lng;
      url += "&zoom=" + opts.zoom;
      url += "&size=" + opts.width + "x" + opts.height;
      url += "&maptype=" + opts.maptype;
      url += "&markers=" + opts.lat + "," + opts.lng;
      url += "&key=<%= gmapsKey %>";
      url += "&sensor=false";
      $map.append($('<img />')
        .attr('width', opts.width)
        .attr('height', opts.height)
        .attr('src', url)
        .attr('alt', 'map')
      );
    }
  });
});
