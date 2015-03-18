package com.vaadin.components.grid.utils;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.widgets.Grid;
import com.vaadin.components.grid.GridComponent;
import com.vaadin.components.grid.data.GridDataSource;

/**
 * Right now we need to notify the grid for size changed.
 */
public class Redraw extends Timer {
    private final Grid<?> grid;
    private Element container;
    private int defaultSize, size;

    public Redraw(GridComponent gridComponent) {
        grid = gridComponent.getGrid();
    }

    public void setContainer(Element containerElement) {
        this.container = containerElement;
        redraw();
    }

    public void redraw() {
        schedule(50);
    }

    public void run() {
        if (defaultSize == 0) {
            defaultSize = size = (int) grid.getHeightByRows();
        }
        // Setting grid to 100% makes it fit to our v-grid container
        grid.setWidth("100%");
        grid.resetSizesFromDom();
        grid.recalculateColumnWidths();

        // Let see if our container has a fixed css height
        int vgridHeight = $(container).height();
        int gridHeight = $(grid).height();
        if (vgridHeight != gridHeight && vgridHeight > 0) {
            grid.setHeight(vgridHeight + "px");
        } else {
            // Check if data-source size is smaller than grid
            // visible rows, and reduce height
            // TODO: this should be done using setHeightByRows, but
            // it has performance issues
            GridDataSource ds = (GridDataSource) grid.getDataSource();

            if (ds != null) {
                int nsize = Math.min(ds.size(), defaultSize);
                if (nsize != size) {
                    size = nsize;
                    int h = $(grid).find("tr td").height() + 2;
                    double s = h * (size
                                    + grid.getHeaderRowCount()
                                    + grid.getFooterRowCount());
                    grid.setHeight(s + "px");
                }
            }
        }
    }

    public void setSize(int size) {
        if (size != defaultSize) {
            defaultSize = size;
            redraw();
        }
    }
    public int getSize() {
        return defaultSize;
    }
}
