package com.minijava.semantic;

abstract class Type {
    public abstract String getName();

    public boolean isEquivalentTo(Type other) {
        if (other == null) return false;

        return this.getClass() == other.getClass();
    }

    @Override
    public String toString() {
        return getName();
    }
}

class IntType extends Type {
    private static final IntType INSTANCE = new IntType();
    private IntType() {}
    public static IntType getInstance() { return INSTANCE; }
    @Override public String getName() { return "int"; }
}

class StringType extends Type {
    private static final StringType INSTANCE = new StringType();
    private StringType() {}
    public static StringType getInstance() { return INSTANCE; }
    @Override public String getName() { return "string"; }
}

class BooleanType extends Type {
    private static final BooleanType INSTANCE = new BooleanType();
    private BooleanType() {}
    public static BooleanType getInstance() { return INSTANCE; }
    @Override public String getName() { return "boolean"; }
}

class UndefinedType extends Type {
    private static final UndefinedType INSTANCE = new UndefinedType();
    private UndefinedType() {}
    public static UndefinedType getInstance() { return INSTANCE; }
    @Override public String getName() { return "undefined"; }
    @Override
    public boolean isEquivalentTo(Type other) {
        return false;
    }
}

/*
class VoidType extends Type {
    private static final VoidType INSTANCE = new VoidType();
    private VoidType() {}
    public static VoidType getInstance() { return INSTANCE; }
    @Override public String getName() { return "void"; }
}
*/