package com.minijava.tac;

public class TACOperand {

    // Enum para representar o tipo de literal: inteiro ou string
    public enum LiteralType { 
        INT, STRING 
    }

    // Enum para representar o tipo de operando TAC
    public enum Type {
        IDENTIFIER,   // Nome de variável
        TEMPORARY,    // Registrador temporário (ex: _t1, _t2)
        LABEL,        // Rótulo de controle de fluxo (ex: L1, L2)
        LITERAL       // Valor literal (inteiro ou string)
    }

    private Type type;             // Tipo do operando
    private String value;          // Valor (nome, literal, label, etc.)
    private LiteralType literalType;  // Tipo do literal, se aplicável

    // Construtor para IDENTIFIER, TEMPORARY ou LABEL
    public TACOperand(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    
    // Construtor para LITERAL (valor + tipo de literal)
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
    
    // Representação textual do operando (somente o valor)
    @Override
    public String toString() {
        return value;
    }

    // Cria um operando temporário (ex: _t1)
    public static TACOperand temporary(int id) {
        return new TACOperand(Type.TEMPORARY, "_t" + id);
    }

    // Cria um rótulo (label) (ex: L1)
    public static TACOperand label(int id) {
        return new TACOperand(Type.LABEL, "L" + id);
    }
    
    // Cria um operando literal (ex: "42" ou "\"hello\"")
    public static TACOperand literal(String val, LiteralType type) {
        return new TACOperand(val, type);
    }
    
    // Cria um identificador (nome de variável)
    public static TACOperand identifier(String name) {
        return new TACOperand(Type.IDENTIFIER, name);
    }
}
