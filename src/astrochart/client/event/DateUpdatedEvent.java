package astrochart.client.event;

import java.util.Date;
import com.google.gwt.event.shared.GwtEvent;

public class DateUpdatedEvent extends GwtEvent<DateUpdatedEventHandler> {
    public static final Type<DateUpdatedEventHandler> TYPE = new Type<DateUpdatedEventHandler>();
    private final Date localDate;

    public DateUpdatedEvent(final Date localDate) {
        this.localDate = localDate;
    }

    @Override
    public final Type<DateUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DateUpdatedEventHandler handler) {
        handler.onDateUpdated(this);
    }

    public final Date getLocalDate() {
        return localDate;
    }
}
