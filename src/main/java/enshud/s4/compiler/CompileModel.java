package enshud.s4.compiler;

import java.util.ArrayList;

import enshud.s3.checker.CheckModel;
import enshud.s3.checker.ProcedureModel;

public class CompileModel extends CheckModel{
	//ArrayList<ProcedureModel> procedureList;
	//ArrayList<Integer> tokenList;
	//ProcedureModel currentProcedure;

	private ArrayList<String> outList = new ArrayList<String>();
	//int pointer = 0;


	public CompileModel(ArrayList<Integer> list, ArrayList<Integer> list2, ArrayList<String> list3/*, ArrayList<ProcedureModel> procedureList*/) {
		super(list, list2, list3);
		//this.currentProcedure = procedureList.get(0);
	}

	public ArrayList<String> getOutList(){
		return procedureList.get(0).getOutList();
	}

	@Override
	public void program() {//(1)
		currentProcedure = new ProcedureModel();
		currentProcedure.setName(wordsList.get(1));
		procedureList.add(currentProcedure);

		String buf = "";
		String name = wordsList.get(pointer + 1);
		if(name.length() > 8) {
			name = name.substring(0, 8);
		}
		buf += name.toUpperCase();
		buf += "\tSTART\tBEGIN";
		currentProcedure.addToList(buf);
		currentProcedure.addToList("BEGIN\tLAD\tGR6,0");
		currentProcedure.addToList("\tLAD\tGR7, LIBBUF");

		header();
		block();
		compoundStatement();
	}

	@Override
	protected void statement() {//(26)
		super.statement();
	}

	@Override
	protected void assignmentStatement() {//(28)
		String leftVar = wordsList.get(pointer);
		int type = typeLeftSide();
		if(tokenList.get(pointer++) != SASSIGN) {
			synError();
		}
		if(typeFormula() != type) {
			semError();
		}
		int varType;
		if((varType = currentProcedure.getType(leftVar, 1)) == -1) {
			varType = procedureList.get(0).getType(leftVar, 1);
		}else {

		}
		if(varType == SARRAY) {
			currentProcedure.addToList("\tST\tGR2, " + currentProcedure.getCaptureName(leftVar) + ", GR1");
		}else {
			currentProcedure.addToList("\tST\tGR1, " + currentProcedure.getCaptureName(leftVar));
		}
	}




	/* ****************************************************************** */

	@Override
	protected int typeFormula() {//(36)
		int type = typePureFormula();
		int ope;
		if((ope = typeRelationalOpe()) != -1) {
			if(typePureFormula() == type) {
				return SBOOLEAN;
			}else {
				semError();
				return -1;
			}
			//currentProcedure.addToList("\tPOP\tGR2");
			//currentProcedure.addToList("\tPOP\tGR1");



		}else {
			pointer--;
			return type;
		}
	}

	@Override
	protected int typePureFormula() {//(37)
		int type = -1;
		int expectedType;
		boolean isMinus;
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
			if(ope == SPLUS) {
				if(expectedType != SINTEGER) {
					semError();
				}
				type = typeTerm();
				if(type != SINTEGER) {
					semError();
				}
				currentProcedure.addToList("\tPOP\tGR2");
				currentProcedure.addToList("\tPOP\tGR1");
				currentProcedure.addToList("\tADDA\tGR1, GR2");
				currentProcedure.addToList("\tPUSH\t0, GR1");

			}else if(ope == SMINUS) {
				if(expectedType != SINTEGER) {
					semError();
				}
				type = typeTerm();
				if(type != SINTEGER) {
					semError();
				}
				currentProcedure.addToList("\tPOP\tGR2");
				currentProcedure.addToList("\tPOP\tGR1");
				currentProcedure.addToList("\tSUBA\tGR1, GR2");
				currentProcedure.addToList("\tPUSH\t0, GR1");

			}else if(ope == SOR){
				if(expectedType != SBOOLEAN) {
					semError();
				}
				type = typeTerm();
				if(type != SBOOLEAN) {
					semError();
				}
				currentProcedure.addToList("\tPOP\tGR2");
				currentProcedure.addToList("\tPOP\tGR1");
				currentProcedure.addToList("\tSUBA\tGR1, GR2");
				currentProcedure.addToList("\tPUSH\t0, GR1");
			}else {
				pointer--;
				break;
			}
		}
		if(isMinus) {
			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tLD\tGR2, =-1");
			currentProcedure.addToList("\tCALL\tMULT");
			currentProcedure.addToList("\tPUSH\t0, GR1");
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

				if(type != SINTEGER) {
					semError();
				}
				currentProcedure.addToList("\tPOP\tGR2");
				currentProcedure.addToList("\tPOP\tGR1");

				if(ope == SSTAR) {
					currentProcedure.addToList("\tCALL\tMULT");
					currentProcedure.addToList("\tPUSH\t0, GR2");
				}else {
					currentProcedure.addToList("\tCALL\tDIV");
					if(ope == SDIVD) {
						currentProcedure.addToList("\tPUSH\t0, GR2");
					}else {
						currentProcedure.addToList("\tPUSH\t0, GR1");
					}
				}

			}else if(ope == SAND) {
				if(expectedType != SBOOLEAN) {
					semError();
				}
				type = typeFactor();
				if(type != SBOOLEAN) {
					semError();
				}
				currentProcedure.addToList("\tPOP\tGR2");
				currentProcedure.addToList("\tPOP\tGR1");
				currentProcedure.addToList("\tAND\tGR1, GR2");
				currentProcedure.addToList("\tPUSH\t0, GR1");
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
			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tXOR\tGR1, =1");
			currentProcedure.addToList("\tPUSH\t0, GR1");
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
			currentProcedure.addToList("\tPUSH\tGR1, =" + Integer.parseInt(wordsList.get(pointer - 1)));
			return SINTEGER;
		case SSTRING:
			pointer--;
			string();
			if(wordsList.get(pointer - 1).length() <= 3) {
				currentProcedure.addToList("\tLD\tGR1, =" + wordsList.get(pointer - 1));
				currentProcedure.addToList("\tPUSH\t0, GR1");
				return SCHAR;
			}else {
				return SSTRING;
			}
		case SFALSE:
			currentProcedure.addToList("\tPUSH\t=0");
			return SBOOLEAN;
		case STRUE:
			currentProcedure.addToList("\tPUSH\t=1");
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
			return SPLUS;
		case SMINUS:
			return SMINUS;
		case SOR:
			return SOR;
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

	protected int typeRelationalOpe() {//(40)
		switch(tokenList.get(pointer++)) {
		case SEQUAL:
			return SEQUAL;
		case SNOTEQUAL:
			return SNOTEQUAL;
		case SLESS:
			return SLESS;
		case SLESSEQUAL:
			return SLESSEQUAL;
		case SGREAT:
			return SGREAT;
		case SGREATEQUAL:
			return SGREATEQUAL;
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
					currentProcedure.addToList("\tLD\tGR1, " + name);
					currentProcedure.addToList("\tPUSH\t0, GR1");
				}
			}
		}else {
			if(!currentProcedure.isArrayType(wordsList.get(pointer - 2))) {
				if(!procedureList.get(0).isArrayType(wordsList.get(pointer - 2))) {
				semError();
				}
			}
			String name;
			if((name = currentProcedure.getCaptureName(wordsList.get(pointer - 2))) == "") {
				if((name = procedureList.get(0).getCaptureName(wordsList.get(pointer - 2))) == "") {

				}else {

				}

			}else {

			}

			suffix();
			currentProcedure.addToList("\tPOP\tGR2");
			currentProcedure.addToList("\tLD\tGR1, " + name + ", GR2");
			currentProcedure.addToList("\tPUSH\t0, GR1");

			if(tokenList.get(pointer++) != SRBRACKET) {
				synError();
			}
		}
	}


}
