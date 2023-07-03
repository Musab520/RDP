package com.example.demo;

import javafx.scene.control.Alert;

import java.util.ArrayList;

public class RecursiveDescentParser {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token lookahead;

    public RecursiveDescentParser(ArrayList<String> input) {
        lexicalAnalyzer = new LexicalAnalyzer(input);
        lookahead = lexicalAnalyzer.nextToken();
    }

    public void parse() {
        ProjectDeclaration();

        if (lookahead.getType() == TokenType.DOT) {
            compareTokenType(TokenType.DOT);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Parsing completed successfully.");
            alert.show();
            System.out.println("Parsing completed successfully.");
        } else {
            error("Expected '.' at the end of the project declaration position: " + lookahead.getCharPosition());
        }
    }

    private void ProjectDeclaration() {
        ProjectDef();
    }

    private void ProjectDef() {
        ProjectHeading();
        Declarations();
        CompoundStmt();
    }

    private void ProjectHeading() {
        compareTokenType(TokenType.PROJECT);
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.SEMICOLON);
    }

    private void Declarations() {
        ConstDecl();
        VarDecl();
        SubroutineDecl();
    }

    private void ConstDecl() {
        if (lookahead.getType() == TokenType.CONST) {
            compareTokenType(TokenType.CONST);
            while (lookahead.getType() == TokenType.NAME) {
                ConstItem();
                compareTokenType(TokenType.SEMICOLON);
            }
        }
    }

    private void ConstItem() {
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.EQUALS);
        compareTokenType(TokenType.INTEGER_VALUE);
    }

    private void VarDecl() {
        if (lookahead.getType() == TokenType.VAR) {
            compareTokenType(TokenType.VAR);
            while (lookahead.getType() == TokenType.NAME) {
                VarItem();
                compareTokenType(TokenType.SEMICOLON);
            }
        }
    }

    private void VarItem() {
        NameList();
        compareTokenType(TokenType.COLON);
        compareTokenType(TokenType.INT);
    }

    private void NameList() {
        compareTokenType(TokenType.NAME);
        while (lookahead.getType() == TokenType.COMMA) {
            compareTokenType(TokenType.COMMA);
            compareTokenType(TokenType.NAME);
        }
    }

    private void SubroutineDecl() {
        if (lookahead.getType() == TokenType.ROUTINE) {
            subroutineHeading();
            Declarations();
            CompoundStmt();
            compareTokenType(TokenType.SEMICOLON);
        }
    }

    private void subroutineHeading() {
        compareTokenType(TokenType.ROUTINE);
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.SEMICOLON);
    }

    private void CompoundStmt() {
        compareTokenType(TokenType.START);
        StmtList();
        compareTokenType(TokenType.END);
    }

    private void StmtList() {
        while (lookahead.getType() != TokenType.END) {
                Statement();
            if (lookahead.getType() != TokenType.SEMICOLON)
                error("Expected ';' between Statements position: " + lookahead.getCharPosition());
            compareTokenType(TokenType.SEMICOLON);
        }
    }

    private void Statement() {
        if (lookahead.getType() == TokenType.NAME)
            AssStmt();
        else if (lookahead.getType() == TokenType.INPUT || lookahead.getType() == TokenType.OUTPUT)
            InoutStmt();
        else if (lookahead.getType() == TokenType.IF)
            IfStmt();
        else if (lookahead.getType() == TokenType.LOOP)
            LoopStmt();
    }

    private void AssStmt() {
        compareTokenType(TokenType.NAME);
        compareTokenType(TokenType.COLON);
        compareTokenType(TokenType.EQUALS);
        ArithExp();
    }

    private void ArithExp() {
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
            ArithExp();
            compareTokenType(TokenType.RIGHT_PAREN);
        } else if (lookahead.getType() == TokenType.NAME || lookahead.getType() == TokenType.INTEGER_VALUE) {
            NameValue();
        } else {
            error("Expected '(' or name or integer value in factor position: " + lookahead.getCharPosition() );
        }
    }

    private void NameValue() {
        if (lookahead.getType() == TokenType.NAME)
            compareTokenType(TokenType.NAME);
        else if (lookahead.getType() == TokenType.INTEGER_VALUE)
            compareTokenType(TokenType.INTEGER_VALUE);
        else
            error("Expected name or integer value in name-value position: " + lookahead.getCharPosition() );
    }

    private void InoutStmt() {
        if (lookahead.getType() == TokenType.INPUT) {
            compareTokenType(TokenType.INPUT);
            compareTokenType(TokenType.LEFT_PAREN);
            compareTokenType(TokenType.NAME);
            compareTokenType(TokenType.RIGHT_PAREN);
        } else if (lookahead.getType() == TokenType.OUTPUT) {
            compareTokenType(TokenType.OUTPUT);
            compareTokenType(TokenType.LEFT_PAREN);
            NameValue();
            compareTokenType(TokenType.RIGHT_PAREN);
        } else {
            error("Expected 'input' or 'output' in inout-stmt position: " + lookahead.getCharPosition());
        }
    }

    private void IfStmt() {
        compareTokenType(TokenType.IF);
        compareTokenType(TokenType.LEFT_PAREN);
        BoolExp();
        compareTokenType(TokenType.RIGHT_PAREN);
        compareTokenType(TokenType.THEN);
        Statement();
        compareTokenType(TokenType.SEMICOLON);
        ElsePart();
        compareTokenType(TokenType.ENDIF);
    }

    private void ElsePart() {
        if (lookahead.getType() == TokenType.ELSE) {
            compareTokenType(TokenType.ELSE);
            Statement();
        }
    }

    private void LoopStmt() {
        compareTokenType(TokenType.LOOP);
        compareTokenType(TokenType.LEFT_PAREN);
        BoolExp();
        compareTokenType(TokenType.RIGHT_PAREN);
        compareTokenType(TokenType.DO);
        Statement();
    }

    private void BoolExp() {
        NameValue();
        RelationalOper();
        NameValue();
    }

    private void RelationalOper() {
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


