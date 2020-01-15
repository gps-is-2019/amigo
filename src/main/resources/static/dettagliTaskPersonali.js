/**
 * Script js per la pagina dettagli_task_personali.html
 */

$('#file-upload').change(function() {
    var i = $(this).prev('label').clone();
    var file = $('#file-upload')[0].files[0].name;
    $(this).prev('label').text(file);
});

$('.message .close').on('click', function () {
    $(this)
        .closest('.message')
        .transition('fade');
});
