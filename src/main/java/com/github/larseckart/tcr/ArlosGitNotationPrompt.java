package com.github.larseckart.tcr;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class ArlosGitNotationPrompt {
  private String message = "";
  private boolean done = false;
  private final JPanel panel = new JPanel();

  public ArlosGitNotationPrompt() {
    setLayout();
  }

  private void setLayout() {
    panel.setLayout(new GridBagLayout());
    addCustomCommitPart();
    addQuickKeys();
    addArlosGitNotation();
  }

  private void addArlosGitNotation() {
    int position = 4;
    addHelpText("Arlo's Git Notation:", position++, true);
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
    addHelpText("Quick Actions:", 2, true);
    createQuickbutton("Rename", "r   Rename", KeyEvent.VK_R, position++);
    createQuickbutton("Inline", "r   Inline", KeyEvent.VK_I, position++);
    createQuickbutton("Extract Method", "r   Extract Method", KeyEvent.VK_M, position++);
    createQuickbutton("Extract Variable", "r   Extract Variable", KeyEvent.VK_V, position++);
    createQuickbutton("Delete Clutter", "r   Delete Clutter", KeyEvent.VK_D, position++);
  }

  private void addCustomCommitPart() {
    {
      // Commit Message:
      GridBagConstraints c = new GridBagConstraints();
      JLabel commitLabel = new JLabel("Commit Message:");
      c.anchor = GridBagConstraints.FIRST_LINE_START;
      c.insets = new Insets(10, 10, 0, 0);
      c.gridx = 1;
      c.gridwidth = 1;
      c.gridy = 1;
      panel.add(commitLabel, c);
    }
    JTextField prompt;
    {
      // [Commit Message Text]
      GridBagConstraints c = new GridBagConstraints();
      prompt = new JTextField("");
      prompt.setAction(
          new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
              doCommit(prompt.getText());
            }
          });
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.NORTHWEST;
      c.insets = new Insets(10, 10, 0, 0);
      c.gridx = 2;
      c.gridwidth = 3;
      c.gridy = 1;
      panel.add(prompt, c);
    }
    {
      // [Commit]
      var gridBagConstraints = new GridBagConstraints();
      JButton commit = new JButton("Commit");
      commit.setMnemonic(KeyEvent.VK_C);
      commit.setDefaultCapable(true);
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new Insets(10, 10, 0, 0);
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridy = 1;
      commit.addActionListener(e -> doCommit(prompt.getText()));
      panel.add(commit, gridBagConstraints);
    }
  }

  private void addHelpText(String text, int position) {
    addHelpText(text, position, false);
  }

  private void addHelpText2(String text, int position) {
    addHelpText2(text, position, false);
  }

  private void addHelpText(String text, int position, boolean first) {
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

  public void doCommit(String text) {
    this.message = text;
    this.done = true;
    Container parent = panel.getParent();
    while (!(parent instanceof JFrame)) {
      parent = parent.getParent();
    }
    ((JFrame) parent).dispose();
  }

  public static String display() {
    ArlosGitNotationPrompt panel = new ArlosGitNotationPrompt();
    openInFrame(panel);
    while (!panel.done) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {

      }
    }
    return panel.message;
  }

  private static void openInFrame(ArlosGitNotationPrompt panel) {
    JFrame test = new JFrame("Commit...");
    test.getContentPane().add(panel.panel);
    test.pack();

    Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    Dimension w = test.getSize();
    int dx = (int) w.getWidth();
    int dy = (int) w.getHeight();
    int x = (int) ((d.getWidth() - dx) / 2);
    int y = (int) ((d.getHeight() - dy) / 2);
    test.setBounds(x, y, dx, dy + 1);

    test.setVisible(true);
  }

  public JPanel getPanel() {
    return panel;
  }
}
