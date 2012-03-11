package astrochart.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelledEvent extends GwtEvent<CancelledEventHandler> {
    public static final Type<CancelledEventHandler> type = new Type<CancelledEventHandler>();

    @Override
    public final Type<CancelledEventHandler> getAssociatedType() {
        return type;
    }

    @Override
    protected final void dispatch(final CancelledEventHandler handler) {
        handler.onCancelled(this);
    }
}
