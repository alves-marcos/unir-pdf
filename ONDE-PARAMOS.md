# UnirPDF — onde paramos

**Última sessão: 30/07/2026** · Próximo: **passo 6 (unir os PDFs com PDFBox)**

---

## Como retomar amanhã

1. Abrir o IntelliJ no projeto `~/Documentos/ProjetosProfissionais/unirPDF`
2. Abrir `UnirPdfApplication.java` → clicar no **▶ verde**
3. Esperar aparecer `Tomcat started on port 8080` no console
4. Testar no navegador:
   - `localhost:8080` → deve mostrar **UnirPDF está no ar!**
   - `localhost:8080/teste.html` → formulário de upload; escolher 2 PDFs → Enviar
   - resposta esperada: `Recebi 2 arquivo(s): - nome.pdf (tamanho bytes) ...`

Se os dois funcionarem, está tudo como deixamos.

> Lembrete: código novo só entra depois de **⏹ parar** e **▶ rodar de novo**. Salvar não basta.

---

## Progresso

```
✅ 1. Maven (já vem embutido no IntelliJ)
✅ 2. esqueleto do projeto (Spring Initializr)
✅ 3. subir o servidor
✅ 4. primeiro endpoint  (GET / → "UnirPDF está no ar!")
✅ 5. receber arquivos   (POST /unir → lista nome e tamanho)
🔸 6. unir com PDFBox    ← PRÓXIMO (dependência já instalada, falta o código)
   7. validações e erros
   8. testes
   9. colocar no ar
```

O front-end de verdade é uma etapa à parte, fora dessa lista.

---

## Estado atual dos arquivos

```
unirPDF/
├── pom.xml                        ← PDFBox 3.0.8 já adicionado ✅
└── src/main/
    ├── java/com/unirpdf/
    │   ├── UnirPdfApplication.java    (classe principal, não mexer)
    │   ├── SaudacaoController.java    (GET /)
    │   └── UnirController.java        (POST /unir) ← é aqui que vamos mexer
    └── resources/static/
        └── teste.html                 (formulário de teste, feio de propósito)
```

### `UnirController.java` — como está hoje

```java
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
```

---

## PASSO 6 — o que fazer amanhã

Trocar o miolo do método `unir` por este:

```java
@PostMapping("/unir")
public String unir(@RequestParam("arquivos") MultipartFile[] arquivos) throws IOException {

    PDFMergerUtility juntador = new PDFMergerUtility();

    ByteArrayOutputStream resultado = new ByteArrayOutputStream();
    juntador.setDestinationStream(resultado);

    for (MultipartFile arquivo : arquivos) {
        juntador.addSource(new RandomAccessReadBuffer(arquivo.getInputStream()));
    }

    juntador.mergeDocuments(null);

    return "PDF unido! Tamanho final: " + resultado.size() + " bytes";
}
```

**Imports** (Alt+Enter em cada palavra vermelha) — atenção nestes dois:
- `RandomAccessReadBuffer` → escolher **`org.apache.pdfbox.io`**
- `IOException` → escolher **`java.io`**

**Como testar:** ⏹ → ▶ → `localhost:8080/teste.html` → 2 PDFs → Enviar.
Deve responder `PDF unido! Tamanho final: XXXXXX bytes`. O número dá um pouco menos
que a soma dos dois arquivos — normal, o PDFBox reaproveita fontes repetidas.

**Ainda não baixa o arquivo.** Isso é o passo 6b, de propósito: primeiro provar que
a união funciona, depois resolver como devolver o arquivo pro navegador.

### A ideia do código em 5 linhas

O PDFBox é uma máquina que você **configura e depois liga**:

```
1. compra a máquina          →  new PDFMergerUtility()
2. põe o balde na saída      →  setDestinationStream(resultado)
3. enfia os PDFs na entrada  →  addSource(...)   ← uma vez por arquivo
4. aperta o botão            →  mergeDocuments(null)
5. o balde está cheio        →  resultado
```

Os passos 1 a 3 não fazem nada — são preparação. O trabalho só acontece no 4.
A ordem dos `addSource` é a ordem das páginas no PDF final.

### Conceitos novos que aparecem aí (para revisar)

- **objeto e `new`** — classe é a planta, `new` constrói a coisa de verdade. `new X()` = "construa um objeto novo desse tipo"
- **stream** — fluxo de bytes. `InputStream` = entrando no programa; `OutputStream` = saindo. `ByteArrayOutputStream` é um balde na memória, sem tocar no disco
- **`RandomAccessReadBuffer`** — embrulho necessário porque PDF precisa ser lido pulando pra frente e pra trás, e um InputStream só anda pra frente. Na versão 2 do PDFBox não precisava — por isso tutoriais antigos não batem
- **`null`** — "nada", "nenhum objeto". Aqui é uma configuração opcional que a gente dispensa
- **`throws IOException`** — exceção é o mecanismo de erro do Java. `throws` significa "eu não trato, quem me chamou que resolve". Vira tratamento de verdade no passo 7

---

## Coisas resolvidas que não precisam voltar

- Maven: **não instalar nada**, o IntelliJ já traz (3.9.16 + Java 21)
- Pacote `com` e `unirpdf` aparecerem em linhas separadas na árvore: só visual, tanto faz
- Sobrou uma cópia velha do projeto na **lixeira** — pode esvaziar, o que vale é o de Documentos
- `Ctrl+Shift+A` acha qualquer configuração do IntelliJ pelo nome
- `Ctrl+Shift+O` recarrega o Maven depois de mexer no `pom.xml`
- Achar dependência: **mvnrepository.com** (olhar data e nº de usos) ou `Alt+Insert` dentro do `pom.xml`.
  Cuidado com groupId de uma palavra só — é código velho abandonado

---

## Combinado sobre o ritmo

- Claude **explica**, o Marcos **digita**. Nada de código gerado automaticamente
- Explicação vem **antes** de mandar digitar, sempre
- Uma ideia por vez, sem despejo de texto
