package astrochart.client.service;

import astrochart.shared.data.Epoch;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {
    void saveEpoch(Epoch epoch);
}
