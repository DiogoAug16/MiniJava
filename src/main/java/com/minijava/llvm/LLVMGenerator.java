package com.minijava.llvm;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLVMGenerator {

    private record LLVMValue(String type, String registerOrValue) {}

    private final List<String> tacLines;
    private final List<String> llvmCode = new ArrayList<>();
    
    private int regCounter = 1;
    private int labelCounter = 1;
    private int stringCounter = 0;

    private final Map<String, String> symbolTable = new HashMap<>();
    private final Map<String, String> variableTypes = new HashMap<>();
    private final Map<String, String> stringLiterals = new HashMap<>();

    private static final Pattern LABEL_PATTERN = Pattern.compile("^(L\\d+):$");
    private static final Pattern IF_GOTO_PATTERN = Pattern.compile("^if (.+?) (==|!=|<|>|<=|>=) (.+?) goto (L\\d+)$");
    private static final Pattern GOTO_PATTERN = Pattern.compile("^goto (L\\d+)$");
    private static final Pattern CONCAT_PATTERN = Pattern.compile("^(\\S+) := concat (.+)$");
    private static final Pattern PRINT_PATTERN = Pattern.compile("^(print|println) (.+)$");
    private static final Pattern READ_PATTERN = Pattern.compile("^read (\\S+)$");
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^(\\S+) := (.+)$");

    public LLVMGenerator(List<String> tacLines) {
        this.tacLines = tacLines;
    }

    private String getNewReg() {
        return "%r" + (regCounter++);
    }

    private String getStringLiteralId(String s) {
        if (!stringLiterals.containsKey(s)) {
            String id = "@.str." + (stringCounter++);
            stringLiterals.put(s, id);
        }
        return stringLiterals.get(s);
    }

    public String generate() {
        generateHeader();
        generateMainFunctionStart();

        for (String line : tacLines) {
            line = line.strip();
            if (line.isEmpty() || line.startsWith(";")) {
                continue;
            }

            llvmCode.add(String.format("\n; TAC: %s", line));

            Matcher m;
            m = LABEL_PATTERN.matcher(line);
            if (m.matches()) {
                handleLabel(m.group(1));
                continue;
            }
            m = IF_GOTO_PATTERN.matcher(line);
            if (m.matches()) {
                handleConditionalBranch(m.group(1), m.group(2), m.group(3), m.group(4));
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

            llvmCode.add(String.format("; WARNING: Linha TAC não reconhecida: %s", line));
        }

        generateMainFunctionEnd();
        generateStringLiterals();

        return String.join("\n", llvmCode);
    }

    private void generateHeader() {
        llvmCode.add("; --- LLVM IR Gerado ---");
        llvmCode.add("declare i32 @printf(i8*, ...)");
        llvmCode.add("declare i32 @scanf(i8*, ...)");
        llvmCode.add("declare noalias i8* @my_itoa(i32)");
        llvmCode.add("declare noalias i8* @concat(i32, ...)");
        getStringLiteralId("%d");
        getStringLiteralId("%s");
    }

    private void generateStringLiterals() {
        llvmCode.add("\n; --- Constantes Globais de String ---");
        for (Map.Entry<String, String> entry : stringLiterals.entrySet()) {
            String s = entry.getKey();
            String id = entry.getValue();
            String s_escaped = s.replace("\"", "\\\"").replace("\n", "\\0A") + "\\00";
            int strLen = s.getBytes(StandardCharsets.UTF_8).length + 1;
            llvmCode.add(String.format("%s = private unnamed_addr constant [%d x i8] c\"%s\"", id, strLen, s_escaped));
        }
    }

    private void generateMainFunctionStart() {
        llvmCode.add("\ndefine i32 @main() {");
        llvmCode.add("entry:");
    }

    private void generateMainFunctionEnd() {
        llvmCode.add("  ret i32 0");
        llvmCode.add("}");
    }

    private String allocVar(String varName, String varType) {
        if (!symbolTable.containsKey(varName)) {
            int entryIndex = llvmCode.indexOf("entry:") + 1;
            llvmCode.add(entryIndex, String.format("  %%%s = alloca %s", varName, varType));
            symbolTable.put(varName, "%" + varName);
            variableTypes.put(varName, varType);
        }
        return symbolTable.get(varName);
    }
    
    private String getVarType(String varName) {
        return variableTypes.getOrDefault(varName, "i32"); // Assume i32 se não encontrado
    }

    private LLVMValue loadValue(String operand) {
        operand = operand.strip();
        if (operand.matches("-?\\d+")) {
            return new LLVMValue("i32", operand);
        } 
        else if (operand.startsWith("\"") && operand.endsWith("\"")) {
            String strContent = operand.substring(1, operand.length() - 1);
            String strId = getStringLiteralId(strContent);
            int strLen = strContent.getBytes(StandardCharsets.UTF_8).length + 1;
            String reg = getNewReg();
            llvmCode.add(String.format("  %s = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0", reg, strLen, strLen, strId));
            return new LLVMValue("i8*", reg);
        } 
        else {
            String varType = getVarType(operand);
            String varPtr = allocVar(operand, varType);
            String reg = getNewReg();
            llvmCode.add(String.format("  %s = load %s, %s* %s", reg, varType, varType, varPtr));
            return new LLVMValue(varType, reg);
        }
    }

    private void handleLabel(String labelName) {
        if (!llvmCode.get(llvmCode.size() - 2).strip().matches("^(br|ret).*")) {
            llvmCode.add(String.format("  br label %%%s", labelName));
        }
        llvmCode.add(labelName + ":");
    }
    
    private void handleGoto(String labelName) {
        llvmCode.add(String.format("  br label %%%s", labelName));
    }

    private void handleConditionalBranch(String arg1, String op, String arg2, String labelName) {
        LLVMValue val1 = loadValue(arg1);
        LLVMValue val2 = loadValue(arg2);
        
        Map<String, String> opMap = Map.of(
            "==", "eq", "!=", "ne", "<", "slt",
            "<=", "sle", ">", "sgt", ">=", "sge"
        );
        String llvmOp = "icmp " + opMap.get(op);
        
        String condReg = getNewReg();
        llvmCode.add(String.format("  %s = %s %s %s, %s", condReg, llvmOp, val1.type(), val1.registerOrValue(), val2.registerOrValue()));
        
        String nextLabel = "L_fall." + (labelCounter++);
        llvmCode.add(String.format("  br i1 %s, label %%%s, label %%%s", condReg, labelName, nextLabel));
        llvmCode.add(nextLabel + ":");
    }

    private void handleAssignment(String resultVar, String expression) {
        Pattern binaryOpPattern = Pattern.compile("(.+?) (\\+|-|\\*|/) (.+)");
        Matcher m = binaryOpPattern.matcher(expression);
        if (m.matches()) {
            String arg1 = m.group(1);
            String op = m.group(2);
            String arg2 = m.group(3);
            
            LLVMValue val1 = loadValue(arg1);
            LLVMValue val2 = loadValue(arg2);
            
            Map<String, String> opMap = Map.of("+", "add nsw", "-", "sub nsw", "*", "mul nsw", "/", "sdiv");
            String resReg = getNewReg();
            llvmCode.add(String.format("  %s = %s %s %s, %s", resReg, opMap.get(op), val1.type(), val1.registerOrValue(), val2.registerOrValue()));
            
            String resultPtr = allocVar(resultVar, "i32");
            llvmCode.add(String.format("  store i32 %s, i32* %s", resReg, resultPtr));
            return;
        }

        LLVMValue valToAssign = loadValue(expression);
        String resultPtr = allocVar(resultVar, valToAssign.type());
        llvmCode.add(String.format("  store %s %s, %s* %s", valToAssign.type(), valToAssign.registerOrValue(), valToAssign.type(), resultPtr));
    }

    private void handlePrint(String command, String operand) {
        LLVMValue val = loadValue(operand);
        String newline = command.equals("println") ? "\n" : "";
        
        String formatStr = val.type().equals("i32") ? "%d" + newline : "%s" + newline;
        
        String formatStrId = getStringLiteralId(formatStr);
        int formatLen = formatStr.getBytes(StandardCharsets.UTF_8).length + 1;
        
        String formatPtr = getNewReg();
        llvmCode.add(String.format("  %s = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0", formatPtr, formatLen, formatLen, formatStrId));
        llvmCode.add(String.format("  call i32 (i8*, ...) @printf(i8* %s, %s %s)", formatPtr, val.type(), val.registerOrValue()));
    }

    private void handleRead(String resultVar) {
        String resultPtr = allocVar(resultVar, "i32");
        String formatStrId = getStringLiteralId("%d");
        int formatLen = "%d".getBytes(StandardCharsets.UTF_8).length + 1;
        
        String formatPtr = getNewReg();
        llvmCode.add(String.format("  %s = getelementptr inbounds [%d x i8], [%d x i8]* %s, i64 0, i64 0", formatPtr, formatLen, formatLen, formatStrId));
        llvmCode.add(String.format("  call i32 (i8*, ...) @scanf(i8* %s, i32* %s)", formatPtr, resultPtr));
    }
    
    private void handleConcat(String resultVar, String argsStr) {
        List<String> args = new ArrayList<>();
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
            
            if (val.type().equals("i32")) {
                llvmCode.add(String.format(";   Convertendo o inteiro %s para string", arg));
                String strReg = getNewReg();
                llvmCode.add(String.format("  %s = call i8* @my_itoa(i32 %s)", strReg, val.registerOrValue()));
                finalArgsRegs.add(strReg);
            } else { // Já é i8*
                finalArgsRegs.add(val.registerOrValue());
            }
        }
    
        StringBuilder callParams = new StringBuilder(String.format("i32 %d", numParams));
        for (String reg : finalArgsRegs) {
            callParams.append(String.format(", i8* %s", reg));
        }
    
        String resReg = getNewReg();
        llvmCode.add(String.format("; Chamando concat com %d argumentos do tipo string", numParams));
        llvmCode.add(String.format("  %s = call i8* (i32, ...) @concat(%s)", resReg, callParams.toString()));
        
        String resultPtr = allocVar(resultVar, "i8*");
        llvmCode.add(String.format("  store i8* %s, i8** %s", resReg, resultPtr));
    }
}