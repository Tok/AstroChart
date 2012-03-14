package astrochart.server.service;

import java.util.logging.Logger;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import astrochart.client.service.AdminService;
import astrochart.shared.data.Epoch;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(AdminServiceImpl.class.getName());
    private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    
    @Override
    public final void saveEpoch(final Epoch epoch) {
    	System.out.println(epoch);
        final PersistenceManager pm = PMF.getPersistenceManager();
        try {
            pm.makePersistent(epoch);
            //LOG.log(Level.INFO, "Epoch stored: " + epoch);
        } finally {
            pm.close();
        }
    }
}
