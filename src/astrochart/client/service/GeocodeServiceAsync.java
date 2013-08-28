package astrochart.client.service;

import astrochart.shared.data.GeocodeData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeocodeServiceAsync {
    void getGeocodeData(final String cityName, final AsyncCallback<GeocodeData> callback);
    void getGeocodeDataForIp(final AsyncCallback<GeocodeData> callback);;
}
