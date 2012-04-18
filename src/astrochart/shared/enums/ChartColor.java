package astrochart.shared.enums;

public enum ChartColor {
    Black("#000000"),
    White("#FFFFFF"),
    Red("#ED1C24"),
    Blue("#3F48CC"),
    Yellow("#FFF200"),
    Green("#22B14C");

    private final String hex;

    private ChartColor(final String hex) {
        this.hex = hex;
    }

    public final String getHex() {
        return hex;
    }

    public final String toString() {
        return name();
    }
}
