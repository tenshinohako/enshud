package enshud.s2.parser;

import java.util.ArrayList;

public class ProgramModel {
	private ArrayList<Integer> tokenList;
	private Integer pointer = new Integer(0);

	public ProgramModel(ArrayList<Integer> list) {
		tokenList = list;
	}


	public void programRoot() {
		header();
		block();
		compoundStatement();
	}

	private void header() {
		switch(tokenList.get(pointer)) {
		case 17:
			break;
		}
	}

	private void block() {

		/*
		 * method追加おじさん
		 */
	}

	private void compoundStatement() {



	}

}
