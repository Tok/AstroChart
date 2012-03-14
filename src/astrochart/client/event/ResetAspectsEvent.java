package astrochart.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ResetAspectsEvent extends GwtEvent<ResetAspectsEventHandler> {
    public static final Type<ResetAspectsEventHandler> TYPE = new Type<ResetAspectsEventHandler>();

    @Override
    public final Type<ResetAspectsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ResetAspectsEventHandler handler) {
        handler.onResetAspects(this);
    }
}
