package ij.plugin.frame;
import ij.IJ;
import ij.WindowManager;
import ij.gui.GUI;
import ij.measure.CurveFitter;
import ij.plugin.PlugIn;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;

/** This is ImageJ's macro recorder. */
public class Recorder extends PlugInFrame implements PlugIn, ActionListener, ItemListener {

	public static boolean record;
	private Button makePlugin;
	private Checkbox recordCB;
	private String fitTypeStr = CurveFitter.fitList[0];
	private static TextArea textArea;
	private static Frame instance;
	private static String commandName;
	private static String commandOptions;

	public Recorder() {
		super("Recorder");
		if (instance!=null) {
			instance.toFront();
			return;
		}
		WindowManager.addWindow(this);
		instance = this;
		Panel panel = new Panel();
		record = true;
		recordCB = new Checkbox("Record", record);
		recordCB.addItemListener(this);
		panel.add(recordCB);
		makePlugin = new Button("Create Plugin");
		makePlugin.addActionListener(this);
		panel.add(makePlugin);
		add("North", panel);
		textArea = new TextArea("",20,80,TextArea.SCROLLBARS_VERTICAL_ONLY);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		add("Center", textArea);
		pack();
		GUI.center(this);
		setVisible(true);
		IJ.register(Recorder.class);
	}

	public static void record(String method) {
		if (textArea==null)
			return;
		textArea.append(method+"();\n");
	}

	public static void setCommand(String command) {
		if (textArea==null)
			return;
		commandName = command;
		commandOptions = null;
		//IJ.write("setCommand: "+command);
	}

	public static void record(String method, String arg) {
		if (textArea==null)
			return;
		textArea.append(method+"(\""+arg+"\");\n");
	}

	static String fixPath (String path) {
		StringBuffer sb = new StringBuffer();
		char c;
		for (int i=0; i<path.length(); i++) {
			sb.append(c=path.charAt(i));
			if (c=='\\')
				sb.append("\\");
		}
		return new String(sb);
	}

	public static void record(String method, int a1) {
		if (textArea==null)
			return;
		textArea.append(method+"("+a1+");\n");
	}

	public static void record(String method, int a1, int a2) {
		if (textArea==null)
			return;
		textArea.append(method+"("+a1+", "+a2+");\n");
	}

	public static void record(String method, int a1, int a2, int a3) {
		if (textArea==null)
			return;
		textArea.append(method+"("+a1+", "+a2+", "+a3+");\n");
	}

	public static void record(String method, String args, int a1, int a2) {
		if (textArea==null)
			return;
		method = "//"+method;
		textArea.append(method+"(\""+args+"\", "+a1+", "+a2+");\n");
	}

	public static void record(String method, int a1, int a2, int a3, int a4) {
		if (textArea==null)
			return;
		textArea.append(method+"("+a1+", "+a2+", "+a3+", "+a4+");\n");
	}

	public static void record(String method, String path, String args, int a1, int a2, int a3, int a4, int a5) {
		if (textArea==null)
			return;
		path = fixPath(path);
		method = "//"+method;
		textArea.append(method+"(\""+path+"\", "+"\""+args+"\", "+a1+", "+a2+", "+a3+", "+a4+", "+a5+");\n");
	}
	
	public static void recordOption(String key, String value) {
		key = trimKey(key);
		value = addQuotes(value);
		if (commandOptions==null)
			commandOptions = key+"="+value;
		else
			commandOptions += " "+key+"="+value;
		//IJ.write("  "+key+"="+value);
	}

	public static void recordPath(String path) {
		String key = "path";
		path = fixPath(path);
		path = addQuotes(path);
		if (commandOptions==null)
			commandOptions = key+"="+path;
		else
			commandOptions += " "+key+"="+path;
		//IJ.write("  "+key+"="+value);
	}

	public static void recordOption(String key) {
		key = trimKey(key);
		if (commandOptions==null)
			commandOptions = key;
		else
			commandOptions += " "+key;
		//IJ.write("  "+key+"="+value);
	}
	
	static String trimKey(String key) {
		int index = key.indexOf(" ");
		if (index>-1)
			key = key.substring(0,index);
		index = key.indexOf(":");
		if (index>-1)
			key = key.substring(0,index);
		key = key.toLowerCase();
		return key;
	}

	public static void saveCommand() {
		if (commandName!=null) {
			if (commandOptions!=null)
				textArea.append("run(\""+commandName+"\", \""+commandOptions+"\");\n");
			else
				textArea.append("run(\""+commandName+"\");\n");
		}
		commandName = null;
		commandOptions = null;
	}

	static String addQuotes(String value) {
		int index = value.indexOf(' ');
		if (index>-1)
			value = "'"+value+"'";
		return value;
	}

	void createPlugin() {
		String text = textArea.getText();
		if (text==null || text.equals("")) {
			IJ.runPlugIn("ij.plugin.NewPlugin", " ");
			return;
		}
		StringTokenizer st = new StringTokenizer(text, "\n");
		int n = st.countTokens();
		String line;
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<n; i++) {
			line = st.nextToken();
			if (line!=null && line.length()>3) {
				sb.append("\t\tIJ.");
				sb.append(line);
				sb.append('\n');
			}
		}
		IJ.runPlugIn("ij.plugin.NewPlugin", new String(sb));
	}
	
	public void itemStateChanged(ItemEvent e) {
		record = recordCB.getState();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==makePlugin)
			createPlugin();
	}

    public void windowClosing(WindowEvent e) {
		super.windowClosing(e);
		record = false;
		textArea = null;
		instance = null;	
	}

}