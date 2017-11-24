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
	protected void pureFormula() {//(37)
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
	@Override
	protected void suffix() {//(33)
		suffixFormula();
	}

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

	private void semError() {
		if(semErrorLine == -1) {
			semErrorLine = lineList.get(pointer);
		}
	}
}