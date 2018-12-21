package com.sandlex.toolboxxy.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class PatternTest {
	
	public static void main(String[] arg) {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(800, 400));
        JPanel panel = new JPanel();
        final JTextField regexp = new JTextField("[\\w.]+");
        regexp.setPreferredSize(new Dimension(300, 20));
        final JTextField value = new JTextField();
        value.setPreferredSize(new Dimension(300, 20));
        final JLabel result = new JLabel();
        JButton test = new JButton("test");
        test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
                Pattern p = Pattern.compile(regexp.getText());
                result.setText(String.valueOf(p.matcher(value.getText()).matches()));
			}
		});
        // [a-zA-Z0-9_.\[\]]*
        // [\w.\[\]]+
        panel.add(regexp);
        panel.add(value);
        panel.add(test);
        panel.add(result);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
	}

}
