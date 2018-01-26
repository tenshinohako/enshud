package enshud.s4.compiler;

import java.util.ArrayList;

import enshud.s3.checker.BooleanType;
import enshud.s3.checker.CharType;
import enshud.s3.checker.CheckModel;
import enshud.s3.checker.IntegerType;
import enshud.s3.checker.ProcedureModel;

public class CompileModel extends CheckModel{
	//ArrayList<ProcedureModel> procedureList;
	//ArrayList<Integer> tokenList;
	//ProcedureModel currentProcedure;

	//private ArrayList<String> outList = new ArrayList<String>();
	private int branchNum = 0;
	private int ifNum = 0;
	private int whileNum = 0;
	private int stringNum = 0;
	private int idNum = 1;
	//int pointer = 0;


	public CompileModel(ArrayList<Integer> list, ArrayList<Integer> list2, ArrayList<String> list3/*, ArrayList<ProcedureModel> procedureList*/) {
		super(list, list2, list3);
		//this.currentProcedure = procedureList.get(0);
	}

	public ArrayList<String> getOutList(){
		ArrayList<String> list = new ArrayList<String>();
		for(ProcedureModel pro: procedureList) {
			list.addAll(pro.getCompoundList());
		}
		for(ProcedureModel pro: procedureList) {
			list.addAll(pro.getVarList());
		}
		list.add("LIBBUF\tDS\t256");
		for(ProcedureModel pro: procedureList) {
			list.addAll(pro.getConstantList());
		}
		list.add("\tEND");
		return list;
	}

	@Override
	public void program() {//(1)
		currentProcedure = new ProcedureModel();
		currentProcedure.setName(wordsList.get(1));
		procedureList.add(currentProcedure);
		currentProcedure.thisIsMain();

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
		currentProcedure.addToList("\tRET");
	}

	@Override
	protected void subprogramDecl() {//(17)
		currentProcedure = new ProcedureModel();
		currentProcedure.setName(wordsList.get(pointer + 1));
		procedureList.add(currentProcedure);
		currentProcedure.setId(idNum++);
		currentProcedure.addToList(currentProcedure.getCaptureName() + "\tNOP");
		subprogramHeader();
		varDecl();
		compoundStatement();
		currentProcedure.addToList("\tRET");
	}

	@Override
	protected void elementOfSeqOfTempParameters() {//(21.5)
		seqOfTempParameterNames();
		if(tokenList.get(pointer++) != SCOLON) {
			synError();
		}
		tempStandardType();
	}

	@Override
	protected void statement() {//(26)
		switch(tokenList.get(pointer++)) {
		case SIF:
			int subIfNum = ifNum++;
			if(typeFormula() != SBOOLEAN) {
				semError();
			}

			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tCPA\tGR1, =1");
			currentProcedure.addToList("\tJNZ\tIFF" + subIfNum);

			if(tokenList.get(pointer++) != STHEN) {
				synError();
			}
			compoundStatement();

			currentProcedure.addToList("\tJUMP\tIFA" + subIfNum);
			currentProcedure.addToList("IFF" + subIfNum + "\tNOP");

			if(tokenList.get(pointer++) != SELSE) {
				pointer--;
			}else {
				compoundStatement();
			}

			currentProcedure.addToList("IFA" + subIfNum + "\tNOP");
			break;
		case SWHILE:
			int subWhileNum = whileNum++;

			currentProcedure.addToList("WHL" + subWhileNum + "\tNOP");

			if(typeFormula() != SBOOLEAN) {
				semError();
			}

			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tCPA\tGR1, =1");
			currentProcedure.addToList("\tJNZ\tWHE" + subWhileNum);

			if(tokenList.get(pointer++) != SDO) {
				synError();
			}
			statement();

			currentProcedure.addToList("\tJUMP\tWHL" + subWhileNum);
			currentProcedure.addToList("WHE" + subWhileNum + "\tNOP");


			break;
		default:
			pointer--;
			basicStatement();
		}
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
		if(currentProcedure.isMain()) {
			varType = currentProcedure.getType(leftVar, 1);
			leftVar = currentProcedure.getCaptureName(leftVar);
		}else {
			if((varType = currentProcedure.getType(leftVar, 1)) == -1) {
				varType = procedureList.get(0).getType(leftVar, 1);
				leftVar = procedureList.get(0).getCaptureName(leftVar);
			}else {
				leftVar = currentProcedure.getCaptureName(leftVar);
				leftVar += currentProcedure.getId();
			}
		}
		if(varType == SARRAY) {
			currentProcedure.addToList("\tPOP\tGR2");
			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tST\tGR2, " + leftVar + ", GR1");
		}else {
			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tST\tGR1, " + leftVar);
		}
	}

	@Override
	protected void procedureCallStatement() {//(34)
		boolean exist = false;
		String tempName;
		ProcedureModel procedure = new ProcedureModel();
		for(ProcedureModel proc: procedureList) {
			if(proc.getName().equals(wordsList.get(pointer))) {
				exist = true;
				procedure = proc;
				break;
			}
		}
		if(!exist) {
			semError();
		}

		procedureName();
		if(tokenList.get(pointer++) != SLPAREN) {
			pointer--;
		}else {
			seqOfFormulae();
			if(tokenList.get(pointer++) != SRPAREN) {
				synError();
			}
		}

		while((tempName = procedure.getTemp()) != null) {
			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tST\tGR1, " + tempName);
		}

		currentProcedure.addToList("\tCALL\t" + procedure.getCaptureName());

		/*    */
	}

	protected void inOutString() {//(43)
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
				writeSeqOfFormulae();
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
					if(currentProcedure.isMain()) {
						name = currentProcedure.getCaptureName(wordsList.get(pointer - 1));
					}else {
						if((name = currentProcedure.getCaptureName(wordsList.get(pointer - 1))) == "") {
							name = procedureList.get(0).getCaptureName(wordsList.get(pointer - 1));
						}else {
							name += currentProcedure.getId();
						}
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
			if(currentProcedure.isMain()) {
				name = currentProcedure.getCaptureName(wordsList.get(pointer - 2));
			}else {
				if((name = currentProcedure.getCaptureName(wordsList.get(pointer - 2))) == "") {
					name = procedureList.get(0).getCaptureName(wordsList.get(pointer - 2));
				}else {
					name += currentProcedure.getId();
				}
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

	@Override
	protected int typeFormula() {//(36)
		int type = typePureFormula();
		int ope;
		if((ope = typeRelationalOpe()) != -1) {
			if(typePureFormula() == type) {
				currentProcedure.addToList("\tPOP\tGR2");
				currentProcedure.addToList("\tPOP\tGR1");
				currentProcedure.addToList("\tCPA\tGR1, GR2");
				switch(ope) {
				case SEQUAL:
					currentProcedure.addToList("\tJZE\tTRUE" + branchNum);
					break;
				case SNOTEQUAL:
					currentProcedure.addToList("\tJNZ\tTRUE" + branchNum);
					break;
				case SLESS:
					currentProcedure.addToList("\tJMI\tTRUE" + branchNum);
					break;
				case SLESSEQUAL:
					currentProcedure.addToList("\tJZE\tTRUE" + branchNum);
					currentProcedure.addToList("\tJMI\tTRUE" + branchNum);
					break;
				case SGREAT:
					currentProcedure.addToList("\tJPL\tTRUE" + branchNum);
					break;
				case SGREATEQUAL:
					currentProcedure.addToList("\tJZE\tTRUE" + branchNum);
					currentProcedure.addToList("\tJPL\tTRUE" + branchNum);
					break;
				}
				currentProcedure.addToList("FALSE" + branchNum + "\tPUSH\t0");
				currentProcedure.addToList("\tJUMP\tALL" + branchNum);
				currentProcedure.addToList("TRUE" + branchNum + "\tPUSH\t1");
				currentProcedure.addToList("ALL" + branchNum + "\tNOP");
				branchNum++;
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

	@Override
	protected int typeConstant() {//(45)
		switch(tokenList.get(pointer++)) {
		case SCONSTANT:
			pointer--;
			unsignedInteger();
			currentProcedure.addToList("\tPUSH\t" + Integer.parseInt(wordsList.get(pointer - 1)));
			return SINTEGER;
		case SSTRING:
			pointer--;
			string();
			if(wordsList.get(pointer - 1).length() <= 3) {
				currentProcedure.addToList("\tLD\tGR1, =" + wordsList.get(pointer - 1));
				currentProcedure.addToList("\tPUSH\t0, GR1");
				return SCHAR;
			}else {
				currentProcedure.addToList("\tPUSH\t" + (wordsList.get(pointer - 1).length() - 2));
				currentProcedure.addToConstantList("STR" + stringNum + "\tDC\t" + wordsList.get(pointer - 1));
				return SSTRING;
			}
		case SFALSE:
			currentProcedure.addToList("\tPUSH\t0");
			return SBOOLEAN;
		case STRUE:
			currentProcedure.addToList("\tPUSH\t1");
			return SBOOLEAN;
		default:
			synError();
			return -1;
		}
	}

	/* ****************************************************************** */


	protected void writeSeqOfFormulae() {//(35)
		writeLine();
		while(true) {
			if(tokenList.get(pointer++) != SCOMMA) {
				pointer--;
				break;
			}else {
				writeLine();
			}
		}
		currentProcedure.addToList("\tCALL\tWRTLN");
	}

	private void writeLine() {
		int type = typeFormula();
		switch(type) {
		case SINTEGER:
			currentProcedure.addToList("\tPOP\tGR2");
			currentProcedure.addToList("\tCALL\tWRTINT");
			break;
		case SCHAR:
			currentProcedure.addToList("\tPOP\tGR2");
			currentProcedure.addToList("\tCALL\tWRTCH");
			break;
		case SSTRING:
			currentProcedure.addToList("\tPOP\tGR1");
			currentProcedure.addToList("\tLAD\tGR2, STR" + (stringNum++));
			currentProcedure.addToList("\tCALL\tWRTSTR");
			break;
		}
	}



	/* ****************************************************************** */


	protected void tempStandardType() {//(10)
		switch(tokenList.get(pointer++)) {
		case SINTEGER:
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(new IntegerType(tempName))) {
					semError();
				}

				if(tempName.length() > 8) {
					tempName = tempName.substring(0, 8);
				}
				tempName = tempName.toUpperCase();
				tempName += currentProcedure.getId();
				currentProcedure.addToTempParameterList(tempName);
			}
			break;
		case SCHAR:
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(new CharType(tempName))) {
					semError();
				}

				if(tempName.length() > 8) {
					tempName = tempName.substring(0, 8);
				}
				tempName = tempName.toUpperCase();
				tempName += currentProcedure.getId();
				currentProcedure.addToTempParameterList(tempName);
			}
			break;
		case SBOOLEAN:
			for(String tempName: tempNameList) {
				if(!currentProcedure.addToList(new BooleanType(tempName))) {
					semError();
				}

				if(tempName.length() > 8) {
					tempName = tempName.substring(0, 8);
				}
				tempName = tempName.toUpperCase();
				tempName += currentProcedure.getId();
				currentProcedure.addToTempParameterList(tempName);
			}
			break;
		default:
			synError();
			break;
		}
	}
}
