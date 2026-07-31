package com.unirpdf;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UnirController {

    @PostMapping("/unir")
    public String unir(@RequestParam("arquivos") MultipartFile[] arquivos) {

        String resposta = "Recebi " + arquivos.length + " arquivo(s):\n";

        for (MultipartFile arquivo : arquivos) {
            resposta = resposta + "- " + arquivo.getOriginalFilename()
                    + " (" + arquivo.getSize() + " bytes)\n";
        }

        return resposta;
    }

}
