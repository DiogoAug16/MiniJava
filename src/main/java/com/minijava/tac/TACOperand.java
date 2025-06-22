package com.minijava.tac;

public class TACOperand {
    public enum Type {
        IDENTIFIER, TEMPORARY, LABEL, LITERAL
    }
    private Type type;
    private String value;

    public TACOperand(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static TACOperand temporary(int id) {
        return new TACOperand(Type.TEMPORARY, "_t" + id);
    }

    public static TACOperand label(int id) {
        return new TACOperand(Type.LABEL, "L" + id);
    }

    public static TACOperand literal(String val) {
        return new TACOperand(Type.LITERAL, val);
    }

    public static TACOperand identifier(String name) {
        return new TACOperand(Type.IDENTIFIER, name);
    }

}
