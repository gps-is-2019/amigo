/**
 * Esegue l'animazione di "fade in" su un elemento
 * @param element L'elemento che subirà la transizione
 */
function fade(element) {
    var op = 0.1;  // initial opacity
    element.style.display = 'block';
    var timer = setInterval(function () {
        if (op >= 1){
            clearInterval(timer);
        }
        element.style.opacity = op;
        element.style.filter = 'alpha(opacity=' + op * 100 + ")";
        op += op * 0.1;
    }, 10);
}
$('.message .close').on('click', function() {
    $(this).closest('.message').fadeOut();
});
// Variabili

var h1 = document.querySelector('h1');
var logo = document.querySelector("img");

// Effetti
fade(h1);
fade(logo);