$('.item').on('click', function() {
    $('.item').removeClass('item');
    $(this).addClass('active item');
    console.log('ciao');
});

console.log('ciaooooo');