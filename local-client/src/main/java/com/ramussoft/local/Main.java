package com.ramussoft.local;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ramussoft.common.AccessRules;
import com.ramussoft.common.Engine;
import com.ramussoft.common.Plugin;
import com.ramussoft.gui.common.GUIFramework;
import com.ramussoft.gui.common.GUIPlugin;
import com.ramussoft.gui.core.AboutDialog;
import com.ramussoft.local.ui.MenuSearch;
import com.ramussoft.local.ui.RamusTheme;

public class Main extends Runner {

    public static void main(String[] args) {
        // T3.1: install FlatLaf before any UI is created.
        // Theme can be selected with -Dramus.theme=dark (default: light).
        try {
            boolean dark = "dark".equalsIgnoreCase(
                    System.getProperty("ramus.theme", "light"));
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

    private void openFromSystem(File file) {
        openedBySystem = true;
        SwingUtilities.invokeLater(() -> {
            if (launcher != null && launcher.isVisible())
                launcher.dispose();
        });
        new Thread(() -> open(file)).start();
    }

    @Override
    protected void postShowVisibaleMainFrame(Engine engine, GUIFramework framework) {
        super.postShowVisibaleMainFrame(engine, framework);

        // T5.3: global Ctrl+K command palette (menu search); install() guards
        // against repeated calls.
        MenuSearch.install(framework.getMainFrame());

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
