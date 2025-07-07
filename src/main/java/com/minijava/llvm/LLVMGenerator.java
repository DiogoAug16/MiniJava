package com.minijava.llvm;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLVMGenerator {

    // Registro interno para representar valores LLVM com seu tipo e nome/valor
    private record LLVMValue(String type, String registerOrValue) {}

    private final List<String> tacLines;  // Linhas de código TAC de entrada
    private final List<String> llvmCode = new ArrayList<>();  // Código LLVM gerado

    private int regCounter = 1;      // Contador para registros temporários LLVM (%r1, %r2, ...)
    private int labelCounter = 1;    // Contador para rótulos auxiliares em condições
    private int stringCounter = 0;   // Contador para strings literais globais

    // Tabelas para controle de variáveis, tipos e strings literais
    private final Map<String, String> symbolTable = new HashMap<>();
    private final Map<String, String> variableTypes = new HashMap<>();
    private final Map<String, String> stringLiterals = new HashMap<>();

    // Expressões regulares para reconhecer padrões TAC
    private static final Pattern LABEL_PATTERN = Pattern.compile("^(L\\d+):$");
    // Ex: "L1:"
    private static final Pattern IF_GOTO_PATTERN = Pattern.compile("^if (.+?) (==|!=|<|>|<=|>=) (.+?) goto (L\\d+)$");
    // Ex: "if t1 == t2 goto L2"
    private static final Pattern GOTO_PATTERN = Pattern.compile("^goto (L\\d+)$");
    // Ex: "goto L3"
    private static final Pattern CONCAT_PATTERN = Pattern.compile("^(\\S+) := concat (.+)$");
    // Ex: "s1 := concat "hello", " world""
    private static final Pattern PRINT_PATTERN = Pattern.compile("^(print|println) (.+)$");
    // Ex: "print t1" ou "println t2"
    private static final Pattern READ_PATTERN = Pattern.compile("^read (\\S+)$");
    // Ex: "read x"
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^(\\S+) := (.+)$");
    // Ex: "t1 := 5" ou "t2 := t0 + t1"

    public LLVMGenerator(List<String> tacLines) {
        this.tacLines = tacLines;
    }

    // Gera um novo nome para registro temporário LLVM (%rN)
    private String getNewReg() {
        return "%r" + (regCounter++);
    }

    // Retorna o identificador global LLVM para uma string literal,
    // garantindo que não haja duplicidade
    private String getLLVMString(String s) {
        if (!stringLiterals.containsKey(s)) {
            String id = "@.str." + (stringCounter++);
            stringLiterals.put(s, id);
        }
        return stringLiterals.get(s);
    }

    // Método principal para geração do código LLVM a partir das linhas TAC
    public String generate() {
        generateHeader();      // Gera cabeçalhos e declarações externas
        generateMainStart();   // Inicia a função main

        for (String line : tacLines) {
            line = line.strip();
            if (line.isEmpty() || line.startsWith(";")) {
                continue;  // Ignora linhas vazias ou comentários TAC
            }

            llvmCode.add(String.format("\n; TAC: %s", line));  // Adiciona comentário para linha TAC

            Matcher m;
            // Tenta corresponder a linha TAC com cada um dos padrões definidos.
            m = LABEL_PATTERN.matcher(line);
            if (m.matches()) {
                handleLabel(m.group(1));
                continue;
            }
            m = IF_GOTO_PATTERN.matcher(line);
            if (m.matches()) {
                handleConditional(m.group(1), m.group(2), m.group(3), m.group(4));
                continue;
            }
            m = GOTO_PATTERN.matcher(line);
            if (m.matches()) {
                handleGoto(m.group(1));
                continue;
            }
            m = CONCAT_PATTERN.matcher(line);
            if (m.matches()) {
                handleConcat(m.group(1), m.group(2));
                continue;
            }
            m = PRINT_PATTERN.matcher(line);
            if (m.matches()) {
                handlePrint(m.group(1), m.group(2));
                continue;
            }
            m = READ_PATTERN.matcher(line);
            if (m.matches()) {
                handleRead(m.group(1));
                continue;
            }
            m = ASSIGNMENT_PATTERN.matcher(line);
            if (m.matches()) {
                handleAssignment(m.group(1), m.group(2));
                continue;
            }

            // Caso não reconheça o padrão da linha TAC
            llvmCode.add(String.format("; WARNING: Linha TAC não reconhecida: %s", line));
        }

        generateMainEnd();   // Finaliza função main
        generateString();    // Gera as strings literais globais no LLVM

        return String.join("\n", llvmCode);
    }

    // Gera o cabeçalho do arquivo LLVM (declarações externas e strings comuns)
    private void generateHeader() {
        llvmCode.add("; --- LLVM IR Gerado ---");
        llvmCode.add("declare i32 @printf(i8*, ...)");   // Declara printf
        llvmCode.add("declare i32 @scanf(i8*, ...)");    // Declara scanf
        llvmCode.add("declare noalias i8* @my_itoa(i32)"); // Declara função externa para itoa ( int para string )
        llvmCode.add("declare noalias i8* @concat(i32, ...)"); // Declara função externa concat
        getLLVMString("%d");  // Garante que as strings de formato estão cadastradas
        getLLVMString("%s");
    }

    // Gera as definições globais para as strings literais usadas
    private void generateString() {
        llvmCode.add("\n; --- Constantes Globais de String ---");
        for (Map.Entry<String, String> entry : stringLiterals.entrySet()) {
            String s = entry.getKey();
            String id = entry.getValue();
            // Escapa aspas e quebras de linha, adiciona caractere nulo final
            String s_escaped = s.replace("\"", "\\\"").replace("\n", "\\0A") + "\\00";
            int strLen = s.getBytes(StandardCharsets.UTF_8).length + 1;
            llvmCode.add(String.format("%s = private unnamed_addr constant [%d x i8] c\"%s\"", id, strLen, s_escaped));
        }
    }

    // Inicia a definição da função main no LLVM
    private void generateMainStart() {
        llvmCode.add("\ndefine i32 @main() {");
        llvmCode.add("entry:");
    }

    // Finaliza a função main com retorno zero
    private void generateMainEnd() {
        llvmCode.add("  ret i32 0");
        llvmCode.add("}");
    }

    // Aloca espaço na pilha para variável local (alloca) se ainda não existir e retorna o ponteiro
    private String allocVar(String varName, String varType) {
        if (!symbolTable.containsKey(varName)) {
            int entryIndex = llvmCode.indexOf("entry:") + 1;  // Insere logo após label 'entry:'
            llvmCode.add(entryIndex, String.format("  %%%s = alloca %s", varName, varType));
            symbolTable.put(varName, "%" + varName);
            variableTypes.put(varName, varType);
        }
        return symbolTable.get(varName);
    }

    // Retorna o tipo da variável; assume i32 por padrão
    private String getVarType(String varName) {
        return variableTypes.getOrDefault(varName, "i32");
    }

    // Carrega o valor de um operando (literal, string ou variável)
    private LLVMValue loadValue(String operand) {
        operand = operand.strip();
        // 1: Literal inteiro
        if (operand.matches("-?\\d+")) {
            return new LLVMValue("i32", operand);
        } 
        // 2: Literal string
        else if (operand.startsWith("\"") && operand.endsWith("\"")) {
            String strContent = operand.substring(1, operand.length() - 1);
            String strId = getLLVMString(strContent);
            int strLen = strContent.getBytes(StandardCharsets.UTF_8).length + 1;
            String reg = getNewReg();
            // Gera instrução getelementptr para obter ponteiro para string
            llvmCode.add(String.format("  %s = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0", reg, strLen, strLen, strId));
            return new LLVMValue("i8*", reg);
        } 
        // 3: Variável: carrega valor da alocação
        else {
            String varType = getVarType(operand);
            String varPtr = allocVar(operand, varType);
            String reg = getNewReg();
            // Carrega o valor do ponteiro da pilha para um registrador.
            llvmCode.add(String.format("  %s = load %s, %s* %s", reg, varType, varType, varPtr));
            return new LLVMValue(varType, reg);
        }
    }

    // Gera código para um label no LLVM, com um branch anterior se necessário
    private void handleLabel(String labelName) {
        // Se o comando anterior não foi um branch ou return, insere branch para o label
        if (!llvmCode.get(llvmCode.size() - 2).strip().matches("^(br|ret).*")) {
            llvmCode.add(String.format("  br label %%%s", labelName));
        }
        llvmCode.add(labelName + ":");
    }

    // Gera um branch incondicional para o label dado
    private void handleGoto(String labelName) {
        llvmCode.add(String.format("  br label %%%s", labelName));
    }

    // Gera código para instrução condicional if ... goto label
    private void handleConditional(String arg1, String op, String arg2, String labelName) {
        LLVMValue val1 = loadValue(arg1);
        LLVMValue val2 = loadValue(arg2);
        
        // Mapeia operadores do TAC
        Map<String, String> opMap = Map.of(
            "==", "eq", "!=", "ne", "<", "slt",
            "<=", "sle", ">", "sgt", ">=", "sge"
        );
        String llvmOp = "icmp " + opMap.get(op);
        
        String condReg = getNewReg();
        // Gera instrução de comparação
        llvmCode.add(String.format("  %s = %s %s %s, %s", condReg, llvmOp, val1.type(), val1.registerOrValue(), val2.registerOrValue()));
        
        // Gera um novo label para o caso 'else'.
        String nextLabel = "L_else." + (labelCounter++);
        // Gera o salto condicional.
        llvmCode.add(String.format("  br i1 %s, label %%%s, label %%%s", condReg, labelName, nextLabel));
        // Define o label do 'else' para a continuação do código.
        llvmCode.add(nextLabel + ":");
    }

    // Trata atribuição simples ou com operação binária (+, -, *, /)
    private void handleAssignment(String resultVar, String expression) {
        // Verifica se a expressão é uma operação binária (ex: t0 + t1).
        Pattern binaryOpPattern = Pattern.compile("(.+?) (\\+|-|\\*|/) (.+)");
        Matcher m = binaryOpPattern.matcher(expression);
        if (m.matches()) {
            // Expressão binária
            String arg1 = m.group(1);
            String op = m.group(2);
            String arg2 = m.group(3);
            
            LLVMValue val1 = loadValue(arg1);
            LLVMValue val2 = loadValue(arg2);
            
            // Mapeia operador para instrução LLVM aritmética
            Map<String, String> opMap = Map.of("+", "add nsw", "-", "sub nsw", "*", "mul nsw", "/", "sdiv");
            String resultReg = getNewReg();
            // Gera a instrução aritmética.
            llvmCode.add(String.format("  %s = %s %s %s, %s", resultReg, opMap.get(op), val1.type(), val1.registerOrValue(), val2.registerOrValue()));
            
            // Armazena o resultado na variável de destino.
            String resultPtr = allocVar(resultVar, "i32");
            llvmCode.add(String.format("  store i32 %s, i32* %s", resultReg, resultPtr));
            return;
        }

        // Se não for uma operação binária, é uma atribuição simples (ex: x := 5, y := z).
        LLVMValue valToAssign = loadValue(expression);
        String resultPtr = allocVar(resultVar, valToAssign.type());
        llvmCode.add(String.format("  store %s %s, %s* %s", valToAssign.type(), valToAssign.registerOrValue(), valToAssign.type(), resultPtr));
    }

    // Gera código para impressão (print ou println)
    private void handlePrint(String command, String operand) {
        LLVMValue val = loadValue(operand);
        String newline = command.equals("println") ? "\n" : "";
        
        // Define string de formato dependendo do tipo (%d ou %s)
        String formatStr = val.type().equals("i32") ? "%d" + newline : "%s" + newline;
        
        String formatStrId = getLLVMString(formatStr);
        int formatLen = formatStr.getBytes(StandardCharsets.UTF_8).length + 1;
        
        String formatPtr = getNewReg();
        // Obtém ponteiro para string de formato
        llvmCode.add(String.format("  %s = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0", formatPtr, formatLen, formatLen, formatStrId));
        // Chama @printf passando formato e valor
        llvmCode.add(String.format("  call i32 (i8*, ...) @printf(i8* %s, %s %s)", formatPtr, val.type(), val.registerOrValue()));
    }

    // Gera código para leitura de entrada pelo scanf para uma variável
    private void handleRead(String resultVar) {
        String resultPtr = allocVar(resultVar, "i32");
        String formatStrId = getLLVMString("%d");
        int formatLen = "%d".getBytes(StandardCharsets.UTF_8).length + 1;
        
        String formatPtr = getNewReg();
        // Ponteiro para string de formato "%d"
        llvmCode.add(String.format("  %s = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0", formatPtr, formatLen, formatLen, formatStrId));
        // Chama scanf para ler inteiro e armazenar em resultVar
        llvmCode.add(String.format("  call i32 (i8*, ...) @scanf(i8* %s, i32* %s)", formatPtr, resultPtr));
    }
    
    // Gera código para concatenação de múltiplas strings e inteiros convertidos para string
    private void handleConcat(String resultVar, String argsStr) {
        List<String> args = new ArrayList<>();
        // Divide os argumentos separados por vírgula, considerando strings entre aspas
        Pattern argPattern = Pattern.compile("\"[^\"]*\"|[^, ]+");
        Matcher m = argPattern.matcher(argsStr);
        while (m.find()) {
            args.add(m.group());
        }
    
        int numParams = args.size();
    
        List<String> finalArgsRegs = new ArrayList<>();
        llvmCode.add("; Preparando argumentos para concat");
        for (String arg : args) {
            LLVMValue val = loadValue(arg);
            
            // Se for inteiro, converte para string com função my_itoa
            if (val.type().equals("i32")) {
                llvmCode.add(String.format("; Convertendo o inteiro %s para string", arg));
                String strReg = getNewReg();
                llvmCode.add(String.format("  %s = call i8* @my_itoa(i32 %s)", strReg, val.registerOrValue()));
                finalArgsRegs.add(strReg);
            } else { // Se já for uma string (i8*), usa diretamente.
                finalArgsRegs.add(val.registerOrValue());
            }
        }
    
        // Monta parâmetros para chamada variádica de concat: primeiro número de parâmetros, depois strings
        StringBuilder callParams = new StringBuilder(String.format("i32 %d", numParams));
        for (String reg : finalArgsRegs) {
            callParams.append(String.format(", i8* %s", reg));
        }
    
        // Chama a função concat passando os parâmetros
        String resultReg = getNewReg();
        llvmCode.add(String.format("; Chamando concat com %d argumentos do tipo string", numParams));
        llvmCode.add(String.format("  %s = call i8* (i32, ...) @concat(%s)", resultReg, callParams.toString()));
        
        // Armazena o resultado do concat em variável do programa
        String resultPtr = allocVar(resultVar, "i8*");
        llvmCode.add(String.format("  store i8* %s, i8** %s", resultReg, resultPtr));
    }
}