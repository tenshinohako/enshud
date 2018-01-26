package enshud.s3.checker;

public class IntegerType{
	String name;
	String captureName;
	int value;
	//int id;
	public IntegerType(String name) {
		this.name = name;
		this.captureName = name;

		if(captureName.length() > 8) {
			captureName = captureName.substring(0, 8);
		}
		captureName = captureName.toUpperCase();
	}

	public void setValue(int num) {
		value = num;
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
