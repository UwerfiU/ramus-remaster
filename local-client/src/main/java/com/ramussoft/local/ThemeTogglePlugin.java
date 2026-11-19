package com.ramussoft.local;

import java.awt.Component;
import java.awt.Container;
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
import com.ramussoft.gui.common.prefrence.Options;
import com.ramussoft.local.ui.RamusTheme;
import com.ramussoft.pb.idef.visual.MovingArea;

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
        // T5.9: no menu path here — Main.installThemeMenu() adds the item
        // directly to the View menu (framework two-segment paths create
        // submenus, and single-segment "View" clashes with the TreeList
        // created by other plugins' "View/..." paths). The ACTION_COMMAND_KEY
        // lets the framework resolve the localized name; without it the
        // label would be overwritten with the literal "Null".

        AbstractAction action = new AbstractAction(
                getString("SwitchTheme")) {
            {
                putValue(ACTION_COMMAND_KEY, "SwitchTheme");
            }

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
                invalidateCanvasCaches(w);
            }
            // T5.9: remember the theme across restarts (options.conf).
            Options.setString("Theme", toDark ? "dark" : "light");
            Options.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * T5.9: diagram canvases cache their painted background in an image;
     * drop the caches so they repaint with the new theme's paper color.
     */
    private static void invalidateCanvasCaches(Component c) {
        if (c instanceof MovingArea) {
            ((MovingArea) c).invalidateBackgroundCache();
            return;
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents())
                invalidateCanvasCaches(child);
        }
    }
}
