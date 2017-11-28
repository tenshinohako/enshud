package enshud.s3.checker;

import java.util.ArrayList;

import enshud.s2.parser.ParseModel;

public class CheckModel extends ParseModel{

	private ArrayList<String> wordsList;
	private Integer semErrorLine = new Integer(-1);
	boolean isPlus;

	private ProcedureModel currentProcedure;
	private ArrayList<ProcedureModel> procedureList = new ArrayList<ProcedureModel>();
	ArrayList<String> tempNameList;
	public CheckModel(ArrayList<Integer> list, ArrayList<Integer> list2, ArrayList<String> list3) {
		super(list, list2);
		wordsList = list3;
	}

	public int getSemErrorLine() {
		return semErrorLine;
	}

	@Override
	protected void block() {//(4)
		currentProcedure = new ProcedureModel();
		varDecl();
		procedureList.add(currentProcedure);
		subprogramDecls();
	}

	@Override
	protected void seqOfVarNames() {//(7)
		tempNameList = new ArrayList<String>();
		varName();
		tempNameList.add(wordsList.get(pointer - 1));

		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				/*
				switch(tokenList.get(pointer + 1)) {
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
				case SARRAY:
					int varType = tokenList.get(pointer + 8);
					int minSuffix = Integer.parseInt(wordsList.get(pointer + 3));
					int maxSuffix = Integer.parseInt(wordsList.get(pointer + 5));
					for(String tempName: tempNameList) {
						if(!currentProcedure.addToList(
								new ArrayType(tempName, varType, minSuffix, maxSuffix))) {
							semError();
						}
					}
					break;
				}*/
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
		super.subprogramDecl();
		procedureList.add(currentProcedure);
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
		}else {
			suffix();
			if(tokenList.get(pointer++) != SRBRACKET) {
				synError();
			}
		}
	}

	@Override
	protected void suffix() {//(33)
		suffixFormula();
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

	private void suffixFormula() {
		suffixPureFormula();
		if(relationalOpe()) {
			semError();
			pureFormula();
		}else {
			pointer--;
		}
	}

	private void suffixPureFormula() {
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
		suffixTerm();
		while(true) {
			if(additiveOpe()) {
				suffixTerm();
			}else {
				pointer--;
				break;
			}
		}
	}

	private void suffixTerm() {
		suffixFactor();
		while(true) {
			if(multiplicativeOpe()) {
				suffixFactor();
			}else {
				pointer--;
				break;
			}
		}
	}

	private void suffixFactor() {
		switch(tokenList.get(pointer++)) {
		case SIDENTIFIER:
			pointer--;
			if(currentProcedure.existsInIntegerList(wordsList.get(pointer))) {

			}else if(currentProcedure.existsInArrayList(wordsList.get(pointer))) {
				if(currentProcedure.getTypeOfArray(wordsList.get(pointer)) != SINTEGER) {
					semError();
				}
			}else {
				if(procedureList.get(0).existsInIntegerList(wordsList.get(pointer))) {

				}else if(procedureList.get(0).existsInArrayList(wordsList.get(pointer))) {
					if(procedureList.get(0).getTypeOfArray(wordsList.get(pointer)) != SINTEGER) {
						semError();
					}
				}else {
					semError();
				}
			}
			var();
			break;
		case SLPAREN:
			suffixFormula();
			if(tokenList.get(pointer++) != SRPAREN) {
				synError();
			}
			break;
		case SNOT:
			semError();
			factor();
			break;
		case SCONSTANT:
			break;
		default:
			semError();
			//pointer--;
			//constant();
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
		while(true) {
			if(additiveOpe()) {
				if(type != SINTEGER) {
					semError();
				}
				type = typeTerm();
			}else {
				pointer--;
				if(type != SINTEGER) {
					semError();
					type = -1;
				}
				break;
			}
		}
		return type;
	}

	protected int typeTerm() {//(38)
		int type = typeFactor();
		while(true) {
			if(multiplicativeOpe()) {
				if(type != SINTEGER) {
					semError();
				}
				type = typeFactor();
			}else {
				pointer--;
				if(type != SINTEGER) {
					semError();
					type = -1;
				}
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

	protected int typeConstant() {//(45)
		switch(tokenList.get(pointer++)) {
		case SCONSTANT:
			pointer--;
			unsignedInteger();
			return SINTEGER;
		case SSTRING:
			pointer--;
			string();
			return SSTRING;
		case SFALSE:
		case STRUE:
			return SBOOLEAN;
		default:
			synError();
			return -1;
		}
	}
	/* ****************************************************************** */

	private void semError() {
		if(semErrorLine == -1) {
			semErrorLine = lineList.get(pointer);
		}
	}
}