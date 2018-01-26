package enshud.s3.checker;

import java.util.ArrayList;

import enshud.s2.parser.ParseModel;

public class CheckModel extends ParseModel {

	protected ArrayList<String> wordsList;
	protected Integer semErrorLine = new Integer(-1);
	private boolean isPlus;

	protected ProcedureModel currentProcedure;
	protected ArrayList<ProcedureModel> procedureList = new ArrayList<ProcedureModel>();
	protected ArrayList<String> tempNameList;
	public CheckModel(ArrayList<Integer> list, ArrayList<Integer> list2, ArrayList<String> list3) {
		super(list, list2);
		wordsList = list3;
	}

	public int getSemErrorLine() {
		return semErrorLine;
	}

	public ArrayList<ProcedureModel> getProcedureList() {
		return procedureList;
	}

	/*
	public void allotId() {
		int index = 0;
		for(ProcedureModel pro: procedureList) {
			index = pro.allotId(index);
		}
	}
	*/

	@Override
	public void program() {//(1)
		currentProcedure = new ProcedureModel();
		currentProcedure.setName(wordsList.get(1));
		procedureList.add(currentProcedure);
		header();
		block();
		compoundStatement();
	}

	@Override
	protected void block() {
		varDecl();
		subprogramDecls();
		currentProcedure = procedureList.get(0);
	}

	@Override
	protected void seqOfVarNames() {//(7)
		tempNameList = new ArrayList<String>();
		varName();
		tempNameList.add(wordsList.get(pointer - 1));

		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				varName();
				tempNameList.add(wordsList.get(pointer - 1));
			}
		}
	}

	@Override
	protected void standardType() {//(10)
		switch(tokenList.get(pointer++)) {
		case SINTEGER:
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(new IntegerType(tempName))) {
					semError();
				}
			}
			break;
		case SCHAR:
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(new CharType(tempName))) {
					semError();
				}
			}
			break;
		case SBOOLEAN:
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(new BooleanType(tempName))) {
					semError();
				}
			}
			break;
		default:
			synError();
			break;
		}
	}

	@Override
	protected void arrayType() {//(11)
		int minSuffix;
		int maxSuffix;
		if(tokenList.get(pointer++) != SARRAY) {
			synError();
		}else {
			if(tokenList.get(pointer++) != SLBRACKET) {
				synError();
			}
			minSuffix();
			try {
				minSuffix = Integer.parseInt(wordsList.get(pointer - 1));
			}catch(NumberFormatException e) {
				minSuffix = 0;
			}
			if(tokenList.get(pointer++) != SRANGE) {
				synError();
			}
			maxSuffix();
			try {
				maxSuffix = Integer.parseInt(wordsList.get(pointer - 1));
			}catch(NumberFormatException e) {
				maxSuffix = 0;
			}

			if(tokenList.get(pointer++) != SRBRACKET) {
				synError();
			}
			if(tokenList.get(pointer++) != SOF) {
				synError();
			}
			super.standardType();
			int varType = tokenList.get(pointer - 1);
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(
						new ArrayType(tempName, varType, minSuffix, maxSuffix))) {
					semError();
				}
			}
		}
	}

	@Override
	protected void integer() {//(14)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			isPlus = true;
			break;
		case SMINUS:
			isPlus = false;
			break;
		default:
			isPlus = true;
			pointer--;
			break;
		}
		unsignedInteger();
	}

	@Override
	protected void subprogramDecl() {//(17)
		currentProcedure = new ProcedureModel();
		currentProcedure.setName(wordsList.get(pointer + 1));
		procedureList.add(currentProcedure);
		subprogramHeader();
		varDecl();
		compoundStatement();
	}

	@Override
	protected void seqOfTempParameterNames() {//(22)
		tempNameList = new ArrayList<String>();
		tempParameterName();
		tempNameList.add(wordsList.get(pointer - 1));

		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				tempParameterName();
				tempNameList.add(wordsList.get(pointer - 1));
			}
		}
	}

	@Override
	protected void statement() {//(26)
		switch(tokenList.get(pointer++)) {
		case SIF:
			if(typeFormula() != SBOOLEAN) {
				semError();
			}
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
			if(typeFormula() != SBOOLEAN) {
				semError();
			}
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

	@Override
	protected void assignmentStatement() {//(28)
		int type = typeLeftSide();
		if(tokenList.get(pointer++) != SASSIGN) {
			synError();
		}
		if(typeFormula() != type) {
			semError();
		}
	}

	@Override
	protected void var() {//(30)
		varName();
		if(!currentProcedure.existsInProcedure(wordsList.get(pointer - 1))) {
			if(currentProcedure != procedureList.get(0)) {
				if(!procedureList.get(0).existsInProcedure(wordsList.get(pointer - 1))){
					semError();
				}
			}else {
				semError();
			}
		}
		if(tokenList.get(pointer++) != SLBRACKET) {
			pointer--;
			if(currentProcedure.isArrayType(wordsList.get(pointer - 1))) {
				if(tokenList.get(pointer - 3) != SWRITELN) {
					semError();
				}
			}else {
				if(procedureList.get(0).isArrayType(wordsList.get(pointer - 1))) {
					if(tokenList.get(pointer - 3) != SWRITELN) {
						semError();
					}
				}
			}
		}else {
			if(!currentProcedure.isArrayType(wordsList.get(pointer - 2))) {
				if(!procedureList.get(0).isArrayType(wordsList.get(pointer - 2))) {
				semError();
				}
			}

			suffix();
			if(tokenList.get(pointer++) != SRBRACKET) {
				synError();
			}
		}
	}

	@Override
	protected void suffix() {//(33)
		if(typeFormula() != SINTEGER) {
			semError();
		}
	}

	@Override
	protected void procedureCallStatement() {//(34)
		boolean exist = false;
		for(ProcedureModel proc: procedureList) {
			if(proc.getName().equals(wordsList.get(pointer))) {
				exist = true;
				break;
			}
		}
		if(!exist) {
			semError();
		}

		/*    */
		procedureName();
		if(tokenList.get(pointer++) != SLPAREN) {
			pointer--;
		}else {
			seqOfFormulae();
			if(tokenList.get(pointer++) != SRPAREN) {
				synError();
			}
		}
		/*    */
	}

	@Override
	protected void unsignedInteger() {//(46)
		if(tokenList.get(pointer++) != SCONSTANT) {
			synError();
			return;
		}
		if(isPlus) {
			try {
				if(Integer.parseInt(wordsList.get(pointer - 1)) > 32767) {
					semError();
				}
			}catch(NumberFormatException e) {

			}
		}else {
			try {
				if(Integer.parseInt(wordsList.get(pointer - 1)) > 32768) {
					semError();
				}
			}catch(NumberFormatException e) {

			}
		}
	}

	/* ****************************************************************** */

	protected int typeFormula() {//(36)
		int type = typePureFormula();
		if(relationalOpe()) {
			if(typePureFormula() == type) {
				return SBOOLEAN;
			}else {
				semError();
				return -1;
			}
		}else {
			pointer--;
			return type;
		}
	}

	protected int typePureFormula() {//(37)
		int type = -1;
		int expectedType;
		switch(tokenList.get(pointer++)) {
		case SPLUS:
		case SMINUS:
			type = SINTEGER;
			break;
		default:
			pointer--;
			break;
		}
		if(type == SINTEGER) {
			if((type = typeTerm()) != SINTEGER) {
				semError();
			}
		}else {
			type = typeTerm();
		}
		expectedType = type;
		while(true) {
			int ope = typeAdditiveOpe();
			if(ope == SINTEGER) {
				if(expectedType != SINTEGER) {
					semError();
				}
				type = typeTerm();
				if(type != SINTEGER) {
					semError();
				}
			}else if(ope == SBOOLEAN){
				if(expectedType != SBOOLEAN) {
					semError();
				}
				type = typeTerm();
				if(type != SBOOLEAN) {
					semError();
				}
			}else {
				pointer--;
				break;
			}
		}
		return type;
	}

	protected int typeTerm() {//(38)
		int type = typeFactor();
		int expectedType = type;
		while(true) {
			int ope = typeMultiplicativeOpe();
			if(ope == SINTEGER) {
				if(expectedType != SINTEGER) {
					semError();
				}
				type = typeFactor();
				if(type != SINTEGER) {
					semError();
				}
			}else if(ope == SBOOLEAN) {
				if(expectedType != SBOOLEAN) {
					semError();
				}
				type = typeFactor();
				if(type != SBOOLEAN) {
					semError();
				}
			}else {
				pointer--;
				break;
			}
		}
		return type;
	}

	protected int typeFactor() {//(39)
		int type;
		switch(tokenList.get(pointer++)) {
		case SIDENTIFIER:
			pointer--;
			if((type = currentProcedure.getType(wordsList.get(pointer))) == -1) {
				if((type = procedureList.get(0).getType(wordsList.get(pointer))) == -1) {
					semError();
				}
			}
			var();
			return type;
		case SLPAREN:
			type = typeFormula();
			if(tokenList.get(pointer++) != SRPAREN) {
				synError();
			}
			return type;
		case SNOT:
			if(typeFactor() != SBOOLEAN) {
				semError();
			}
			return SBOOLEAN;
		default:
			pointer--;
			return typeConstant();
		}
	}

	protected int typeAdditiveOpe() {//(41)
		switch(tokenList.get(pointer++)) {
		case SPLUS:
		case SMINUS:
			return SINTEGER;
		case SOR:
			return SBOOLEAN;
		default:
			return -1;
		}
	}

	protected int typeMultiplicativeOpe() {//(42)
		switch(tokenList.get(pointer++)) {
		case SSTAR:
		case SDIVD:
		case SMOD:
			return SINTEGER;
		case SAND:
			return SBOOLEAN;
		default:
			return -1;
		}
	}

	protected int typeConstant() {//(45)
		switch(tokenList.get(pointer++)) {
		case SCONSTANT:
			pointer--;
			unsignedInteger();
			return SINTEGER;
		case SSTRING:
			pointer--;
			string();
			if(wordsList.get(pointer - 1).length() <= 3) {
				return SCHAR;
			}else {
				return SSTRING;
			}
		case SFALSE:
		case STRUE:
			return SBOOLEAN;
		default:
			synError();
			return -1;
		}
	}
	/* ****************************************************************** */

	protected int typeLeftSide() {//(29)
		int type;
		if((type = currentProcedure.getType(wordsList.get(pointer))) == -1) {
			if((type = procedureList.get(0).getType(wordsList.get(pointer))) == -1) {
				semError();
			}
		}
		leftSide();
		return type;
	}

	/* ****************************************************************** */

	protected void semError() {
		if(semErrorLine == -1) {
			semErrorLine = lineList.get(pointer);
		}
	}
}