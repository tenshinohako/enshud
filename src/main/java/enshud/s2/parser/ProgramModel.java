package enshud.s2.parser;

import java.util.ArrayList;

public class ProgramModel {
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

	private ArrayList<Integer> tokenList;
	private Integer pointer = new Integer(0);

	public ProgramModel(ArrayList<Integer> list) {
		tokenList = list;
	}


	public void programRoot() {
		header();
		block();
		compoundStatement();
	}

	private void header() {
		if(tokenList.get(pointer++) != SPROGRAM) {
			error();
		}
		if(tokenList.get(pointer++) != SIDENTIFIER) {
			error();
		}
		if(tokenList.get(pointer++) != SLPAREN) {
			error();
		}
		sequenceOfName();
		if(tokenList.get(pointer++) != SRPAREN) {
			error();
		}
		if(tokenList.get(pointer++) != SSEMICOLON) {
			error();
		}
	}

	private void block() {
		varDecl();
		subprogramDecls();
	}

	private void compoundStatement() {

	}

	private void sequenceOfName() {

	}

	private void varDecl() {
		if(tokenList.get(pointer++) == SVAR) {
			sequenceOfVarDecls();
		}
	}

	private void subprogramDecls() {

	}

	private void sequenceOfVarDecls() {

	}

	private void error() {

	}

}
