package astrochart.shared.enums;

public enum HouseType {
	Equal("Equal House"), 
	WholeSigns("Whole Sign");
//	Placidus, 
//	Campanus, 
//	Regiomontanus,
//	KochHousesSystem;
	
	private final String name;
	
	private HouseType(final String name) {
		this.name = name;
    }
	
	public final String getName() {
    	return name;
    }
	
	public final String toString() {
    	return name();
    }

	public static HouseType getTypeForName(final String name) {
	    for (final HouseType type : HouseType.values()) {
	    	if (type.getName().equals(name)) {
	    		return type;
	    	}
	    }
	    throw new IllegalArgumentException();
    }
}
