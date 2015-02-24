$(function(){
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
