package com.example.demo;

import java.util.ArrayList;

public class LexicalAnalyzer {
    private final ArrayList<String> input;
    private int position;
    private int lineNumber;
    private int charPosition;

    public LexicalAnalyzer(ArrayList<String> input) {
        this.input = input;
        this.position = 0;
        this.lineNumber = 1;
        this.charPosition = 1;
    }

    public Token nextToken() {
        if (position >= input.size()) {
            return new Token(TokenType.DOT, ".", lineNumber, charPosition);
        }

        String currentString = input.get(position);

        if (currentString.equals(".")) {
            position++;
            charPosition++;
            return new Token(TokenType.DOT, ".", lineNumber, charPosition);
        } else if (currentString.equals(";")) {
            position++;
            charPosition++;
            return new Token(TokenType.SEMICOLON, ";", lineNumber, charPosition);
        } else if (currentString.equals("=")) {
            position++;
            charPosition++;
            return new Token(TokenType.EQUALS, "=", lineNumber, charPosition);
        } else if (Character.isLetter(currentString.charAt(0))) {
            return parseIdentifier(currentString);
        } else if (Character.isDigit(currentString.charAt(0))) {
            return parseInteger(currentString);
        } else if (currentString.equals("+")) {
            position++;
            charPosition++;
            return new Token(TokenType.ADD_SIGN, "+", lineNumber, charPosition);
        } else if (currentString.equals("-")) {
            position++;
            charPosition++;
            return new Token(TokenType.SUB_SIGN, "-", lineNumber, charPosition);
        } else if (currentString.equals("*")) {
            position++;
            charPosition++;
            return new Token(TokenType.MUL_SIGN, "*", lineNumber, charPosition);
        } else if (currentString.equals("/")) {
            position++;
            charPosition++;
            return new Token(TokenType.DIV_SIGN, "/", lineNumber, charPosition);
        } else if (currentString.equals("%")) {
            position++;
            charPosition++;
            return new Token(TokenType.MOD_SIGN, "%", lineNumber, charPosition);
        } else if (currentString.equals("(")) {
            position++;
            charPosition++;
            return new Token(TokenType.LEFT_PAREN, "(", lineNumber, charPosition);
        } else if (currentString.equals(")")) {
            position++;
            charPosition++;
            return new Token(TokenType.RIGHT_PAREN, ")", lineNumber, charPosition);
        } else if (currentString.equals(",")) {
            position++;
            charPosition++;
            return new Token(TokenType.COMMA, ",", lineNumber, charPosition);
        } else if (currentString.equals(":")) {
            position++;
            charPosition++;
            return new Token(TokenType.COLON, ",", lineNumber, charPosition);
        } else if (currentString.equals(">")) {
            position++;
            charPosition++;
            return new Token(TokenType.GREATER_THAN, ">", lineNumber, charPosition);
        } else if (currentString.equals("<")) {
            position++;
            charPosition++;
            return new Token(TokenType.LESS_THAN, "<", lineNumber, charPosition);
        }  else if (currentString.equals("<=")) {
            position++;
            charPosition++;
            return new Token(TokenType.LESS_THAN_EQUALS, "<=", lineNumber, charPosition);
        }  else if (currentString.equals(">=")) {
            position++;
            charPosition++;
            return new Token(TokenType.GREATER_THAN_EQUALS, ">=", lineNumber, charPosition);
        } else if (currentString.equals("<>")) {
            position++;
            charPosition++;
            return new Token(TokenType.NOT_EQUALS, "<>", lineNumber, charPosition);
        } else if (currentString.equals(" ")) {
            position++;
            charPosition++;
            return new Token(TokenType.SPACE, " ", lineNumber, charPosition);
        } else if (currentString.equals("\n")) {
            lineNumber++;
            position++;
            charPosition = 1;
            return new Token(TokenType.NEW_LINE, "\n", lineNumber, charPosition);
        }
        // Add more token recognition logic here based on your grammar

        return new Token(TokenType.ERROR, "Invalid token", lineNumber, charPosition);
    }

    private Token parseIdentifier(String identifierStr) {
        position++;
        // Check if the identifier is a reserved word
        switch (identifierStr) {
            case "project" -> {
                return new Token(TokenType.PROJECT, identifierStr, lineNumber, charPosition);
            }
            case "const" -> {
                return new Token(TokenType.CONST, identifierStr, lineNumber, charPosition);
            }
            case "var" -> {
                return new Token(TokenType.VAR, identifierStr, lineNumber, charPosition);
            }
            case "int" -> {
                return new Token(TokenType.INT, identifierStr, lineNumber, charPosition);
            }
            case "routine" -> {
                return new Token(TokenType.ROUTINE, identifierStr, lineNumber, charPosition);
            }
            case "start" -> {
                return new Token(TokenType.START, identifierStr, lineNumber, charPosition);
            }
            case "end" -> {
                return new Token(TokenType.END, identifierStr, lineNumber, charPosition);
            }
            case "input" -> {
                return new Token(TokenType.INPUT, identifierStr, lineNumber, charPosition);
            }
            case "output" -> {
                return new Token(TokenType.OUTPUT, identifierStr, lineNumber, charPosition);
            }
            case "if" -> {
                return new Token(TokenType.IF, identifierStr, lineNumber, charPosition);
            }
            case "then" -> {
                return new Token(TokenType.THEN, identifierStr, lineNumber, charPosition);
            }
            case "else" -> {
                return new Token(TokenType.ELSE, identifierStr, lineNumber, charPosition);
            }
            case "endif" -> {
                return new Token(TokenType.ENDIF, identifierStr, lineNumber, charPosition);
            }
            case "loop" -> {
                return new Token(TokenType.LOOP, identifierStr, lineNumber, charPosition);
            }
            case "do" -> {
                return new Token(TokenType.DO, identifierStr, lineNumber, charPosition);
            }
        }
        if(identifierStr.matches("[a-zA-Z]+")) {
            return new Token(TokenType.NAME, identifierStr, lineNumber, charPosition);
        }
        return new Token(TokenType.ERROR, "Invalid identifier: " + identifierStr, lineNumber, charPosition);
    }

    private Token parseInteger(String integer) {
        position++;
        return new Token(TokenType.INTEGER_VALUE, integer, lineNumber, charPosition);
    }
}
