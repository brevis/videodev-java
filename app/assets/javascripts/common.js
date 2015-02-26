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
