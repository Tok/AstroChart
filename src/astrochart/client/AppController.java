package astrochart.client;

import java.util.Date;
import astrochart.client.event.CancelledEvent;
import astrochart.client.event.CancelledEventHandler;
import astrochart.client.presenter.AdminPresenter;
import astrochart.client.presenter.InfoPresenter;
import astrochart.client.presenter.ChartPresenter;
import astrochart.client.presenter.Presenter;
import astrochart.client.util.DateTimeUtil;
import astrochart.client.view.AdminView;
import astrochart.client.view.InfoView;
import astrochart.client.view.ChartView;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabPanel;

public class AppController implements Presenter, ValueChangeHandler<String> {
    private final HandlerManager eventBus = new HandlerManager(null);
    private final DateTimeUtil dateTimeUtil = new DateTimeUtil();
    private HasWidgets container;

    private final TabPanel tabPanel = new TabPanel();
    private final ChartView chartView = new ChartView(eventBus, dateTimeUtil);
    private final InfoView infoView = new InfoView();
    private final AdminView adminView = new AdminView();

    private ChartPresenter mainPresenter;
    private Presenter infoPresenter;
    private Presenter adminPresenter;

    public AppController() {
        prepareTabs();
        bind();
    }

    private void prepareTabs() {
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(final SelectionEvent<Integer> event) {
                final int selection = event.getSelectedItem().intValue();
                if (selection == 0) {
                    if (!History.getToken().startsWith("chart")) {
                        History.newItem("chart/");
                    }
                }
                if (selection == 1) {
                    if (!History.getToken().startsWith("info")) {
                        History.newItem("info/");
                    }
                }
            }
        });
        //tabPanel.setAnimationEnabled(true);
        tabPanel.add(chartView.asWidget(), "Chart");
        tabPanel.add(infoView.asWidget(), "Info");
    }

    private void bind() {
        History.addValueChangeHandler(this);
        eventBus.addHandler(CancelledEvent.TYPE, new CancelledEventHandler() {
            @Override
            public void onCancelled(final CancelledEvent event) {
                doEditTermCancelled();
            }
        });
    }

    private void doEditTermCancelled() {
        History.newItem("chart/");
    }

    @Override
    public final void go(final HasWidgets container) {
        this.container = container;
        if ("".equals(History.getToken())) {
            History.newItem("chart/");
        } else {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public final void onValueChange(final ValueChangeEvent<String> event) {
        final String token = event.getValue();
        if (token.startsWith("chart/")) {
            Date providedUtcDate = null;
            String dateString = "";
            if (token.split("/").length > 1) {
                dateString = token.split("/")[1];
                try {
                    providedUtcDate = dateTimeUtil.getUtcDateFromUtcString(dateString);
                } catch (IllegalArgumentException iae) {
                    assert true; // ignore
                }
            }
            tabPanel.selectTab(0);
            mainPresenter = new ChartPresenter(eventBus, dateTimeUtil, tabPanel, chartView, providedUtcDate);
            mainPresenter.go(container);
        } else if (token.startsWith("info/")) {
            tabPanel.selectTab(1);
            infoPresenter = new InfoPresenter(eventBus, tabPanel, infoView);
            infoPresenter.go(container);
        } else if (token.startsWith("admin/")) {
            adminPresenter = new AdminPresenter(adminView);
            adminPresenter.go(container);
        } else {
            assert true; // ignore
        }
    }

}
