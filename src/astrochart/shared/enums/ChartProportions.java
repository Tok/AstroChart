package astrochart.shared.enums;

public enum ChartProportions {
	Outer(1.00D),
	HouseNumber(0.98D),
	OuterEclyptic(0.95D),
	EclypticCenter(0.85D),
	InnerEclyptic(0.75D),
	OuterMark(0.72D),
	InnerMark(0.70D),
	PlanetMark(0.66D),
	PlanetSign(0.62D),
	Degree(0.55D),
	Minute(0.48D),
	InnerLine(0.44D),
	Inner(0.40D);
	
	private final double relativeRadius;
	
	private ChartProportions(final double relativeRadius) {
        this.relativeRadius = relativeRadius;
    }
	
    public final double getRelativeRadius() {
        return relativeRadius;
    }

	public static final int getRadius(final int halfChartSize, final ChartProportions prop) {
		return (int) (halfChartSize * prop.getRelativeRadius());
	}

    @Override
    public final String toString() {
        return this.name();
    }
}
