package brainventorypackage;

// UiTheme: central place for fonts, colors and spacing constants
// Improves consistency across panels and components.

import java.awt.*;

public final class UiTheme {
    private UiTheme() {}

    // Colors
    public static final Color BG = new Color(245,246,248);
    public static final Color PRIMARY = new Color(33,36,43);
    public static final Color CARD = Color.WHITE;

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FORM_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font TIMER_FONT = new Font("Segoe UI", Font.BOLD, 56);
    public static final Font MONO_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // Spacing
    public static final int PANEL_PADDING = 16;
    public static final int CARD_RADIUS = 20;
}
