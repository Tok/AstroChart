package astrochart.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DateUpdatedEventHandler extends EventHandler {
    void onDateUpdated(final DateUpdatedEvent event);
}
