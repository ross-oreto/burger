var infScroll = new InfiniteScroll( '.container-fluid', {
    path: function () {
        if (this.loadCount < window.total) {
            return window.path +
                (window.location.href.endsWith('/') ? '' : '/') +
                (window.total - this.pageIndex);
        }
    },
    append: '.burger-container',
    button: '.scroll-btn',
    scrollThreshold: false,
    loadOnScroll: false
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