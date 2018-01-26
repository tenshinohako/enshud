package enshud.casl;

import java.io.IOException;

public class MySim extends CaslSimulator{
	public static void main(final String[] args) throws IOException {
		//CaslSimulator.run("data/cas/normal01.cas", "tmp/out1.ans");
		//CaslSimulator.run("data/cas/normal04.cas", "tmp/out.ans", "8", "12");
		//CaslSimulator.run("data/cas/mycas.cas", "tmp/out.ans");


		CaslSimulator.run("tmp/out.cas", "tmp/out.ans", "8", "12");
	}

}
