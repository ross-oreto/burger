var infScroll = new InfiniteScroll( '.container-fluid', {
    path: function () {
        if (window.index > 0) {
            return window.path + '/' + window.year + '/'
                 +
                (window.index - 1);
        }
    },
    append: '.burger-container'
    , button: '.scroll-btn'
});

infScroll.on( 'request', function( path ) {
    window.index--;
});

$(document).ready(function(){
    $(".scroll-btn").click( function(event) {
        event.preventDefault();
        $('html, body').animate({
            scrollTop: $(this).offset().top
        }, 800, function(){

        });
    });
});