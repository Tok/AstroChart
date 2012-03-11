package astrochart.server.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import astrochart.client.service.EpochService;
import astrochart.shared.Planet;
import astrochart.shared.data.Epoch;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


@SuppressWarnings("serial")
public class EpochServiceImpl extends RemoteServiceServlet implements EpochService {
	private static final long MILLISECONDS_PER_DAY = 86400000;
	
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EpochServiceImpl.class.getName());    

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
	@Override
    public Epoch readEpoch(final Date date) {
//		final long low = date.getTime() - ((MILLISECONDS_PER_DAY * 3) / 2);
//		final long high = date.getTime() + ((MILLISECONDS_PER_DAY * 3) / 2);
//		final long low = date.getTime() - MILLISECONDS_PER_DAY;
//		final long high = date.getTime() + MILLISECONDS_PER_DAY;
		final long low = date.getTime() - ((MILLISECONDS_PER_DAY * 10) / 8);
		final long high = date.getTime() + ((MILLISECONDS_PER_DAY * 10) / 8);
		
        Epoch first = new Epoch();
        Epoch second = new Epoch();
        Epoch result = new Epoch();

        final Query q = new Query("Epoch");
        q.addFilter("sidDate", Query.FilterOperator.GREATER_THAN_OR_EQUAL, new Date(low));
        q.addFilter("sidDate", Query.FilterOperator.LESS_THAN_OR_EQUAL, new Date(high));
        q.addSort("sidDate", SortDirection.ASCENDING);
        final PreparedQuery pq = datastore.prepare(q);

        List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(2));
        Entity firstEntity = entities.get(0);
        Entity secondEntity = entities.get(1);
            
        first.setSidDate((Date) firstEntity.getProperty("sidDate"));
        second.setSidDate((Date) secondEntity.getProperty("sidDate"));
        first.setDay((String) firstEntity.getProperty("day"));
        second.setDay((String) secondEntity.getProperty("day"));
        for (Planet planet : Planet.values()) {
           	first.setPosition(planet, (String) firstEntity.getProperty(planet.name().toLowerCase()));           		
           	second.setPosition(planet, (String) secondEntity.getProperty(planet.name().toLowerCase()));           		
        }


        long firstTs = first.getSidDate().getTime();
        long originalTs = date.getTime();
        long secondTs = second.getSidDate().getTime();
           	
        long totalDifference = secondTs - firstTs; //--> 100%
        long firstDifference = originalTs - firstTs;
        long secondDifference = secondTs - originalTs;
           	
        result.setSidDate(date);
        if (firstDifference < secondDifference) {
        	//first result is closer to original date
        	result.setDay(first.getDay());
        } else {
        	result.setDay(second.getDay());
        }

        for (Planet planet : Planet.values()) {
        	final String firstToken = (String) firstEntity.getProperty(planet.name().toLowerCase());
        	final String secondToken = (String) secondEntity.getProperty(planet.name().toLowerCase());
        	final int firstDegrees = Integer.parseInt(firstToken.substring(0,2));
        	final int secondDegrees = Integer.parseInt(secondToken.substring(0,2));
        	final String firstSign = firstToken.substring(2,4);
        	final String secondSign = secondToken.substring(2,4);
        	final int firstMinutes = Integer.parseInt(firstToken.substring(4,6));
        	final int secondMinutes = Integer.parseInt(secondToken.substring(4,6));
           		
        	int averageDegrees = 0;
        	String tokenSign = "";
        	int averageMinutes = 0;
        	long averageTotalMinutes = 0;
        	if (firstSign.equals(secondSign)) {
        		tokenSign = firstSign;
        		//Note: firstDifference and secondDifference are switched in order to weight 
        		//the nearer epoch with the smaller difference more than the further
        		averageTotalMinutes = (
        				((firstMinutes + (firstDegrees * 60)) * secondDifference) + 
        				((secondMinutes + (secondDegrees * 60)) * firstDifference)
        			) / totalDifference;
        	} else {
        		if (firstDifference < secondDifference) {
        			//high degrees
        			tokenSign = firstSign;
           			averageTotalMinutes = (
           					((firstMinutes + (firstDegrees * 60)) * secondDifference) + 
           					((secondMinutes + ((secondDegrees + 30) * 60)) * firstDifference)
               			) / totalDifference;
           		} else {
           			//low degrees
           			tokenSign = secondSign;
               		averageTotalMinutes = (
               				((firstMinutes + ((firstDegrees + 30) * 60)) * secondDifference) + 
               				((secondMinutes + (secondDegrees * 60)) * firstDifference)
               			) / totalDifference;
           		}
           	}
           		
       		averageDegrees = (int) averageTotalMinutes / 60;
       		if (averageDegrees >= 30) {
       			averageDegrees = averageDegrees - 30;
       		}
       		averageMinutes = (int) averageTotalMinutes % 60;

           	String tokenDegrees = String.valueOf(averageDegrees);
       		if (tokenDegrees.length() < 2) {
       			tokenDegrees = "0" + tokenDegrees;
       		}
       		String tokenMinutes = String.valueOf(averageMinutes);
       		if (tokenMinutes.length() < 2) {
       			tokenMinutes = "0" + tokenMinutes;
       		}

           	result.setPosition(planet, tokenDegrees + tokenSign + tokenMinutes);
        }
        
        /*
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String firstPercent = (firstDifference * 100 / totalDifference) + "%";
        String secondPercent = (secondDifference * 100 / totalDifference) + "%";
        System.out.println("First:  " + sdf.format(first.getSidDate()) + " " + first.toString() + " " + firstPercent);
        System.out.println("Result: " + sdf.format(result.getSidDate()) + " " + result.toString() + "<");
        System.out.println("Second: " + sdf.format(second.getSidDate()) + " " + second.toString() + " " + secondPercent);
        */
        return result;
    }

}
