package com.ramussoft.gui.common;

import java.awt.Color;

import javax.swing.UIManager;

/**
 * T3.2: theme-aware color accessors for custom-painting code.
 *
 * <p>Ramus is a legacy Swing app with many components that draw backgrounds,
 * lines and icons themselves. Hardcoded {@code new Color(...)} values look
 * fine in the old Metal L&amp;F but break under FlatLaf dark theme. All such
 * drawing code should take its colors from this class instead: every accessor
 * reads the standard Swing/FlatLaf UIManager keys of the <em>current</em>
 * look-and-feel, so the same component renders correctly in both
 * {@code FlatLightLaf} and {@code FlatDarkLaf} (and in the legacy L&amp;F
 * fallback), including after a runtime theme switch.</p>
 *
 * <p>App-level flatlaf.* design tokens are centralized in
 * {@code com.ramussoft.local.ui.RamusTheme} (local-client module, which is
 * not visible from this one).</p>
 */
public final class ThemeColors {

    private ThemeColors() {
    }

    /** Primary text color: labels and custom-drawn lines/icons. */
    public static Color text() {
        return get("Label.foreground", Color.BLACK);
    }

    /** Background of regular panels. */
    public static Color panelBackground() {
        return get("Panel.background", new Color(243, 244, 245));
    }

    /** Focus/selection accent: editor borders, highlights. */
    public static Color focus() {
        return get("Component.focusColor", new Color(0x26, 0x75, 0xBF));
    }

    /** Selection background: pressed-state fills. */
    public static Color selectionBackground() {
        return get("Table.selectionBackground", new Color(0x26, 0x75, 0xBF));
    }

    /** Text color on top of {@link #selectionBackground()}. */
    public static Color selectionForeground() {
        return get("Table.selectionForeground", Color.WHITE);
    }

    /**
     * Panel background with the given alpha (0-255), for dimming veils drawn
     * over live content (loading overlays, scroll previews).
     */
    public static Color overlay(int alpha) {
        Color c = panelBackground();
        int a = Math.max(0, Math.min(255, alpha));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    private static Color get(String key, Color fallback) {
        Color c = UIManager.getColor(key);
        return c != null ? c : fallback;
    }
}
