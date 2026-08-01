package com.unirpdf;

import org.apache.pdfbox.io.RandomAccessRead;
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
    public ResponseEntity<byte[]> unir(@RequestParam("arquivos") MultipartFile[] arquivos) throws IOException {

        PDFMergerUtility juntador = new PDFMergerUtility();

        ByteArrayOutputStream resultado = new ByteArrayOutputStream();
        juntador.setDestinationStream(resultado);

        for (MultipartFile arquivo : arquivos) {
            juntador.addSource(new RandomAccessReadBuffer(arquivo.getInputStream()));
        }
            juntador.mergeDocuments(null);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=unido.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resultado.toByteArray());

    }

}
