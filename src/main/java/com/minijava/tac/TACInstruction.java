package com.minijava.tac;

import java.util.Arrays;

public class TACInstruction {
    private String op;
    private TACOperand result;
    private TACOperand arg1;
    private TACOperand arg2;

    public TACInstruction(String op, TACOperand result, TACOperand arg1, TACOperand arg2) {
        this.op = op;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
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
        } else if (Arrays.asList("==", "!=", "<", ">", "<=", ">=").contains(op)) {
            return "if " + arg1 + " " + op + " " + arg2 + " goto " + result;
        } else if (op.equals("goto") || op.equals("print") || op.equals("println") || op.equals("read")) {
            return op + " " + result;
        } else if (op.equals("ifFalse")) {
            return "ifFalse " + arg1 + " goto " + result;
        } else if(op.equals("COPY")) {
            return result + " := " + arg1;
        } else if (arg2 != null) {
            return result + " := " + arg1 + " " + op + " " + arg2;
        } else if (arg1 != null) {
            return result + " := " + op + " " + arg1;
        }

        return "Instrução desconhecida: " + op;
    }
}

