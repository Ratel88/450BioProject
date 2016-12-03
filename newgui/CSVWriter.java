package newgui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

	public static void saveCSV(StringBuilder strBuilder) {

		BufferedWriter CSVwriter;

		File CSVfile = new File("./Hoopes Gene List (Modified).csv");
		try {
			CSVwriter = new BufferedWriter(new FileWriter(CSVfile));
			CSVwriter.append(strBuilder.toString());
			CSVwriter.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void saveCSV(String strBuilder, String CSVfile) {

		BufferedWriter CSVwriter;

		try {
			CSVwriter = new BufferedWriter(new FileWriter(CSVfile));
			CSVwriter.append(strBuilder.toString());
			CSVwriter.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}


}
