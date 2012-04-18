package astrochart.client.service;

import astrochart.shared.data.GeocodeData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeocodeServiceAsync {
    void getGeocodeData(String cityName, AsyncCallback<GeocodeData> callback);
    void getGeocodeDataForIp(AsyncCallback<GeocodeData> callback);;
}
