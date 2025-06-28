package com.minijava.tac;

public class TACOperand {
    public enum LiteralType { 
        INT, STRING 
    }

    public enum Type {
        IDENTIFIER, TEMPORARY, LABEL, LITERAL
    }

    private Type type;
    private String value;
    private LiteralType literalType;

    public TACOperand(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public TACOperand(String value, LiteralType literalType) {
        this.type = Type.LITERAL;
        this.value = value;
        this.literalType = literalType;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public LiteralType getLiteralType() {
        return literalType;
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
    
    public static TACOperand literal(String val, LiteralType type) {
        return new TACOperand(val, type);
    }
    
    public static TACOperand literal(String val) {
        return new TACOperand(val, LiteralType.INT);
    }
    
    public static TACOperand identifier(String name) {
        return new TACOperand(Type.IDENTIFIER, name);
    }
}
