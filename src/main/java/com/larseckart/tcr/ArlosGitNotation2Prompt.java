package com.larseckart.tcr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

class ArlosGitNotation2Prompt extends AbstractCommitPrompt {

  @Override
  protected void setLayout() {
    panel.setLayout(new GridBagLayout());
    addCustomCommitPart();
    addQuickKeys();
    int position = addArlosGitNotation();
    addExamples(position);
  }

  private int addArlosGitNotation() {
    int position = 4;
    JPanelHelpers.addHelpText(panel, "Arlo's Commit Notation:", position++, true);
    addHelpText("------ Risk ------", position++);
    addHelpText(".   Provable", position++);
    addHelpText("-   Tested", position++);
    addHelpText("!   Single Action", position++);
    addHelpText("@   Other", position++);

    position = 5;
    addHelpText2("------ Action ------", position++);
    addHelpText2("r   Refactoring", position++);
    addHelpText2("e   Environment (non-code)", position++);
    addHelpText2("d   Documentation", position++);
    addHelpText2("t   Test only", position++);
    addHelpText2("F   Feature", position++);
    addHelpText2("B   Bugfix", position++);
    return position;
  }

  private void addQuickKeys() {
    int position = 1;
    JPanelHelpers.addHelpText(panel, "Quick Actions:", 2, true);
    createQuickbutton("Rename", ". r Rename", KeyEvent.VK_R, position++);
    createQuickbutton("Inline", ". r Inline", KeyEvent.VK_I, position++);
    createQuickbutton("Extract Method", ". r Extract Method", KeyEvent.VK_M, position++);
    createQuickbutton("Extract Variable", ". r Extract Variable", KeyEvent.VK_V, position++);
    createQuickbutton("Delete Clutter", ". r Delete Clutter", KeyEvent.VK_D, position++);
  }

  private void addExamples(int position) {
    JPanelHelpers.addHelpText(panel, "Examples:", position++, true);
    addHelpText(". r rename variable", position++);
    addHelpText("! B fixed spelling on label", position++);

  }


  private void addHelpText(String text, int position) {
    JPanelHelpers.addHelpText(panel, text, position, false);
  }

  private void addHelpText2(String text, int position) {
    addHelpText2(text, position, false);
  }

  private void addHelpText2(String text, int position, boolean first) {
    var gridBagConstraints = new GridBagConstraints();
    JLabel helpText = new JLabel(text);
    var font = new Font("Courier", first ? Font.BOLD : Font.PLAIN, 16);
    helpText.setFont(font);
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.insets = new Insets(first ? 50 : 0, 10, 10, 0);
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridwidth = 5;
    gridBagConstraints.gridy = position;
    panel.add(helpText, gridBagConstraints);
  }

  private void createQuickbutton(String label, String commitMessage, int shortcut, int position) {
    var gridBagConstraints = new GridBagConstraints();
    var quick = new JButton(label);
    quick.setMnemonic(shortcut);
    quick.setDefaultCapable(true);
    gridBagConstraints.insets = new Insets(10, 10, 10, 10);
    gridBagConstraints.gridx = position;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridy = 3;
    quick.addActionListener(e -> doCommit(commitMessage));
    panel.add(quick, gridBagConstraints);
  }


  public static String display() {
    ArlosGitNotation2Prompt panel = new ArlosGitNotation2Prompt();
    openInFrame(panel, "Commit...");
    while (!panel.done) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {

      }
    }
    return panel.message;
  }
}
