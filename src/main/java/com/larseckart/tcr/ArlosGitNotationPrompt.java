package com.larseckart.tcr;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

class ArlosGitNotationPrompt extends AbstractCommitPrompt {

  @Override
  protected void setLayout() {
    panel.setLayout(new GridBagLayout());
    addCustomCommitPart();
    addQuickKeys();
    addArlosGitNotation();
  }

  private void addArlosGitNotation() {
    int position = 4;
    JPanelHelpers.addHelpText(panel, "Arlo's Git Notation:", position++, true);
    addHelpText("------ High Risk ------", position++);
    addHelpText("F   Feature (< 9 LoC)", position++);
    addHelpText("B   Bug (< 9 LoC)", position++);
    addHelpText("R   Test-supported Refactoring", position++);
    addHelpText("F!! Feature (> 8 LoC)", position++);
    addHelpText("B!! Bug (> 8 LoC)", position++);
    addHelpText("R!! Non-provable refactoring", position++);
    addHelpText("*** Non-compiling commit", position++);
    position = 5;
    addHelpText2("------ Low  Risk ------", position++);
    addHelpText2("c   Comments (add/delete)", position++);
    addHelpText2("d   Developer documentation changes", position++);
    addHelpText2("e   Environment (non-code) changes", position++);
    addHelpText2("t   Test only change", position++);
    addHelpText2("r   Provable Refactoring", position++);
    addHelpText2("a   Automated formatting", position++);
  }

  private void addQuickKeys() {
    int position = 1;
    JPanelHelpers.addHelpText(panel, "Quick Actions:", 2, true);
    createQuickbutton("Rename", "r   Rename", KeyEvent.VK_R, position++);
    createQuickbutton("Inline", "r   Inline", KeyEvent.VK_I, position++);
    createQuickbutton("Extract Method", "r   Extract Method", KeyEvent.VK_M, position++);
    createQuickbutton("Extract Variable", "r   Extract Variable", KeyEvent.VK_V, position++);
    createQuickbutton("Delete Clutter", "r   Delete Clutter", KeyEvent.VK_D, position++);
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
    ArlosGitNotationPrompt panel = new ArlosGitNotationPrompt();
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
