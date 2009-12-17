
(function($) {

/**
 * Plugin to create element ids, assigning labels to the right controls.<p>
 *
 * @callee {jQuery} control container
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 */ 
$.fn.mmIdentify = function(/**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/config = $.extend(true, {}, $.fn.mmIdentify.defaults, options || {});
  // counter for generating ids in case of conflict
  var /**int*/count = 0;

  // iterate the selected elements
  return this.each(function() {
    // this is the container
    var /**jQuery*/ $container = $(this);

    // compute element specific options
    var /**AsocArray*/opts = $.metadata ? $.extend({}, config, $container.metadata()) : config;
    
    $.each(opts.rules, function(i, rule) {
      $container.find(rule.find).each(function() {
        var /**jQuery*/ $elem = $(this);
        var /**String*/ ref = $elem.attr(rule.attr);
        if (rule.attr == 'href') {
          // remove hash char
          ref = ref.substring(1);
        }
        var /**String*/ baseId = opts.prefix + ref + opts.postfix;
        var /**String*/ newId = baseId;
        while ($('#' + newId).length) {
          // generate a new id until it is unique
          count ++;
          newId = baseId + count;
        }
        if (!rule.nomatch) {
          $container.find('.' + ref + ':first').attr('id', newId);
          if (rule.attr == 'href') {
            // add hash char
            newId = '#' + newId;
          }
          $elem.attr(rule.attr, newId);
        } else {
          $elem.attr('id', newId);
        }
      });
    });
  });
};

/**
 * Plugin Defaults:
 *
 * prefix  : {String}  the prefix for new generated ids
 * postfix : {String}  the postfix for new generated ids
 * rules   : {Array}   the array of rules. 
 *                     where each rule can have following attributes: 
 *                     'find' jquery expression to match, 
 *                     'attr' the attribute of the matched element to read as classname
 *                     'nomatch' boolean, 
 *                       if false only the matched element will get an id equals to pre+attr+post
 *                       if true the matched element will get a new value for the given attribute equals to pre+attr+post
 *                         additionally the element with a class equals to the matched element given attribute value will get the id pre+attr+post
 */
$.fn.mmIdentify.defaults = {
  prefix  : /** String */ '',
  postfix : /** String */ '',
  rules   : /** Array */  [ { find: 'label', attr: 'for' } ]
};

/**
 * Plugin to create a button to control a collapsible panel on click.<p>
 *
 * @callee {jQuery} the button, anchor where the href is the id of the detail panel
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 *
 * @TODO add some animation to the hide and show actions
 * @TODO style disabled buttons
 */ 
$.fn.mmCollapsible = function(/**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/config = $.extend(true, {}, $.fn.mmCollapsible.defaults, options || {});

  // iterate the selected elements
  return this.each(function(/**int*/i) {
    // this is the button
    var /**jQuery*/$button = $(this);
    // compute element specific options
    var /**AsocArray*/opts = $.metadata ? $.extend({}, config, $button.metadata()) : config;
    // get the detail panel
    var /**jQuery*/$detailPanel = $($button.attr('href'));

    // create the actual needed markup
    $button
      .attr('title', opts.lessText)
      .addClass('fg-button ui-widget ui-corner-all fg-button-icon-solo ui-state-default')
      .addClass(opts.lessClass)
      .append($('<span />').addClass('ui-icon').addClass(opts.lessIcon).text(opts.lessText));
    
    if (opts.popup) {
     // enable popup
      $detailPanel.dialog($.extend({}, { close: function() { $button.click(); } }, opts.popupOpts));
    }

    $button.toggle(function() {
      // activate the 'show more' button
      $(this)
        .attr('title', opts.moreText)
        .removeClass(opts.lessClass)
        .addClass(opts.moreClass)
        .find('span')
          .removeClass(opts.lessIcon)
          .addClass(opts.moreIcon)
          .text(opts.moreText);
      // hide the detail panel
      if (!opts.popup) {
        $detailPanel.hide();
      }
      // fire event
      if ($.isFunction(opts.onCollapse)) {
        opts.onCollapse.call(this, opts);
      }
    }, function() {
      // activate the 'show less' button
      $(this)
        .attr('title', opts.lessText)
        .removeClass(opts.moreClass)
        .addClass(opts.lessClass)
        .find('span')
          .removeClass(opts.moreIcon)
          .addClass(opts.lessIcon)
          .text(opts.lessText);
      // show the detail panel
      if (opts.popup) {
        $detailPanel.dialog('open');
      } else {
        $detailPanel.show();
      }
      // fire event
      if ($.isFunction(opts.onExpand)) {
        opts.onExpand.call(this, opts);
      }
    }).hover(function() {
        // on mouseover
        var $icon = $(this)
          .addClass(opts.hoverClass)
          .find('span');
        // change icon
        if ($(this).hasClass(opts.lessClass)) {
          $icon.removeClass(opts.lessIcon)
            .addClass(opts.moreIcon);
        } else {
          $icon.removeClass(opts.moreIcon)
            .addClass(opts.lessIcon);
        }
    }, function() {
        // on mouseout
        var $icon = $(this)
          .removeClass(opts.hoverClass)
          .find('span');
        // restore icon
        if ($(this).hasClass(opts.moreClass)) {
          $icon.removeClass(opts.lessIcon)
            .addClass(opts.moreIcon);
        } else {
          $icon.removeClass(opts.moreIcon)
            .addClass(opts.lessIcon);
        }
    }).focus(function() {
        $(this).addClass(opts.focusClass);
    }).blur(function() {
        $(this).removeClass(opts.focusClass);
    });
    // close the detail panel if requested
    if (!opts.startOpen) {
      $button.click();
    }
  });
};

/**
 * Plugin Defaults:
 * 
 * lessText  : {String}   The text to use for the 'show less' button
 * lessClass : {String}   The class to use to identify the 'show less' button
 * lessIcon  : {String}   The icon class to use for the 'show less' button
 * moreText  : {String}   The text to use for the 'show more' button
 * moreClass : {String}   The class to use to identify the 'show more' button
 * moreIcon  : {String}   The icon class to use for the 'show more' button
 * hoverClass: {String}   The class to toggle on hover
 * focusClass: {String}   The class to toggle on focus
 * startOpen : {boolean}  If the detail panel should start expanded
 * onExpand  : {Function} Callback after expanding
 * onCollapse: {Function} Callback after collapsing
 * popup     : {boolean}  If to use a popup instead of a panel
 * popupOpts : {Map}      The popup options siehe UI/Dialog
 */
$.fn.mmCollapsible.defaults = {
  lessText  : /** String */ 'Less',
  lessClass : /** String */ 'mm-collapse-less',
  lessIcon  : /** String */ 'ui-icon-triangle-1-s',
  moreText  : /** String */ 'More',
  moreClass : /** String */ 'mm-collapse-more',
  moreIcon  : /** String */ 'ui-icon-triangle-1-e',
  hoverClass: /** String */ 'ui-state-hover',
  focusClass: /** String */ 'ui-state-focus',
  startOpen : /** boolean*/ true,
  onExpand  : /** Function*/false,
  onCollapse: /** Function*/false,
  popup     : /** boolean */false,
  popupOpts : /** Map */    {
        autoOpen: false, 
        closeOnEscape: true, 
        modal: true, 
        resizable: true, 
        width: 800,
        minWidth: 300, 
        minHeight: 300, 
        maxWidth: 5000, 
        maxHeight: 5000,
        buttons: { "Ok": function() { 
                            $(this).dialog("close"); 
                         }
                 } 
      }
};

/**
 * Plugin to style text boxes with the theme roller.<p>
 *
 * @callee {jQuery} the text boxes to style
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 */ 
$.fn.mmInput = function(/**AsocArray*/options) /**jQuery*/{

  // compute options before element iteration
  var /**AsocArray*/config = $.extend(true, {}, $.fn.mmInput.defaults, options || {});

  // iterate the selected elements
  return this.each(function(i) {
  
    // this is the input
    var /**jQuery*/$input = $(this);
    // compute element specific options
    var /**AsocArray*/opts = $.metadata ? $.extend({}, config, $input.metadata()) : config;
    
    // wrap for styling
    var /**jQuery*/$span = $("<span />")
      .addClass('ui-widget ui-corner-all ui-state-default')
      .addClass(opts.className);
    $input.addClass('ui-state-default').wrap($span);
    var /**jQuery*/$wrapper = $input.parent();
    
    // style the input box
    $input.css( {
       backgroundColor: 'transparent', // we want to see the background provided by the wrapper
       padding: 0,                     // no padding
       borderLeft: 'none',             // these borders are also provided by the wrapper
       borderRight: 'none'
    });
   
    if ($input.filter(":disabled").length) {
       // disable
       $wrapper.addClass(opts.disabledClass);
       $input.addClass(opts.disabledClass);
       var /**int*/w = $input.width();
       $wrapper.append("<span/>");
       var /**int*/pr = parseInt($wrapper.css('paddingRight'));
       $input.hide().change(function() {
         $wrapper.find('span').text($input.val());
         $wrapper.css('paddingRight', pr+'px');
         var /**int*/w2 = $wrapper.width();
         $wrapper.css('paddingRight', (pr+w-w2)+'px');
       }).change();
       return;
    } 
    
    // activate events
    $input.focus(function() {
       $wrapper.addClass(opts.focusClass);
       $input.addClass(opts.focusClass);
    }).blur(function() {
       $wrapper.removeClass(opts.focusClass);
       $input.removeClass(opts.focusClass);
    }).hover(function() {
       // on mouse over
       $wrapper.addClass(opts.hoverClass);
       $input.addClass(opts.hoverClass);
    }, function() {
       // on mouse out
       $wrapper.removeClass(opts.hoverClass);
       $input.removeClass(opts.hoverClass);
    });
  });
};

/**
 * Plugin Defaults:
 * 
 * disabledClass: {String} The class to use for disabled widgets
 * hoverClass   : {String} The class to toggle on hover
 * focusClass   : {String} The class to toggle on focus
 * className    : {String} The class name to use for the wrapping span
 */
$.fn.mmInput.defaults = {
  disabledClass: /**String*/ 'ui-state-disabled',
  hoverClass   : /**String*/ 'ui-state-hover',
  focusClass   : /**String*/ 'ui-state-focus',
  className    : /**String*/ 'mm-input' // set the padding-left and right to the border-radius of your theme
};

/**
 * Plugin to style select controls with the theme roller.<p>
 *
 * @callee {jQuery} the select controls to style
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 * 
 * @TODO: support size > 1
 * @TODO: support multiple selection
 * @TODO: support disabled option
 * @TODO: support option group
 * @TODO: width handling
 */ 
$.fn.mmSelect = function(/**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/config = $.extend(true, {}, $.fn.mmSelect.defaults, options || {});

  // iterate the selected elements
  return this.each(function(i) {
  
    // this is the select
    var /**jQuery*/$select = $(this);
    // compute element specific options
    var /**AsocArray*/opts = $.metadata ? $.extend({}, config, $select.metadata()) : config;

    $select.hide(); // hide original select
    
    // create new select box
    var /**jQuery*/$newSel = $("<a/>").attr('href', '#');
    if ($select.is(':disabled')) {
      $newSel = $("<span/>");
    }
    $newSel  
      .addClass('fg-button mm-select fg-button-icon-right ui-widget ui-corner-all ui-state-default')
      .addClass(opts.className);
      
    // set title if present
    if (opts.title) {
      $newSel.attr('title', opts.title);
    }
    
    // insert the new select box
    $select.before($newSel);
    $newSel = $select.prev(); // the insertion will clone it, so we have to getthe actual node
    $newSel
      .append($('<span />').addClass('menu-selection'))
      .append($("<span />").addClass('ui-icon ui-icon-triangle-1-s'));
      
    if ($select.is(':disabled')) {
      $newSel.addClass('ui-state-disabled');
      // set the right option into the new select box
      $newSel.find('.menu-selection').text($select.find('option:selected').text()); 
      return;
    }

    // collect the options
    var /**jQuery*/$opts = $("<ul />");
    $select.find('option').each(function() {
      var /**jQuery*/$opt = $(this);
      // create the new option
      var /**jQuery*/$newOpt = $("<a />")
        .attr('href', '#' + $opt.attr('value'));
      if ($opt.is(':disabled')) {
        $newOpt =  $("<span />").addClass('ui-state-disabled');
      }
      $newOpt
        .attr('title', $opt.attr('title'))
        .append($('<span />').text($opt.text()));
      // append it
      $opts.append($("<li />").append($newOpt));
    });
    
    // activate the new select box
    $newSel.menu({ 
      content: $opts.wrap("<div/>").parent().html(), 
      flyOut: true, 
      width: '', /* remove default width */
      maxHeight: 50, /* not working */
      onChange: function(item) { 
        // remove hover and foxus effects
        $newSel.removeClass(opts.hoverClass).removeClass(opts.focusClass); 
        // set the new value
        $select.val($(item).attr('href').substring(1));
        // trigger the change
        $select.change();
      } 
    });
    
    // on change of the original select 
    $select
      .change(function() { 
        // set the right option into the new select box
        $newSel.find('.menu-selection').text($select.find('option:selected').text()); 
      })
      .change() // trigger change
      .listenForChange({interval: 500}); // listen for change twice per second
    
    // activate events on new select box
    $newSel.focus(function() {
      $(this).addClass(opts.focusClass);
    }).blur(function() {
      $(this).removeClass(opts.focusClass);
    }).hover(function() {
      // on mouse over
      $(this).addClass(opts.hoverClass);
    }, function() {
      // on mouse out
      $(this).removeClass(opts.hoverClass); 
    });
  });    
};

/**
 * Plugin Defaults:
 * 
 * title: The title to use for the new select box
 * disabledClass: The class to use for disabled widgets
 * hoverClass: The class to toggle on hover
 * focusClass: The class to toggle on focus
 * className: The class name to use for the wrapping span
 */
$.fn.mmSelect.defaults = {
  title: false, // no title
  disabledClass: 'ui-state-disabled',
  hoverClass: 'ui-state-hover',
  focusClass: 'ui-state-focus',
  className: 'mm-select' // set the padding-left and right to the border-radius of your theme
};

/**
 * Plugin to style radio buttons with the theme roller.<p>
 *
 * @callee {jQuery} the radio buttons to style
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 */ 
$.fn.mmRadio = function(/**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/opts = $.extend(true, {}, $.fn.mmRadio.defaults, options || {});

  // this is all radio buttons
  var /**jQuery*/$allRadios = this;
  // compute the different groups
  var /**Array*/ groups = [];
  $allRadios.each(function() { 
    var /**String*/group = $(this).attr('name'); 
    if ($.inArray(group, groups) < 0) {
      groups.push(group);
    }
  });

  // iterate the groups
  $.each(groups, function(/**int*/i) {
  
    // select radio buttons of a single group
    var $group = $allRadios.filter('[name='+groups[i]+']');
    // hide them
    $group.hide();

    // add the new group before the first radio
    $group.eq(0).before($('<span />').addClass('fg-buttonset fg-buttonset-single'));
    var /**jQuery*/$newGroup = $group.eq(0).prev('span.fg-buttonset-single');
    
    $group.each(function(/**int*/j) {
      // this is the original radio button
      var /**jQuery*/$radio = $(this);
    
      // create a new one
      var $newRadio = $('<a />');
      if ($radio.is(':disabled')) {
        // generate a <span> instead of <a>
        $newRadio = $('<span />');
        $newRadio.addClass('ui-state-disabled');
      } else {
        // save value
        $newRadio.attr('href', '#' + $radio.attr('value'));
      }
      // style the new radio button
      $newRadio.addClass('fg-button ui-widget ui-state-default');
      // take care of the corners
      if (j == 0) {
        $newRadio.addClass('ui-corner-left');
      } else if (j == $group.length - 1) {
        $newRadio.addClass('ui-corner-right');
      }
      // activate selected
      if ($radio.is(':checked')) {
        $newRadio.addClass('ui-state-active');
      }
      // get the label
      var $label = $radio.parents('form').find('label[for='+$radio.attr('id')+']');
      $label.hide();
      $newRadio.attr('title', $label.text()).text($label.text());
      $newRadio.data('radio', $radio);
      // add it to the group
      $newGroup.append($newRadio);
    });
    
    // handle click
    $newGroup.find('a.fg-button:not(.'+opts.disabledClass+')').click(function(evt) {
      var $radio = $(this);
      $radio
        .parents('.fg-buttonset-single:first').find(".fg-button."+opts.activeClass)
        .removeClass(opts.activeClass)
        .each(function() {
          if ($(this).data('radio').attr('checked')) {
            $(this).data('radio').attr('checked', '');
          }
        });
      if ($radio.is('.ui-state-active.fg-button-toggleable, .fg-buttonset-multi .'+opts.activeClass)) {
        $radio.removeClass(opts.activeClass);
        $radio.data('radio').attr('checked', '');
      } else {
        $radio.data('radio').attr('checked', 'checked').change();
        $radio.addClass(opts.activeClass).focus();
      }
      if ( !$radio.is('.fg-button-toggleable, .fg-buttonset-single .fg-button,  .fg-buttonset-multi .fg-button')) {
        $radio.removeClass(opts.activeClass);
      }
      evt.stopPropagation();
      evt.preventDefault();
    }).hover(function() {
      // on mouse over
      $(this).addClass(opts.hoverClass);
    }, function() {
      // on mouse out
      $(this).removeClass(opts.hoverClass);
    }).focus(function() {
      $(this).addClass(opts.focusClass);
    }).blur(function() {
       $(this).removeClass(opts.focusClass);
    });
  });
  
  return this;
};

/**
 * Plugin Defaults:
 * 
 * activeClass: The class to use for active buttons
 * disabledClass: The class to use for disabled buttons
 * hoverClass: The class to toggle on hover
 * focusClass: The class to toggle on focus
 * className: The class name to use for the wrapping span
 */
$.fn.mmRadio.defaults = {
  activeClass: 'ui-state-active',
  disabledClass: 'ui-state-disabled',
  hoverClass: 'ui-state-hover',
  focusClass: 'ui-state-focus',
  className: 'mm-radio' 
};


/**
 * Plugin to style check boxes as toogle bar with the theme roller.<p>
 *
 * @callee {jQuery} the check boxes to style
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 */ 
$.fn.mmToggleBar = function(/**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/opts = $.extend(true, {}, $.fn.mmToggleBar.defaults, options || {});

  var /**jQuery*/$allChecks = this;

  // hide them
  $allChecks.hide();
  // add the new toolbar before the first checkbox
  $allChecks.eq(0).before($('<span />').addClass('fg-buttonset fg-buttonset-multi'));
  var /**jQuery*/$toolbar = $allChecks.eq(0).prev('span.fg-buttonset-multi');

  // iterate the check boxes
  this.each(function(/**int*/i) {
  
      // this is the original radio button
      var /**jQuery*/$check = $(this);
    
      // create a new one
      var $newCheck = $('<a />');
      if ($check.is(':disabled')) {
        // generate a <span> instead of <a>
        $newCheck = $('<span />');
        $newCheck.addClass('ui-state-disabled');
      } else {
        // save value
        $newCheck.attr('href', '#' + $check.attr('value'));
      }
      // style the new radio button
      $newCheck.addClass('fg-button ui-widget ui-state-default');
      // take care of the corners
      if (i == 0) {
        $newCheck.addClass('ui-corner-left');
      } else if (i == $allChecks.length - 1) {
        $newCheck.addClass('ui-corner-right');
      }
      // activate selected
      if ($check.is(':checked')) {
        $newCheck.addClass('ui-state-active');
      }
      // get the label
      var $label = $check.parents('form').find('label[for='+$check.attr('id')+']');
      $label.hide();
      $newCheck.attr('title', $label.text()).text($label.text());
      $newCheck.data('check', $check);
      // add it to the group
      $toolbar.append($newCheck);
    });
    
    // handle click
    $toolbar.find('a.fg-button:not(.'+opts.disabledClass+')').click(function(evt) {
      var $check = $(this);
      if ($check.is('.ui-state-active.fg-button-toggleable, .fg-buttonset-multi .'+opts.activeClass)) {
        $check.removeClass(opts.activeClass);
        $check.data('check').attr('checked', '').change();
      } else {
        $check.data('check').attr('checked', 'checked').change();
        $check.addClass(opts.activeClass).focus();
      }
      if ( !$check.is('.fg-button-toggleable, .fg-buttonset-single .fg-button,  .fg-buttonset-multi .fg-button')) {
        $check.removeClass(opts.activeClass);
      }
      evt.stopPropagation();
      evt.preventDefault();
    }).hover(function() {
      // on mouse over
      $(this).addClass(opts.hoverClass);
    }, function() {
      // on mouse out
      $(this).removeClass(opts.hoverClass);
    }).focus(function() {
      $(this).addClass(opts.focusClass);
    }).blur(function() {
       $(this).removeClass(opts.focusClass);
    });
  
  return this;
};

/**
 * Plugin Defaults:
 * 
 * activeClass: The class to use for active buttons
 * disabledClass: The class to use for disabled buttons
 * hoverClass: The class to toggle on hover
 * focusClass: The class to toggle on focus
 * className: The class name to use for the wrapping span
 */
$.fn.mmToggleBar.defaults = {
  activeClass: 'ui-state-active',
  disabledClass: 'ui-state-disabled',
  hoverClass: 'ui-state-hover',
  focusClass: 'ui-state-focus',
  className: 'mm-check' 
};


/**
 * Plugin to create a suggestion box styleable with the theme roller.<p>
 *
 * @callee {jQuery} the element to attach the suggestion box to, everything else than the first element is ignored
 * @param {Array} data the suggest data, ie [{id:1,value:'one'},{id:2,value:'two'}]
 * @param {AsocArray} options the options
 *
 * @returns the callee to keep the chain
 *
 * @author Michael Moossen
 * @version 0.9
 * 
 * @TODO: support size > 1
 * @TODO: support multiple selection
 * @TODO: support disabled select and/or option
 */ 
$.fn.mmSuggest = function(/**Array*/data, /**AsocArray*/options) /**jQuery*/ {

  // compute options before element iteration
  var /**AsocArray*/config = $.extend(true, {}, $.fn.mmSuggest.defaults, options || {});

  // narrow to only one element
  var $obj = this.eq(0);
  
  // iterate the selected elements
  return $obj.each(function() {
  
    // this is the element
    var /**jQuery*/$elem = $(this);
    // compute element specific options
    var /**AsocArray*/opts = $.metadata ? $.extend({}, config, $elem.metadata()) : config;

    // create the content
    var /**jQuery*/$ul = $("<ul />");
    $.each(data, function(/**int*/i, /**AsocArray*/item) {
      $ul
        .append($('<li/>')
          .append($('<a/>')
            .attr('href', '#'+item.id)
            .append($('<span />')
              .text(item.value))));
    });
    // set the content
    opts.content = $ul.wrap("<div/>").parent().html();
    // set the on change handler
    var onChange = opts.onChange;
    opts.onChange = function(item, caller, options) {
      if ($.isFunction(onChange)) {
        onChange.call(this, item, caller, options);
      }
      $elem.menu({destroy: true});
    };
    // create the suggestion box
    $elem.menu(opts);
  });
};

/**
 * Plugin Defaults.
 * 
 * @see fg-menu options for details
 */
$.fn.mmSuggest.defaults = {
  flyOut: true,
  width: '',
  startOpen: true,
  callerBehaviour: false
};

})(jQuery);