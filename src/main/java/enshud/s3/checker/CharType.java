package enshud.s3.checker;

public class CharType{
	String name;
	String captureName;
	char value;
	//int id;
	public CharType(String name) {
		this.name = name;
		this.captureName = name;

		if(captureName.length() > 8) {
			captureName = captureName.substring(0, 8);
		}
		captureName = captureName.toUpperCase();
	}

	public void setValue(char letter) {
		value = letter;
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