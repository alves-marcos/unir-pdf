package com.unirpdf;

import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class UnirController {

    @PostMapping("/unir")
    public ResponseEntity<?> unir(@RequestParam("arquivos") MultipartFile[] arquivos) {

        if (arquivos == null || arquivos.length < 2) {
            return ResponseEntity.badRequest().body("Envie pelo menos 2 arquivos PDF.");
        }

        for (MultipartFile arquivo : arquivos) {
            if (!"application/pdf".equals(arquivo.getContentType())) {
                return ResponseEntity.badRequest()
                        .body("Só aceito PDF. Problema em: " + arquivo.getOriginalFilename());
            }
        }

        PDFMergerUtility juntador = new PDFMergerUtility();
        ByteArrayOutputStream resultado = new ByteArrayOutputStream();
        juntador.setDestinationStream(resultado);

        try {

            for (MultipartFile arquivo : arquivos) {
                juntador.addSource(new RandomAccessReadBuffer(arquivo.getInputStream()));
            }

            juntador.mergeDocuments(null);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Não consegui ler um dos PDFs. Pode estar corrompido ou protegido por senha.");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=unido.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resultado.toByteArray());

    }


}
