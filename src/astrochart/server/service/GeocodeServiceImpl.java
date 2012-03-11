package astrochart.server.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import astrochart.client.service.GeocodeService;
import astrochart.shared.data.GeocodeData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


@SuppressWarnings("serial")
public class GeocodeServiceImpl extends RemoteServiceServlet implements GeocodeService {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(GeocodeServiceImpl.class.getName());

	@Override
    public final GeocodeData getGeocodeData(String cityName) {
		final String encodedCityName = encode(cityName);
		final String geocodeUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=" + 
				encodedCityName + "&sensor=false";
		final GeocodeData result = makeGeocodeRequest(geocodeUrl);
	    return result;
    }
	
	@Override
    public final GeocodeData getGeocodeDataForIp() {
		final String ipString = getThreadLocalRequest().getRemoteAddr();
		final String ipGeocodeUrl = "http://freegeoip.net/json/" + ipString;		
		final GeocodeData result = makeIpGeocodeRequest(ipGeocodeUrl);
	    return result;
    }
	
	private final GeocodeData makeGeocodeRequest(final String geocodeUrl) {
		GeocodeData result;
		try {
			URL url = new URL(geocodeUrl);		
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(reader);
			reader.close();
			JsonObject rootObject = element.getAsJsonObject();
			result = parseJsonGeocodeResult(rootObject);
		} catch (MalformedURLException e) {
			result = new GeocodeData();
			result.setCityName("Error finding location.");
			result.setLatitude(0.0D);
			result.setLongitude(0.0D);
		} catch (IOException e) {
			result = new GeocodeData();
			result.setCityName("Error finding location.");
			result.setLatitude(0.0D);
			result.setLongitude(0.0D);
		}
		return result;
	}

	private GeocodeData parseJsonGeocodeResult(final JsonObject rootObject) {
		final GeocodeData result = new GeocodeData();
		JsonArray array = rootObject.getAsJsonArray("results");
		if (array.size() > 0) {
			JsonObject firstResult = array.get(0).getAsJsonObject();

			JsonElement address = firstResult.get("formatted_address");
			result.setCityName(address.getAsString());
		
			JsonElement geometryElement = firstResult.get("geometry");
			JsonElement locationElement = geometryElement.getAsJsonObject().get("location");
		
			JsonElement latElement = locationElement.getAsJsonObject().get("lat");
			result.setLatitude(latElement.getAsDouble());
			JsonElement lngElement = locationElement.getAsJsonObject().get("lng");
			result.setLongitude(lngElement.getAsDouble());
		} else {
			result.setCityName("Location not found.");
			result.setLatitude(0.0D);
			result.setLongitude(0.0D);
		}
		return result;
	}
	
	private final GeocodeData makeIpGeocodeRequest(final String ipGeocodeUrl) {
		GeocodeData result;
		try {
			URL url = new URL(ipGeocodeUrl);		
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(reader);
			reader.close();
			JsonObject rootObject = element.getAsJsonObject();
			result = parseJsonIpGeocodeResult(rootObject);
		} catch (MalformedURLException e) {
			result = new GeocodeData();
			result.setCityName("Error finding location.");
			result.setLatitude(0.0D);
			result.setLongitude(0.0D);
		} catch (IOException e) {
			result = new GeocodeData();
			result.setCityName("Error finding location.");
			result.setLatitude(0.0D);
			result.setLongitude(0.0D);
		}
		return result;
	}
	
	private GeocodeData parseJsonIpGeocodeResult(final JsonObject rootObject) {
		final GeocodeData result = new GeocodeData();
		JsonElement cityElement = rootObject.getAsJsonObject().get("city");
		result.setCityName(cityElement.getAsString());
		JsonElement latElement = rootObject.getAsJsonObject().get("latitude");
		result.setLatitude(latElement.getAsDouble());
		JsonElement lngElement = rootObject.getAsJsonObject().get("longitude");
		result.setLongitude(lngElement.getAsDouble());
		return result;
	}
	
    final String encode(final String in) {
        String out = in.replaceAll(" ", "%20");
        return out;
    }
}
