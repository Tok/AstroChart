package astrochart.shared.data;

import java.io.Serializable;

public class GeocodeData implements Serializable {
    private static final long serialVersionUID = 2353838294439322239L;
    
	private double latitude;
	private double longitude;
	private String cityName;
	
	public GeocodeData() {
	}

	public final double getLatitude() {
    	return latitude;
    }

	public final void setLatitude(final double latitude) {
    	this.latitude = latitude;
    }

	public final double getLongitude() {
    	return longitude;
    }
	
	public final void setLongitude(final double longitude) {
    	this.longitude = longitude;
    }

	public final String getCityName() {
    	return cityName;
    }

	public final void setCityName(final String cityName) {
    	this.cityName = cityName;
    }

}
