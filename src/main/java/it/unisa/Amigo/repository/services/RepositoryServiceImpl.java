package it.unisa.Amigo.repository.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RepositoryServiceImpl implements RepositoryService {

    @Autowired
    private DocumentoService documentoService;


    @Override
    public boolean addDocumentoInRepository(MultipartFile file){
        System.out.println(file.getSize());
        if (file.getSize()< 10485760){
            documentoService.addDocumento(file);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ResponseEntity<Resource> downloadDocumento(int idDocument) {
        Documento documento = documentoService.findDocumento(idDocument);
        Resource resource = documentoService.loadAsResource(documento);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getFormat()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + documento.getNome() + "\"")
                .body(resource);
    }
}
