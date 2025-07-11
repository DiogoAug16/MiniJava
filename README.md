<br>

<table align="center">
  <tr>
    <td align="center" style="padding-top: 20px;">
      <img src="docs/images/banner_minijava.png" alt="MiniJava banner" width="700" height="auto" />
    </td>
  </tr>
</table>

<p align="center">
  <img src="https://visitor-badge.laobi.icu/badge?page_id=DiogoAug16.MiniJava" alt="Visitors" />
  <img src="https://img.shields.io/github/languages/top/DiogoAug16/MiniJava?color=blue&logo=java" alt="Top Language" />
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License: MIT" />
  <img src="https://img.shields.io/github/last-commit/DiogoAug16/MiniJava?color=brightgreen" alt="Last Commit" />
  <img src="https://img.shields.io/badge/ANTLR-4.13.1-ff69b4?logo=antlr" alt="ANTLR" />
  <img src="https://img.shields.io/badge/Graphviz-12.2.1-8A2BE2?logo=graphviz" alt="Graphviz" />
  <img src="https://img.shields.io/badge/Java-11%2B-blue?logo=java" alt="Java" />
  <img src="https://img.shields.io/github/issues/DiogoAug16/MiniJava?color=orange" alt="GitHub Issues" />
  <img src="https://img.shields.io/badge/Gera-Código_de_Três_Endereços-orange" alt="TAC Generation" />
</p>


---

## <img src="docs/images/Package2.gif" alt="Package" width="40" height="auto" /> Pré-requisitos

- Java JDK 11+ instalado

- ANTLR 4.13.1 jar disponível no projeto (em lib/)

- (Opcional) Visual Studio Code + extensões de Java + extensões do Antlr

- Graphviz 12.2.1+ instalado

- LLVM 20.1.0+ instalado

## <img src="docs/images/Flame.png" alt="Lightning" height="30" /> Objetivo do Projeto

O objetivo principal deste projeto é ***desenvolver um compilador funcional*** como requisito obrigatório para aprovação na disciplina de Compiladores. O compilador deve ser capaz de processar uma linguagem definida por uma gramática própria e realizar:

- ***Analise lexica***: identificar tokens a partir de um arquivo-fonte, gerando saídas no formato `<Tipo do Token, Lexema, Linha, Coluna>`, com logs detalhados e mensagens claras de erro.
- ***Analise sintatica***: validar a estrutura do código-fonte de acordo com a gramática definida, gerando uma `AST (árvore sintática abstrata)` e emitindo mensagens de erro intuitivas.
- ***Analise semântica***: verificar se o código-fonte faz sentido dentro das regras da linguagem, garantindo que variáveis sejam declaradas antes do uso, impedindo divisão por zero, tipos sejam compatíveis e identificadores sejam usados corretamente em seus escopos, utilizando a AST e uma tabela de símbolos para rastrear declarações e usos. Reportando erros com mensagens claras e precisas para descrição do problema.
- ***Execução de testes*** com programas como:
    - Geração do **Triângulo de Pascal** (para testar laços e estruturas de repetição);
    - **Classificação de triângulos** (para testar expressões lógicas e condicionais aninhadas).
- ***Geração de Código Intermediário***: Traduz a AST para uma representação linear de baixo nível, o **Código de Três Endereços (TAC)**. Esta representação simplifica o processo de otimização e a tradução para o código final.

## <img src="docs/images/Rocket.png" alt="Rocket" width="25" height="25" /> Como compilar e rodar

### 1. Gerar o Lexer, Parser, Listener e Visitor com ANTLR

Execute o comando abaixo:

- Para windows:
```bash
java -Xmx500m -cp "lib/antlr-4.13.1-complete.jar" org.antlr.v4.Tool -Dlanguage=Java -visitor -o src/main/java/com/minijava/antlr src/main/java/com/minijava/antlr/MiniJava.g4
```

- Para Linux:
```bash
java -Xmx500m -cp lib/antlr-4.13.1-complete.jar org.antlr.v4.Tool -Dlanguage=Java -visitor src/main/java/com/minijava/antlr/MiniJava.g4
```

- Isso gera os arquivos necessários dentro da pasta `antlr`.

### 2. Compilar o projeto

Compile todos os arquivos `.java`:

```bash
mvn clean package
```
ou
```bash
mvn clean install
```

- Os .class serão gerados dentro da pasta `target/`.

### 3. Rodar o interpretador

Execute o programa:

```bash
mvn exec:java
```

- O interpretador irá ler o arquivo `input/triangulo_pascal.txt` (ou outro arquivo configurado) e interpretar os comandos.

### 4. Gerar o Código TAC

Para gerar o arquivo `.tac` em vez de interpretar o código, execute o Maven com o argumento `--gerar-tac`:

```bash
mvn exec:java -D exec.args="--gerar-tac"
```
- O programa irá solicitar a escolha de um arquivo de entrada.
- Após a execução, um arquivo `programa_fonte.tac` será gerado em `output/tac/`.

### 5. Gerar o código LLVM

Para gerar o arquivo `.ll` a partir do `.tac`, execute o Maven com o argumento `--gerar-tac --gerar-llvm`:

```bash
mvn exec:java -D exec.args="--gerar-tac --gerar-llvm"
```
- O programa irá solicitar a escolha de um arquivo de entrada.
- Após a execução, um arquivo `programa_fonte.ll` será gerado em `output/llvm/` e seu `programa_fonte.exe` será gerado em `output/programa/`.

### 6. Rodar o executavel

Após gerar o executável, você pode executá-lo diretamente no seu sistema:

- No Windows:

```bash
output\programa\programa_fonte.exe
``` 

- No Linux:
  
```bash
./output/programa/programa_fonte
```

- Certifique-se que o arquivo tenha permissão de execução (chmod +x no Linux).

## <img src="docs/images/Abacus2.gif" alt="Abacus" width="40" height="auto" /> Expressões

A linguagem suporta:

- Concatenação de strings e números com `+`
- Operações aritméticas (`+`, `-`, `*`, `/`)
- Expressões lógicas (`&&`, `||`, `!`)
- Comparações (`==`, `>`, `<`, `>=`, `<=`)

## <img src="docs/images/Input Latin Uppercase.png" alt="Input Latin Uppercase" width="25" height="25" /> Tokens

- **ID**: identificadores (`[a-zA-Z_][a-zA-Z0-9_]*`)
- **INT**: inteiros (`[0-9]+`)
- **STRING**: cadeias de caracteres entre aspas (`"texto"`)
- **WS**: espaços e quebras de linha (ignorado)
- **COMMENT**: comentários de linha iniciados com `//` (ignorados)

## <img src="docs/images/Pushpin.png" alt="Pushpin" width="25" height="25" /> Exemplo de Código

```bash
class Main {
    int x;
    string nome;

    scanf(x);
    nome = "João";
    x = x + 5;

    if (x > 10) {
        println(nome);
    } else {
        print("Valor baixo");
    }

    while (x < 20) {
        x = x + 1;
    }
}
```

## <img src="docs/images/File Folder2.gif" alt="File Folder" width="40" height="auto" /> Estrutura de pastas

```bash
MiniJava
├─ docs                                         # pasta docs para guardar algumas imagens
│  └─ images
├─ input                                        # Pasta onde ficam os arquivos de teste para rodar o programa
├─ lib                                          # Biblioteca ANTLR
│  └─ antlr-4.13.1-complete.jar
├─ LICENSE
├─ pom.xml
├─ README.md
├─ runtime                                      # Codigos em C que auxiliam no llvm
└─ src
   └─ main
      └─ java
         └─ com
            └─ minijava
               ├─ antlr                         # Lexer, Parser, Listener e Visitor gerados
               │  ├─ MiniJava.g4
               ├─ ast                           # Pasta de arquivos que manipulam ast
               │  ├─ dot                        # Gerador dot
               │  │  └─ DotGenerator.java
               │  ├─ image                      # Gerador de imagem do ast a partir do dot
               │  │  └─ AstImageGenerator.java
               │  └─ viewer                     # Visualizador da tree ast 
               │     └─ AstViewer.java
               ├─ classcheck                    # Verificação da classe se o nome condiz com o nome do arquivo
               │  └─ ClassVerification.java
               ├─ exception                     # Tratamento de erros personalizados
               │  ├─ LexerErrorListener.java
               │  └─ ParserErrorListener.java
               ├─ flags                         # Flags usado no Main
               │  └─ MainFlags.java
               ├─ interpreter                   # Interpretador da linguagem
               │  └─ Interpreter.java
               ├─ llvm                          # Gerador dee llvm a partir do tac
               │  └─ LLVMGenerator.java
               ├─ Main.java
               ├─ semantic                      # Analisador semantico
               │  ├─ MiniJavaSemantic.java
               │  ├─ SemanticLogger.java
               │  └─ Type.java
               ├─ tac                           # Gerador do codigo de tres endereços
               │  ├─ TACGenerator.java
               │  ├─ TACInstruction.java
               │  └─ TACOperand.java
               └─ tokengenerator                # Gerador de tokens já formatado
                  └─ TokenGenerator.java

```
## <img src="docs/images/page_facing_up_3d.png" alt="File Folder" width="25" height="25" /> Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).