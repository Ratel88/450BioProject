package ij.gui;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** A modal dialog box that displays information. Based on the
	InfoDialogclass from "Java in a Nutshell" by David Flanagan. */
public class MessageDialog extends Dialog implements ActionListener {
    protected Button button;
    protected MultiLineLabel label;

    public MessageDialog(Frame parent, String title, String message) {
        super(parent, title, true);
        setLayout(new BorderLayout());
        if (message==null) message = "";
	    label = new MultiLineLabel(message);
		label.setFont(new Font("Dialog", Font.PLAIN, 12));
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.add(label);
        add("Center", panel);
        button = new Button("  OK  ");
		button.addActionListener(this);
        panel = new Panel();
        panel.setLayout(new FlowLayout());
        panel.add(button);
        add("South", panel);
        if (ij.IJ.isMacintosh())
        	setResizable(false);
        pack();
		GUI.center(this);
        show();
    }
    

	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}

}