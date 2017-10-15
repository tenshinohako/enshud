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
		seqOfVarDecls();




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
		switch(tokenList.get(pointer++)) {
		case SINTEGER:
			break;
		case SCHAR:
			break;
		case SBOOLEAN:
			break;
		case SARRAY:
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
			break;
		default:
			error();
			break;
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

	private void sign() {//(15)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			break;
		case SMINUS:
			break;
		default:
			error();
			break;
		}
	}

	private void subprogramDecls() {//(16)
		while(true) {
			if(tokenList.get(pointer++) != SPROCEDURE) {
				pointer--;
				break;
			}else {
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

	}

	private void procedureName() {//(19)

	}

	private void tempParameter() {//(20)

	}

	private void seqOfTempParameters() {//(21)

	}

	private void seqOfTempParameterNames() {//(22)

	}

	private void tempParameterName() {//(23)

	}

	private void compoundStatement() {//(24)
		if(tokenList.get(pointer++) != SBEGIN) {
			error();
		}
		sequenceOfStatements();
		if(tokenList.get(pointer++) != SEND) {
			error();
		}
	}

	private void sequenceOfStatements() {//(25)
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

	}

	private void basicStatement() {//(27)

	}

	private void assignmentStatement() {//(28)

	}

	private void leftSide() {//(29)

	}

	private void var() {//(30)

	}

	private void pureVar() {//(31)

	}

	private void varWithSuffix() {//(32)

	}

	private void suffix() {//(33)

	}

	private void procedureCallStatement() {//(34)

	}

	private void seqOfFormulae() {//(35)

	}

	private void formula() {//(36)

	}

	private void pureFormula() {//(37)

	}

	private void term() {//(38)

	}

	private void factor() {//(39)

	}

	private void relationalOpe() {//(40)

	}

	private void additiveOpe() {//(41)

	}

	private void multiplicativeOpe() {//(42)

	}

	private void inOutString() {//(43)

	}

	private void seqOfVars() {//(44)

	}

	private void canstant() {//(45)

	}

	private void unsignedInteger() {//(46)

	}

	private void string() {//(47)

	}

	private void stringElement() {//(48)

	}

	private void name() {//(49)

	}

	private void alphabet() {//(50)

	}

	private void digit() {//(51)

	}

	private void error() {

	}

}
