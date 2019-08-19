var infScroll = new InfiniteScroll( '.container-fluid', {
    path: function () {
        if (server.url.pathParams.page > 0) {
            return server.route('burgers-by-year-by-page')
                .toString(server.url.pathParams.year, server.url.pathParams.page - 1);
        }
    },
    append: '.burger-container'
    , button: '.scroll-btn'
});

infScroll.on( 'request', function( path ) {
    server.url.pathParams.page--;
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