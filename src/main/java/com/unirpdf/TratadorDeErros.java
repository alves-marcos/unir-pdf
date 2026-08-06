package com.unirpdf;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> arquivoGrandeDemais(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body("Arquivo grande demais. Limite: 25MB por arquivo.");
    }

}
