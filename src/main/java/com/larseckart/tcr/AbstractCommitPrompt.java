package com.larseckart.tcr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

abstract class AbstractCommitPrompt {
  protected String message = "";
  protected boolean done = false;
  protected final JPanel panel = new JPanel();

  public AbstractCommitPrompt() {
    setLayout();
  }

  protected abstract void setLayout();

  protected void addCustomCommitPart() {
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
      commit.setMnemonic(java.awt.event.KeyEvent.VK_C);
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

  public void doCommit(String text) {
    this.message = text;
    this.done = true;
    Container parent = panel.getParent();
    while (!(parent instanceof JFrame)) {
      parent = parent.getParent();
    }
    ((JFrame) parent).dispose();
  }

  protected static void openInFrame(AbstractCommitPrompt panel, String title) {
    JFrame test = new JFrame(title);
    test.getContentPane().add(panel.panel);
    test.pack();

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
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