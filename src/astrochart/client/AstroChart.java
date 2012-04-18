package astrochart.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AstroChart implements EntryPoint {	
    private final AppController appController = new AppController();

    @Override
    public final void onModuleLoad() {
        final Image titleImage = new Image("/images/AstroChartTitle.png");
        RootPanel.get("title").add(titleImage);
        appController.go(RootPanel.get("content"));
    }
}
