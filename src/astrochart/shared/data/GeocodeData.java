package astrochart.shared.data;

import java.io.Serializable;

public class GeocodeData implements Serializable {
    private static final long serialVersionUID = 2353838294439322239L;
    
    
	private double latitude;
	private double longitude;
	private String cityName;
	
	public GeocodeData() {
	}

	public double getLatitude() {
    	return latitude;
    }

	public void setLatitude(double latitude) {
    	this.latitude = latitude;
    }

	public double getLongitude() {
    	return longitude;
    }
	
	public void setLongitude(double longitude) {
    	this.longitude = longitude;
    }

	public String getCityName() {
    	return cityName;
    }

	public void setCityName(String cityName) {
    	this.cityName = cityName;
    }

}
