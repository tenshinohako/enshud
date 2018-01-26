package enshud.s3.checker;

public class BooleanType{
	String name;
	String captureName;
	boolean value;
	//int id;
	public BooleanType(String name){
		this.name = name;
		this.captureName = name;

		if(captureName.length() > 8) {
			captureName = captureName.substring(0, 8);
		}
		captureName = captureName.toUpperCase();
	}

	public void setValue(boolean is) {
		value = is;
	}

	public String getName() {
		return name;
	}

	public String getCaptureName() {
		return captureName;
	}

	/*
	public void allotId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	*/
}
