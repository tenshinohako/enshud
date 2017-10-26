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

		new Parser().run("data/ts/synerr04.ts");

		/*
		// normalの確認
		new Parser().run("data/ts/normal01.ts");
		new Parser().run("data/ts/normal02.ts");

		// synerrの確認
		new Parser().run("data/ts/synerr01.ts");
		new Parser().run("data/ts/synerr02.ts");
		*/
	}
	
	static final int SAND = 0;
	static final int SARRAY = 1;
	static final int SBEGIN = 2;
	static final int SBOOLEAN = 3;
	static final int SCHAR = 4;
	static final int SDIVD = 5;
	static final int SDO = 6;
	static final int SELSE = 7;
	static final int SEND = 8;
	static final int SFALSE = 9;
	static final int SIF = 10;
	static final int SINTEGER = 11;
	static final int SMOD = 12;
	static final int SNOT = 13;
	static final int SOF = 14;
	static final int SOR = 15;
	static final int SPROCEDURE = 16;
	static final int SPROGRAM = 17;
	static final int SREADLN = 18;
	static final int STHEN = 19;
	static final int STRUE = 20;
	static final int SVAR = 21;
	static final int SWHILE = 22;
	static final int SWRITELN = 23;
	static final int SEQUAL = 24;
	static final int SNOTEQUAL = 25;
	static final int SLESS = 26;
	static final int SLESSEQUAL = 27;
	static final int SGREATEQUAL = 28;
	static final int SGREAT = 29;
	static final int SPLUS = 30;
	static final int SMINUS = 31;
	static final int SSTAR = 32;
	static final int SLPAREN = 33;
	static final int SRPAREN = 34;
	static final int SLBRACKET = 35;
	static final int SRBRACKET = 36;
	static final int SSEMICOLON = 37;
	static final int SCOLON = 38;
	static final int SRANGE = 39;
	static final int SASSIGN = 40;
	static final int SCOMMA = 41;
	static final int SDOT = 42;
	static final int SIDENTIFIER = 43;
	static final int SCONSTANT = 44;
	static final int SSTRING = 45;
	
	ArrayList<Integer> tokenList = new ArrayList<Integer>();
	ArrayList<Integer> lineList = new ArrayList<Integer>();
	Integer pointer;
	int errorLine = -1;

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

			pointer = new Integer(0);
			//ArrayList<Integer> tokenList = new ArrayList<Integer>();
			//ArrayList<Integer> lineList = new ArrayList<Integer>();
			String lineBuf;
			while((lineBuf = br.readLine()) != null) {
				String[] tokenBuf = lineBuf.split("\t", 0);
				tokenList.add(new Integer(tokenBuf[2]).intValue());
				lineList.add(new Integer(tokenBuf[3]).intValue());
			}
			br.close();

			//ProgramModel pm = new ProgramModel(tokenList, lineList);
			program();

			if(errorLine == -1) {
				System.out.println("OK");
			}else {
				System.err.println("Syntax error: line " + errorLine);
			}


		}catch(FileNotFoundException e){
			System.err.println("File not found");
		}catch(IOException e) {
			System.err.println(e);
		}


	}
	

	public void program() {//(1)
		header();
		block();
		compoundStatement();
	}

	private void header() {
		if(tokenList.get(pointer++) != SPROGRAM) {
			error();
		}
		programName();
		if(tokenList.get(pointer++) != SLPAREN) {
			error();
		}
		seqOfNames();
		if(tokenList.get(pointer++) != SRPAREN) {
			error();
		}
		if(tokenList.get(pointer++) != SSEMICOLON) {
			error();
		}
	}

	private void programName() {//(2)
		name();
	}

	private void seqOfNames() {//(3)
		name();
		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				name();
			}
		}
	}

	private void block() {//(4)
		varDecl();
		subprogramDecls();
	}

	private void varDecl() {//(5)
		if(tokenList.get(pointer++) != SVAR) {
			pointer--;
		}else {
			seqOfVarDecls();
		}
	}

	private void seqOfVarDecls() {//(6)
		elementOfSeqOfVarDecls();
		while(true) {
			if(tokenList.get(pointer++) != SIDENTIFIER) {
				pointer--;
				break;
				}else {
					pointer--;
					elementOfSeqOfVarDecls();
				}
		}
	}

	private void elementOfSeqOfVarDecls() {
		seqOfVarNames();
		if(tokenList.get(pointer++) != SCOLON) {
			error();
		}
		type();
		if(tokenList.get(pointer++) != SSEMICOLON) {
			error();
		}
	}

	private void seqOfVarNames() {//(7)
		varName();
		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				varName();
			}
		}
	}

	private void varName() {//(8)
		name();
	}

	private void type() {//(9)
		if(tokenList.get(pointer++) != SARRAY) {
			pointer--;
			standardType();
		}else {
			pointer--;
			arrayType();
		}
	}

	private void standardType() {//(10)
		switch(tokenList.get(pointer++)) {
		case SINTEGER:
			break;
		case SCHAR:
			break;
		case SBOOLEAN:
			break;
		default:
			error();
			break;
		}
	}

	private void arrayType() {//(11)
		if(tokenList.get(pointer++) != SARRAY) {
			error();
		}else {
			if(tokenList.get(pointer++) != SLBRACKET) {
				error();
			}
			maxSuffix();
			if(tokenList.get(pointer++) != SRANGE) {
				error();
			}
			minSuffix();
			if(tokenList.get(pointer++) != SRBRACKET) {
				error();
			}
			if(tokenList.get(pointer++) != SOF) {
				error();
			}
			standardType();
		}
	}

	private void maxSuffix() {//(12)
		integer();
	}

	private void minSuffix() {//(13)
		integer();
	}

	private void integer() {//(14)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			break;
		case SMINUS:
			break;
		default:
			pointer--;
			break;
		}
		unsignedInteger();
	}

/*	private void sign() {//(15)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			break;
		case SMINUS:
			break;
		default:
			error();
			break;
		}
	}*/

	private void subprogramDecls() {//(16)
		while(true) {
			if(tokenList.get(pointer++) != SPROCEDURE) {
				pointer--;
				break;
			}else {
				pointer--;
				subprogramDecl();
				if(tokenList.get(pointer++) != SSEMICOLON) {
					error();
				}
			}
		}
	}

	private void subprogramDecl() {//(17)
		subprogramHeader();
		varDecl();
		compoundStatement();
	}

	private void subprogramHeader() {//(18)
		if(tokenList.get(pointer++) != SPROCEDURE) {
			error();
		}
		procedureName();
		tempParameter();
		if(tokenList.get(pointer++) != SSEMICOLON) {
			error();
		}
	}

	private void procedureName() {//(19)
		name();
	}

	private void tempParameter() {//(20)
		if(tokenList.get(pointer++) != SLPAREN) {
			pointer--;
		}else {
			seqOfTempParameters();
			if(tokenList.get(pointer++) != SRPAREN) {
				error();
			}
		}
	}

	private void seqOfTempParameters() {//(21)
		elementOfSeqOfTempParameters();
		while(true) {
			if(tokenList.get(pointer++) != SSEMICOLON) {
				pointer--;
				break;
			}else {
				elementOfSeqOfTempParameters();
			}
		}
	}

	private void elementOfSeqOfTempParameters() {
		seqOfTempParameterNames();
		if(tokenList.get(pointer++) != SCOLON) {
			error();
		}
		standardType();
	}

	private void seqOfTempParameterNames() {//(22)
		tempParameterName();
		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				tempParameterName();
			}
		}
	}

	private void tempParameterName() {//(23)
		name();
	}

	private void compoundStatement() {//(24)
		if(tokenList.get(pointer++) != SBEGIN) {
			error();
		}
		seqOfStatements();
		if(tokenList.get(pointer++) != SEND) {
			error();
		}
	}

	private void seqOfStatements() {//(25)
		statement();
		while(true) {
			if(tokenList.get(pointer++) != SSEMICOLON) {
				pointer--;
				break;
			}else {
				statement();
			}
		}
	}

	private void statement() {//(26)
		switch(tokenList.get(pointer++)) {
		case SIF:
			formula();
			if(tokenList.get(pointer++) != STHEN) {
				error();
			}
			compoundStatement();
			if(tokenList.get(pointer++) != SELSE) {
				pointer--;
			}else {
				compoundStatement();
			}
			break;
		case SWHILE:
			formula();
			if(tokenList.get(pointer++) != SDO) {
				error();
			}
			statement();
			break;
		default:
			pointer--;
			basicStatement();
		}

	}

	private void basicStatement() {//(27)
		switch(tokenList.get(pointer++)) {
		case SIDENTIFIER:
			switch(tokenList.get(pointer++)) {
			case SLPAREN:
			case SSEMICOLON:
				pointer--;
				pointer--;
				procedureCallStatement();
				break;
			case SASSIGN:
			case SLBRACKET:
				pointer--;
				pointer--;
				assignmentStatement();
				break;
			case SEND:
				pointer--;
				break;
			default:
				error();
				break;
			}
			break;
		case SREADLN:
			pointer--;
			inOutString();
			break;
		case SWRITELN:
			pointer--;
			inOutString();
			break;
		case SBEGIN:
			pointer--;
			compoundStatement();
			break;
		default:
			error();
			break;
		}

	}

	private void assignmentStatement() {//(28)
		leftSide();
		if(tokenList.get(pointer++) != SASSIGN) {
			error();
		}
		formula();
	}

	private void leftSide() {//(29)
		var();
	}

	private void var() {//(30)
		varName();
		if(tokenList.get(pointer++) != SLBRACKET) {
			pointer--;
		}else {
			suffix();
			if(tokenList.get(pointer++) != SRBRACKET) {
				error();
			}
		}
	}

/*	private void pureVar() {//(31)
		varName();
	}*/

/*	private void varWithSuffix() {//(32)
		varName();
		if(tokenList.get(pointer++) != SLBRACKET) {
			error();
		}
		suffix();
		if(tokenList.get(pointer++) != SRBRACKET) {
			error();
		}
	}*/

	private void suffix() {//(33)
		formula();
	}

	private void procedureCallStatement() {//(34)
		procedureName();
		if(tokenList.get(pointer++) != SLPAREN) {
			pointer--;
		}else {
			seqOfFormulae();
			if(tokenList.get(pointer++) != SRPAREN) {
				error();
			}
		}
	}

	private void seqOfFormulae() {//(35)
		formula();
		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				formula();
			}
		}
	}

	private void formula() {//(36)
		pureFormula();
		if(relationalOpe()) {
			pureFormula();
		}else {
			pointer--;
		}
	}

	private void pureFormula() {//(37)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			break;
		case SMINUS:
			break;
		default:
			pointer--;
			break;
		}
		term();
		while(true) {
			if(additiveOpe()) {
				term();
			}else {
				pointer--;
				break;
			}
		}
	}

	private void term() {//(38)
		factor();
		while(true) {
			if(multiplicativeOpe()) {
				factor();
			}else {
				pointer--;
				break;
			}
		}
	}

	private void factor() {//(39)
		switch(tokenList.get(pointer++)) {
		case SIDENTIFIER:
			pointer--;
			var();
			break;
		case SLPAREN:
			formula();
			if(tokenList.get(pointer++) != SRPAREN) {
				error();
			}
			break;
		case SNOT:
			factor();
			break;
		default:
			pointer--;
			constant();
		}
	}

	private boolean relationalOpe() {//(40)
		switch(tokenList.get(pointer++)) {
		case SEQUAL:
			return true;
		case SNOTEQUAL:
			return true;
		case SLESS:
			return true;
		case SLESSEQUAL:
			return true;
		case SGREAT:
			return true;
		case SGREATEQUAL:
			return true;
		default:
			return false;
		}
	}

	private boolean additiveOpe() {//(41)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			return true;
		case SMINUS:
			return true;
		case SOR:
			return true;
		default:
			return false;
		}
	}

	private boolean multiplicativeOpe() {//(42)
		switch(tokenList.get(pointer++)) {
		case SSTAR:
			return true;
		case SDIVD:
			return true;
		case SMOD:
			return true;
		case SAND:
			return true;
		default:
			return false;
		}
	}

	private void inOutString() {//(43)
		switch(tokenList.get(pointer++)) {
		case SREADLN:
			if(tokenList.get(pointer++) != SLPAREN) {
				pointer--;
			}else {
				seqOfVars();
				if(tokenList.get(pointer++) != SRPAREN) {
					error();
				}
			}
			break;
		case SWRITELN:
			if(tokenList.get(pointer++) != SLPAREN) {
				pointer--;
			}else {
				seqOfFormulae();
				if(tokenList.get(pointer++) != SRPAREN) {
					error();
				}
			}
			break;
		default:
			error();
			break;
		}
	}

	private void seqOfVars() {//(44)
		var();
		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				var();
			}
		}
	}

	private void constant() {//(45)
		switch(tokenList.get(pointer++)) {
		case SCONSTANT:
			pointer--;
			unsignedInteger();
			break;
		case SSTRING:
			pointer--;
			string();
			break;
		case SFALSE:
			break;
		case STRUE:
			break;
		default:
			error();
			break;
		}
	}

	private void unsignedInteger() {//(46)
		if(tokenList.get(pointer++) != SCONSTANT) {
			error();
		}
	}

	private void string() {//(47)
		if(tokenList.get(pointer++) != SSTRING) {
			error();
		}
	}

/*	private void stringElement() {//(48)

	}*/

	private void name() {//(49)
		if(tokenList.get(pointer++) != SIDENTIFIER) {
			error();
		}
	}

/*	private void alphabet() {//(50)

	}*/

/*	private void digit() {//(51)

	}*/

	private void error(){
		if(errorLine == -1) {
			errorLine = lineList.get(pointer - 1);
		}
	}
}
