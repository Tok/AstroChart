package astrochart.shared.wrappers;

import java.util.Date;

public class RiseAndSet {
	private final Date rise;
	private final Date set;
	
	public RiseAndSet(final Date rise, final Date set) {
		this.rise = rise;
		this.set = set;
	}

	public final Date getRise() {
	    return rise;
    }

	public final Date getSet() {
	    return set;
    }
	
	public final String toString() {
		return "rise: " + rise + " set: " + set;
	}
	
}
