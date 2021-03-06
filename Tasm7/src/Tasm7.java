/*
 * 作成日: 2004/03/31
 *
 * Copyright (c) 2004 重村哲至
 * All rights reserved.
 */
//package jp.ac.tokuyama.pico.tec6;

import java.io.*;

/**
 * @author sigemura
 *
 */
public class Tasm7 {
  private static final String srcExt = ".t7"; // ソースファイルの拡張子
  private static final String binExt = ".bin"; // オブジェクトファイルの拡張子
  private static final String lstExt = ".lst"; // リストファイルの拡張子

  private static void usage() {
    System.err.println("使用方法： tasm7 [-l <リスト行数>] [-w <リスト文字数>] <ソースファイル>");
    System.exit(1);
  }
  public static void main(String[] args) {
    Lexer lexer = null;
    int width = 79; // リストの１行の文字数
    int height = 66; // リストの１ページの行数
    String srcName;

    // オプションの処理
    int i = 0;
    try {
      for (i = 0; i < args.length; i++) {
        if (args[i].equals("-l")) {
          i++;
          if (i >= args.length)
            usage();
          else
            height = Integer.parseInt(args[i]);
        } else if (args[i].equals("-w")) {
          i++;
          if (i >= args.length)
            usage();
          else
            width = Integer.parseInt(args[i]);
        } else {
          break;
        }
      }
    } catch (NumberFormatException e) {
      usage();
    }

    // 引数のチェック
    if (i + 1 != args.length) {
      usage();
    }
    srcName = args[i];

    // ファイル名解析
    if (!srcName.endsWith(srcExt)) {
      System.err.println("ファイル名は " + srcExt + " で終了する必要があります。");
      System.exit(1);
    }

    // ファイル名の拡張子を除いた部分
    String baseName = srcName.substring(0, srcName.length() - srcExt.length());

    // ソースファイルのチェック    
    File srcFile = new File(srcName);

    if (!srcFile.exists()) {
      System.err.println("ファイル [ " + srcName + " ] が見つかりません。");
      System.exit(1);
    }

    if (!srcFile.isFile()) {
      System.err.println("ファイル [ " + srcName + " ] はファイルではありません。");
      System.exit(1);
    }

    // アセンブラ本体を作成する。
    Asm asm = new Asm(srcName);

    // パス１を実行する。
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(srcFile));
      lexer = new Lexer(br);

      // パス１
      asm.pass1(lexer);

    } catch (IOException e) {
      System.err.println("ファイル [ " + srcName + " ] のオープンに失敗しました。");
      System.exit(1);
    }

    // ファイルをオープンしなおす。
    try {
      br.close();
      br = new BufferedReader(new FileReader(srcName));
      lexer = new Lexer(br);
    } catch (IOException e) {
      System.err.println("ファイル [ " + srcName + " ] のオープンに失敗しました。");
      System.exit(1);
    }

    // 出力ファイルの作成
    File lstFile = new File(baseName + lstExt);
    File binFile = new File(baseName + binExt);

    // パス２の実行
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(lstFile));
      BufferedOutputStream bo =
        new BufferedOutputStream(new FileOutputStream(binFile));
      asm.pass2(lexer, new ListFormatter(bw, width, height), bo);
      bw.close();
      bo.close();
      System.err.println("アセンブル成功");
      System.err.println("結果は [" + lstFile + "] と [" + binFile + "] に格納しました。");
    } catch (FileNotFoundException e) {
      System.err.println("出力ファイルを開くことができません。");
      System.exit(1);
    } catch (SecurityException e) {
      System.err.println("出力ファイルへの書き込みが禁止されています。");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("ファイル書き込みでエラーが発生しました。");
      System.exit(1);
    }
  }
}
