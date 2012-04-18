package astrochart.client.service;

import astrochart.shared.data.Epoch;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminServiceAsync {
    void saveEpoch(final Epoch epoch, final AsyncCallback<Void> callback);;
}
