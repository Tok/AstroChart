package astrochart.client.service;

import astrochart.shared.data.GeocodeData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("geocode")
public interface GeocodeService extends RemoteService {
    GeocodeData getGeocodeData(String cityName);
    GeocodeData getGeocodeDataForIp();
}
