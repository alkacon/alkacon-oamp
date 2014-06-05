$(function() {

// *** Scope-Global Object *** //
    
    // parameters used for nearly all links
    var data = null;
    
    // links object filled by getLinks when document is ready
    var link = null;
    
    // Object to track pagination information
    var pagination = {
        exists: false,
        current : 0,
        commentstotal : null,
        perpage : null,
        elemId : "#cmtPagination ul",
        elem : null,
        getPages : function () {return Math.floor((pagination.commentstotal == 0) ? 0 : ((pagination.commentstotal-1) / pagination.perpage) + 1);},
        update : function () {
                if (pagination.elem != null) {
                    pagination.elem.remove();
                    pagination.elem = null;
                }
                if ($(pagination.elemId).length) {
                    pagination.elem = $(pagination.elemId);
                    var e = pagination.elem.parent();
                    pagination.current = e.attr('cmt-page');
                    pagination.commentstotal = e.attr('cmt-count-comment');
                    pagination.perpage = e.attr('cmt-item-per-page');
                    pagination.exists = true;
                    cmtCreatePagination(pagination.current);
                } else {
                    pagination.exists = false;
                    pagination.current = 0;
                }
            }
    }
    
// *** Helper functions *** //

    // get links exposed to the HTML and used for AJAX loads
    function getLinks(elem) {
        var linkIds = ["list", "form", "innerlist", "replies", "actions", "page", "login"];
        var length = linkIds.length;
        result = {}
        for (var i=0; i < length; i++) {
            var id = linkIds[i]
            result[id] = elem.attr('cmt-link-' + id);
        }
        return result;
    }
    
    function getData(elem) {
        var data = elem.attr('cmt-param-data');
        return $.parseJSON(data);
    }
    
    function dataCopy() {
        var copy = {}
        for (var key in data) {
            if (data.hasOwnProperty(key)){
                copy[key] = data[key];
            }
        }
        return copy;
    }
    
    function getPage(num) {
        return $("#comments_page_" + num);
    }
    
    function getValueOfNameValueArray(array, name) {
        for (var i = 0; i < array.length; i++) {
            if (array[i].name == name) {
                return array[i].value;
            }
        }
        return undefined;   
    }

// *** Reloadind all comments *** //

    function reloadComments(state, page) {
        $('body').css("cursor", "wait");
        if (state !== undefined && page !== undefined) {
            data.cmtstate= state; 
            data.cmtpage= page;
        } else if (state !== undefined) {
            data.cmtstate= state; 
            data.cmtpage= 0;
        } else {
            data.cmtstate= undefined; 
            data.cmtpage= 0;        
        }
        $('#comments').load(link.innerlist,data,pagination.update);
        $('body').css("cursor", "auto");
    }
    
// *** Showing and Hiding Replies *** //

    function showReplies(entryId, canManage) {
        var divId = "#cmtCommentShowReplies-" + entryId;
        var data = dataCopy();
        data["resourceBundle"] = $('#commentbox').attr('cmt-resourcebundle');
        data["entryId"] = entryId;
        data["userCanManage"] = canManage;
        data["cmtallowreplies"] = "false";
        $(divId).toggle();      
        if($(divId).css('display') != 'none') {
            $(divId).addClass("cmtLoading");
            $(divId).load(link.replies, data,
                          function() {
                              $(divId).removeClass("cmtLoading");
                          });
        }
    }           
    
// *** Perform administrative actions *** //

    function doAction(elem) {
        $('body').css("cursor", "wait");
        var action = elem.attr('cmt-action');
        var confirmationMessage = elem.attr('cmt-confirmation-message');
        if ((action == 'delete') && !confirm(confirmationMessage)) {
          return;
        }
        var state = elem.attr('cmt-state');
        var entryId = elem.attr('cmt-comment-entry');
        entryId = parseInt(entryId)
        data = dataCopy();
        data.cmtentry = entryId;
        data.cmtaction = action;
        var isEntryRemoved = (function () {
                if (action == 'delete') {
                    return true;
                } else {
                    switch (state) {
                        case "0": //all new comments shown
                            return true; break; 
                        case "1": //all approved comments shown
                            return (action == 'block'); break;
                        case "2": //all blocked comments shown
                            return (action == 'approve'); break;
                        default:  //all comments shown
                            return false; break;
                    }
                }
        }) ();
        var page = pagination.current;
        if (pagination.exists) {
            var lastPage = pagination.getPages() -1;
            if (isEntryRemoved) {
                // remove cached pages > current page, because their content changed
                for (var i = pagination.current + 1; i <= lastPage; i++) {
                    getPage(i).remove();
                }
                pagination.commentstotal -= 1;
                
                // update pagination if necessary
                if (pagination.commentstotal % lastPage == 0) {
                    if(pagination.current == lastPage) {
                        page -= 1;
                    }
                    cmtCreatePagination(page);
                }
            }
            // reload the current page that should be shown
            $.post(
                link.actions, 
                data,
                function() {
                    //getPage(pagination.current).remove();
                    paginationCallback(page,true);
                }
            );
        } else {
            //reload the current page
            $.post(
                link.actions, 
                data,
                function() {
                    paginationCallback(pagination.current,true);
                }
            );
        }
        $('body').css("cursor", "auto");
    }

// *** Switching and reloading single pages *** //

    // function reloads the given page, if not present and switches
    // from the current page to the given page
    function paginationCallback(page, forceReload) {
        $('body').css("cursor", "wait");
        var oldPage = pagination.current;
        var newPage = page;
        if (getPage(newPage).length == 0) {
            $("<div></div>").attr("id", "comments_page_" + newPage).css("display", "none").appendTo("#comments");
            data.cmtpage=newPage;
            $.post(
                link.page,
                data,
                function(html) {
                    getPage(newPage).html(html);
                    changePage(oldPage, newPage);
                }
            );
        } else if (forceReload) {
            $.post(
                link.page,
                data,
                function(html) {
                    getPage(newPage).html(html);
                    changePage(oldPage, newPage);
                }
            );          
        } else {
            changePage(oldPage, newPage);
        }
        pagination.current = newPage;
        $('body').css("cursor", "auto");
        return false;
    }

    function changePage(oldPage, newPage) {
        if(oldPage == newPage) return;
        var showNewPage = function () {
                getPage(newPage).fadeIn('slow');
            };
        if (getPage(oldPage).length) {
            getPage(oldPage).fadeOut('slow', showNewPage);
        } else { showNewPage(); }
    }

//*** Pagination *** //

    function createPaginationOptions(page) {
        var options = {
            bootstrapMajorVersion: 3,
            size: "normal",
            currentPage: ((page == null) ? pagination.current : page) + 1,
            numberOfPages: pagination.perPage,
            onPageClicked: function(e,originalEvent,type,page){
                    paginationCallback(page-1);
                },
            totalPages: pagination.getPages(),
            tooltipTitles: function (type, page, current){ return ""; }     
        }
        return options;
    }
    
    function cmtCreatePagination(page) {
        if (pagination.getPages() > 1) {
            var options = createPaginationOptions(page);
            $(pagination.elem).bootstrapPaginator(options);
        } else if (pagination.elem != null) {
            pagination.elem.remove();
            pagination.update();
        }
    }
  

/*** LOGIN FORM ***/

    function cmtLogin() {
        var loginErrorMessage = $("form#fid").attr("cmt-login-error");
        $("div#errmsg_cnt").html('&nbsp;<br>&nbsp;');
        $.post(link.login,
               $("form#fid").serializeArray(), 
               function(txt) {
                   if (txt == 'ok') {
                       $('#cmtLoginModal').modal('hide');
                       $.post(
                           link.list,
                           data,
                           function(html) { 
                               $("#commentbox").html(html); 
                           }
                       );
                   } else {
                       $("div#errmsg").addClass("cmtErrorMessage").html(loginErrorMessage + ':<br />' + txt);
                   }
               }
        );
    }

// *** POSTING COMMENTS *** //

    function cmtPost() {
        var formdata = $("form#cmtFormComment").serializeArray();
        $.post(link.form,
               formdata,
               function(txt) {
                    if (txt == 'ok') {
                        $('#cmtFormModal').modal('hide');
                    } else {
                        $("div#cmtFormModal .modal-content").html(txt);
                    }
                    if (getValueOfNameValueArray(formdata,"cmtparentid") == "null") {
                        reloadComments(data.cmtstate);
                    } else {
                        paginationCallback(pagination.current,true);
                    }
               }
              );
    } 

/*** ON READY ***/

    $(document).ready(function () {
        var commentBox = $("#commentbox");
        commentBox.addClass("cmtLoading");
        
        load_script(commentBox.attr('cmt-stylesheet'), 'css');
        
        var url = commentBox.attr('cmt-url');
        data = commentBox.attr('cmt-param-data');
        data = $.parseJSON(data);
        link = getLinks(commentBox);
        
        commentBox.load(url, data, function() {
            reloadComments();
            commentBox.removeClass("cmtLoading");
        });
        
        $("body").on("click",".showFormModal", function(){
            var data = dataCopy();
            data.cmtparentid = $(this).attr("cmt-parent-id");
            $("#cmtFormModal .modal-content").load(link.form, data);
        });
        
        $("body").on("click","#paginationAll", function(e) {
            e.preventDefault();
            reloadComments();
        })
        
        $("body").on("click","#paginationNew", function(e) {
            e.preventDefault();
            reloadComments(0);
        })
        
        $("body").on("click","#paginationBlocked", function(e) {
            e.preventDefault();
            reloadComments(2);
        })
        
        $("body").on("click","#paginationApproved", function(e) {
            e.preventDefault();
            reloadComments(1);
        })
        
        $("body").on("click",".cmtShowRepliesButton", function() {
            var entryId = $(this).attr("cmt-comment-entry");
            entryId = parseInt(entryId);
            var canManage = $(this).attr("cmt-user-can-manage");
            canManage = canManage == "true" ? true : false;
            showReplies(entryId, canManage);
        })
        $("body").on("click",".cmtAction", function(e) {
            e.preventDefault();
            doAction($(this));
        })
        
        $("body").on("click",".showLoginModal", function(){
            $("#cmtLoginModal .modal-content").load(link.login, data);
        })
        $("body").on("click","#cmtLoginLoginButton", function() {
            cmtLogin();
        })
        $("body").on("click","#cmtFormSubmitButton", function() {
            cmtPost();
        })  
        $("body").on("click",".cmtLoadComments", function() {
            $('#commentbox').load(link.list,data,
                function () {
                $('#comments').load(link.innerlist,data,pagination.update);
                }
            );
        })  
    });
});
