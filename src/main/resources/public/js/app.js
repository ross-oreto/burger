var pageContainer = 'burger-container';

if (server.url.pathParams.Rank > 0) {
    var infScroll = new InfiniteScroll( '.container-fluid', {
        path: function () {
            if (server.url.pathParams.Rank > 0) {
                return server.route('burgers-by-Year-by-Rank')
                    .toString(server.url.pathParams.Year, server.url.pathParams.Rank - 1);
            }
        },
        append: '.' + pageContainer
    });

    infScroll.on( 'request', function( path ) {
        var index = (server.args.Pages - server.url.pathParams.Rank);
        if (this.loadCount > 0) {
            initScroll(index);
        }
        if (server.url.pathParams.Rank === 1) removeBounce(index);
        removeBounce(index - 1);

        server.url.pathParams.Rank--;
    });
}

$(document).ready(function() {
    var len = server.args.Pages - server.url.pathParams.Rank;
    for(var i = 0; i < len; i++) {
        initScroll(i);
        removeBounce(i);
    }
    initScroll(len);
    $('html, body').animate({ scrollTop: $("."+pageContainer).last().offset().top }, "slow");
});

function initScroll(index) {
    $(".scroll-btn:eq(" + index + ")").click( function(event) {
        event.preventDefault();
        var next = $("."+pageContainer).get(index + 1);
        var t = next ? $(next).offset().top : $(document).height();

        $('html, body').animate({
            scrollTop: t
        }, 800, function() {
        });
    });
}

function removeBounce(index) {
    $(".scroll-btn img:eq(" + index + ")").removeClass("bounce");
}