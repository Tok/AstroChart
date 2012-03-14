package astrochart.client.service;

import java.util.Date;
import astrochart.shared.data.Epoch;
import astrochart.shared.exceptions.EpochNotFoundException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("epoch")
public interface EpochService extends RemoteService {
	Epoch readEpoch(final Date date) throws EpochNotFoundException;
}
