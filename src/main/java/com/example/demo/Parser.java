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
            matchTokenType(TokenType.DOT);
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
        matchTokenType(TokenType.PROJECT);
        matchTokenType(TokenType.NAME);
        matchTokenType(TokenType.SEMICOLON);
    }

    private void declarations() {
        constDecl();
        varDecl();
        subroutineDecl();
    }

    private void constDecl() {
        if (lookahead.getType() == TokenType.CONST) {
            matchTokenType(TokenType.CONST);
            while (lookahead.getType() == TokenType.NAME) {
                constItem();
                matchTokenType(TokenType.SEMICOLON);
            }
        }
    }

    private void constItem() {
        matchTokenType(TokenType.NAME);
        matchTokenType(TokenType.EQUALS);
        matchTokenType(TokenType.INTEGER_VALUE);
    }

    private void varDecl() {
        if (lookahead.getType() == TokenType.VAR) {
            matchTokenType(TokenType.VAR);
            while (lookahead.getType() == TokenType.NAME) {
                varItem();
                matchTokenType(TokenType.SEMICOLON);
            }
        }
    }

    private void varItem() {
        nameList();
        matchTokenType(TokenType.COLON);
        matchTokenType(TokenType.INT);
    }

    private void nameList() {
        matchTokenType(TokenType.NAME);
        while (lookahead.getType() == TokenType.COMMA) {
            matchTokenType(TokenType.COMMA);
            matchTokenType(TokenType.NAME);
        }
    }

    private void subroutineDecl() {
        if (lookahead.getType() == TokenType.ROUTINE) {
            subroutineHeading();
            declarations();
            compoundStmt();
            matchTokenType(TokenType.SEMICOLON);
        }
    }

    private void subroutineHeading() {
        matchTokenType(TokenType.ROUTINE);
        matchTokenType(TokenType.NAME);
        matchTokenType(TokenType.SEMICOLON);
    }

    private void compoundStmt() {
        matchTokenType(TokenType.START);
        stmtList();
        matchTokenType(TokenType.END);
    }

    private void stmtList() {
        while (lookahead.getType() != TokenType.END) {
                statement();
            if (lookahead.getType() != TokenType.SEMICOLON)
                error("Expected ';' between statements position: " + lookahead.getCharPosition());
            matchTokenType(TokenType.SEMICOLON);
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
        matchTokenType(TokenType.NAME);
        matchTokenType(TokenType.COLON);
        matchTokenType(TokenType.EQUALS);
        arithExp();
    }

    private void arithExp() {
        term();
        while (lookahead.getType() == TokenType.ADD_SIGN || lookahead.getType() == TokenType.SUB_SIGN) {
            if (lookahead.getType() == TokenType.ADD_SIGN) {
                matchTokenType(TokenType.ADD_SIGN);
            }
            else if (lookahead.getType() == TokenType.SUB_SIGN) {
                matchTokenType(TokenType.SUB_SIGN);
            }
            term();
        }

    }

    private void term() {
        factor();
        while (lookahead.getType() == TokenType.MUL_SIGN || lookahead.getType() == TokenType.DIV_SIGN
                || lookahead.getType() == TokenType.MOD_SIGN) {
            if (lookahead.getType() == TokenType.MUL_SIGN) {
                matchTokenType(TokenType.MUL_SIGN);
            }
            else if (lookahead.getType() == TokenType.DIV_SIGN) {
                matchTokenType(TokenType.DIV_SIGN);
            }
            else if (lookahead.getType() == TokenType.MOD_SIGN) {
                matchTokenType(TokenType.MOD_SIGN);
            }
            factor();
        }
    }

    private void factor() {
        if (lookahead.getType() == TokenType.LEFT_PAREN) {
            matchTokenType(TokenType.LEFT_PAREN);
            arithExp();
            matchTokenType(TokenType.RIGHT_PAREN);
        } else if (lookahead.getType() == TokenType.NAME || lookahead.getType() == TokenType.INTEGER_VALUE) {
            nameValue();
        } else {
            error("Expected '(' or name or integer value in factor position: " + lookahead.getCharPosition() );
        }
    }

    private void nameValue() {
        if (lookahead.getType() == TokenType.NAME)
            matchTokenType(TokenType.NAME);
        else if (lookahead.getType() == TokenType.INTEGER_VALUE)
            matchTokenType(TokenType.INTEGER_VALUE);
        else
            error("Expected name or integer value in name-value position: " + lookahead.getCharPosition() );
    }

    private void inoutStmt() {
        if (lookahead.getType() == TokenType.INPUT) {
            matchTokenType(TokenType.INPUT);
            matchTokenType(TokenType.LEFT_PAREN);
            matchTokenType(TokenType.NAME);
            matchTokenType(TokenType.RIGHT_PAREN);
        } else if (lookahead.getType() == TokenType.OUTPUT) {
            matchTokenType(TokenType.OUTPUT);
            matchTokenType(TokenType.LEFT_PAREN);
            nameValue();
            matchTokenType(TokenType.RIGHT_PAREN);
        } else {
            error("Expected 'input' or 'output' in inout-stmt position: " + lookahead.getCharPosition());
        }
    }

    private void ifStmt() {
        matchTokenType(TokenType.IF);
        matchTokenType(TokenType.LEFT_PAREN);
        boolExp();
        matchTokenType(TokenType.RIGHT_PAREN);
        matchTokenType(TokenType.THEN);
        statement();
        matchTokenType(TokenType.SEMICOLON);
        elsePart();
        matchTokenType(TokenType.ENDIF);
    }

    private void elsePart() {
        if (lookahead.getType() == TokenType.ELSE) {
            matchTokenType(TokenType.ELSE);
            statement();
        }
    }

    private void loopStmt() {
        matchTokenType(TokenType.LOOP);
        matchTokenType(TokenType.LEFT_PAREN);
        boolExp();
        matchTokenType(TokenType.RIGHT_PAREN);
        matchTokenType(TokenType.DO);
        statement();
    }

    private void boolExp() {
        nameValue();
        relationalOper();
        nameValue();
    }

    private void relationalOper() {
        if (lookahead.getType() == TokenType.EQUALS)
            matchTokenType(TokenType.EQUALS);
        else if (lookahead.getType() == TokenType.NOT_EQUALS)
            matchTokenType(TokenType.NOT_EQUALS);
        else if (lookahead.getType() == TokenType.LESS_THAN)
            matchTokenType(TokenType.LESS_THAN);
        else if (lookahead.getType() == TokenType.LESS_THAN_EQUALS)
            matchTokenType(TokenType.LESS_THAN_EQUALS);
        else if (lookahead.getType() == TokenType.GREATER_THAN)
            matchTokenType(TokenType.GREATER_THAN);
        else if (lookahead.getType() == TokenType.GREATER_THAN_EQUALS)
            matchTokenType(TokenType.GREATER_THAN_EQUALS);
        else
            error("Expected relational operator position: " + lookahead.getCharPosition());
    }

    private void matchTokenType(TokenType expected) {
        if (lookahead.getType() == expected) {
            lookahead = lexicalAnalyzer.nextToken();
        } else {
            error("Expected " + expected + ", found " + lookahead.getType() + ", line:" + lookahead.getLineNumber() + ", charPosition:" + lookahead.getCharPosition());
        }
    }

    private void matchTokenType(TokenType expected, TokenType expected2) {
        if (lookahead.getType() == expected || lookahead.getType() == expected2) {
            lookahead = lexicalAnalyzer.nextToken();
        } else {
            error("Expected " + expected + ", found " + lookahead.getType() + ", line:" + lookahead.getLineNumber() + ", charPosition" + lookahead.getCharPosition());
        }
    }

    private void error(String message) {
        System.err.println("Error: " + message);
        System.exit(1);
    }
}


