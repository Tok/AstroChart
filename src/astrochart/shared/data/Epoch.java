package astrochart.shared.data;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.gwt.i18n.client.NumberFormat;
import astrochart.shared.enums.Planet;
import astrochart.shared.enums.ZodiacSign;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Epoch implements Serializable {
    private static final long serialVersionUID = 546722654874032959L;
	
	@PrimaryKey
	@Persistent
	private Long key; //Timestamp of sidereal Date
	
	@Persistent
    private Date sidDate;
    @Persistent
    private String day;
    
    @Persistent
    private String sun;
    @Persistent
    private String moon;
    @Persistent
    private String mercury;
    @Persistent
    private String venus;
    @Persistent
    private String mars;
    @Persistent
    private String jupiter;
    @Persistent
    private String saturn;
    @Persistent
    private String uranus;
    @Persistent
    private String neptune;
    @Persistent
    private String pluto;
    @Persistent
    private String node;
    
    public Epoch() {
    }

	public final Long getKey() {
		return key;
	}
	
	public final Date getSidDate() {
    	return sidDate;
    }

	public final void setSidDate(final Date sidDate) {
    	this.sidDate = sidDate;
    	this.key = sidDate.getTime();
    }

	public final String getDay() {
	    return day;
    }

	public final void setDay(String day) {
	    this.day = day;
    }
	
	public final void setPosition(Planet planet, String position) {
		if (planet.equals(Planet.Sun)) {
			sun = position;
		} else if (planet.equals(Planet.Moon)) {
			moon = position;
		} else if (planet.equals(Planet.Mercury)) {
			mercury = position;
		} else if (planet.equals(Planet.Venus)) {
			venus = position;
		} else if (planet.equals(Planet.Mars)) {
			mars = position;
		} else if (planet.equals(Planet.Jupiter)) {
			jupiter = position;
		} else if (planet.equals(Planet.Saturn)) {
			saturn = position;
		} else if (planet.equals(Planet.Uranus)) {
			uranus = position;
		} else if (planet.equals(Planet.Neptune)) {
			neptune = position;
		} else if (planet.equals(Planet.Pluto)) {
			pluto = position;
		} else if (planet.equals(Planet.Node)) {
			node = position;
		} else if (planet.equals(Planet.SouthNode)) {
			//ignore
//			throw new IllegalArgumentException("Cannot set south node position.");
		} else {
			throw new IllegalArgumentException("Planet unknown.");
		}
	}

	public final String getPosition(final Planet planet) {
		if (planet.equals(Planet.Sun)) {
			return sun;
		} else if (planet.equals(Planet.Moon)) {
			return moon;
		} else if (planet.equals(Planet.Mercury)) {
			return mercury;
		} else if (planet.equals(Planet.Venus)) {
			return venus;
		} else if (planet.equals(Planet.Mars)) {
			return mars;
		} else if (planet.equals(Planet.Jupiter)) {
			return jupiter;
		} else if (planet.equals(Planet.Saturn)) {
			return saturn;
		} else if (planet.equals(Planet.Uranus)) {
			return uranus;
		} else if (planet.equals(Planet.Neptune)) {
			return neptune;
		} else if (planet.equals(Planet.Pluto)) {
			return pluto;
		} else if (planet.equals(Planet.Node)) {
			return node;
		} else if (planet.equals(Planet.SouthNode)) {
			return determineSouthNodePosition();
		} else {
			throw new IllegalArgumentException("Planet unknown");
		}
	}
	

	
	public final int getDegrees(final Planet planet) {
		return Integer.parseInt(getPosition(planet).substring(0, 2));
	}
	
	public final String getSign(final Planet planet) {
		return getPosition(planet).substring(2, 4);
	}

	public final int getMinutes(final Planet planet) {
		return Integer.parseInt(getPosition(planet).substring(4, 6));
	}

	public final double getPreciseDegrees(final Planet planet) {
		return getDegrees(planet) + (((getMinutes(planet) * 100D) / 60D) / 100D);
	}
	
	/**
	 * Determines and returns the position of the south node 
	 * by evaluating the north node position.
	 */
	private final String determineSouthNodePosition() {
		final String northNode = getPosition(Planet.Node);
		final String northSign = northNode.substring(2,4);
		final String sign = ZodiacSign.valueOfAbbrevistion(northSign).getDescendent();
		final String southNode = northNode.substring(0,2) + sign + northNode.substring(4,6);
		return southNode;
	}
	
	public final String getPositionString(final Planet planet) {
		final StringBuilder builder = new StringBuilder();
		final ZodiacSign sign = ZodiacSign.valueOfAbbrevistion(getSign(planet));
		builder.append(sign.getUnicode());
		builder.append(" ");
		builder.append(sign);
		builder.append(" ");
		final double degree = getPreciseDegrees(planet);
		builder.append(NumberFormat.getFormat("0.000").format(degree));
		return builder.toString();	
	}
	
	public final String getPositionDegreeString(final Planet planet) {
		final StringBuilder builder = new StringBuilder();
		final ZodiacSign sign = ZodiacSign.valueOfAbbrevistion(getSign(planet));
		builder.append(sign.getUnicode());
		builder.append(" ");
		builder.append(sign);
		builder.append(" ");
		builder.append(getDegrees(planet));
		builder.append(String.valueOf('\u00B0'));
		builder.append(" ");
		builder.append(getMinutes(planet));
		builder.append(String.valueOf('\u2032'));
		return builder.toString();	
	}
	
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		for (final Planet planet : Planet.values()) {
			builder.append(planet.name());
			builder.append(":");
			builder.append(getPosition(planet));
			builder.append(" ");
		}
		return builder.toString();	
	}
}
