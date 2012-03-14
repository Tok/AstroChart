package astrochart.server.service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import astrochart.client.service.EpochService;
import astrochart.shared.data.Epoch;
import astrochart.shared.enums.Planet;
import astrochart.shared.exceptions.EpochNotFoundException;
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
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EpochServiceImpl.class.getName());
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
	@Override
    public final Epoch readEpoch(final Date date) throws EpochNotFoundException {
		final Date low = new Date(date.getTime() - ((MILLISECONDS_PER_DAY * 10) / 8));
		final Date med = new Date(date.getTime());
		final Date high = new Date(date.getTime() + ((MILLISECONDS_PER_DAY * 10) / 8));
		
		Epoch first = null;
		Epoch second = null;
		final Epoch result = new Epoch();

		//try to get Epochs from cache
		Cache cache = null;
		String firstCacheKey = sdf.format(low);
		String medCacheKey = sdf.format(med);
		String secondCacheKey = sdf.format(high);
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            first = (Epoch) cache.get(firstCacheKey);
            if (first == null) {
                first = (Epoch) cache.get(medCacheKey);
            } else {
            	second = (Epoch) cache.get(medCacheKey);            	
            }
            if (second == null) {
            	second = (Epoch) cache.get(secondCacheKey);
            }
        } catch (CacheException e) {
        	//ignore
        }
        
        if (first == null || second == null) {
        	first = new Epoch();
			second = new Epoch();
	        final Query q = new Query("Epoch");
	        q.addFilter("sidDate", Query.FilterOperator.GREATER_THAN_OR_EQUAL, low);
	        q.addFilter("sidDate", Query.FilterOperator.LESS_THAN_OR_EQUAL, high);
	        q.addSort("sidDate", SortDirection.ASCENDING);
	        final PreparedQuery pq = datastore.prepare(q);        
	        final List<Entity> entities = pq.asList(FetchOptions.Builder.withLimit(2));
	        if (entities.size() < 2) {
	        	throw new EpochNotFoundException();
	        }
	        
	        final Entity firstEntity = entities.get(0);
	        final Entity secondEntity = entities.get(1);

	        first.setSidDate((Date) firstEntity.getProperty("sidDate"));
	        second.setSidDate((Date) secondEntity.getProperty("sidDate"));
	        first.setDay((String) firstEntity.getProperty("day"));
	        second.setDay((String) secondEntity.getProperty("day"));
	        for (final Planet planet : Planet.values()) {
	           	first.setPosition(planet, (String) firstEntity.getProperty(planet.name().toLowerCase()));           		
	           	second.setPosition(planet, (String) secondEntity.getProperty(planet.name().toLowerCase()));           		
	        }
        }
		
        //write epochs to cache
        if (cache != null) {
        	cache.put(sdf.format(first.getSidDate()), first);
        	cache.put(sdf.format(second.getSidDate()), second);
        }
        
        final long firstTs = first.getSidDate().getTime();
        final long originalTs = date.getTime();
        final long secondTs = second.getSidDate().getTime();
           	
        final long totalDifference = secondTs - firstTs; //--> 100%
        final long firstDifference = originalTs - firstTs;
        final long secondDifference = secondTs - originalTs;

        result.setSidDate(date);
        if (firstDifference < secondDifference) {
        	//first result is closer to original date
        	result.setDay(first.getDay());
        } else {
        	result.setDay(second.getDay());
        }

        for (final Planet planet : Planet.values()) {
        	if (!planet.equals(Planet.SouthNode)) {
//        		final String firstToken = (String) firstEntity.getProperty(planet.name().toLowerCase());
//        		final String secondToken = (String) secondEntity.getProperty(planet.name().toLowerCase());
        		final String firstToken = first.getPosition(planet);
        		final String secondToken = second.getPosition(planet);

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
