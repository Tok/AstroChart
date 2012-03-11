package astrochart.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class InfoPresenter extends AbstractTabPresenter implements Presenter {
    private final Display display;
    private int row = 0;
    
    public interface Display {
        FlexTable getInfoList();
        Widget asWidget();
    }

    public InfoPresenter(final HandlerManager eventBus,
            final TabPanel tabPanel, final Display view) {
        super(eventBus, tabPanel);
        this.display = view;
    }

    public void bind() {
    }

    @Override
    public final void go(final HasWidgets container) {
        bind();
        prepareTable();
        container.clear();
        container.add(super.getTabPanel());
    }

    //formatter:off
    private void prepareTable() {
        display.getInfoList().removeAllRows();
        display.getInfoList().getColumnFormatter().setWidth(0, "100px");

        addRowText("Welcome to Astrology Chart Generator.");
        addRowText("This application is work in progress and may therefore still display incorrect results.");
        addRowWidget(new HTML("&nbsp;"));
        addRowText("The ephremeris used in this application can be found at:");
        addRowWidget(new Anchor("http://www.findyourfate.com/astrology/ephemeris/ephemeris.html", "http://www.findyourfate.com/astrology/ephemeris/ephemeris.html"));
        addRowWidget(new HTML("&nbsp;"));
        addRowText("Astrology Chart Generator uses the following IP-location lookup service:");
        addRowWidget(new Anchor("http://freegeoip.net", "http://freegeoip.net"));
        addRowWidget(new HTML("&nbsp;"));
        addRowText("The location lookup by city-name is powered by:");
        addRowWidget(new Anchor("http://code.google.com/apis/maps/index.html", "http://code.google.com/apis/maps/index.html"));
        addRowWidget(new HTML("&nbsp;"));
        addRowText("Sunrise and sunset calculations are done according to the formulas provided at:");
        addRowWidget(new Anchor("http://users.electromagnetic.net/bu/astro/sunrise-set.php", "http://users.electromagnetic.net/bu/astro/sunrise-set.php"));
        addRowText("This application will therefore calculate the same results for sunrise and sunset as calculated here:");
        addRowWidget(new Anchor("http://users.electromagnetic.net/bu/astro/iyf-calc.php", "http://users.electromagnetic.net/bu/astro/iyf-calc.php"));
        addRowText("(Beware: There are many other online calculators that result in different results.)");
        addRowWidget(new HTML("&nbsp;"));
        addRowText("The source code and the unit tests of this application can be found at:");
        addRowWidget(new Anchor("https://github.com/Tok/AstroChart", "https://github.com/Tok/AstroChart"));
    }
    //formatter:on
    
    private final void addRowText(final String text) {
    	display.getInfoList().setText(row, 0, text);
    	row++;
    }

    private final void addRowWidget(final Widget widget) {
    	display.getInfoList().setWidget(row, 0, widget);
    	row++;
    }
    
}
