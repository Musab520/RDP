package com.example.demo;

public class Token {
    private TokenType type;
    private String value;

    private int lineNumber;

    private int charPosition;

    public Token(TokenType type, String value, int lineNumber, int charPosition) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getCharPosition() {
        return charPosition;
    }
}
