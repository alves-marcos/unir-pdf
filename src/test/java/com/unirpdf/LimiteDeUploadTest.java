package com.unirpdf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.servlet.multipart.max-file-size=1KB")
@AutoConfigureRestTestClient
class LimiteDeUploadTest {

    @Autowired
    RestTestClient cliente;

    @Test
    void recusaArquivoAcimaDoLimite() {

        MultiValueMap<String, Object> corpo = new LinkedMultiValueMap<>();
        corpo.add("arquivos", arquivoDe(5000, "a.pdf"));
        corpo.add("arquivos", arquivoDe(5000, "b.pdf"));

        cliente.post().uri("/unir")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(corpo)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Arquivo grande demais. Limite: 25MB por arquivo.");
    }

    private HttpEntity<byte[]> arquivoDe(int tamanho, String nome) {
        HttpHeaders cabecalhos = new HttpHeaders();
        cabecalhos.setContentDisposition(
                ContentDisposition.formData().name("arquivos").filename(nome).build());
        cabecalhos.setContentType(MediaType.APPLICATION_PDF);
        return new HttpEntity<>(new byte[tamanho], cabecalhos);
    }
}
