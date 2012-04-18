package astrochart.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CancelledEventHandler extends EventHandler {
    void onCancelled(final CancelledEvent event);
}
