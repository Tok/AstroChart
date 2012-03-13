package astrochart.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DateUpdatedEvent extends GwtEvent<DateUpdatedEventHandler> {
    public static final Type<DateUpdatedEventHandler> TYPE = new Type<DateUpdatedEventHandler>();

    @Override
    public final Type<DateUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final DateUpdatedEventHandler handler) {
        handler.onDateUpdated(this);
    }
}
