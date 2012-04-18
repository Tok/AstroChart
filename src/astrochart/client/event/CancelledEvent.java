package astrochart.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelledEvent extends GwtEvent<CancelledEventHandler> {
    public static final Type<CancelledEventHandler> TYPE = new Type<CancelledEventHandler>();

    @Override
    public final Type<CancelledEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final CancelledEventHandler handler) {
        handler.onCancelled(this);
    }
}
