package enshud.s3.checker;

import java.util.ArrayList;

public class ProcedureModel {
	protected static final int SARRAY = 1;
	protected static final int SBOOLEAN = 3;
	protected static final int SCHAR = 4;
	protected static final int SINTEGER = 11;

	private ArrayList<IntegerType> integerList = new ArrayList<IntegerType>();
	private ArrayList<CharType> charList = new ArrayList<CharType>();
	private ArrayList<BooleanType> booleanList = new ArrayList<BooleanType>();
	private ArrayList<ArrayType> arrayList = new ArrayList<ArrayType>();
	private ArrayList<String> compoundList = new ArrayList<String>();
	private ArrayList<String> constantList = new ArrayList<String>();
	private ArrayList<String> tempParameterList = new ArrayList<String>();

	private String procedureName;
	private String captureProcedureName;
	//private int beginId;
	//private int endId;
	private int id = 0;
	private boolean isMain = false;

	public String getTemp() {
		try {
			String temp = tempParameterList.get(tempParameterList.size() - 1);
			tempParameterList.remove(tempParameterList.size() - 1);
			return temp;
		}catch(IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void addToTempParameterList(String str) {
		tempParameterList.add(str);
	}

	public void setId(int id) {
		id = this.id;
	}

	public int getId() {
		return id;
	}

	public void thisIsMain() {
		isMain = true;
	}

	public boolean isMain() {
		return isMain;
	}

	public ArrayList<String> getConstantList(){
		return constantList;
	}

	public void addToConstantList(String str) {
		constantList.add(str);
	}

	public ArrayList<String> getVarList(){
		ArrayList<String> list = new ArrayList<String>();


		if(isMain) {
			for(int i=0; i<integerList.size(); i++) {
				list.add(integerList.get(i).getCaptureName() + "\tDS\t1");
			}

			for(int i=0; i<charList.size(); i++) {
				list.add(charList.get(i).getCaptureName() + "\tDS\t1");
			}

			for(int i=0; i<booleanList.size(); i++) {
				list.add(booleanList.get(i).getCaptureName() + "\tDS\t1");
			}

			for(int i=0; i<arrayList.size(); i++) {
				list.add(arrayList.get(i).getCaptureName() + "\tDS\t" + arrayList.get(i).getMax());
			}
		}else {
			for(int i=0; i<integerList.size(); i++) {
				list.add(integerList.get(i).getCaptureName()+ id + "\tDS\t1");
			}

			for(int i=0; i<charList.size(); i++) {
				list.add(charList.get(i).getCaptureName()+ id + "\tDS\t1");
			}

			for(int i=0; i<booleanList.size(); i++) {
				list.add(booleanList.get(i).getCaptureName() + id + "\tDS\t1");
			}

			for(int i=0; i<arrayList.size(); i++) {
				list.add(arrayList.get(i).getCaptureName() + id + "\tDS\t" + arrayList.get(i).getMax());
			}
		}


		return list;
	}

	public ArrayList<String> getCompoundList(){
		return compoundList;
	}

	public void addToList(String str) {
		compoundList.add(str);
	}

	public String getCaptureName(String varName) {
		if(existsInIntegerList(varName)) {
			for(IntegerType var: integerList) {
				if(varName.equals(var.getName())) {
					return var.getCaptureName();
				}
			}
		}else if(existsInCharList(varName)) {
			for(CharType var: charList) {
				if(varName.equals(var.getName())) {
					return var.getCaptureName();
				}
			}
		}else if(existsInBooleanList(varName)) {
			for(BooleanType var: booleanList) {
				if(varName.equals(var.getName())) {
					return var.getCaptureName();
				}
			}
		}else if(existsInArrayList(varName)) {
			for(ArrayType var: arrayList) {
				if(varName.equals(var.getName())) {
					return var.getCaptureName();
				}
			}
		}
		return "";
	}

	/*
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
	*/

	/*
	public boolean existsInProcedure(int id) {
		if(beginId <= id && id <= endId) {
			return true;
		}else {
			return false;
		}
	}
	*/

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

	public int getType(String name, int mode) {
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
					return SARRAY;
				}
			}
		}

		return -1;
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

		captureProcedureName = name;

		if(captureProcedureName.length() > 8) {
			captureProcedureName = captureProcedureName.substring(0, 8);
		}
		captureProcedureName = captureProcedureName.toUpperCase();
	}

	public String getCaptureName() {
		return captureProcedureName;
	}

	public String getName() {
		return procedureName;
	}

}