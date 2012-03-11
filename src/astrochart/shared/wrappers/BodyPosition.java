package astrochart.shared.wrappers;

import com.google.gwt.i18n.client.NumberFormat;

public class BodyPosition {
	private double azimuth;
	private double height;
	
	public BodyPosition(final double azimuth, final double height) {
		this.azimuth = azimuth;
		this.height = height;
	}

	public double getAzimuth() {
	    return azimuth;
    }

	public double getHeight() {
	    return height;
    }
	
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Azimuth: ");
		builder.append(NumberFormat.getFormat("#.000").format(azimuth));
		builder.append(" Height: ");
		builder.append(NumberFormat.getFormat("#.000").format(height));
		return builder.toString();
	}
	
}
