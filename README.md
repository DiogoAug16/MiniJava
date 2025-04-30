# 📦 Pré-requisitos

- Java JDK 11+ instalado

- ANTLR 4.13.1 jar disponível no projeto (em lib/)

- (Opcional) Visual Studio Code + extensões de Java + extensões do Antlr

## 🚀 Como compilar e rodar

### 1. Gerar o Lexer, Parser, Listener e Visitor com ANTLR

Execute o comando abaixo (ou use a task pronta do VSCode usando shift+ctrl+b):

```bash
java -Xmx500m -cp "lib/antlr-4.13.1-complete.jar" org.antlr.v4.Tool -Dlanguage=Java -visitor -package antlr -o src/antlr src/grammar/Grammar.g4
```

- Isso gera os arquivos necessários dentro da pasta src/antlr.

### 2. Compilar o projeto

Compile todos os arquivos .java manualmente caso você não tenha usado a task pronta:

```bash
javac -d output -cp lib/antlr-4.13.1-complete.jar src/antlr/*.java src/Main.java src/exception/*.java src/interpreter/*.java
```

- Os .class serão gerados dentro da pasta output/.

### 3. Rodar o interpretador

Execute o programa passando o classpath correto( Caso tenha a extensão java no vscode, é possivel rodar diretamente usando Run java):

```bash
java -cp ".;lib/antlr-4.13.1-complete.jar;output" Main
```

- O interpretador irá ler o arquivo input/triangulo_pascal.txt (ou outro arquivo configurado) e interpretar os comandos.

## 📂 Estrutura de pastas

```bash
src/
├── antlr/               # Lexer, Parser, Listener e Visitor gerados
├── grammar/             # Arquivo Grammar.g4
├── Main.java            # Ponto de entrada do programa
interpreter/    # Interpretador da linguagem
├── Interpreter.java
exception/ # Tratamento de erros personalizados
├── CustomErrorListener.java
lib/
├── antlr-4.13.1-complete.jar # Biblioteca ANTLR
input/ # Pasta onde ficam os arquivos de teste para rodar o programa
│ 
output/                  # Arquivos .class compilados

```
