package com.unirpdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UnirPdfApplicationTests {

    @Autowired
    MockMvc navegadorFalso;

    @Test
    void contextLoads() {
    }

    @Test
    void recusaQuandoVemUmArquivoSo() throws Exception {

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivos", "um.pdf", "application/pdf", "qualquer coisa".getBytes());

        navegadorFalso.perform(multipart("/unir").file(arquivo))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Envie pelo menos 2 arquivos PDF."));
    }

    @Test
    void recusaQuandoUmDosArquivosNaoEhPdf() throws Exception {

        MockMultipartFile pdf = new MockMultipartFile(
                "arquivos", "um.pdf", "application/pdf", "conteudo".getBytes());

        MockMultipartFile imagem = new MockMultipartFile(
                "arquivos", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        navegadorFalso.perform(multipart("/unir").file(pdf).file(imagem))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Só aceito PDF. Problema em: foto.jpg"));
    }

    private byte[] pdfDeUmaPagina() throws Exception {
        try (PDDocument documento = new PDDocument()) {
            documento.addPage(new PDPage());
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            documento.save(saida);
            return saida.toByteArray();
        }
    }

    @Test
    void uneDoisPdfsEDevolveUmComDuasPaginas() throws Exception {

        MockMultipartFile primeiro = new MockMultipartFile(
                "arquivos", "a.pdf", "application/pdf", pdfDeUmaPagina());

        MockMultipartFile segundo = new MockMultipartFile(
                "arquivos", "b.pdf", "application/pdf", pdfDeUmaPagina());

        byte[] resposta = navegadorFalso.perform(multipart("/unir").file(primeiro).file(segundo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        try (PDDocument unido = Loader.loadPDF(resposta)) {
            assertEquals(2, unido.getNumberOfPages());
        }
    }

    @Test
    void recusaPdfIlegivel() throws Exception {

        MockMultipartFile bom = new MockMultipartFile(
                "arquivos", "a.pdf", "application/pdf", pdfDeUmaPagina());

        MockMultipartFile ruim = new MockMultipartFile(
                "arquivos", "b.pdf", "application/pdf", "isso nao e um pdf".getBytes());

        navegadorFalso.perform(multipart("/unir").file(bom).file(ruim))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Não consegui ler um dos PDFs. Pode estar corrompido ou protegido por senha."));
    }


}
