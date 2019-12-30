package it.unisa.Amigo.repository.controller;

/*
@Controller
public class RepositoryController {
    @Autowired
    private DocumentoService documentoService;

    //show form
    @GetMapping("/repository")
    public String repository(Model model) {
        List<Documento> documenti = documentoService.searchDocumentoFromRepository("");
        model.addAttribute("documenti", documenti);
        return "repository/updownrep";
    }

    //submit form
    @PostMapping("/repository/uploadDocumento")
    public String uploadDocumento(Model model, @RequestParam("file") MultipartFile file) {
        documentoService.addDocToRepository(file);
        model.addAttribute("flagAggiunta", 1); //cambiare
        model.addAttribute("documentoNome", file.getOriginalFilename());
        List<Documento> documenti = documentoService.searchDocumentoFromRepository("");
        model.addAttribute("documenti", documenti);
        return "repository/updownrep";
    }

    @GetMapping("/repository/{idDocument}")
    public ResponseEntity<Resource> downloadDocumento(Model model, @PathVariable("idDocument") int idDocument) {
        Documento documento = documentoService.downloadDocumentoFromRepository(idDocument);
        Resource resource = documentoService.loadAsResource(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }
}
*/