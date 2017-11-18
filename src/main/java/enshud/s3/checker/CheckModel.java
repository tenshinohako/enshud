package enshud.s3.checker;

import java.util.ArrayList;

public class CheckModel extends ProgramModel{

	private ArrayList<String> wordsList;
	private Integer semErrorLine = new Integer(-1);
	boolean isPlus;

	private ArrayList<IntegerType> integerList = new ArrayList<IntegerType>();
	private ArrayList<CharType> charList = new ArrayList<CharType>();
	private ArrayList<BooleanType> booleanList = new ArrayList<BooleanType>();

	public CheckModel(ArrayList<Integer> list, ArrayList<Integer> list2, ArrayList<String> list3) {
		super(list, list2);
		wordsList = list3;

	}

	public int getSemErrorLine() {
		return semErrorLine;
	}

	@Override
	public void elementOfSeqOfVarDecls() {
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
	public void seqOfVarNames() {//(7)
		ArrayList<String> tempNameList = new ArrayList<String>();
		varName();
		tempNameList.add(wordsList.get(pointer - 1));

		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				switch(tokenList.get(pointer + 1)) {
				case SINTEGER:
					for(String tempName: tempNameList) {
						if(!integerList.isEmpty()) {
							for(IntegerType var: integerList) {
								if(var.name.equals(tempName)) {
									semError();
									return;
								}
							}
						}
						integerList.add(new IntegerType(tempName));
					}
					break;
				case SCHAR:
					for(String tempName: tempNameList) {
						if(!charList.isEmpty()) {
							for(CharType var: charList) {
								if(var.name.equals(tempName)) {
									semError();
									return;
								}
							}
						}
						charList.add(new CharType(tempName));
					}
					break;
				case SBOOLEAN:
					for(String tempName: tempNameList) {
						if(!booleanList.isEmpty()) {
							for(BooleanType var: booleanList) {
								if(var.name.equals(tempName)) {
									semError();
									return;
								}
							}
						}
						booleanList.add(new BooleanType(tempName));
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
	public void integer() {//(14)
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
	public void pureFormula() {//(37)
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
	public void unsignedInteger() {//(46)
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
	public void suffix() {//(33)
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

class IntegerType{
	String name;
	int value;
	public IntegerType(String name) {

	}
}

class CharType{
	String name;
	char value;
	public CharType(String name) {

	}
}

class BooleanType{
	String name;
	boolean value;
	BooleanType(String name){

	}
}

class ArrayType{
	String name;
	String type;
	int min;
	int max;
	ArrayList<Integer> intList;
	ArrayList<Character> charList;
	ArrayList<Boolean> booleanList;
	ArrayType(String name, String type, int min, int max){

	}
}
