/**
 * Si occupa di mostrare in quale sezione del sito si ci trova evidenziando l'opportuno link nella barra di navigazione.
 */
$(document).ready(function() {
    var path = window.location.href;
    var longestMatch = "";
    $(".item").each(function() {
        if (path.indexOf(this.href) >= 0)
            if (longestMatch.length < $(this).attr("href").length)
                longestMatch = $(this).attr("href");
    });
    $('a[href="'+longestMatch+'"]').addClass("active");
});