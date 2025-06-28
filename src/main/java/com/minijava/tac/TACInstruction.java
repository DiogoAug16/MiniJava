package com.minijava.tac;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TACInstruction {
    private String op;
    private TACOperand result;
    private TACOperand arg1;
    private TACOperand arg2;
    private List<TACOperand> args;

    public TACInstruction(String op, TACOperand result, TACOperand arg1, TACOperand arg2) {
        this.op = op;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public TACInstruction(String op, TACOperand result, List<TACOperand> args) {
        this.op = op;
        this.result = result;
        this.args = args;
    }

    public List<TACOperand> getArgs() {
        return this.args;
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

    @Override
    public String toString() {
        if (op.endsWith(":")) {
            return op;
        }

        if (Arrays.asList("==", "!=", "<", ">", "<=", ">=").contains(op) && arg1 != null && arg2 != null) {
            return "if " + arg1 + " " + op + " " + arg2 + " goto " + result;
        }

        if (op.equals("goto") || op.equals("print") || op.equals("println") || op.equals("read")) {
            return op + " " + result;
        }

        if (op.equals("COPY")) {
            return result + " := " + arg1;
        }

        if (arg2 != null) {
            return result + " := " + arg1 + " " + op + " " + arg2;
        }

        if (arg1 != null) {
            return result + " := " + op + " " + arg1;
        }

        if (args != null && op.equals("concat")) {
            String joined = args.stream()
                                 .map(Object::toString)
                                 .collect(Collectors.joining(", "));
            return result + " := concat " + joined;
        }
        
        return "Instrução desconhecida: " + op;
    }
}
