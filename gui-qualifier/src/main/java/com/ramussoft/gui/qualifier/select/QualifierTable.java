package com.ramussoft.gui.qualifier.select;


import com.ramussoft.gui.common.RamusIcons;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.JXTree.DelegatingRenderer;

public class QualifierTable extends JXTreeTable {

    /**
     *
     */
    private static final long serialVersionUID = 78357925573817284L;

    private Icon leafIcon = RamusIcons.get("qualifier");

    private Icon folderIcon = RamusIcons.get("folder");

    private Icon folderSheetIcon = RamusIcons.get("folder-sheet");

    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {

        JTree tree = (JTree) super.getCellRenderer(row, column);
        DelegatingRenderer rend = ((DelegatingRenderer) tree.getCellRenderer());

        rend.setLeafIcon(leafIcon);
        rend.setOpenIcon(folderIcon);
        rend.setClosedIcon(folderSheetIcon);

        return (TableCellRenderer) tree;
    }

}
