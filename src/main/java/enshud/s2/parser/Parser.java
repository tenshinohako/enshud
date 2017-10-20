package enshud.s2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		new Parser().run("data/ts/normal01.ts");
		new Parser().run("data/ts/normal02.ts");

		// synerrの確認
		new Parser().run("data/ts/synerr01.ts");
		new Parser().run("data/ts/synerr02.ts");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるParser実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，構文解析を行う．
	 * 構文が正しい場合は標準出力に"OK"を，正しくない場合は"Syntax error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を標準エラーに出力すること （例: "Syntax error: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを出力すること．
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力tsファイル名
	 */
	public void run(final String inputFileName) {

		// TODO
		try {
			File inputFile = new File(inputFileName);
			BufferedReader br = new BufferedReader(new FileReader(inputFile));

			ArrayList<Integer> tokenList = new ArrayList<Integer>();
			ArrayList<Integer> lineList = new ArrayList<Integer>();
			String lineBuf;
			while((lineBuf = br.readLine()) != null) {
				String[] tokenBuf = lineBuf.split("\t", 0);
				tokenList.add(new Integer(tokenBuf[2]).intValue());
				lineList.add(new Integer(tokenBuf[3]).intValue());
			}
			br.close();

			ProgramModel pm = new ProgramModel(tokenList, lineList);
			pm.program();

			System.out.println("OK");


		}catch(FileNotFoundException e){
			System.err.println("File not found");
		}catch(IOException e) {
			System.err.println(e);
		}


	}
}
