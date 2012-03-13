package astrochart.shared.enums.time;

public enum Month {
	January(  1, 31),
	February( 2, 28), //29 days in leap years
	March(    3, 31), 
	April(    4, 30),
	May(      5, 31), 
	June(     6, 30), 
	July(     7, 31),
	August(   8, 31),
	September(9, 30),
	October( 10, 31), 
	November(11, 30), 
	December(12, 31);
		
	private final int number;
	private final int days;
	
	private Month(final int number, final int days) {
        this.number = number;
        this.days = days;
    }
    
    public final int getNumber() {
        return number;
    }
    
    public final int getDays() {
        return days;
    }
    
    @Override
    public final String toString() {
        return this.name();
    }
}
