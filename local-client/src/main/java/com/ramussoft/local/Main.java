package com.ramussoft.local;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ramussoft.common.AccessRules;
import com.ramussoft.common.Engine;
import com.ramussoft.common.Plugin;
import com.ramussoft.gui.common.GUIFramework;
import com.ramussoft.gui.common.GUIPlugin;
import com.ramussoft.gui.common.GlobalResourcesManager;
import com.ramussoft.gui.common.prefrence.Options;
import com.ramussoft.gui.core.AboutDialog;
import com.ramussoft.local.ui.MenuSearch;
import com.ramussoft.local.ui.RamusTheme;

public class Main extends Runner {

    public static void main(String[] args) {
        // T3.1: install FlatLaf before any UI is created.
        // Theme: -Dramus.theme=... overrides; otherwise the last used theme
        // is remembered in options.conf (T5.9); default: light.
        try {
            String themeProp = System.getProperty("ramus.theme");
            boolean dark = themeProp != null
                    ? "dark".equalsIgnoreCase(themeProp)
                    : "dark".equalsIgnoreCase(
                            Options.getString("Theme", "light"));
            UIManager.setLookAndFeel(dark ? new FlatDarkLaf() : new FlatLightLaf());
            // T5.2: custom FlatLaf window title bars on Windows/Linux; macOS
            // keeps its native frame and screen menu bar.
            if (!System.getProperty("os.name", "").toLowerCase().contains("mac")) {
                UIManager.put("flatlaf.windowDecorations", true);
            }
            // T3.2: apply centralized design tokens after the L&F is set.
            RamusTheme.apply();
        } catch (Exception e) {
            // Fallback: system look-and-feel if FlatLaf is unavailable.
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep the default L&F.
            }
        }

        // Set macOS-specific system properties
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "Ramus");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Ramus MacOS");
        }

        Main main = new Main();
        if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                .isSupported(Desktop.Action.APP_OPEN_FILE)) {
            Desktop.getDesktop().setOpenFileHandler(e -> {
                for (File file : e.getFiles())
                    main.openFromSystem(file);
            });
        }
        main.load(args);
    }

    @Override
    protected void initAdditionalGUIPlugins(List<GUIPlugin> list,
                                            Engine engine, AccessRules rules) {
        super.initAdditionalGUIPlugins(list, engine, rules);
        // T3.1: light/dark theme switcher in the View menu.
        list.add(new ThemeTogglePlugin());
    }

    /**
     * T5.4: adds a localized "Recent files" submenu to the File menu (the
     * first JMenu of the main frame) with up to 10 entries from the file
     * history; missing or unreadable files are disabled.
     */
    private void installRecentFilesMenu(JFrame frame) {
        JMenuBar bar = frame.getJMenuBar();
        if (bar == null || bar.getComponentCount() < 1)
            return;
        Object first = bar.getComponent(0);
        if (!(first instanceof JMenu))
            return;
        String title = GlobalResourcesManager.getString("file.menu.recent");
        JMenu fileMenu = (JMenu) first;
        // JMenu keeps its entries in the popup, not in its own container
        JPopupMenu items = fileMenu.getPopupMenu();
        for (int i = 0; i < items.getComponentCount(); i++) {
            if (items.getComponent(i) instanceof JMenu
                    && title.equals(((JMenu) items.getComponent(i)).getText()))
                return;
        }
        String[] files = Runner.getLastOpenedFiles();
        JMenu recent = new JMenu(title);
        int count = 0;
        for (String path : files) {
            if (count++ >= 10 || path == null)
                break;
            File file = new File(path);
            JMenuItem item = new JMenuItem(file.getName());
            item.setToolTipText(path);
            if (!file.exists() || !file.canRead()) {
                item.setEnabled(false);
            } else {
                final File target = file;
                item.addActionListener(e -> new Thread(() -> open(target))
                        .start());
            }
            recent.add(item);
        }
        if (recent.getItemCount() > 0) {
            // insert right after the "Open" item; fall back to appending
            int index = -1;
            for (int i = 0; i < items.getComponentCount(); i++) {
                Object c = items.getComponent(i);
                if (c instanceof JMenuItem
                        && ((JMenuItem) c).getAction() != null
                        && "FileOpen"
                                .equals(((JMenuItem) c).getAction()
                                        .getValue(Action.ACTION_COMMAND_KEY))) {
                    index = i;
                    break;
                }
            }
            if (index >= 0)
                items.insert(recent, index + 1);
            else
                items.add(recent);
        }
    }

    private void openFromSystem(File file) {
        openedBySystem = true;
        SwingUtilities.invokeLater(() -> {
            if (launcher != null && launcher.isVisible())
                launcher.dispose();
        });
        new Thread(() -> open(file)).start();
    }

    private void installThemeMenu(JFrame frame) {
        JMenuBar bar = frame.getJMenuBar();
        if (bar == null)
            return;
        String viewName = GlobalResourcesManager.getString("Menu.View");
        for (int i = 0; i < bar.getMenuCount(); i++) {
            JMenu menu = bar.getMenu(i);
            if (viewName != null && !viewName.equals(menu.getText()))
                continue;
            String themeText = GlobalResourcesManager.getString("SwitchTheme");
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem existing = menu.getItem(j);
                if (existing != null && themeText != null
                        && themeText.equals(existing.getText()))
                    return; // already installed (repeated call)
            }
            AbstractAction action = new AbstractAction(themeText) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ThemeTogglePlugin.toggleTheme();
                }
            };
            // JMenu has no indexed add: append first, then move to the top.
            final JMenuItem item = new JMenuItem(action);
            final javax.swing.JSeparator separator = new javax.swing.JSeparator();
            final java.awt.Container c = menu;
            c.add(item);
            c.add(separator);
            c.remove(item);
            c.remove(separator);
            // AWT-style indexed add: (component, position)
            c.add(separator, 0);
            c.add(item, 0);
            return;
        }
    }

    protected void postShowVisibaleMainFrame(Engine engine, GUIFramework framework) {
        super.postShowVisibaleMainFrame(engine, framework);

        // T5.3: global Ctrl+K command palette (menu search); install() guards
        // against repeated calls.
        MenuSearch.install(framework.getMainFrame());

        // T5.4: "Recent files" submenu in the File menu; guarded against
        // repeated installation on the same frame.
        installRecentFilesMenu(framework.getMainFrame());

        // T5.9: "Switch light/dark theme" as a direct item at the top of the
        // View menu (framework two-segment menu paths create submenus).
        installThemeMenu(framework.getMainFrame());

        // Properly wire the macOS About menu to the app's About dialog
        if (Desktop.isDesktopSupported() &&
            System.getProperty("os.name", "").toLowerCase().contains("mac")) {
            try {
                Desktop.getDesktop().setAboutHandler(e -> {
                    @SuppressWarnings("unchecked")
                    List<Plugin> corePlugins = (List<Plugin>) engine.getPluginProperty("Core", "PluginList");
                    @SuppressWarnings("unchecked")
                    List<GUIPlugin> guiPlugins = (List<GUIPlugin>) engine.getPluginProperty("GUI", "PluginList");

                    AboutDialog dialog = new AboutDialog(
                            framework.getMainFrame(), corePlugins, guiPlugins);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setVisible(true);
                    dialog.dispose();
                });
            } catch (UnsupportedOperationException ex) {
                System.err.println("macOS About handler unsupported: " + ex.getMessage());
            } catch (Throwable t) {
                System.err.println("Failed to set macOS About handler: " + t.getMessage());
            }
        }
    }
}
