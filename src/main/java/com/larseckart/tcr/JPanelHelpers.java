package com.larseckart.tcr;

import javax.swing.*;
import java.awt.*;

class JPanelHelpers {
    public static void addHelpText(JPanel panel, String text, int position, boolean first) {
        var gridBagConstraints = new GridBagConstraints();
        JLabel helpText = new JLabel(text);
        var font = new Font("Courier", first ? Font.BOLD : Font.PLAIN, 16);
        helpText.setFont(font);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(first ? 50 : 0, 10, 10, 0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridy = position;
        panel.add(helpText, gridBagConstraints);
    }
}
