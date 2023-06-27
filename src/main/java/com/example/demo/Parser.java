package com.example.demo;

import javafx.scene.control.Alert;

import java.util.ArrayList;

public class Parser {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token lookahead;

    public Parser(ArrayList<String> input) {
        lexicalAnalyzer = new LexicalAnalyzer(input);
        lookahead = lexicalAnalyzer.nextToken();
    }

    public void parse() {
        projectDeclaration();

        if (lookahead.getType() == TokenType.DOT) {
            compareTokenType(TokenType.DOT);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Parsing completed successfully.");
            alert.show();
            System.out.println("Parsing completed successfully.");
        } else {
            error("Expected '.' at the end of the project declaration position: " + lookahead.getCharPosition());
        }
    }

    private void projectDeclaration() {
        projectDef();
    }

    private void projectDef() {
        projectHeading();
        declarations();
        compoundStmt();
    }

    private void projectHeading() {
        compareTokenType(TokenType.PROJECT);
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.SEMICOLON);
    }

    private void declarations() {
        constDecl();
        varDecl();
        subroutineDecl();
    }

    private void constDecl() {
        if (lookahead.getType() == TokenType.CONST) {
            compareTokenType(TokenType.CONST);
            while (lookahead.getType() == TokenType.NAME) {
                constItem();
                compareTokenType(TokenType.SEMICOLON);
            }
        }
    }

    private void constItem() {
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.EQUALS);
        compareTokenType(TokenType.INTEGER_VALUE);
    }

    private void varDecl() {
        if (lookahead.getType() == TokenType.VAR) {
            compareTokenType(TokenType.VAR);
            while (lookahead.getType() == TokenType.NAME) {
                varItem();
                compareTokenType(TokenType.SEMICOLON);
            }
        }
    }

    private void varItem() {
        nameList();
        compareTokenType(TokenType.COLON);
        compareTokenType(TokenType.INT);
    }

    private void nameList() {
        compareTokenType(TokenType.NAME);
        while (lookahead.getType() == TokenType.COMMA) {
            compareTokenType(TokenType.COMMA);
            compareTokenType(TokenType.NAME);
        }
    }

    private void subroutineDecl() {
        if (lookahead.getType() == TokenType.ROUTINE) {
            subroutineHeading();
            declarations();
            compoundStmt();
            compareTokenType(TokenType.SEMICOLON);
        }
    }

    private void subroutineHeading() {
        compareTokenType(TokenType.ROUTINE);
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.SEMICOLON);
    }

    private void compoundStmt() {
        compareTokenType(TokenType.START);
        stmtList();
        compareTokenType(TokenType.END);
    }

    private void stmtList() {
        while (lookahead.getType() != TokenType.END) {
                statement();
            if (lookahead.getType() != TokenType.SEMICOLON)
                error("Expected ';' between statements position: " + lookahead.getCharPosition());
            compareTokenType(TokenType.SEMICOLON);
        }
    }

    private void statement() {
        if (lookahead.getType() == TokenType.NAME)
            assStmt();
        else if (lookahead.getType() == TokenType.INPUT || lookahead.getType() == TokenType.OUTPUT)
            inoutStmt();
        else if (lookahead.getType() == TokenType.IF)
            ifStmt();
        else if (lookahead.getType() == TokenType.LOOP)
            loopStmt();
    }

    private void assStmt() {
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.COLON);
        compareTokenType(TokenType.EQUALS);
        arithExp();
    }

    private void arithExp() {
        term();
        while (lookahead.getType() == TokenType.ADD_SIGN || lookahead.getType() == TokenType.SUB_SIGN) {
            if (lookahead.getType() == TokenType.ADD_SIGN) {
                compareTokenType(TokenType.ADD_SIGN);
            }
            else if (lookahead.getType() == TokenType.SUB_SIGN) {
                compareTokenType(TokenType.SUB_SIGN);
            }
            term();
        }

    }

    private void term() {
        factor();
        while (lookahead.getType() == TokenType.MUL_SIGN || lookahead.getType() == TokenType.DIV_SIGN
                || lookahead.getType() == TokenType.MOD_SIGN) {
            if (lookahead.getType() == TokenType.MUL_SIGN) {
                compareTokenType(TokenType.MUL_SIGN);
            }
            else if (lookahead.getType() == TokenType.DIV_SIGN) {
                compareTokenType(TokenType.DIV_SIGN);
            }
            else if (lookahead.getType() == TokenType.MOD_SIGN) {
                compareTokenType(TokenType.MOD_SIGN);
            }
            factor();
        }
    }

    private void factor() {
        if (lookahead.getType() == TokenType.LEFT_PAREN) {
            compareTokenType(TokenType.LEFT_PAREN);
            arithExp();
            compareTokenType(TokenType.RIGHT_PAREN);
        } else if (lookahead.getType() == TokenType.NAME || lookahead.getType() == TokenType.INTEGER_VALUE) {
            nameValue();
        } else {
            error("Expected '(' or name or integer value in factor position: " + lookahead.getCharPosition() );
        }
    }

    private void nameValue() {
        if (lookahead.getType() == TokenType.NAME)
            compareTokenType(TokenType.NAME);
        else if (lookahead.getType() == TokenType.INTEGER_VALUE)
            compareTokenType(TokenType.INTEGER_VALUE);
        else
            error("Expected name or integer value in name-value position: " + lookahead.getCharPosition() );
    }

    private void inoutStmt() {
        if (lookahead.getType() == TokenType.INPUT) {
            compareTokenType(TokenType.INPUT);
            compareTokenType(TokenType.LEFT_PAREN);
            compareTokenType(TokenType.NAME);
            compareTokenType(TokenType.RIGHT_PAREN);
        } else if (lookahead.getType() == TokenType.OUTPUT) {
            compareTokenType(TokenType.OUTPUT);
            compareTokenType(TokenType.LEFT_PAREN);
            nameValue();
            compareTokenType(TokenType.RIGHT_PAREN);
        } else {
            error("Expected 'input' or 'output' in inout-stmt position: " + lookahead.getCharPosition());
        }
    }

    private void ifStmt() {
        compareTokenType(TokenType.IF);
        compareTokenType(TokenType.LEFT_PAREN);
        boolExp();
        compareTokenType(TokenType.RIGHT_PAREN);
        compareTokenType(TokenType.THEN);
        statement();
        compareTokenType(TokenType.SEMICOLON);
        elsePart();
        compareTokenType(TokenType.ENDIF);
    }

    private void elsePart() {
        if (lookahead.getType() == TokenType.ELSE) {
            compareTokenType(TokenType.ELSE);
            statement();
        }
    }

    private void loopStmt() {
        compareTokenType(TokenType.LOOP);
        compareTokenType(TokenType.LEFT_PAREN);
        boolExp();
        compareTokenType(TokenType.RIGHT_PAREN);
        compareTokenType(TokenType.DO);
        statement();
    }

    private void boolExp() {
        nameValue();
        relationalOper();
        nameValue();
    }

    private void relationalOper() {
        if (lookahead.getType() == TokenType.EQUALS)
            compareTokenType(TokenType.EQUALS);
        else if (lookahead.getType() == TokenType.NOT_EQUALS)
            compareTokenType(TokenType.NOT_EQUALS);
        else if (lookahead.getType() == TokenType.LESS_THAN)
            compareTokenType(TokenType.LESS_THAN);
        else if (lookahead.getType() == TokenType.LESS_THAN_EQUALS)
            compareTokenType(TokenType.LESS_THAN_EQUALS);
        else if (lookahead.getType() == TokenType.GREATER_THAN)
            compareTokenType(TokenType.GREATER_THAN);
        else if (lookahead.getType() == TokenType.GREATER_THAN_EQUALS)
            compareTokenType(TokenType.GREATER_THAN_EQUALS);
        else
            error("Expected relational operator position: " + lookahead.getCharPosition());
    }

    private void compareTokenType(TokenType expected) {
        if (lookahead.getType() == expected) {
            lookahead = lexicalAnalyzer.nextToken();
        } else {
            error("Expected " + expected + ", found " + lookahead.getType() + ", line:" + lookahead.getLineNumber() + ", charPosition:" + lookahead.getCharPosition());
        }
    }

    private void compareTokenType(TokenType expected, TokenType expected2) {
        if (lookahead.getType() == expected || lookahead.getType() == expected2) {
            lookahead = lexicalAnalyzer.nextToken();
        } else {
            error("Expected " + expected + ", found " + lookahead.getType() + ", line:" + lookahead.getLineNumber() + ", charPosition" + lookahead.getCharPosition());
        }
    }

    private void error(String message) {
        throw new IllegalArgumentException(message);
    }
}


