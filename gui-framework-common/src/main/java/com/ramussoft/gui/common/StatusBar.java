package com.ramussoft.gui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * T5.6: a minimal status bar for the bottom of the main window: context text
 * on the left (e.g. the open file name) and app info on the right. Colors are
 * taken from UIManager so both FlatLaf themes render it correctly.
 */
public class StatusBar extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel statusLabel = new JLabel("");

    private final JLabel infoLabel = new JLabel(appVersion());

    /** "Ramus v<version>" from the jar manifest (Implementation-Version);
     *  falls back to plain "Ramus" when running without a versioned jar. */
    private static String appVersion() {
        try {
            Package p = StatusBar.class.getPackage();
            String v = (p == null) ? null : p.getImplementationVersion();
            return (v == null || v.isEmpty()) ? "Ramus" : "Ramus v" + v;
        } catch (Throwable t) {
            return "Ramus";
        }
    }

    public StatusBar() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(UIManager.getColor("MenuBar.background"));
        Color line = UIManager.getColor("Separator.foreground");
        if (line != null)
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, line));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN));
        infoLabel.setOpaque(false);
        Color dim = UIManager.getColor("Label.disabledForeground");
        if (dim != null)
            infoLabel.setForeground(dim);
        add(statusLabel, BorderLayout.WEST);
        add(infoLabel, BorderLayout.EAST);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, 24);
    }

    public void setStatusText(String text) {
        statusLabel.setText(text == null ? "" : text);
    }

    public String getStatusText() {
        return statusLabel.getText();
    }

    public void setInfoText(String text) {
        infoLabel.setText(text == null ? "" : text);
    }
}
