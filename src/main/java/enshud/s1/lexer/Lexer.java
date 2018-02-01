package enshud.s1.lexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Lexer {
	static final char SNULL = 1;
	static final char SSQUOTE = 2;
	static final char SDOT = 3;
	static final char SDIGIT = 4;
	static final char SCOLON = 5;
	static final char SLESS = 6;
	static final char SGREAT = 7;
	static final char SALPHA = 8;
	static final char SLBRACE = 9;
	static final char SRBRACE = 10;
	static final char SSSYMBOL = 11;

	static final char[] C_TABLE = {
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SSQUOTE,
			SSSYMBOL, SSSYMBOL, SSSYMBOL, SSSYMBOL, SSSYMBOL, SSSYMBOL, SDOT, SSSYMBOL,
			SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT,
			SDIGIT, SDIGIT, SCOLON, SSSYMBOL, SLESS, SSSYMBOL, SGREAT, SNULL,
			SNULL, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SSSYMBOL, SNULL, SSSYMBOL, SNULL, SALPHA,
			SNULL, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SLBRACE, SNULL, SRBRACE, SNULL, SNULL
	};

	/*
	static final char SNULL = 1;
	static final char SLNOT = 2;
	static final char SDQUOTE = 3;
	static final char SMOD = 4;
	static final char SBAND = 5;
	static final char SSQUOTE = 6;
	static final char SLPAREN = 7;
	static final char SRPAREN = 8;
	static final char SSTAR = 9;
	static final char SPLUS = 10;
	static final char SCOMMA = 11;
	static final char SMINUS = 12;
	static final char SDOT = 13;
	static final char SDIV = 14;
	static final char SDIGIT = 15;
	static final char SCOLON = 16;
	static final char SSEMI = 17;
	static final char SLESS = 18;
	static final char SASSIGN = 19;
	static final char SGREAT = 20;
	static final char SQUEST = 21;
	static final char SALPHA = 22;
	static final char SLSQP = 23;
	static final char SRSQP = 24;
	static final char SBEOR = 25;
	static final char SLBRACE = 26;
	static final char SBOR = 27;
	static final char SRBRACE = 28;
	static final char SBNOT = 29;
	static final char SEOF = 30;
	static final char SSSYMBOL = 31;
	*/


/*	static final char[] C_TABLE = {
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SLNOT, SDQUOTE, SNULL, SNULL, SMOD, SBAND, SSQUOTE,
			SLPAREN, SRPAREN, SSTAR, SPLUS, SCOMMA, SMINUS, SDOT, SDIV,
			SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT, SDIGIT,
			SDIGIT, SDIGIT, SCOLON, SSEMI, SLESS, SASSIGN, SGREAT, SQUEST,
			SNULL, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SLSQP, SNULL, SRSQP, SBEOR, SALPHA,
			SNULL, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SLBRACE, SBOR, SRBRACE, SBNOT, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA, SALPHA,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL, SNULL,
			SEOF
	};*/

	static final Map<String, Integer> RESERVED_SYMBOLS =
			Collections.unmodifiableMap(new HashMap<String, Integer>() {{
				put("and", 0);
				put("array", 1);
				put("begin", 2);
				put("boolean", 3);
				put("char", 4);
				put("div", 5);
				put("/", 5);
				put("do", 6);
				put("else", 7);
				put("end", 8);
				put("false", 9);
				put("if", 10);
				put("integer", 11);
				put("mod", 12);
				put("not", 13);
				put("of", 14);
				put("or", 15);
				put("procedure", 16);
				put("program", 17);
				put("readln", 18);
				put("then", 19);
				put("true", 20);
				put("var", 21);
				put("while", 22);
				put("writeln", 23);
				put("=", 24);
				put("<>", 25);
				put("<", 26);
				put("<=", 27);
				put(">=", 28);
				put(">", 29);
				put("+", 30);
				put("-", 31);
				put("*", 32);
				put("(", 33);
				put(")", 34);
				put("[", 35);
				put("]", 36);
				put(";", 37);
				put(":", 38);
				put("..", 39);
				put(":=", 40);
				put(",", 41);
				put(".", 42);
			}}
	);

	static final String[] TOKEN_NAME= {
			"SAND",
			"SARRAY",
			"SBEGIN",
			"SBOOLEAN",
			"SCHAR",
			"SDIVD",
			"SDO",
			"SELSE",
			"SEND",
			"SFALSE",
			"SIF",
			"SINTEGER",
			"SMOD",
			"SNOT",
			"SOF",
			"SOR",
			"SPROCEDURE",
			"SPROGRAM",
			"SREADLN",
			"STHEN",
			"STRUE",
			"SVAR",
			"SWHILE",
			"SWRITELN",
			"SEQUAL",
			"SNOTEQUAL",
			"SLESS",
			"SLESSEQUAL",
			"SGREATEQUAL",
			"SGREAT",
			"SPLUS",
			"SMINUS",
			"SSTAR",
			"SLPAREN",
			"SRPAREN",
			"SLBRACKET",
			"SRBRACKET",
			"SSEMICOLON",
			"SCOLON",
			"SRANGE",
			"SASSIGN",
			"SCOMMA",
			"SDOT"
	};

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		//new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts");
		//new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts");
		new Lexer().run("data/pas/normal09.pas", "tmp/out9.ts");
	}

	/**
	 * TODO
	 *
	 * 開発対象となるLexer実行メソッド．
	 * 以下の仕様を満たすこと．
	 *
	 * 仕様:
	 * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
	 * トークン列は第二引数で指定されたtsファイルに書き出すこと．
	 * 正常に処理が終了した場合は標準出力に"OK"を，
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 *
	 * @param inputFileName 入力pasファイル名
	 * @param outputFileName 出力tsファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {

		// TODO
		try{
			File inputFile = new File(inputFileName);
			BufferedReader br = new BufferedReader(new FileReader(inputFile));

			ArrayList<String> outList = new ArrayList<String>();

			int lineNum = 0;
			String inLineBuf;
			while((inLineBuf = br.readLine()) != null) {
				inLineBuf += " ";
				lineNum++;
				for(int i = 0; i < inLineBuf.length(); i++) {
					String tokenBuf = "";
					char cBuf = inLineBuf.charAt(i);

					switch(C_TABLE[cBuf]){
					case SNULL:
						continue;
					case SLBRACE:
						while(C_TABLE[cBuf] != SRBRACE) {
							cBuf = inLineBuf.charAt(++i);
						}
						continue;
					case SALPHA:
						while(C_TABLE[cBuf] == SALPHA || C_TABLE[cBuf] == SDIGIT) {
							tokenBuf += String.valueOf(cBuf);
							cBuf = inLineBuf.charAt(++i);
						}
						i--;
						break;
					case SDIGIT:
						while(C_TABLE[cBuf] == SDIGIT) {
							tokenBuf += String.valueOf(cBuf);
							cBuf = inLineBuf.charAt(++i);
						}
						i--;
						break;
					case SSQUOTE:
						tokenBuf = "'";
						cBuf = inLineBuf.charAt(++i);
						while(C_TABLE[cBuf] != SSQUOTE) {
							tokenBuf += String.valueOf(cBuf);
							cBuf = inLineBuf.charAt(++i);
						}
						tokenBuf += "'";
						break;
					case SSSYMBOL:
						tokenBuf = String.valueOf(cBuf);
						break;
					case SDOT:
						tokenBuf = String.valueOf(cBuf);
						cBuf = inLineBuf.charAt(++i);
						if(cBuf == '.') {
							tokenBuf += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					case SCOLON:
						tokenBuf = String.valueOf(cBuf);
						cBuf = inLineBuf.charAt(++i);
						if(cBuf == '=') {
							tokenBuf += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					case SLESS:
						tokenBuf = String.valueOf(cBuf);
						cBuf = inLineBuf.charAt(++i);
						if(cBuf == '>' || cBuf == '=') {
							tokenBuf += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					case SGREAT:
						tokenBuf = String.valueOf(cBuf);
						cBuf = inLineBuf.charAt(++i);
						if(cBuf == '=') {
							tokenBuf += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					}

					String outLineBuf = "";
					if(RESERVED_SYMBOLS.containsKey(tokenBuf)) {
						outLineBuf = tokenBuf
								+ "\t"
								+ TOKEN_NAME[RESERVED_SYMBOLS.get(tokenBuf)]
								+ "\t"
								+ RESERVED_SYMBOLS.get(tokenBuf)
								+ "\t"
								+ lineNum;
					}else {
						switch(C_TABLE[tokenBuf.charAt(0)]){
						case SALPHA:
							outLineBuf = tokenBuf
									+ "\t"
									+ "SIDENTIFIER"
									+ "\t"
									+ 43
									+ "\t"
									+ lineNum;
							break;
						case SDIGIT:
							outLineBuf = tokenBuf
									+ "\t"
									+ "SCONSTANT"
									+ "\t"
									+ 44
									+ "\t"
									+ lineNum;
							break;
						case SSQUOTE:
							outLineBuf = tokenBuf
									+ "\t"
									+ "SSTRING"
									+ "\t"
									+ 45
									+ "\t"
									+ lineNum;
							break;
						}
					}
					outList.add(outLineBuf);
				}
			}
			br.close();

			File outputFile = new File(outputFileName);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			for(String line : outList) {
				pw.println(line);
			}
			pw.close();

			System.out.println("OK");

		}catch(FileNotFoundException e){
			System.err.println("File not found");
		}catch(IOException e) {
			System.err.println(e);
		}
	}
}