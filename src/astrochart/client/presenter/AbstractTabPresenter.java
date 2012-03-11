package astrochart.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class AbstractTabPresenter {
    private final TabPanel tabPanel;
    private final HandlerManager eventBus;

    public AbstractTabPresenter(final HandlerManager eventBus, final TabPanel tabPanel) {
        this.tabPanel = tabPanel;
        this.eventBus = eventBus;
    }

    public final Widget getTabPanel() {
        return tabPanel;
    }

    public final HandlerManager getEventBus() {
        return eventBus;
    }
}
