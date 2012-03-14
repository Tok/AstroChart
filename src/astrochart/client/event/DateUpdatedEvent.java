package astrochart.client.event;

import java.util.Date;
import com.google.gwt.event.shared.GwtEvent;

public class DateUpdatedEvent extends GwtEvent<DateUpdatedEventHandler> {
    public static final Type<DateUpdatedEventHandler> TYPE = new Type<DateUpdatedEventHandler>();
    private final Date utcDate;
    
    public DateUpdatedEvent(Date utcDate) {
    	this.utcDate = utcDate;
    }

	@Override
    public final Type<DateUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DateUpdatedEventHandler handler) {
        handler.onDateUpdated(this);
    }

	public Date getUtcDate() {
	    return utcDate;
    }
}
