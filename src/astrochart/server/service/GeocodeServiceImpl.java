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
    public final GeocodeData getGeocodeData(final String cityName) {
        final String encodedCityName = encode(cityName);
        final String geocodeUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=" + encodedCityName + "&sensor=false";
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

    private GeocodeData makeGeocodeRequest(final String geocodeUrl) {
        GeocodeData result;
        try {
            final URL url = new URL(geocodeUrl);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final JsonParser parser = new JsonParser();
            final JsonElement element = parser.parse(reader);
            reader.close();
            final JsonObject rootObject = element.getAsJsonObject();
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
        final JsonArray array = rootObject.getAsJsonArray("results");
        if (array.size() > 0) {
            final JsonObject firstResult = array.get(0).getAsJsonObject();

            final JsonElement address = firstResult.get("formatted_address");
            result.setCityName(address.getAsString());

            final JsonElement geometryElement = firstResult.get("geometry");
            final JsonElement locationElement = geometryElement.getAsJsonObject().get("location");

            final JsonElement latElement = locationElement.getAsJsonObject().get("lat");
            result.setLatitude(latElement.getAsDouble());
            final JsonElement lngElement = locationElement.getAsJsonObject().get("lng");
            result.setLongitude(lngElement.getAsDouble());
        } else {
            result.setCityName("Location not found.");
            result.setLatitude(0.0D);
            result.setLongitude(0.0D);
        }
        return result;
    }

    private GeocodeData makeIpGeocodeRequest(final String ipGeocodeUrl) {
        GeocodeData result;
        try {
            final URL url = new URL(ipGeocodeUrl);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final JsonParser parser = new JsonParser();
            final JsonElement element = parser.parse(reader);
            reader.close();
            final JsonObject rootObject = element.getAsJsonObject();
            result = parseJsonIpGeocodeResult(rootObject);
        } catch (final MalformedURLException e) {
            result = new GeocodeData();
            result.setCityName("Error finding location.");
            result.setLatitude(0.0D);
            result.setLongitude(0.0D);
        } catch (final IOException e) {
            result = new GeocodeData();
            result.setCityName("Error finding location.");
            result.setLatitude(0.0D);
            result.setLongitude(0.0D);
        }
        return result;
    }

    private GeocodeData parseJsonIpGeocodeResult(final JsonObject rootObject) {
        final GeocodeData result = new GeocodeData();
        final JsonElement cityElement = rootObject.getAsJsonObject().get("city");
        result.setCityName(cityElement.getAsString());
        final JsonElement latElement = rootObject.getAsJsonObject().get("latitude");
        result.setLatitude(latElement.getAsDouble());
        final JsonElement lngElement = rootObject.getAsJsonObject().get("longitude");
        result.setLongitude(lngElement.getAsDouble());
        return result;
    }

    private String encode(final String in) {
        String out = in.replaceAll(" ", "%20");
        return out;
    }
}
