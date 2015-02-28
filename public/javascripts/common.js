// facebook login
function checkFBLoginState() {
    FB.getLoginStatus(function(response) {
        if (response.status === 'connected') {
            document.location.href = "/login?accessToken=" + response.authResponse.accessToken;
        } else {
            // TODO: any notify?
        }
    });
}

$(function(){

    // edit course
    function switchTab(tab) {
        if (tab == 'course') {
            $('#contentType' ).val('course');
            $('#tablesson input, #tablesson select, #tablesson textarea').removeAttr("required");
            $('#tabcourse input, #tabcourse select, #tabcourse textarea' ).not('.coverinput').attr("required", "required");
        } else {
            $('#contentType' ).val('lesson');
            $('#tablesson input, #tablesson select, #tablesson textarea').not('.coverinput').attr("required", "required");
            $('#tabcourse input, #tabcourse select, #tabcourse textarea').removeAttr("required");
        }
    }
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        if (e.target.hash == '#tabcourse') {
            switchTab('course');
        } else {
            switchTab('lesson');
        }
    });
    switchTab(activeTab);

    $('.addlesson').on('click', function() {
        var html = lessonTemplate.replace(/%i%/g, lessonsCount).replace(/%n%/g, lessonsCount + 1);
        $('#accordioncourse').append(html);
        lessonsCount++;
        $('#lessonsCount').val(lessonsCount);
    });

    $(document.body).on('click', '.deletelesson' ,function() {
        if (lessonsCount < 2) return;
        var $el = $(this).parents('.lessongroup');
        $el.fadeOut();
        setTimeout(function() {
            $el.remove();
            $('#lessonsCount').val(--lessonsCount);
            var i = 1;
            $('.lessongroup .accordion-heading a.accordion-toggle').each(function() {
                $(this).text('Урок ' + (i++));
            });
        }, 500);
    });

    $('.deletecover').on('click', function() {
        var type = $(this).data('type');
        $('#' + type + 'CoverId').val('');
        $('#' + type + 'CoverImage' ).hide();
        $('#' + type + 'CoverUrl' ).removeAttr("disabled");
        $(this).hide();
    });


    // fixed sidebar
    var listval = $('.fixedcontainer')[0].offsetTop;
    $(document).scroll(function() {
        var topval = $(document).scrollTop();
        if( topval >= listval) {
            $('.fixedcontainer').removeClass('fixed').addClass('fixed');
        } else {
            $('.fixedcontainer').removeClass('fixed');
        }
    });
});
