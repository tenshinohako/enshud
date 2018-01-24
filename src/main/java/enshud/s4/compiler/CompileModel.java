package enshud.s4.compiler;

import java.util.ArrayList;

import enshud.s3.checker.CheckModel;

public class CompileModel extends CheckModel{
	//ArrayList<ProcedureModel> procedureList;
	//ArrayList<Integer> tokenList;
	//ProcedureModel currentProcedure;

	private ArrayList<String> outList = new ArrayList<String>();
	//int pointer = 0;
	private boolean isMinus;

	private int currentGR;

	public CompileModel(ArrayList<Integer> list, ArrayList<Integer> list2, ArrayList<String> list3/*, ArrayList<ProcedureModel> procedureList*/) {
		super(list, list2, list3);
		//this.currentProcedure = procedureList.get(0);
	}

	public ArrayList<String> getOutList(){
		return outList;
	}

	@Override
	public void program() {//(1)
		//pointer++;
		String buf = "";
		String name = wordsList.get(pointer + 1);
		if(name.length() > 8) {
			name = name.substring(0, 8);
		}
		buf += name.toUpperCase();
		buf += "\tSTART\tBEGIN";
		outList.add(buf);
		outList.add("BEGIN\tLAD\tGR6,0");
		outList.add("\tLAD\tGR7, LIBBUF");

		super.program();
	}

	@Override
	protected void statement() {//(26)
		super.statement();
	}

	@Override
	protected void assignmentStatement() {//(28)
		int type = typeLeftSide();
		String leftVar = wordsList.get(pointer);
		if(tokenList.get(pointer++) != SASSIGN) {
			synError();
		}
		if(typeFormula() != type) {
			semError();
		}
	}




	/* ****************************************************************** */

	@Override
	protected int typePureFormula() {//(37)
		int type = -1;
		int expectedType;
		currentGR = 0;
		switch(tokenList.get(pointer++)) {
		case SPLUS:
			isMinus = false;
			type = SINTEGER;
			break;
		case SMINUS:
			isMinus = true;
			type = SINTEGER;
			break;
		default:
			isMinus = false;
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

	@Override
	protected int typeTerm() {//(38)
		int type = typeFactor();
		int expectedType = type;
		while(true) {
			int ope = typeMultiplicativeOpe();
			if(ope == SSTAR || ope == SDIVD || ope == SMOD) {
				if(expectedType != SINTEGER) {
					semError();
				}
				type = typeFactor();
				outList.add("\tPUSH\t0,GR1");
				outList.add("\tPUSH\t0,GR2");
				outList.add("\tLD\tGR1, GR" + (currentGR - 1));
				outList.add("\tLD\tGR2, GR" + (currentGR));

				if(ope == SSTAR) {
					outList.add("\tCALL\tMULT");
					outList.add("\tLD\tGR" + (currentGR - 1) +", GR2");
				}else {
					outList.add("\tCALL\tDIV");
					if(ope == SDIVD) {
						outList.add("\tLD\tGR" + (currentGR - 1) +", GR2");
					}else {
						outList.add("\tLD\tGR" + (currentGR - 1) +", GR1");
					}
				}
				outList.add("\tPOP\tGR2");
				outList.add("\tPOP\tGR1");

				currentGR--;

				if(type != SINTEGER) {
					semError();
				}
			}else if(ope == SAND) {
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

	@Override
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
			typeVar();
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
			outList.add("\tXOR\tGR" + currentGR + ", =1");
			return SBOOLEAN;
		default:
			pointer--;
			return typeConstant();
		}
	}

	@Override
	protected int typeConstant() {//(45)
		switch(tokenList.get(pointer++)) {
		case SCONSTANT:
			pointer--;
			unsignedInteger();
			if(isMinus) {
				outList.add("\tLD\tGR" + ++currentGR + ", =-" + Integer.parseInt(wordsList.get(pointer - 1)));
			}else {
				outList.add("\tLD\tGR" + ++currentGR + ", =" + Integer.parseInt(wordsList.get(pointer - 1)));
			}
			return SINTEGER;
		case SSTRING:
			pointer--;
			string();
			if(wordsList.get(pointer - 1).length() <= 3) {
				outList.add("\tLD\tGR" + ++currentGR + ", =" + wordsList.get(pointer - 1));
				return SCHAR;
			}else {
				return SSTRING;
			}
		case SFALSE:
			outList.add("\tLD\tGR" + ++currentGR + ", =0");
			return SBOOLEAN;
		case STRUE:
			outList.add("\tLD\tGR" + ++currentGR + ", =1");
			return SBOOLEAN;
		default:
			synError();
			return -1;
		}
	}

	@Override
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

	@Override
	protected int typeMultiplicativeOpe() {//(42)
		switch(tokenList.get(pointer++)) {
		case SSTAR:
			return SSTAR;
		case SDIVD:
			return SDIVD;
		case SMOD:
			return SMOD;
		case SAND:
			return SAND;
		default:
			return -1;
		}
	}


	/* ****************************************************************** */

	protected void typeVar() {//(30)
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
				}else {
					String name;
					if((name = currentProcedure.getCaptureName(wordsList.get(pointer - 1))) == "") {
						if((name = procedureList.get(0).getCaptureName(wordsList.get(pointer - 1))) == "") {

						}else {

						}

					}else {

					}
					outList.add("\tLD\tGR" + currentGR + ", " + name);
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


}
