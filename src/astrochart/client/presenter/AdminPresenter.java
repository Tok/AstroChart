package astrochart.client.presenter;

import java.util.logging.Logger;
import astrochart.client.service.AdminService;
import astrochart.client.service.AdminServiceAsync;
import astrochart.shared.data.Epoch;
import astrochart.shared.enums.Planet;
import astrochart.shared.enums.Weekday;
import astrochart.shared.enums.time.Month;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class AdminPresenter implements Presenter {
    private static final Logger LOG = Logger.getLogger(AdminPresenter.class.getName());
    private final AdminServiceAsync adminService = GWT.create(AdminService.class);
    private final DateTimeFormat format = DateTimeFormat.getFormat("dd MM yyyy HH:mm:ss");
    private final Display display;

    private Month epochMonth = Month.January;
    private int epochYear = 0;
    private String[] lines;
    private int lineIndex;
    private int counter;
    private boolean hasError = false;

    public interface Display {
        Widget asWidget();
        Button getSubmitButton();
        TextArea getInputTextArea();
        Label getStatusLabel();
        Label getSecondStatusLabel();
    }

    public AdminPresenter(final Display view) {
        this.display = view;
    }

    public final void bind() {
        display.getSubmitButton().addClickHandler(new ClickHandler() {
            @Override
            public final void onClick(final ClickEvent event) {
                parseAndSubmitEpochs();
            }
        });
    }

    @Override
    public final void go(final HasWidgets container) {
        bind();
        container.clear();
        container.add(display.asWidget());
        display.getStatusLabel().setText("Ready.");
        display.getInputTextArea().setFocus(true);
    }

    private void parseAndSubmitEpochs() {
        display.getSubmitButton().setEnabled(false);
        counter = 0;
        lineIndex = 0;
        lines = display.getInputTextArea().getText().split("\n");
        hasError = false;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                parseLine(lineIndex);
            }
        });
    }

    private synchronized void parseLine(final int index) {
        if (!hasError) {
            display.getStatusLabel().setText("Processing line: " + index);
            display.getSecondStatusLabel().setText(epochYear + " " + epochMonth);
            for (final Month month : Month.values()) {
                if (lines[index].startsWith(month.name().toUpperCase())) {
                    epochMonth = month;
                    epochYear = Integer.parseInt(lines[index].split(" ")[1]);
                }
            }
            final Month finalEpochMonth = epochMonth;
            final int finalEpochYear = epochYear;
            parseEpochLine(finalEpochYear, finalEpochMonth, lines[index]);
            if (lineIndex >= lines.length) {
                display.getSubmitButton().setEnabled(true);
                display.getStatusLabel().setText("Done.");
                display.getSecondStatusLabel().setText("Number of Epochs submitted: " + counter);
            } else { // next
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        parseLine(lineIndex++);
                    }
                });
            }
        } else {
            display.getSubmitButton().setEnabled(true);
            return;
        }
    }

    private void parseEpochLine(final int epochYear, final Month epochMonth, final String line) {
        for (final Weekday weekday : Weekday.values()) {
            if (line.startsWith(weekday.getAbbreviation())) {
                final String[] split = line.split(" ");
                //DATE  SID.TIME  SUN   MOON  MERCURY  VENUS  MARS JUPITER SATURN URANUS NEPTUNE PLUTO  NODE
                //0  1  2        3      4      5      6      7      8      9      10     11     12     13
                //Fr 01 06:42:57 10CP36 07VI24 18CP00 20AQ04 29AR32 21LI12 25LI38 17AR44 17VI18 02TA24 20PI56

                final Epoch epoch = new Epoch();
                String monthString = String.valueOf(epochMonth.getNumber());
                if (monthString.length() == 1) {
                    monthString = "0" + monthString;
                }
                epoch.setSidDate(format.parse(
                    split[1] + " " + monthString + " " + epochYear + " " + split[2]
                ));
                epoch.setDay(weekday.getAbbreviation());
                for (final Planet planet : Planet.values()) {
                    epoch.setPosition(planet, split[planet.getToken()]);
                }
                submitEpoch(epoch);
            }
        }
    }

    private void submitEpoch(final Epoch epoch) {
        adminService.saveEpoch(epoch, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {
                counter++;
            }
            @Override
            public void onFailure(final Throwable caught) {
                hasError = true;
                LOG.severe("Fail submitting Epoch: " + caught.getMessage());
                LOG.severe("Year: " + epochYear + " Month: " + epochMonth + " Epoch: " + epoch);
                display.getStatusLabel().setText("Fail submitting Epoch: " + caught.getMessage());
                display.getSecondStatusLabel().setText("Year: " + epochYear + " Month: " + epochMonth + " Epoch: " + epoch);
            }
        });
    }

}
