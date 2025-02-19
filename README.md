# Tasm
TeC Assembler.

Tasmは
(TeC7)[https://github.com/tctsigemura/TeC7]
のクロス開発用のツール群です．
クロスアセンブラ(tasm7)とMac/Unix用のダウンロードプログラム(twrite7，tsend7)，
Windows用のダウンロードプログラム(Serial)を含んでいます．

## クロス開発環境のインストールと使用方法

* 注意：クロスアセンブラは Java で記述されています．
Java の実行環境を予めパソコンにインストールして下さい．

### UNIX (FreeBSD, macOS) の場合

#### クロスアセンブラのインストール
Tasm7 ディレクトリに移動し，次を実行してください．

    # make install        (FreeBSD)
    $ sudo make install   (macOS)

不具合がある場合は，Makefileを確認して手作業でお願いします．

#### （TeC7c 以降）ダウンロードプログラムのインストール
Twrite7 ディレクトリに移動し，次を実行してください．

    # make install        (FreeBSD)
    $ sudo make install   (macOS)

#### （TeC7a の場合）ダウンロードプログラムのインストール
Tsend7 ディレクトリに移動し，次を実行してください．

    # make install        (FreeBSD)
    $ sudo make install   (macOS)

#### プログラムの作成
適当なテキストエディタで TeC7 のプログラムを入力します．
その際，拡張子を ".t7" にする必要があります．
[詳細は（※１）参照]

#### プログラムのダウンロード（TeC7c以降）
twrite7 コマンドを使用します．
[詳細は（※１）参照]

#### プログラムのダウンロード（TeC7a）
TeC7を受信状態にしてから tsend7 コマンドを使用します．
[詳細は（※１）参照]

(※１）アセンブラの使用方法，アセンブラの文法，ダウンロード方法は，
[TeC教科書]
(https://github.com/tctsigemura/TecTextBook/)
付録A「TeCクロス開発環境」をご覧ください．

### Windows の場合

Windows7 での使用を前提にしています．

#### クロスアセンブラのインストール
Tasm7 フォルダの Tasm7.jar を適切なフォルダ [jar_dir] に
	コピーして下さい．

#### ダウンロードプログラムのインストール
Serial\Serial.exe を適切なフォルダ [exe_dir] にコピーして下さい．

#### プログラムの作成
1. メモ帳等でアセンブラソース(xxx.t7)を作成します．
アセンブラの文法等は UNIX と共通です．
[詳細は（※１）参照]

1. コマンドプロンプトから次のようにアセンブラを実行すると，xxx.bin(機械語ファイル)とxxx.lst(アセンブルリストファイル)ができます．

    > java -jar [jar_dir]¥Tasm7.jar xxx.t7

#### プログラムのダウンロード
xxx.bin ファイルをTeC7にダウンロードします．

1. TeC7 とパソコンを USB ケーブルで接続します．

1. Windows のデバイスマネージャで TeC7(FT232) に相当するシリアルポートの名前を調べます．(例えば COM3)

1. Windows のコマンドプロンプトから次のようにダウンロードプログラムを実行します．(com3 の部分は，シリアルケーブルを接続したポートにより変化します．)

    > [exe_dir]¥serial.exe xxx.bin com3

1. TeC7 の PC を E0H にセットし，RUN ボタンを押します．
これで， TeC7 の IPL が起動されます．
次に，パソコンの Enter キーを押します．

1. TeC7 の RUN ランプが消灯したらダウンロード終了です．
