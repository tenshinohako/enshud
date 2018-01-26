package enshud.s3.checker;

import java.util.ArrayList;

public
class ArrayType{
	protected static final int SBOOLEAN = 3;
	protected static final int SCHAR = 4;
	protected static final int SINTEGER = 11;
	String name;
	String captureName;
	int type;
	int min;
	int max;
	//int id;
	ArrayList<Integer> intList;
	ArrayList<Character> charList;
	ArrayList<Boolean> booleanList;

	public ArrayType(String name, int type, int min, int max){
		this.name = name;
		this.type = type;
		this.min = min;
		this.max = max;

		this.captureName = name;

		if(captureName.length() > 8) {
			captureName = captureName.substring(0, 8);
		}
		captureName = captureName.toUpperCase();
	}

	public String getName() {
		return name;
	}

	public String getCaptureName() {
		return captureName;
	}

	public int getType() {
		return type;
	}

	/*
	public int allotId(int begin) {
		id = begin;
		return id + ((max - min) + 1);
	}

	public int getId() {
		return id;
	}
	*/

	public int getMax() {
		return max;
	}

}