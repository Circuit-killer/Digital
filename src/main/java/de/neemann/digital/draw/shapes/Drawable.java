package de.neemann.digital.draw.shapes;

import de.neemann.digital.draw.graphics.Graphic;
import de.neemann.digital.draw.graphics.Style;

/**
 * Interface implemented by the elements which can draw itself to a {@link Graphic} instance.
 *
 * @author hneemann
 */
public interface Drawable {
    /**
     * Draws an element depending on its state.
     *
     * @param graphic   interface to draw to
     * @param highLight null means no highlighting at all. If highlight is not null, highlight is active.
     *                  The given style should be used to highlight the drawing.
     */
    void drawTo(Graphic graphic, Style highLight);
}
