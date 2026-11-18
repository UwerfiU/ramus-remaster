package com.ramussoft.local.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ramussoft.gui.common.GlobalResourcesManager;

/**
 * T5.3: global Ctrl+K command palette over the main menu bar.
 */
public class MenuSearch {

    private static volatile boolean installed = false;

    /** One searchable menu entry (a leaf {@link JMenuItem}). */
    private static final class Entry {
        final JMenuItem item;
        final String text;
        final String path;
        final String accelerator;

        Entry(JMenuItem item, String text, String path, String accelerator) {
            this.item = item;
            this.text = text;
            this.path = path;
            this.accelerator = accelerator;
        }
    }

    /**
     * Binds Ctrl+K on the given frame (once per JVM).
     */
    public static void install(final JFrame frame) {
        if (installed || frame == null)
            return;
        installed = true;
        SwingUtilities.invokeLater(() -> {
            final int mask = Toolkit.getDefaultToolkit()
                    .getMenuShortcutKeyMaskEx();
            JComponent rootPane = frame.getRootPane();
            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_K, mask),
                    "ramus.menuSearch");
            rootPane.getActionMap().put("ramus.menuSearch",
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            open(frame);
                        }
                    });
        });
    }

    private static void open(JFrame frame) {
        final String title = getString("menu.search.title",
                "Search commands\u2026");
        final String emptyText = getString("menu.search.empty",
                "No matching commands");

        final List<Entry> all = new ArrayList<>();
        JMenuBar bar = frame.getJMenuBar();
        if (bar != null)
            for (Component c : bar.getComponents())
                if (c instanceof JMenu menu)
                    collect(menu, menu.getText(), all);

        final DefaultListModel<Entry> model = new DefaultListModel<>();
        @SuppressWarnings("unchecked")
        final JList<Entry> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l,
                    Object value, int index, boolean selected, boolean focus) {
                Entry e = (Entry) value;
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

                JLabel text = new JLabel(e.text);
                text.setFont(text.getFont().deriveFont(Font.BOLD));
                JLabel accel = new JLabel(e.accelerator);
                int pathMax = ROW_MAX_WIDTH - 20 /* insets */ - 16 /* gaps */
                        - text.getPreferredSize().width
                        - (e.accelerator.isEmpty() ? 0
                                : accel.getPreferredSize().width + 8);
                JLabel path = new JLabel(ellipsis(e.path, pathMax,
                        new JLabel().getFont()));

                if (selected) {
                    row.setBackground(l.getSelectionBackground());
                    text.setForeground(l.getSelectionForeground());
                    path.setForeground(l.getSelectionForeground());
                    accel.setForeground(l.getSelectionForeground());
                } else {
                    row.setBackground(l.getBackground());
                    text.setForeground(l.getForeground());
                    Color dim = new Color(128, 128, 128);
                    path.setForeground(dim);
                    accel.setForeground(dim);
                }

                row.add(text, BorderLayout.WEST);
                row.add(path, BorderLayout.CENTER);
                if (!e.accelerator.isEmpty())
                    row.add(accel, BorderLayout.EAST);
                return row;
            }
        });

        final JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);

        final JLabel emptyLabel = new JLabel(emptyText, JLabel.CENTER);
        emptyLabel.setFont(emptyLabel.getFont().deriveFont(Font.ITALIC));
        emptyLabel.setForeground(new Color(128, 128, 128));

        final JPanel center = new JPanel(new CardLayout());
        center.add(scroll, "list");
        center.add(emptyLabel, "empty");

        final JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(496, 36));

        final JDialog dialog = new JDialog(frame, title, false);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(frame);
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(field, BorderLayout.NORTH);
        content.add(center, BorderLayout.CENTER);
        dialog.setContentPane(content);

        final Runnable[] fire = new Runnable[1];
        fire[0] = () -> {
            Entry e = list.getSelectedValue();
            if (e == null)
                return;
            dialog.dispose();
            Action action = e.item.getAction();
            if (action != null)
                action.actionPerformed(new ActionEvent(e.item,
                        ActionEvent.ACTION_PERFORMED, ""));
            else
                e.item.doClick();
        };

        field.addActionListener(e -> fire[0].run());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && list.locationToIndex(
                        e.getPoint()) >= 0)
                    fire[0].run();
            }
        });
        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ramus.menuSearch.close");
        dialog.getRootPane().getActionMap().put("ramus.menuSearch.close",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMatches(field, model, all, list, center);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMatches(field, model, all, list, center);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMatches(field, model, all, list, center);
            }
        };
        field.getDocument().addDocumentListener(docListener);
        updateMatches(field, model, all, list, center); // initial fill

        dialog.setVisible(true);
        field.requestFocusInWindow();
    }

    /** Max preferred width of a row; keeps the list inside the dialog. */
    private static final int ROW_MAX_WIDTH = 470;

    /** Truncates s with an ellipsis so its rendered width is at most maxWidth. */
    private static String ellipsis(String s, int maxWidth, Font font) {
        if (s == null || maxWidth <= 20)
            return "";
        java.awt.FontMetrics fm = Toolkit.getDefaultToolkit()
                .getFontMetrics(font);
        if (fm.stringWidth(s) <= maxWidth)
            return s;
        String ell = "\u2026";
        int limit = maxWidth - fm.stringWidth(ell);
        int len = s.length();
        while (len > 0 && fm.stringWidth(s.substring(0, len)) > limit)
            len--;
        return s.substring(0, len) + ell;
    }

    /** Re-filters the list by the current query and shows it (or the placeholder). */
    private static void updateMatches(JTextField field,
            DefaultListModel<Entry> model, List<Entry> all, JList<Entry> list,
            JPanel content) {
        String q = field.getText().toLowerCase(Locale.ROOT);
        model.clear();
        for (Entry e : all)
            if (e.text.toLowerCase(Locale.ROOT).contains(q))
                model.addElement(e);
        CardLayout cards = (CardLayout) content.getLayout();
        if (model.isEmpty()) {
            cards.show(content, "empty");
        } else {
            cards.show(content, "list");
            list.setSelectedIndex(0);
            list.ensureIndexIsVisible(0);
        }
    }

    /** Recursively collects enabled, non-empty leaf items of a menu. */
    private static void collect(JMenu menu, String prefix, List<Entry> out) {
        for (Component c : menu.getMenuComponents()) {
            if (!(c instanceof JMenuItem item))
                continue; // separators and the like
            if (!item.isEnabled() || item.getText() == null
                    || item.getText().isEmpty())
                continue;
            String path = prefix.isEmpty() ? item.getText()
                    : prefix + " \u2192 " + item.getText();
            if (item instanceof JMenu sub) {
                collect(sub, path, out);
            } else {
                out.add(new Entry(item, item.getText(), path,
                        describeAccelerator(acceleratorOf(item))));
            }
        }
    }

    /** Accelerator of a menu item: own one or the one from its Action. */
    private static KeyStroke acceleratorOf(JMenuItem item) {
        KeyStroke ks = item.getAccelerator();
        if (ks == null && item.getAction() != null)
            ks = (KeyStroke) item.getAction().getValue(Action.ACCELERATOR_KEY);
        return ks;
    }

    private static String describeAccelerator(KeyStroke ks) {
        if (ks == null)
            return "";
        StringBuilder sb = new StringBuilder();
        int m = ks.getModifiers();
        if ((m & InputEvent.CTRL_DOWN_MASK) != 0)
            sb.append("Ctrl");
        if ((m & InputEvent.ALT_DOWN_MASK) != 0)
            append(sb, "Alt");
        if ((m & InputEvent.SHIFT_DOWN_MASK) != 0)
            append(sb, "Shift");
        append(sb, KeyEvent.getKeyText(ks.getKeyCode()));
        return sb.toString();
    }

    private static void append(StringBuilder sb, String part) {
        if (sb.length() > 0)
            sb.append('+');
        sb.append(part);
    }

    private static String getString(String key, String fallback) {
        String s = GlobalResourcesManager.getString(key);
        return s == null ? fallback : s;
    }
}
