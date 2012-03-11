package astrochart.shared;

public enum Weekday {
	Monday(   "Mo"),
	Tuesday(  "Tu"),
	Wednesday("We"),
	Thursday( "Th"),
	Friday(   "Fr"),
	Saturday( "Sa"),
	Sunday(   "Su");
	
	final String abbreviation;
	
	private Weekday(final String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public String getAbbreviation() {
        return abbreviation;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}
