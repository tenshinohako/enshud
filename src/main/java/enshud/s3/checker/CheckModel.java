package enshud.s3.checker;

import java.util.ArrayList;

import enshud.s2.parser.ParseModel;

public class CheckModel extends ParseModel{

	private ArrayList<String> wordsList;
	private Integer semErrorLine = new Integer(-1);
	boolean isPlus;

	private ProcedureModel currentProcedure;
	private ArrayList<ProcedureModel> procedureList = new ArrayList<ProcedureModel>();

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

	@Override
	protected void seqOfVarNames() {//(7)
		ArrayList<String> tempNameList = new ArrayList<String>();
		varName();
		tempNameList.add(wordsList.get(pointer - 1));

		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				switch(tokenList.get(pointer + 1)) {
				case SINTEGER:
					for(String tempName: tempNameList) {
						if(!currentProcedure.integerList.isEmpty()) {
							for(IntegerType var: currentProcedure.integerList) {
								if(var.name.equals(tempName)) {
									semError();
									return;
								}
							}
						}
						currentProcedure.integerList.add(new IntegerType(tempName));
					}
					break;
				case SCHAR:
					for(String tempName: tempNameList) {
						if(!currentProcedure.charList.isEmpty()) {
							for(CharType var: currentProcedure.charList) {
								if(var.name.equals(tempName)) {
									semError();
									return;
								}
							}
						}
						currentProcedure.charList.add(new CharType(tempName));
					}
					break;
				case SBOOLEAN:
					for(String tempName: tempNameList) {
						if(!currentProcedure.booleanList.isEmpty()) {
							for(BooleanType var: currentProcedure.booleanList) {
								if(var.name.equals(tempName)) {
									semError();
									return;
								}
							}
						}
						currentProcedure.booleanList.add(new BooleanType(tempName));
					}
					break;
				}
				break;
			}else {
				varName();
				tempNameList.add(wordsList.get(pointer - 1));
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
		subprogramHeader();
		varDecl();
		compoundStatement();
		procedureList.add(currentProcedure);
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
			if(Integer.parseInt(wordsList.get(pointer - 1)) > 32767) {
				semError();
			}
		}else {
			if(Integer.parseInt(wordsList.get(pointer - 1)) > 32768) {
				semError();
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
			semErrorLine = lineList.get(pointer - 1);
		}
	}
}