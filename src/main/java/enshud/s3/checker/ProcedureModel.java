package enshud.s3.checker;

import java.util.ArrayList;

public class ProcedureModel {
	protected static final int SBOOLEAN = 3;
	protected static final int SCHAR = 4;
	protected static final int SINTEGER = 11;

	public ArrayList<IntegerType> integerList = new ArrayList<IntegerType>();
	public ArrayList<CharType> charList = new ArrayList<CharType>();
	public ArrayList<BooleanType> booleanList = new ArrayList<BooleanType>();
	public ArrayList<ArrayType> arrayList = new ArrayList<ArrayType>();

	public boolean existsInProcedure(String varName) {
		if(!integerList.isEmpty()) {
			for(IntegerType integerVar: integerList) {
				if(varName.equals(integerVar.getName())) {
					return true;
				}
			}
		}

		if(!charList.isEmpty()) {
			for(CharType charVar: charList) {
				if(varName.equals(charVar.getName())) {
					return true;
				}
			}
		}

		if(!booleanList.isEmpty()) {
			for(BooleanType booleanVar: booleanList) {
				if(varName.equals(booleanVar.getName())) {
					return true;
				}
			}
		}

		if(!arrayList.isEmpty()) {
			for(ArrayType arrayVar: arrayList) {
				if(varName.equals(arrayVar.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean existsInIntegerList(String varName) {
		if(!integerList.isEmpty()) {
			for(IntegerType integerVar: integerList) {
				if(varName.equals(integerVar.getName())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}

	public boolean existsInCharList(String varName) {
		if(!charList.isEmpty()) {
			for(CharType charVar: charList) {
				if(varName.equals(charVar.getName())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}

	public boolean existsInBooleanList(String varName) {
		if(!booleanList.isEmpty()) {
			for(BooleanType booleanVar: booleanList) {
				if(varName.equals(booleanVar.getName())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}

	public boolean existsInArrayList(String varName) {
		if(!arrayList.isEmpty()) {
			for(ArrayType arrayVar: arrayList) {
				if(varName.equals(arrayVar.getName())) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}

	public boolean addToList(IntegerType varAdded) {
		if(existsInProcedure(varAdded.getName())){
			return false;
		}
		integerList.add(varAdded);
		return true;
	}

	public boolean addToList(CharType varAdded) {
		if(existsInProcedure(varAdded.getName())){
			return false;
		}
		charList.add(varAdded);
		return true;
	}

	public boolean addToList(BooleanType varAdded) {
		if(existsInProcedure(varAdded.getName())){
			return false;
		}
		booleanList.add(varAdded);
		return true;
	}

	public boolean addToList(ArrayType varAdded) {
		if(existsInProcedure(varAdded.getName())){
			return false;
		}
		arrayList.add(varAdded);
		return true;
	}

	public int getType(String name) {
		if(!integerList.isEmpty()) {
			for(IntegerType integerVar: integerList) {
				if(name.equals(integerVar.getName())) {
					return SINTEGER;
				}
			}
		}

		if(!charList.isEmpty()) {
			for(CharType charVar: charList) {
				if(name.equals(charVar.getName())) {
					return SCHAR;
				}
			}
		}

		if(!booleanList.isEmpty()) {
			for(BooleanType booleanVar: booleanList) {
				if(name.equals(booleanVar.getName())) {
					return SBOOLEAN;
				}
			}
		}

		if(!arrayList.isEmpty()) {
			for(ArrayType arrayVar: arrayList) {
				if(name.equals(arrayVar.getName())) {
					return arrayVar.getType();
				}
			}
		}

		return -1;
	}

	public int getTypeOfArray(String name) {
		for(ArrayType arrayVar: arrayList) {
			if(arrayVar.getName().equals(name)) {
				return arrayVar.getType();
			}
		}
		return -1;
	}

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

	public String getName() {
		return name;
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

	public String getName() {
		return name;
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

	public String getName() {
		return name;
	}
}

class ArrayType{
	protected static final int SBOOLEAN = 3;
	protected static final int SCHAR = 4;
	protected static final int SINTEGER = 11;
	String name;
	int type;
	int min;
	int max;
	ArrayList<Integer> intList;
	ArrayList<Character> charList;
	ArrayList<Boolean> booleanList;

	public ArrayType(String name, int type, int min, int max){
		this.name = name;
		this.type = type;
		this.min = min;
		this.max = max;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

}