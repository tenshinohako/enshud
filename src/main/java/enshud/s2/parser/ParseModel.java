package enshud.s2.parser;

import java.util.ArrayList;

public class ParseModel {
	protected static final int SAND = 0;
	protected static final int SARRAY = 1;
	protected static final int SBEGIN = 2;
	protected static final int SBOOLEAN = 3;
	protected static final int SCHAR = 4;
	protected static final int SDIVD = 5;
	protected static final int SDO = 6;
	protected static final int SELSE = 7;
	protected static final int SEND = 8;
	protected static final int SFALSE = 9;
	protected static final int SIF = 10;
	protected static final int SINTEGER = 11;
	protected static final int SMOD = 12;
	protected static final int SNOT = 13;
	protected static final int SOF = 14;
	protected static final int SOR = 15;
	protected static final int SPROCEDURE = 16;
	protected static final int SPROGRAM = 17;
	protected static final int SREADLN = 18;
	protected static final int STHEN = 19;
	protected static final int STRUE = 20;
	protected static final int SVAR = 21;
	protected static final int SWHILE = 22;
	protected static final int SWRITELN = 23;
	protected static final int SEQUAL = 24;
	protected static final int SNOTEQUAL = 25;
	protected static final int SLESS = 26;
	protected static final int SLESSEQUAL = 27;
	protected static final int SGREATEQUAL = 28;
	protected static final int SGREAT = 29;
	protected static final int SPLUS = 30;
	protected static final int SMINUS = 31;
	protected static final int SSTAR = 32;
	protected static final int SLPAREN = 33;
	protected static final int SRPAREN = 34;
	protected static final int SLBRACKET = 35;
	protected static final int SRBRACKET = 36;
	protected static final int SSEMICOLON = 37;
	protected static final int SCOLON = 38;
	protected static final int SRANGE = 39;
	protected static final int SASSIGN = 40;
	protected static final int SCOMMA = 41;
	protected static final int SDOT = 42;
	protected static final int SIDENTIFIER = 43;
	protected static final int SCONSTANT = 44;
	protected static final int SSTRING = 45;

	protected ArrayList<Integer> tokenList;
	protected ArrayList<Integer> lineList;
	protected Integer pointer = new Integer(0);
	protected Integer synErrorLine = new Integer(-1);

	public ParseModel(ArrayList<Integer> list, ArrayList<Integer> list2) {
		tokenList = list;
		lineList = list2;
	}

	public int getSynErrorLine() {
		return synErrorLine;
	}

	public void program() {//(1)
		header();
		block();
		compoundStatement();
	}

	private void header() {
		if(tokenList.get(pointer++) != SPROGRAM) {
			synError();
		}
		programName();
		if(tokenList.get(pointer++) != SLPAREN) {
			synError();
		}
		seqOfNames();
		if(tokenList.get(pointer++) != SRPAREN) {
			synError();
		}
		if(tokenList.get(pointer++) != SSEMICOLON) {
			synError();
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

	protected void block() {//(4)
		varDecl();
		subprogramDecls();
	}

	protected void varDecl() {//(5)
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

	protected void elementOfSeqOfVarDecls() {
		seqOfVarNames();
		if(tokenList.get(pointer++) != SCOLON) {
			synError();
		}
		type();
		if(tokenList.get(pointer++) != SSEMICOLON) {
			synError();
		}
	}

	protected void seqOfVarNames() {//(7)
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

	protected void varName() {//(8)
		name();
	}

	protected void type() {//(9)
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
			synError();
			break;
		}
	}

	private void arrayType() {//(11)
		if(tokenList.get(pointer++) != SARRAY) {
			synError();
		}else {
			if(tokenList.get(pointer++) != SLBRACKET) {
				synError();
			}
			minSuffix();
			if(tokenList.get(pointer++) != SRANGE) {
				synError();
			}
			maxSuffix();
			if(tokenList.get(pointer++) != SRBRACKET) {
				synError();
			}
			if(tokenList.get(pointer++) != SOF) {
				synError();
			}
			standardType();
		}
	}

	private void minSuffix() {//(12)
		integer();
	}

	private void maxSuffix() {//(13)
		integer();
	}

	protected void integer() {//(14)
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
			synError();
			break;
		}
	}*/

	protected void subprogramDecls() {//(16)
		while(true) {
			if(tokenList.get(pointer++) != SPROCEDURE) {
				pointer--;
				break;
			}else {
				pointer--;
				subprogramDecl();
				if(tokenList.get(pointer++) != SSEMICOLON) {
					synError();
				}
			}
		}
	}

	protected void subprogramDecl() {//(17)
		subprogramHeader();
		varDecl();
		compoundStatement();
	}

	private void subprogramHeader() {//(18)
		if(tokenList.get(pointer++) != SPROCEDURE) {
			synError();
		}
		procedureName();
		tempParameter();
		if(tokenList.get(pointer++) != SSEMICOLON) {
			synError();
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
				synError();
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
			synError();
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
			synError();
		}
		seqOfStatements();
		if(tokenList.get(pointer++) != SEND) {
			synError();
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
				synError();
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
				synError();
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
				synError();
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
			synError();
			break;
		}

	}

	private void assignmentStatement() {//(28)
		leftSide();
		if(tokenList.get(pointer++) != SASSIGN) {
			synError();
		}
		formula();
	}

	private void leftSide() {//(29)
		var();
	}

	protected void var() {//(30)
		varName();
		if(tokenList.get(pointer++) != SLBRACKET) {
			pointer--;
		}else {
			suffix();
			if(tokenList.get(pointer++) != SRBRACKET) {
				synError();
			}
		}
	}

/*	private void pureVar() {//(31)
		varName();
	}*/

/*	private void varWithSuffix() {//(32)
		varName();
		if(tokenList.get(pointer++) != SLBRACKET) {
			synError();
		}
		suffix();
		if(tokenList.get(pointer++) != SRBRACKET) {
			synError();
		}
	}*/

	protected void suffix() {//(33)
		formula();
	}

	private void procedureCallStatement() {//(34)
		procedureName();
		if(tokenList.get(pointer++) != SLPAREN) {
			pointer--;
		}else {
			seqOfFormulae();
			if(tokenList.get(pointer++) != SRPAREN) {
				synError();
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

	protected void formula() {//(36)
		pureFormula();
		if(relationalOpe()) {
			pureFormula();
		}else {
			pointer--;
		}
	}

	protected void pureFormula() {//(37)
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

	protected void term() {//(38)
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

	protected void factor() {//(39)
		switch(tokenList.get(pointer++)) {
		case SIDENTIFIER:
			pointer--;
			var();
			break;
		case SLPAREN:
			formula();
			if(tokenList.get(pointer++) != SRPAREN) {
				synError();
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

	protected boolean relationalOpe() {//(40)
		switch(tokenList.get(pointer++)) {
		case SEQUAL:
		case SNOTEQUAL:
		case SLESS:
		case SLESSEQUAL:
		case SGREAT:
		case SGREATEQUAL:
			return true;
		default:
			return false;
		}
	}

	protected boolean additiveOpe() {//(41)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
		case SMINUS:
		case SOR:
			return true;
		default:
			return false;
		}
	}

	protected boolean multiplicativeOpe() {//(42)
		switch(tokenList.get(pointer++)) {
		case SSTAR:
		case SDIVD:
		case SMOD:
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
					synError();
				}
			}
			break;
		case SWRITELN:
			if(tokenList.get(pointer++) != SLPAREN) {
				pointer--;
			}else {
				seqOfFormulae();
				if(tokenList.get(pointer++) != SRPAREN) {
					synError();
				}
			}
			break;
		default:
			synError();
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

	protected void constant() {//(45)
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
			synError();
			break;
		}
	}

	protected void unsignedInteger() {//(46)
		if(tokenList.get(pointer++) != SCONSTANT) {
			synError();
		}
	}

	private void string() {//(47)
		if(tokenList.get(pointer++) != SSTRING) {
			synError();
		}
	}

/*	private void stringElement() {//(48)

	}*/

	private void name() {//(49)
		if(tokenList.get(pointer++) != SIDENTIFIER) {
			synError();
		}
	}

/*	private void alphabet() {//(50)

	}*/

/*	private void digit() {//(51)

	}*/


	protected void synError(){
		if(synErrorLine == -1) {
			synErrorLine = lineList.get(pointer - 1);
		}
	}

}
