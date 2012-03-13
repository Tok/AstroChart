package astrochart.shared.enums;

public enum Weekday {
	Monday(   "Mo"),
	Tuesday(  "Tu"),
	Wednesday("We"),
	Thursday( "Th"),
	Friday(   "Fr"),
	Saturday( "Sa"),
	Sunday(   "Su");
	
	private final String abbreviation;
	
	private Weekday(final String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public final String getAbbreviation() {
        return abbreviation;
    }
    
    @Override
    public final String toString() {
        return this.name();
    }
}
