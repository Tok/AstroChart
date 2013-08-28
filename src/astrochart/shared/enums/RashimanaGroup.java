package astrochart.shared.enums;

/**
 * compare: http://www.vedicastro.com/astronomy6.asp
 */
public enum RashimanaGroup {
    I(1674), II(1795), III(1931);

    private final int equatorialAsus;

    private RashimanaGroup(final int equatorialAsus) {
        this.equatorialAsus = equatorialAsus;
    }

    public final int getEquatorialAsus() {
        return equatorialAsus;
    }
}
