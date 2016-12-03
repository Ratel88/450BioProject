package newgui;

public class ArrayToCSV {

	static StringBuilder strBuilder = new StringBuilder();
	private static String dataOut[][];

	public static void arrayChopper(String[] tempArray) {

		String lines = null;
		int j;

		dataOut = new String[tempArray.length][11];

		for (int i = 0; i < tempArray.length; i++) {

			lines = tempArray[i];

			StringBuilder sb = new StringBuilder();

			String[] linesRead = lines.split("\\t");

			for (j = 0; j < linesRead.length; j++) {

				dataOut[i][j] = linesRead[j];
				dataOut[i][j] = dataOut[i][j] + ",";

				sb.append(dataOut[i][j]);

			}

			String line = sb.toString();
			addLine(line);

		}

		CSVWriter.saveCSV(strBuilder);

	}

	private static void addLine(String line) {

		line = line.substring(0, line.length() - 1);
		strBuilder.append(line);
		strBuilder.append("\n");

	}

	public static String getCSV() {
		return strBuilder.toString();
	}

}
