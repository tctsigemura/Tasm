/*
 * 作成日: 2004/04/01
 *
 * Copyright (c) 2004 重村哲至
 * All rights reserved.
 */
//package jp.ac.tokuyama.pico.tec6;

/**
 * @author sigemura
 * IN,OUT
 */
class M3 extends SyntaxAnalyzer {

  /* (非 Javadoc)
   * @see jp.ac.tokuyama.pico.tec.SyntaxAnalyzer#analyzer(jp.ac.tokuyama.pico.tec.Lexer, boolean)
   */
  byte[] analyzer(byte op, Lexer lexer, SymTbl symTbl) throws AsmException {
    Object w = lexer.getNextTok().getValue();

    if (!isReg(w)) {
      throw new AsmException(Err.BAD_Reg);
    }
    
    byte[] obj = new byte[2];
    obj[0] = (byte)(op | (regNo(w)<<2));
    
    lexer.setNextTok(false);
    Token token = lexer.getNextTok();
    
    if (token.getType()!=TokenType.CMM) {
      throw new AsmException(Err.Exp_Cmm);
    }

    lexer.setNextTok(false);
    obj[1] = (byte) expr(lexer, symTbl, false);

    return obj;
  }
}
