package astrochart.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SetStatusEventHandler extends EventHandler {
    void onSetStatus(final SetStatusEvent event);
}
