package enshud.s4.compiler;

import enshud.s3.checker.ProcedureModel;

import java.util.ArrayList;

public class CompileModel {
	ArrayList<ProcedureModel> procedureList;
	ArrayList<Integer> tokenList;
	ProcedureModel currentProcedure;
	
	ArrayList<String> outList = new ArrayList<String>();
	int pointer = 0;
	
	public CompileModel(ArrayList<ProcedureModel> procedureList, ArrayList<Integer> tokenList) {
		this.procedureList = procedureList;
		this.tokenList = tokenList;
		this.currentProcedure = procedureList.get(0);
	}
	
	public ArrayList<String> getOutList(){
		return outList;
	}

	public void program() {
		pointer++;
		String buf = "";
		String name = currentProcedure.getName();
		if(name.length() > 8) {
			name = name.substring(0, 8);
		}
		buf += name.toUpperCase();
		buf += "\tSTART\tBEGIN";
		outList.add(buf);
		outList.add("BEGIN\tLAD\tGR6,0");
		outList.add("\tLAD\tGR7, LIBBUF");
		
		compoundStatement();
	}
	
	public void compoundStatement() {
		
	}
}
