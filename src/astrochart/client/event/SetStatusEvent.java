package astrochart.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SetStatusEvent extends GwtEvent<SetStatusEventHandler> {
    public static final Type<SetStatusEventHandler> TYPE = new Type<SetStatusEventHandler>();
    private final String statusMessage;
    
    public SetStatusEvent(String statusMessage) {
    	this.statusMessage = statusMessage;
    }

	@Override
    public final Type<SetStatusEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SetStatusEventHandler handler) {
        handler.onSetStatus(this);
    }

	public String getStatusMessage() {
	    return statusMessage;
    }
}
