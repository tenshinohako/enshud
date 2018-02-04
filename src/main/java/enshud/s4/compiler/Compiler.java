package enshud.s4.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import enshud.casl.CaslSimulator;

public class Compiler {
	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// Compilerを実行してcasを生成する
		//new Compiler().run("data/ts/normal01.ts", "tmp/out.cas");
		//new Compiler().run("data/ts/normal02.ts", "tmp/out.cas");
		//new Compiler().run("data/ts/normal03.ts", "tmp/out.cas");
		new Compiler().run("data/ts/normal04.ts", "tmp/out.cas");
		//new Compiler().run("data/ts/normal07.ts", "tmp/out.cas");
		//new Compiler().run("data/ts/normal08.ts", "tmp/out.cas");
		//new Compiler().run("data/ts/normal09.ts", "tmp/out.cas");

		// CaslSimulatorクラスを使ってコンパイルしたcasを，CASLアセンブラ & COMETシミュレータで実行する
		//CaslSimulator.run("tmp/out.cas", "tmp/out.ans", "36", "48");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるCompiler実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする．
	 * コンパイル結果のCASL IIプログラムは第二引数で指定されたcasファイルに書き出すこと．
	 * 構文的もしくは意味的なエラーを発見した場合は標準エラーにエラーメッセージを出力すること．
	 * （エラーメッセージの内容はChecker.run()の出力に準じるものとする．）
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力tsファイル名
	 * @param outputFileName 出力casファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {

		// TODO
		try {
			File inputFile = new File(inputFileName);
			BufferedReader br = new BufferedReader(new FileReader(inputFile));

			ArrayList<Integer> tokenList = new ArrayList<Integer>();
			ArrayList<Integer> lineList = new ArrayList<Integer>();
			ArrayList<String> wordsList = new ArrayList<String>();
			String lineBuf;
			while((lineBuf = br.readLine()) != null) {
				String[] tokenBuf = lineBuf.split("\t", 0);
				tokenList.add(new Integer(tokenBuf[2]).intValue());
				lineList.add(new Integer(tokenBuf[3]).intValue());
				wordsList.add(tokenBuf[0]);
			}
			br.close();

			//CheckModel cm = new CheckModel(tokenList, lineList, wordsList);
			CompileTask cm = new CompileTask(tokenList, lineList, wordsList);
			cm.program();

			if(cm.getSynErrorLine() == -1) {
				if(cm.getSemErrorLine() == -1) {

					ArrayList<String> outList = cm.getOutList();

					File outputFile = new File(outputFileName);
					BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
					for(String buf: outList) {
						bw.write(buf);
						bw.write("\n");
					}

					bw.close();

					CaslSimulator.appendLibcas(outputFileName);


					//System.out.println("OK");
				}else {
					System.err.println("Semantic error: line " + cm.getSemErrorLine());
				}
			}else {
				System.err.println("Syntax error: line " + cm.getSynErrorLine());
			}
		}catch(FileNotFoundException e){
			System.err.println("File not found");
		}catch(IOException e) {
			System.err.println(e);
		}

	}
}
