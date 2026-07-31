package com.unirpdf;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaudacaoController {

    @GetMapping("/")
    public String ola() {
        return "UnirPDF está no ar!";
    }
}
