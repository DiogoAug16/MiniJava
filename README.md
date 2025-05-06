# <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Package.png" alt="Package" width="25" height="25" /> Pré-requisitos

- Java JDK 11+ instalado

- ANTLR 4.13.1 jar disponível no projeto (em lib/)

- (Opcional) Visual Studio Code + extensões de Java + extensões do Antlr

- Graphviz 12.2.1+ instalado

## <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Travel%20and%20places/Rocket.png" alt="Rocket" width="25" height="25" /> Como compilar e rodar

### 1. Gerar o Lexer, Parser, Listener e Visitor com ANTLR

Execute o comando abaixo (ou use a task pronta do VSCode usando shift+ctrl+b):

```bash
java -Xmx500m -cp "lib/antlr-4.13.1-complete.jar" org.antlr.v4.Tool -Dlanguage=Java -visitor -o src/antlr src/grammar/Grammar.g4
```

- Isso gera os arquivos necessários dentro da pasta src/antlr.

### 2. Compilar o projeto

Compile todos os arquivos .java manualmente caso você não tenha usado a task pronta:

```bash
javac -d output -cp lib/antlr-4.13.1-complete.jar src/antlr/*.java src/Main.java src/exception/*.java src/interpreter/*.java src/astviewer/*.java src/dotgenerator/*.java src/classcheck/*.java src/astimage/*.java
```

- Os .class serão gerados dentro da pasta output/.

### 3. Rodar o interpretador

Execute o programa passando o classpath correto( Caso tenha a extensão java no vscode, é possivel rodar diretamente usando Run java):

```bash
java -cp ".;lib/antlr-4.13.1-complete.jar;output" Main
```

- O interpretador irá ler o arquivo input/triangulo_pascal.txt (ou outro arquivo configurado) e interpretar os comandos.

## <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Abacus.png" alt="Abacus" width="25" height="25" /> Expressões

A linguagem suporta:

- Concatenação de strings e números com `+`
- Operações aritméticas (`+`, `-`, `*`, `/`)
- Expressões lógicas (`&&`, `||`, `!`)
- Comparações (`==`, `>`, `<`, `>=`, `<=`)

## <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Input%20Latin%20Uppercase.png" alt="Input Latin Uppercase" width="25" height="25" /> Tokens

- **ID**: identificadores ([a-zA-Z_][a-zA-Z0-9_]*)
- **INT**: inteiros ([0-9]+)
- **STRING**: cadeias de caracteres entre aspas (`"texto"`)
- **WS**: espaços e quebras de linha (ignorado)
- **COMMENT**: comentários de linha iniciados com `//` (ignorados)

## <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Pushpin.png" alt="Pushpin" width="25" height="25" /> Exemplo de Código

```bash
public class Main {
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

## <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/File%20Folder.png" alt="File Folder" width="25" height="25" /> Estrutura de pastas

```bash
src/
├── antlr/                  # Lexer, Parser, Listener e Visitor gerados
├── grammar/                # Pasta da gramatica do projeto
│   └── Grammar.g4          
├── astviewer/              # Visualizador da tree ast
│   └── AstViewer.java
├── dotgenerator/           # Gerador dot
│   └── DotGenerator.java 
├── Main.java               # Ponto de entrada do programa
├── interpreter/            # Interpretador da linguagem
│   └── Interpreter.java
├── classcheck/             # Verificação da classe se o nome condiz com o nome do arquivo
│   └── ClassVerification.java
exception/                  # Tratamento de erros personalizados
├── CustomErrorListener.java
lib/                        # Biblioteca ANTLR
├── antlr-4.13.1-complete.jar 
input/                      # Pasta onde ficam os arquivos de teste para rodar o programa
│ 
output/                     # Arquivos .class compilados

```
