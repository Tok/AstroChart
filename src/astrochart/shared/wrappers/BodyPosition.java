package astrochart.shared.wrappers;

import com.google.gwt.i18n.client.NumberFormat;

public class BodyPosition {
	private final double azimuth;
	private final double height;
	
	public BodyPosition(final double azimuth, final double height) {
		this.azimuth = azimuth;
		this.height = height;
	}

	public final double getAzimuth() {
	    return azimuth;
    }

	public final double getHeight() {
	    return height;
    }
	
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Azimuth: ");
		builder.append(NumberFormat.getFormat("0.000").format(azimuth));
		builder.append(" Height: ");
		builder.append(NumberFormat.getFormat("0.000").format(height));
		return builder.toString();
	}
	
}
