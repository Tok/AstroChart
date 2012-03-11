package astrochart.shared.wrappers;

import java.util.Date;

public class RiseAndSet {
	private Date rise;
	private Date set;
	
	public RiseAndSet(final Date rise, final Date set) {
		this.rise = rise;
		this.set = set;
	}

	public Date getRise() {
	    return rise;
    }

	public Date getSet() {
	    return set;
    }
	
	public String toString() {
		return "rise: " + rise + " set: " + set;
	}
	
}
