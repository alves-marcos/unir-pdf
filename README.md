## Sobre

Juntar PDFs é uma tarefa comum e chata: contratos com anexos, documentos
escaneados em partes, comprovantes que precisam virar um arquivo só. As
ferramentas online que fazem isso costumam exigir cadastro, impor limites de
uso ou encher a tela de anúncios.

O UnirPDF é uma alternativa simples: você envia os arquivos, recebe um só de
volta. Sem cadastro e sem armazenamento — os arquivos são processados em
memória e descartados assim que a resposta é enviada.

O projeto também é meu laboratório de aprendizado de Java no backend: API REST,
upload de arquivos, tratamento de erros, testes automatizados e deploy.

## Funcionalidades

- [x] Receber múltiplos arquivos via upload
- [ ] Unir os PDFs em um único documento
- [ ] Devolver o arquivo unido para download
- [ ] Validar tipo, quantidade e tamanho dos arquivos
- [ ] Tratar erros com mensagens claras
- [ ] Testes automatizados
- [ ] Deploy em ambiente público

## Tecnologias

| Ferramenta | Uso |
  |---|---|
| Java 21 | Linguagem |
| Spring Boot 4.1 | Framework web, servidor embutido |
| Apache PDFBox 3.0.8 | Manipulação dos arquivos PDF |
| Maven | Gerenciamento de dependências e build |

## Como rodar

**Pré-requisito:** Java 21 ou superior instalado.

  ```bash
  git clone https://github.com/alves-marcos/unir-pdf.git
  cd unir-pdf
  ./mvnw spring-boot:run
  ```

O servidor sobe em `http://localhost:8080`.

Não é necessário instalar o Maven — o projeto inclui o Maven Wrapper (`mvnw`),
que baixa a versão correta automaticamente.

## Endpoints

| Método | Endereço | Descrição |
  |--------|----------|-----------|
| `GET` | `/` | Verifica se o serviço está no ar |
| `POST` | `/unir` | Recebe os arquivos no campo `arquivos` e devolve o PDF unido |

Exemplo com curl:

  ```bash
  curl -X POST http://localhost:8080/unir \
    -F "arquivos=@primeiro.pdf" \
    -F "arquivos=@segundo.pdf"
  ```

Há também um formulário simples para testes manuais em
`http://localhost:8080/teste.html`.

## Estrutura

  ```
  src/main/java/com/unirpdf/
  ├── UnirPdfApplication.java    # ponto de entrada da aplicação
  ├── SaudacaoController.java    # GET /
  └── UnirController.java        # POST /unir

  src/main/resources/
  ├── application.properties     # configurações
  └── static/teste.html          # formulário de teste
  ```

## Decisões de projeto

**Processamento em memória.** Os arquivos não são gravados em disco em nenhum
momento. Isso simplifica a operação, evita acúmulo de lixo no servidor e é
melhor para a privacidade de quem usa. O custo é o consumo de memória por
requisição, aceitável para o tamanho de arquivo previsto.

**Sem banco de dados.** O serviço não guarda estado: recebe, processa,
responde e esquece. Não há o que persistir.

**Empacotamento em JAR executável.** Com o servidor embutido, subir a aplicação
é copiar um arquivo e executá-lo — sem instalar servidor de aplicação.

## Autor

**Marcos Alves** — [github.com/alves-marcos](https://github.com/alves-marcos)

  ---
