/*
 * 作成日: 2004/04/01
 *
 * Copyright (c) 2004 重村哲至
 * All rights reserved.
 */
//package jp.ac.tokuyama.pico.tec6;

import java.util.*;

/**
 * @author sigemura
 *
 */
abstract class SyntaxAnalyzer {
  static final SyntaxAnalyzer M1 = new M1(); // 引数の無い機械語
  static final SyntaxAnalyzer M2 = new M2(); // レジスタ指定のみ
  static final SyntaxAnalyzer M3 = new M3(); // レジスタとアドレス
  static final SyntaxAnalyzer M4 = new M4(); // LD,ADD,SUB ...
  static final SyntaxAnalyzer M5 = new M5(); // ST命令
  static final SyntaxAnalyzer M6 = new M6(); // JMP,JZ,JC,JM,CALL,JNZ,JNC,JNM
  static final SyntaxAnalyzer P1 = new P1(); // EQU,DS,ORG,DC

  private static void strToByte(String s, Vector<Byte> v) {
    int len = s.length();
    for (int i = 1; i < len - 1; i++) {
      byte b = (byte) (s.charAt(i));
      v.add(new Byte(b));
    }
  }

  private static void dc(Lexer lexer, SymTbl symTbl, Vector<Byte> v, boolean parseOnly)
    throws AsmException {
    Token token = lexer.getNextTok();
    if (token.getType() == TokenType.STR) {
      strToByte((String) token.getValue(), v);
      lexer.setNextTok(false);
    } else {
      byte b = (byte) expr(lexer, symTbl, parseOnly);
      v.add(new Byte(b));
    }
  }

  static byte[] dcAnalyzer(byte op, Lexer parser, SymTbl symTbl, boolean parseOnly) throws AsmException {
    Vector<Byte> v = new Vector<Byte>();
    Token token;

    dc(parser, symTbl, v, parseOnly);
    token = parser.getNextTok();
    while (token.getType() == TokenType.CMM) {
      parser.setNextTok(false);
      dc(parser, symTbl, v, parseOnly);
      token = parser.getNextTok();
    }

    byte[] obj = new byte[v.size()];
    for (int i = 0; i < v.size(); i++) {
      obj[i] = v.get(i).byteValue();
    }
    return obj;
  }
  
  static boolean isIndexReg(Object o) {
    if (o == RsvWords.G1 || o == RsvWords.G2) {
      return true;
    }
    return false;
  }

  static boolean isReg(Object o) {
    return o == RsvWords.G0 || o == RsvWords.G1 || o == RsvWords.G2 || o == RsvWords.SP;
  }

  static int regNo(Object o) {
    if (o == RsvWords.G0)
      return 0;
    if (o == RsvWords.G1)
      return 1;
    if (o == RsvWords.G2)
      return 2;
    if (o == RsvWords.SP)
      return 3;
    return -1;
  }

  private static int factor(Lexer parser, SymTbl symTbl, boolean parseOnly)
    throws AsmException {
    Token token = parser.getNextTok();
    int v = 1;

    if (token.getType() == TokenType.PLS) { // 単項演算子 ＋
      parser.setNextTok(false);
      token = parser.getNextTok();
    } else if (token.getType() == TokenType.MNS) { //単項演算子 ー
      v = -1;
      parser.setNextTok(false);
      token = parser.getNextTok();
    }

    if (token.getType() == TokenType.NUM) { // 数値
      v *= ((Integer) (token.getValue())).intValue();
    } else if (token.getType() == TokenType.NAM) { // ラベル
      Integer i = symTbl.getValue((String) (token.getValue()));
      if (i == null) {
        if (!parseOnly)
          throw new AsmException(Err.UND_Idt);
      } else {
        v *= i.intValue();
      }    
    } else if (token.getType() == TokenType.LBR) { // 左括弧
      parser.setNextTok(false);
      v *= expr(parser, symTbl, parseOnly);
      token = parser.getNextTok();
      if (token.getType() != TokenType.RBR)
        throw new AsmException(Err.UNB_Br);
    } else {
      throw new AsmException(Err.BAD_Exp);
    }
    
    parser.setNextTok(false);
    return v;
  }

  private static int clause(Lexer parser, SymTbl symTbl, boolean parseOnly)
    throws AsmException {
    int v = factor(parser, symTbl, parseOnly);
    Token token = parser.getNextTok();
    while (token.getType() == TokenType.MUL
      || token.getType() == TokenType.DIV) {
      if (token.getType() == TokenType.MUL) {
        parser.setNextTok(false);
        v *= factor(parser, symTbl, parseOnly);
      } else {
        parser.setNextTok(false);
        int rv = factor(parser, symTbl, parseOnly);
        if (!parseOnly) {
          v /= rv;
        }
      }
      token = parser.getNextTok();
    }
    return v;
  }

  static int expr(Lexer parser, SymTbl symTbl, boolean parseOnly) throws AsmException {
    int v = clause(parser, symTbl, parseOnly);
    Token token = parser.getNextTok();
    while (token.getType() == TokenType.PLS
      || token.getType() == TokenType.MNS) {
      if (token.getType() == TokenType.PLS) {
        parser.setNextTok(false);
        v += clause(parser, symTbl, parseOnly);
      } else {
        parser.setNextTok(false);
        v -= clause(parser, symTbl, parseOnly);
      }
      token = parser.getNextTok();
    }
    return v;
  }

  abstract byte[] analyzer(byte op, Lexer parser, SymTbl symTbl)
    throws AsmException;
}
