package com.ramussoft.local;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.ramussoft.gui.common.AbstractViewPlugin;
import com.ramussoft.gui.common.ActionDescriptor;
import com.ramussoft.gui.common.ActionLevel;
import com.ramussoft.gui.common.GlobalResourcesManager;
import com.ramussoft.local.ui.RamusTheme;

/**
 * T3.1: View menu item that switches between FlatLaf light and dark themes
 * at runtime. Minimal implementation, no settings persistence.
 */
public class ThemeTogglePlugin extends AbstractViewPlugin {

    @Override
    public String getName() {
        return "ThemeToggle";
    }

    @Override
    public String getString(String key) {
        try {
            return GlobalResourcesManager.getString(key);
        } catch (NullPointerException e) {
            return key;
        }
    }

    @Override
    public ActionDescriptor[] getActionDescriptors() {
        ActionDescriptor ad = new ActionDescriptor();
        ad.setActionLevel(ActionLevel.GLOBAL);
        ad.setMenu("View/SwitchTheme");

        AbstractAction action = new AbstractAction(
                getString("Menu.SwitchTheme")) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        };

        ad.setAction(action);
        return new ActionDescriptor[] { ad };
    }

    /**
     * Switches between FlatLightLaf and FlatDarkLaf and refreshes all open
     * windows.
     */
    public static void toggleTheme() {
        try {
            boolean toDark = !(UIManager.getLookAndFeel() instanceof FlatDarkLaf);
            UIManager.setLookAndFeel(toDark ? new FlatDarkLaf() : new FlatLightLaf());
            // T5.2: setLookAndFeel resets put properties — keep the custom
            // window decorations (Windows/Linux only; macOS stays native).
            if (!System.getProperty("os.name", "").toLowerCase().contains("mac")) {
                UIManager.put("flatlaf.windowDecorations", true);
            }
            // T3.2: re-apply design tokens (setLookAndFeel resets them).
            RamusTheme.apply();
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
