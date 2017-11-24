package enshud.s3.checker;

import java.util.ArrayList;

public class ProcedureModel {
	public ArrayList<IntegerType> integerList = new ArrayList<IntegerType>();
	public ArrayList<CharType> charList = new ArrayList<CharType>();
	public ArrayList<BooleanType> booleanList = new ArrayList<BooleanType>();

}

class IntegerType{
	String name;
	int value;
	public IntegerType(String name) {
		this.name = name;
	}

	public void setValue(int num) {
		value = num;
	}
}

class CharType{
	String name;
	char value;
	public CharType(String name) {
		this.name = name;
	}

	public void setValue(char letter) {
		value = letter;
	}
}

class BooleanType{
	String name;
	boolean value;
	BooleanType(String name){
		this.name = name;
	}

	public void setValue(boolean is) {
		value = is;
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
	public ArrayType(String name, String type, int min, int max){
		this.name = name;

	}
}