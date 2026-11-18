package com.ramussoft.local.ui;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

/**
 * T3.2: centralized design tokens for the FlatLaf-based UI.
 *
 * <p>{@link #apply()} must be called right after every
 * {@code UIManager.setLookAndFeel(...)} — at startup in
 * {@code Main.main()} and on each runtime theme switch in
 * {@code ThemeTogglePlugin} — because {@code setLookAndFeel} resets all
 * previously put properties.</p>
 *
 * <p>Policy: FlatLaf's built-in light/dark palettes are already well
 * balanced, so this class only adjusts what is really off in this legacy
 * app. Do not add tokens "for symmetry" — every property here must fix an
 * observed problem.</p>
 *
 * <p>Theme-aware colors used by custom-painting code (panels, tables, tab
 * close icons, ...) are centralized in
 * {@code com.ramussoft.gui.common.ThemeColors} (gui-framework-common), since
 * most painting code lives in modules that cannot see local-client.</p>
 */
public final class RamusTheme {

    private RamusTheme() {
    }

    /**
     * Applies the Ramus design tokens on top of the current FlatLaf theme.
     * The active look-and-feel must already be installed.
     */
    public static void apply() {
        if (!(UIManager.getLookAndFeel() instanceof FlatLaf)) {
            // Legacy L&F fallback (FlatLaf unavailable): nothing to tune.
            return;
        }

        // NOTE: as of T3.2 no flatlaf.* override is needed — the default
        // FlatLight/FlatDark palettes, fonts and control sizes render this
        // app correctly in both themes (see docs/baseline/t32_*.png).

        // T5.2: "airier" metrics on top of the built-in sizes. Every key
        // below was verified to exist in FlatLaf 3.8's defaults table
        // (UIManager.getLookAndFeel().getDefaults()); values are modest
        // bumps over the built-in ones, identical for both themes.
        UIManager.put("Button.margin", new Insets(4, 16, 4, 16));
        // roomier push buttons in dialogs and option panes (toolbar buttons
        // use the separate Button.toolbar.margin key and stay compact)
        UIManager.put("MenuItem.margin", new Insets(5, 8, 5, 8));
        // more breathing room between menu rows in this app's deep menus
        UIManager.put("MenuBar.itemMargins", new Insets(5, 10, 5, 10));
        // airier top-level menu bar
        UIManager.put("TextField.margin", new Insets(4, 8, 4, 8));
        // more padding inside text fields and other single-line inputs
        UIManager.put("TabbedPane.tabInsets", new Insets(6, 14, 6, 14));
        // roomier diagram/model tabs (the main workspace is tab-heavy)
        UIManager.put("Table.cellMargins", new Insets(4, 6, 4, 6));
        // taller table rows in attribute/entity tables
        UIManager.put("List.cellMargins", new Insets(3, 8, 3, 8));
        // roomier list and combo-drop-down rows
        UIManager.put("ToolBar.borderMargins", new Insets(4, 4, 4, 4));
        // uniform padding around all toolbars for a consistent look

        // T3.3: SwingX JXHeader (MainPanel wizard) paints a hardcoded
        // white->control gradient in BasicHeaderUI; under FlatDarkLaf the
        // light title/description text becomes unreadable on it. Point the
        // gradient at theme-aware colors; the light theme keeps the SwingX
        // defaults (verified in docs/baseline/t33_*).
        if (UIManager.getLookAndFeel() instanceof FlatDarkLaf) {
            Color headerBg = UIManager.getColor("Panel.background");
            UIManager.put("JXHeader.startBackground", headerBg);
            UIManager.put("JXHeader.background", headerBg);
        }
    }
}
