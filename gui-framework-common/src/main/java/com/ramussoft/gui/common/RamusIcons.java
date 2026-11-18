package com.ramussoft.gui.common;

import java.awt.Component;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.formdev.flatlaf.extras.FlatSVGIcon;

/**
 * Central icon provider (T5.1).
 * <p>
 * Modern icons are flat vector assets in {@code /icons/modern/<name>.svg}
 * (Tabler Icons, MIT license) rendered through {@link FlatSVGIcon}, which
 * follows the current FlatLaf theme (light/dark) and disabled state.
 * If a modern asset is missing, the legacy raster resource from
 * {@link #LEGACY} is used as a fallback so nothing ever renders empty.
 */
public final class RamusIcons {

    /** Fallback: icon name -> legacy raster resource path. */
    private static final Map<String, String> LEGACY = new HashMap<>();

    private static final Map<String, Icon> CACHE = new ConcurrentHashMap<>();

    static {
        put("new", "/com/ramussoft/gui/new.png");
        put("open", "/com/ramussoft/gui/open.png");
        put("save", "/images/save.png");
        put("import", "/com/ramussoft/gui/import.png");
        put("export", "/com/ramussoft/gui/export.png");
        put("excel-import", "/com/ramussoft/excel/import-from-excel.png");
        put("excel-export", "/com/ramussoft/excel/export-to-excel.png");
        put("print", "/com/ramussoft/gui/print.png");
        put("print-preview", "/com/ramussoft/gui/print-preview.png");
        put("page-setup", "/com/ramussoft/gui/page-setup.png");
        put("pdf", "/print/to/pdf/icon.jpg");
        put("edit", "/images/edit.png");
        put("undo", "/images/iUndo.png");
        put("redo", "/images/iRedo.png");
        put("cut", "/images/cut.png");
        put("copy", "/images/copy.png");
        put("paste", "/images/paste.png");
        put("delete", "/com/ramussoft/gui/table/delete.png");
        put("revert", "/com/ramussoft/gui/revert.png");
        put("refresh", "/images/refresh.png");
        put("recalculate", "/com/ramussoft/gui/table/recalculate.png");
        put("back", "/com/dsoft/pb/master/icons/back.png");
        put("next", "/com/dsoft/pb/master/icons/next.png");
        put("finish", "/com/dsoft/pb/master/icons/finish.png");
        put("cancel", "/com/dsoft/pb/master/icons/cancel.png");
        put("configure", "/com/dsoft/pb/master/icons/configure.png");
        put("go-back", "/images/go_back.png");
        put("go-forward", "/images/go_forward.png");
        put("go-home", "/images/go_home.png");
        put("left", "/images/left.png");
        put("right", "/images/right.png");
        put("up", "/images/up.gif");
        put("down", "/images/down.gif");
        put("top", "/images/top.png");
        put("bottom", "/images/bottom.png");
        put("small-up", "/images/smallUp.gif");
        put("small-down", "/images/smallDown.gif");
        put("close", "/com/ramussoft/gui/table/close.gif");
        put("find-next", "/images/findnext_1.gif");
        put("add", "/images/add.png");
        put("add-child", "/images/add_child.png");
        put("check-all", "/images/check_all.gif");
        put("uncheck-all", "/images/uncheck_all.gif");
        put("collapse", "/com/ramussoft/gui/table/collapse.png");
        put("expand", "/com/ramussoft/gui/table/expand.png");
        put("sort-incr", "/com/ramussoft/gui/table/sort-incr.png");
        put("move-up", "/com/ramussoft/gui/table/move-up.png");
        put("move-down", "/com/ramussoft/gui/table/move-down.png");
        put("move-left", "/com/ramussoft/gui/table/move-left.png");
        put("move-right", "/com/ramussoft/gui/table/move-right.png");
        put("move-all-left", "/com/ramussoft/gui/table/move-all-left.png");
        put("move-all-right", "/com/ramussoft/gui/table/move-all-right.png");
        put("sheet", "/com/ramussoft/gui/table/sheet.png");
        put("folder", "/com/ramussoft/gui/table/folder.png");
        put("folder-sheet", "/com/ramussoft/gui/table/folder-sheet.png");
        put("table", "/com/ramussoft/report/table.png");
        put("formula", "/images/function.png");
        put("history", "/com/ramussoft/gui/table/history.png");
        put("qualifier", "/com/ramussoft/gui/table/qualifier.png");
        put("element-to-qualifier", "/com/ramussoft/gui/table/element-to-qualifier.png");
        put("qualifier-to-element", "/com/ramussoft/gui/table/qualifier-to-element.png");
        put("set-element-qualifier", "/com/ramussoft/gui/table/set-element-qualifier.png");
        put("copy-table-element", "/com/ramussoft/gui/table/copy-table-element-to-other-element.png");
        put("move-table-element", "/com/ramussoft/gui/table/move-table-element-to-other-element.png");
        put("join-elements", "/com/ramussoft/gui/table/join-elements.png");
        put("select-unconnected", "/com/ramussoft/gui/table/select-unconnected.png");
        put("edit-comment-branch", "/com/ramussoft/gui/table/edit-comment-branch.png");
        put("table-preferencies", "/com/ramussoft/gui/table/table-preferencies.png");
        put("table-insert-column", "/com/ramussoft/report/table-insert-column.png");
        put("main", "/images/main.gif");
        put("block", "/images/block.gif");
        put("function-el", "/images/function.png");
        put("data-store", "/images/data-store.gif");
        put("external-reference", "/images/external-reference.gif");
        put("role", "/images/role.png");
        put("roles", "/images/roles.png");
        put("net", "/images/net.png");
        put("note", "/images/note.png");
        put("text-el", "/images/text.gif");
        put("tilda", "/images/tilda.gif");
        put("classificator", "/images/clasificatorIcon.png");
        put("create-diagram", "/images/create-diagram.png");
        put("idef0-model", "/images/idef0-model.png");
        put("build", "/images/build.png");
        put("dropper", "/images/dropper.png");
        put("cursor", "/images/cursor.gif");
        put("pen-cursor", "/images/pen-cursor.png");
        put("eraser", "/images/sel_ather.png");
        put("insert", "/images/insert.png");
        put("arrow", "/images/arrow.gif");
        put("branch-actual", "/com/ramussoft/gui/branch-actual.png");
        put("branch-down", "/com/ramussoft/gui/branch-down.png");
        put("branch-down-right", "/com/ramussoft/gui/branch-down-right.png");
        put("about", "/com/ramussoft/gui/about.png");
        put("set-icon", "/com/ramussoft/gui/set-icon.png");
        put("open-element-list", "/com/ramussoft/gui/open-element-list.png");
        put("layout-col", "/com/ramussoft/gui/print/preview/previewLayoutCol.png");
        put("layout-grid", "/com/ramussoft/gui/print/preview/previewLayoutGrid.png");
        put("format", "/com/ramussoft/report/format.png");
        put("label", "/com/ramussoft/report/label.png");
    }

    private RamusIcons() {
    }

    private static void put(String name, String legacyPath) {
        LEGACY.put(name, legacyPath);
    }

    /** Standard (menu) icon size. */
    public static final int MENU = 16;
    /** Toolbar / palette icon size. */
    public static final int TOOLBAR = 24;

    public static Icon get(String name) {
        return get(name, MENU);
    }

    /**
     * Returns a theme-aware icon for the given modern icon name, falling back
     * to the legacy raster resource when no SVG asset exists.
     */
    public static Icon get(String name, int size) {
        Icon cached = CACHE.get(name + "/" + size);
        if (cached != null)
            return cached;
        Icon icon = loadModern(name, size);
        if (icon == null)
            icon = loadLegacy(name);
        if (icon == null)
            icon = new EmptyIcon(size);
        CACHE.put(name + "/" + size, icon);
        return icon;
    }

    private static Icon loadModern(String name, int size) {
        try {
            URL url = RamusIcons.class.getResource("/icons/modern/" + name + ".svg");
            if (url == null)
                return null;
            FlatSVGIcon svg = new FlatSVGIcon(url);
            return svg.derive(size, size);
        } catch (Exception e) {
            System.err.println("[Ramus] Failed to load modern icon: " + name);
            return null;
        }
    }

    private static Icon loadLegacy(String name) {
        String path = LEGACY.get(name);
        if (path == null)
            return null;
        URL url = RamusIcons.class.getResource(path);
        return url == null ? null : new ImageIcon(url);
    }

    /** Minimal placeholder so a missing icon never renders as null. */
    private static final class EmptyIcon implements Icon {
        private final int size;

        EmptyIcon(int size) {
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, java.awt.Graphics g, int x, int y) {
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
