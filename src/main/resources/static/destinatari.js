/**
 * Script js per la pagina destinatari.html
 */

$('.ui.fluid.dropdown')
  .dropdown({placeholder:'Destinatari'});

$('#file-upload').change(function() {
    var i = $(this).prev('label').clone();
    var file = $('#file-upload')[0].files[0].name;
    $(this).prev('label').text(file);
});