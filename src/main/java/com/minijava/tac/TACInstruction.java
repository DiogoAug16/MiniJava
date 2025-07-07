package com.minijava.tac;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TACInstruction {
    private String op;                    // Operação (ex: +, -, *, goto, print, etc.)
    private TACOperand result;           // Resultado da operação (lado esquerdo da atribuição)
    private TACOperand arg1;             // Primeiro argumento (lado direito)
    private TACOperand arg2;             // Segundo argumento (em operações binárias)
    private List<TACOperand> args;       // Lista de argumentos (usado em concatenação, por exemplo)

    // Construtor para instruções com até dois argumentos (ex: aritméticas, condicionais, copy)
    public TACInstruction(String op, TACOperand result, TACOperand arg1, TACOperand arg2) {
        this.op = op;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    // Construtor para instruções com múltiplos argumentos (ex: concat)
    public TACInstruction(String op, TACOperand result, List<TACOperand> args) {
        this.op = op;
        this.result = result;
        this.args = args;
    }

    public String getOp() {
        return op;
    }

    public TACOperand getResult() {
        return result;
    }

    public TACOperand getArg1() {
        return arg1;
    }

    public TACOperand getArg2() {
        return arg2;
    }

    // Gera a representação textual da instrução TAC
    @Override
    public String toString() {

        // Caso seja um rótulo (label)
        if (op.endsWith(":")) {
            return op;
        }

        // Instrução condicional (ex: if a < b goto L1)
        if (Arrays.asList("==", "!=", "<", ">", "<=", ">=").contains(op) && arg1 != null && arg2 != null) {
            return "if " + arg1 + " " + op + " " + arg2 + " goto " + result;
        }

        // Instruções simples: goto, print, println, read
        if (op.equals("goto") || op.equals("print") || op.equals("println") || op.equals("read")) {
            return op + " " + result;
        }

        // Instrução de cópia (COPY representa atribuição direta)
        if (op.equals("COPY")) {
            return result + " := " + arg1;
        }

        // Instrução binária (ex: t1 := a + b)
        if (arg2 != null) {
            return result + " := " + arg1 + " " + op + " " + arg2;
        }

        // chamada de função com 1 argumento (ex: t1 := -a)
        if (arg1 != null) {
            return result + " := " + op + " " + arg1;
        }

        // Instrução de concatenação com múltiplos argumentos (ex: t1 := concat "a", b, "c")
        if (args != null && op.equals("concat")) {
            String joined = args.stream()
                                 .map(Object::toString)
                                 .collect(Collectors.joining(", "));
            return result + " := concat " + joined;
        }
        
        // Caso nenhum padrão seja reconhecido
        return "Instrução desconhecida: " + op;
    }
}
