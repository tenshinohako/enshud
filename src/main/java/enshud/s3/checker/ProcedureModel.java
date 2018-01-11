package enshud.s3.checker;

import java.util.ArrayList;

public class ProcedureModel {
	protected static final int SBOOLEAN = 3;
	protected static final int SCHAR = 4;
	protected static final int SINTEGER = 11;

	private ArrayList<IntegerType> integerList = new ArrayList<IntegerType>();
	private ArrayList<CharType> charList = new ArrayList<CharType>();
	private ArrayList<BooleanType> booleanList = new ArrayList<BooleanType>();
	private ArrayList<ArrayType> arrayList = new ArrayList<ArrayType>();

	private String procedureName;
	private int beginId;
	private int endId;
	
	public int allotId(int begin) {
		beginId = begin;
		int index = begin;
		for(int i=0; i<integerList.size(); i++) {
			integerList.get(i).allotId(index);
			index++;
		}
		for(int i=0; i<charList.size(); i++) {
			charList.get(i).allotId(index);
			index++;
		}
		for(int i=0; i<booleanList.size(); i++) {
			booleanList.get(i).allotId(index);
			index++;
		}
		for(int i=0; i<arrayList.size(); i++) {
			index = arrayList.get(i).allotId(index);
		}
		endId = index - 1;
		return index;
	}
	
	public boolean existsInProcedure(int id) {
		if(beginId <= id && id <= endId) {
			return true;
		}else {
			return false;
		}
	}

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

	public boolean isArrayType(String name) {
		if(!arrayList.isEmpty()) {
			for(ArrayType arrayVar: arrayList) {
				if(name.equals(arrayVar.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public void setName(String name) {
		procedureName = name;
	}

	public String getName() {
		return procedureName;
	}

}

class IntegerType{
	String name;
	int value;
	int id;
	public IntegerType(String name) {
		this.name = name;
	}

	public void setValue(int num) {
		value = num;
	}

	public String getName() {
		return name;
	}
	
	public void allotId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}

class CharType{
	String name;
	char value;
	int id;
	public CharType(String name) {
		this.name = name;
	}

	public void setValue(char letter) {
		value = letter;
	}

	public String getName() {
		return name;
	}
	
	public void allotId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}

class BooleanType{
	String name;
	boolean value;
	int id;
	BooleanType(String name){
		this.name = name;
	}

	public void setValue(boolean is) {
		value = is;
	}

	public String getName() {
		return name;
	}
	
	public void allotId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
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
	int id;
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
	
	public int allotId(int begin) {
		id = begin;
		return id + ((max - min) + 1);
	}
	
	public int getId() {
		return id;
	}

}