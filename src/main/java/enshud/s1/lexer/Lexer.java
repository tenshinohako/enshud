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

	static final char[] cTable = {
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

/*	static final char[] cTable = {
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

	static final Map<String, Integer> RESERVED_SYMBOLS = Collections.unmodifiableMap(
			new HashMap<String, Integer>() {{
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
		new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts");
		new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts");
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
			String outLineBuf;
			String lineBuffer;
			int lineNum = 0;
			while((lineBuffer = br.readLine()) != null) {
				lineBuffer += " ";
				lineNum++;
				for(int i = 0; i < lineBuffer.length(); i++) {
					String tokenBuffer = "";
					char cBuf = lineBuffer.charAt(i);

					switch(cTable[cBuf]){
					case SNULL:
						break;
					case SLBRACE:
						while(cTable[cBuf] != SRBRACE && cTable[cBuf] != '\n') {
							cBuf = lineBuffer.charAt(++i);
						}
						break;
					case SALPHA:
						while(cTable[cBuf] == SALPHA || cTable[cBuf] == SDIGIT) {
							tokenBuffer += String.valueOf(cBuf);
							cBuf = lineBuffer.charAt(++i);
						}
						i--;
						break;
					case SDIGIT:
						while(cTable[cBuf] == SDIGIT) {
							tokenBuffer += String.valueOf(cBuf);
							cBuf = lineBuffer.charAt(++i);
						}
						i--;
						break;
					case SSQUOTE:
						tokenBuffer = "'";
						cBuf = lineBuffer.charAt(++i);
						while(cTable[cBuf] != SSQUOTE && cBuf != '\n') {
							tokenBuffer += String.valueOf(cBuf);
							cBuf = lineBuffer.charAt(++i);
						}
						if(cTable[cBuf] == SSQUOTE) {
							tokenBuffer += "'";
						}
						break;
					case SSSYMBOL:
						tokenBuffer = String.valueOf(cBuf);
						break;
					case SDOT:
						tokenBuffer = String.valueOf(cBuf);
						cBuf = lineBuffer.charAt(++i);
						if(cBuf == '.') {
							tokenBuffer += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					case SCOLON:
						tokenBuffer = String.valueOf(cBuf);
						cBuf = lineBuffer.charAt(++i);
						if(cBuf == '=') {
							tokenBuffer += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					case SLESS:
						tokenBuffer = String.valueOf(cBuf);
						cBuf = lineBuffer.charAt(++i);
						if(cBuf == '>' || cBuf == '=') {
							tokenBuffer += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;
					case SGREAT:
						tokenBuffer = String.valueOf(cBuf);
						cBuf = lineBuffer.charAt(++i);
						if(cBuf == '=') {
							tokenBuffer += String.valueOf(cBuf);
						}else {
							i--;
						}
						break;

					}

					if(tokenBuffer.equals("")) {
						continue;
					}

					if(RESERVED_SYMBOLS.containsKey(tokenBuffer)) {
						outLineBuf = tokenBuffer
								+ "\t"
								+ TOKEN_NAME[RESERVED_SYMBOLS.get(tokenBuffer)]
								+ "\t"
								+ RESERVED_SYMBOLS.get(tokenBuffer)
								+ "\t"
								+ lineNum;
					}else {
						switch(cTable[tokenBuffer.charAt(0)]){
						case SALPHA:
							outLineBuf = tokenBuffer
									+ "\t"
									+ "SIDENTIFIER"
									+ "\t"
									+ 43
									+ "\t"
									+ lineNum;
							break;
						case SDIGIT:
							outLineBuf = tokenBuffer
									+ "\t"
									+ "SCONSTANT"
									+ "\t"
									+ 44
									+ "\t"
									+ lineNum;
							break;
						case SSQUOTE:
							outLineBuf = tokenBuffer
									+ "\t"
									+ "SSTRING"
									+ "\t"
									+ 45
									+ "\t"
									+ lineNum;
							break;
						default:
							outLineBuf = tokenBuffer;
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


















