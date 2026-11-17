package com.ramussoft.local.ui;

import javax.swing.UIManager;

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
        // app correctly in both themes (see docs/baseline/t32_*.png). This
        // method exists so that future tokens have a single place to live
        // and are re-applied on every runtime theme switch.
    }
}
