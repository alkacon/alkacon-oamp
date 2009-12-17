(function($) {

/**
 * Helper to create 'on return' event handlers.<p>
 *
 * @callee {Element} The element that generated the event
 * @param {Function} handler the actual event handler
 *
 * @returns a new 'on return' event handler
 */ 
var onReturn = function(handler) {

  // return a new handler
  return function(/**jQuery.Event*/event, /**int*/ kc) {
    
    // check the key code depending on different browser implementations
    var /*int*/ key = (kc || event.keyCode || event.which || event.charCode);
    // all browsers use the same code for the return key
    if (key == 13) { 
      // call the actual event handler
      handler.call(this, event);
    }
  };
};

/** 
 * Converts a string into an associative array or map.
 *
 * @callee {null} not used
 * @param {String} value the string to convert
 * @param {String} entrySep the entry separator
 * @param {String} keyvalSep the key and value pair separator
 *
 * @return {AssocArray} the deserialized string
 */
var /**Function*/ stringToMap = function(/**String*/value,/**String*/entrySep,/**String*/keyvalSep) /**AssocArray*/ {
  var /**AssocArray*/ vals = {};
  $.each(value.split(entrySep), function(/**int*/i, /*String*/keyval) {
    var/**Array*/kv = keyval.split(keyvalSep);
    vals[kv[0]]=kv[1];
  });
  return vals;
};
    
/** 
 * Converts a associative array or map into an array.
 *
 * @callee {null} not used
 * @param {Map} map the map to convert
 * @param {String} entrySep the entry separator
 * @param {String} keyvalSep the key and value pair separator
 *
 * @return {String} the serialized string
 */
var /**Function*/ mapToString = function(/**Map*/value,/**String*/entrySep,/**String*/keyvalSep) /**String*/ {
  var /**String*/ vals = '';
  $.each(value, function(/**String*/key, /*String*/val) {
    if (vals != '') {
      vals = vals + entrySep;
    }
    vals = vals + key + keyvalSep + val;
  });
  return vals;
};
    
/**
 * Plugin to bind google map controls with an html form.<p>
 *
 * @callee {jQuery} control container
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 */ 
$.fn.mmMap = function(/**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/config = $.extend({}, $.fn.mmMap.defaults, options || {});

  // iterate the selected elements
  return this.each(function() {
    // this is the container
    var /**jQuery*/ $container = $(this);

    // compute element specific options
    var /**AsocArray*/opts = $.metadata ? $.extend({}, config, $container.metadata()) : config;

    // cache some controls
    var /**jQuery*/ $base = $container.find('input[type=hidden]:first');
    var /**jQuery*/ $lat = $container.find('input[type=text].map-lat');
    var /**jQuery*/ $lng = $container.find('input[type=text].map-lng');
    var /**jQuery*/ $address = $container.find('input[type=text].map-address');
    var /**jQuery*/ $zoom = $container.find('input[type=text].map-zoom');
    var /**jQuery*/ $width = $container.find('input[type=text].map-width');
    var /**jQuery*/ $height = $container.find('input[type=text].map-height');
    var /**jQuery*/ $type = $container.find('select.map-type');
    var /**jQuery*/ $map = $container.find('div.map-container');
    var /**jQuery*/ $mode = $container.find('input[type=radio][name=mode]');
    var /**jQuery*/ $allow = $container.find('input[type=checkbox].map-allow');
    
    var /**GMap2*/ map = null;
	
    // hide controls depending on options
    var /**Function*/ hideParent = function() { $(this).parent().hide(); };
    if ($.inArray('coords', opts.show) == -1) {
      $lat.add($lng).each(hideParent);
    }
    if ($.inArray('zoom', opts.show) == -1) {
      $zoom.each(hideParent);
    }
    if ($.inArray('address', opts.show) == -1) {
      $address.each(hideParent);
    }
    if ($.inArray('type', opts.show) == -1) {
      $type.each(hideParent);
    }
    if ($.inArray('mode', opts.show) == -1) {
      $mode.each(hideParent);
    }
    if ($.inArray('size', opts.show) == -1) {
      $width.add($height).each(hideParent);
    }
    if ($.inArray('allow', opts.show) == -1) {
      $allow.each(hideParent);
    }
    var baseId = opts.idPre ? opts.idPre : $base.attr('id');
    // assign labels to controls
    $container.mmIdentify({
      prefix: baseId + '-', 
      rules: [ 
        { find: 'label', attr: 'for' },
        { find: 'a.collapsible', attr: 'href' },
        { find: 'a.popup', attr: 'href' },
        { find: 'div.map', attr: 'class', nomatch: true }
      ]
    });
    
    // get the  initial values
    var /**AssocArray*/ vals = eval('({' + $base.val() + '})');
    // be sure to use default values if something is missing
    vals = $.extend({ lat: 0, lng: 0, zoom: 10, type: 'map', mode: 'dynamic', width: 400, height: 300 }, vals);
    vals.lat = vals.lat.toFixed(6);
    vals.lng = vals.lng.toFixed(6);
    vals.zoom = parseInt(vals.zoom);

  /**
   * Method for setting the address after dragging.
   *
   * @callee {not well documented by google} not used
   * @param {not well documented by google} response 
   */
  var /**Function*/ setAddress = function(response) {
  
    // check that everything is ok
    if (response && (response.Status.code == 200) && response.Placemark[0] && response.Placemark[0].address) {
      // set the new address
      $address.val(response.Placemark[0].address).change();
    } else {
      // reset the address
      $address.val('').change();
    }
  };

    var /**GLatLng*/ center = new GLatLng(vals.lat, vals.lng);
    // set the starting address 
    var /**GClientGeocoder*/ geocoder = new GClientGeocoder();
    geocoder.getLocations(center, setAddress);

    // disable coords depending on the options
    if ($.inArray('coords', opts.edit) == -1) {
      $lat.add($lng).attr('disabled', 'disabled');
    }
    // disable zoom depending on the options
    if ($.inArray('zoom', opts.edit) == -1) {
      $zoom.attr('disabled', 'disabled');
    }
    // disable type depending on the options
    if ($.inArray('type', opts.edit) == -1) {
      $type.attr('disabled', 'disabled');
    }
    // disable mode depending on the options
    if ($.inArray('mode', opts.edit) == -1) {
      $mode.attr('disabled', 'disabled');
    }
    // disable allow depending on the options
    if ($.inArray('allow', opts.edit) == -1) {
      $allow.attr('disabled', 'disabled');
    }
    // disable size depending on the options
    if ($.inArray('size', opts.edit) == -1) {
      $width.add($height).attr('disabled', 'disabled');
    }
    // disable address depending on the options
    if ($.inArray('address', opts.edit) == -1) {
      $address.attr('disabled', 'disabled');
    }
    
    /**
     * Method for centering the map on the given point.
     *
     * @callee {null} not used
     * @param {GLatLng} point where to center the map
     * @param {boolean} actCoords if to actualize the coordinate fields
     * @param {boolean} actAddress if to actualize the address field
     * @param {boolean} actMarker if to actualize the marker position
     */
    var /**Function*/setMapCenter = function(point, actCoords, actAddress, actMarker) {
      // set map center
      center = point;
      if (map) {
        map.panTo(point);
        map.setCenter(point);
      }
      if (actCoords) {
        // set coordinates
        $lat.val(point.lat().toFixed(6));
        $lng.val(point.lng().toFixed(6));
      }
      if (actAddress && geocoder) {
        // actualize address
        geocoder.getLocations(point, setAddress);
      }
      if (map && actMarker) {
        // move marker
        marker.setLatLng(point);
      }
      $lat.change();
    }
    
    // bind zoom to map
    $zoom.bind('keydown', onReturn(function() {
      var /**int*/zoom = parseInt($(this).val());
      $zoom.not(this).val(zoom);
      if (map) {
        map.setZoom(zoom);
      }
      setMapCenter(center);
    })).val(vals.zoom);

    // bind lat & lng to map
    $lat.bind('keydown', onReturn(function() { 
      setMapCenter(new GLatLng($lat.val(), $lng.val()), false, true, true);
    })).val(center.lat());
    $lng.bind('keydown', onReturn(function() { 
      setMapCenter(new GLatLng($lat.val(), $lng.val()), false, true, true);
    })).val(center.lng());

    var /**GControl*/ mapCtl = null;
    var /**GControl*/ typeCtl = null;
    // bind width & height to map
    $width.bind('keydown', onReturn(function() {
      var /**string*/ width = $(this).val();
      $width.not(this).val(width);
      $map.css('width', width);
      if (map) {
        map.checkResize();
        setMapCenter(marker.getLatLng());
      } else {
        setMapCenter(new GLatLng($lat.val(), $lng.val()));
      }
      if (map && (typeCtl != null)) {
        if (parseInt($width.val()) < 250) {
          if (typeCtl instanceof GMapTypeControl) {
            map.removeControl(typeCtl);
            typeCtl = new GMenuMapTypeControl();
            map.addControl(typeCtl);
          }
        } else {
          if (typeCtl instanceof GMenuMapTypeControl) {
            map.removeControl(typeCtl);
            typeCtl = new GMapTypeControl();
            map.addControl(typeCtl);
          }
        }
      }
    })).val(vals.width + 'px').trigger('keydown', [13]);
    $height.bind('keydown', onReturn(function() {
      var /**string*/ height = $(this).val();
      $height.not(this).val(height);
      $map.css('height', height);
      if (map) {
        map.checkResize();
        setMapCenter(marker.getLatLng());
      } else {
        setMapCenter(new GLatLng($lat.val(), $lng.val()));
      }
      if (map && (mapCtl != null)) {
        if (parseInt($height.val()) < 300) {
          if (mapCtl instanceof GLargeMapControl) {
            map.removeControl(mapCtl);
            mapCtl = new GSmallMapControl();
            map.addControl(mapCtl);
          }
        } else {
          if (mapCtl instanceof GSmallMapControl) {
            map.removeControl(mapCtl);
            mapCtl = new GLargeMapControl();
            map.addControl(mapCtl);
          }
        }
      }
    })).val(vals.height + 'px').trigger('keydown', [13]);
    
    /**
     * Method for setting the coordinates after selecting an address.
     *
     * @callee {null} not used
     * @param {GPlacemark} place the selected address
     */
    var /**Function*/ selectAddress = function(/**GPlacemark*/place) {
    
      var /**GLatLng*/point = new GLatLng(place.Point.coordinates[1], place.Point.coordinates[0]);
      setMapCenter(point, true, false, true);
      // set the address
      $address.val(place.address);
      // trigger value setter
      $lat.change();
    };
    
    // bind the address to the map
    $address.bind('keydown', onReturn(function() {
      var /**jQuery*/ thisAddr = $(this);
      var /**String*/ address = thisAddr.val();
      geocoder.getLocations(address, function(response) {
        // check to see if we have at least one valid address
        if (!response || (response.Status.code != 200) || !response.Placemark.length) {
          alert("address not found");
          return;
        }
        // in case of only one place,  just set it
        if (response.Placemark.length == 1) {
          selectAddress(response.Placemark[0]);
          return;
        }
        // in case of more, display a selection list
        var /**jQuery*/ $span = thisAddr.parent();
        // prepare the data to display
        var data = $.map(response.Placemark, function(place, i) { return { id: i, value: place.address}; });
        // display options
        var opts = {
	  onChange: function(item) {
	    // set the selected adress
	    selectAddress(response.Placemark[$(item).attr('href').substring(1)]);
	  },
	  onClose: function() { 
	    // style managing
	    thisAddr.focus().removeClass('ui-state-active'); 
	    $span.removeClass('ui-state-active'); 
	  }
        };
        // show the list
        $span.mmSuggest(data, opts);
        // style managing
	thisAddr.blur().addClass('ui-state-active');
	$span.addClass('ui-state-active');
      });
    })).val('');
    
    // select the initial mode
    $container.find('input[type=radio].map-mode-'+vals.mode).attr('checked', 'checked');
    
    // ensure than any change will be applied to the main control
    $lat.add($lng).add($zoom).add($width).add($height).add($type).add($mode).change(function() {
      var /**Map*/ values = vals;
	  if ($lat.length) {
        values.lat   = $lat.val();
      }
	  if ($lng.length) {
        values.lng   = $lng.val();
      }
	  if ($zoom.length) {
        values.zoom  = $zoom.val();
      }
	  if ($width.length) {
        values.width = parseInt($width.val());
      }
	  if ($height.length) {
        values.height= parseInt($height.val());
      }
	  if ($type.length) {
        values.type  = "'" + $type.val() + "'";
      }
	  if ($mode.length) {
        values.mode  = "'" + $mode.filter(':checked').val() + "'"; 
      }
      $base.val(mapToString(values, ',', ':'));
    });
    // ensure all controls of the same type have the same value
    $address.change(function() {
      $address.not(this).val($(this).val());
    });
    $lat.change(function() {
      $lat.not(this).val($(this).val());
    });
    $lng.change(function() {
      $lng.not(this).val($(this).val());
    });
    $zoom.change(function() {
      $zoom.not(this).val($(this).val());
    });
    $width.change(function() {
      $width.not(this).val($(this).val());
    });
    $height.change(function() {
      $height.not(this).val($(this).val());
    });
    $type.change(function() {
      $type.not(this).val($(this).val());
    });
    $mode.change(function() {
      $mode.not(this).val($(this).val());
    });
    
    // style check boxes
    $allow.mmToggleBar();
    // style input text boxes
    $container.find('input[type=text]').mmInput();
    // style radio buttons
    $container.find('input[type=radio]').mmRadio();

    // enable option panel expander
    $container.find('a.collapsible')
      .mmCollapsible({ lessText: opts.lessText, moreText: opts.moreText, startOpen: false, popupOpts: { title: 'Map Widget' }, onCollapse: function() { $lat.change(); } })
      .bind('click.first', function() {
        $(this).unbind('click.first');

  if ($map.length) {
    // create and center the map
    map = new GMap2($map.find('div.map').get(0));
    map.setCenter(center, parseInt(vals.zoom));
  }
  var /**boolean*/mapEnabled = (map && ($.inArray('map', opts.edit) != -1));
  if ($.inArray('zoom', opts.edit) == -1) {
    // disable zoom depending on the options
    if (map) {
      map.disableDoubleClickZoom();
      map.disableScrollWheelZoom();
    }
  } else if (mapEnabled) {
    // choose control depending on the map size
    if (parseInt(vals.height) < 300) {
      mapCtl = new GSmallMapControl();
    } else {
      mapCtl = new GLargeMapControl();
    }
    map.addControl(mapCtl);
    // enable some additional zoom possibilities
    map.enableDoubleClickZoom();
    map.enableScrollWheelZoom();
  }
  if (map && !mapEnabled) {
    // disable map depending on the options
    map.disableDoubleClickZoom();
    map.disableScrollWheelZoom();
    map.disableDragging();
  }
  // enable type depending on the options
  if (mapEnabled && ($.inArray('type', opts.edit) != -1)) {
    // choose control depending on the map size
    if (parseInt(vals.width) < 250) {
      typeCtl = new GMenuMapTypeControl();
    } else {
      typeCtl = new GMapTypeControl();
    }
    map.addControl(typeCtl);
  }
  // enable map size controls
  if (mapEnabled && ($.inArray('size', opts.edit) != -1)) {
    // make the map resizable
    $map.resizable({
      autoHide: true,
      grid: [10, 10],
      stop: function(/**Event*/event, /**UIHelper*/ui) {
        var /**String*/width = $map.css('width');
        var /**String*/height = $map.css('height');
        $width.val(width);
        $height.val(height)
          .add($width).trigger('keydown', [13]);
      },
      resize: function(/**Event*/event, /**UIHelper*/ui) {
        // HACK: we can not use ui.size because of bug #4500
        var /**String*/width = $map.css('width');
        var /**String*/height = $map.css('height');
        if ((width == $width.val()) && (height == $height.val())) {
          // prevent unneeded calls
          return;
        }
        $width.val(width);
        $height.val(height)
          .add($width).trigger('keydown', [13]);
        }
    });
  }

  // handle double click
  if (mapEnabled) {
    GEvent.addListener(map, "dblclick", function(/**GOverlay*/ overlay, /**GLatLng*/ point) {
      setMapCenter(point, true, true, true);
    });
  }
  
  if (map) {
    // create marker in the center
    marker = new GMarker(map.getCenter(), {
      draggable: mapEnabled // enable dnd
    });
  
    if (mapEnabled) {
      // handle marker dnd
      GEvent.addListener(marker, "dragend", function() {
        setMapCenter(marker.getLatLng(), true, true, false);
      });
    }
    // position marker
    map.addOverlay(marker);
    // marker.openInfoWindowHtml('Drag Me!');
  }

  // bind zoom to map
  if (mapEnabled) {
    GEvent.addListener(map, "zoomend", function(oldZoom, newZoom) {
      $zoom.val(map.getZoom());
      setMapCenter(marker.getLatLng());
    });
  }

  if (map) {
    // fill the supported map types
    var types = {};
    $.each(map.getMapTypes(), function(/**int*/i, /**GMapType*/type) {
      // remember the type
      types[type.getName().toLowerCase()] = type;
      // create a new option for this type
      $type.append($("<option />").attr('value', type.getName().toLowerCase()).text(type.getName()));
    });
    // bind type to map
    $type.bind('change', function() {
      map.setMapType(types[$type.val()]);
    });
    if (mapEnabled) {
      GEvent.addListener(map, "maptypechanged", function() {
        $type.val(map.getCurrentMapType().getName().toLowerCase());
      });
    }
    $type.val(vals.type);
  } else {
    // TODO: to fill the map types even without map
    $type.append($("<option />").attr('value', 'map').text('Map'));
    $type.val('map');
  }
  // style select boxes
  $type.mmSelect();
  		
	});
  });
};

/**
 * Plugin Defaults:
 * 
 * idPre : {String}  string to prefix all control ids, or false to use input[type=hidden]:first id
 * edit  : {Array}   the list of editable properties
 */
$.fn.mmMap.defaults = {
  idPre : /**String*/  false,
  edit  : /**Array*/   ['zoom', 'size', 'mode', 'type', 'address'],
  show  : /**Array*/   ['zoom', 'size', 'mode', 'type', 'address', 'coords', 'map'],
  button: /**Map*/     { lessText: 'Less', moreText: 'More' }
};

// free memory
$(window).unload(function () { 
  GUnload(); 
});

// install the widgets after loading
$(document).ready(function() {
  $('input[type=hidden].map-widget').each(function() {
    $(this).parent('div').mmMap({
      idPre: this.id.replace(/[\._]/g, '-')
    });
  });
});
  
})(jQuery);

