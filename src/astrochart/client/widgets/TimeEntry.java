package astrochart.client.widgets;

import java.util.Date;
import astrochart.client.event.DateUpdatedEvent;
import astrochart.client.util.Constants;
import astrochart.client.util.DateTimeUtil;
import astrochart.shared.enums.time.Month;
import astrochart.shared.enums.time.TimeZone;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class TimeEntry extends Composite {
    private static final int HIGHER_BOUNDARY = 2100;
    private static final int LOWER_BOUNDARY = 1900;
    private final HandlerManager eventBus;
    private final DateTimeUtil dateTimeUtil;
    private final FlexTable flex = new FlexTable();
    private final TextBox yearTextBox = new TextBox();
    private final ListBox monthListBox = new ListBox();
    private final TextBox dayTextBox = new TextBox();
    private final TextBox hoursTextBox = new TextBox();
    private final TextBox minutesTextBox = new TextBox();
    private final TextBox secondsTextBox = new TextBox();
    private final ListBox timeZoneListBox = new ListBox();
    private final Button updateButton = new Button("Enter");
    private final Label statusLabel = new Label(String.valueOf('\u00A0')); // NBSP
    private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
    private int clientOffset;
    private Date localDate; // local date of the client, not the ListBox

    private enum Column {
        Year(0),
        Month(1),
        Day(2),
        Hours(3),
        Minutes(4),
        Seconds(5),
        Timezone(6),
        Action(7);

        private int number;

        Column(final int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    };

    public TimeEntry(final HandlerManager eventBus, final DateTimeUtil dateTimeUtil) {
        this.eventBus = eventBus;
        this.dateTimeUtil = dateTimeUtil;
        localDate = new Date();

        for (final Column column : Column.values()) {
            flex.setText(0, column.getNumber(), column.name());
        }

        yearTextBox.setWidth("50px");
        flex.setWidget(1, Column.Year.getNumber(), yearTextBox);
        for (final Month month : Month.values()) {
            monthListBox.addItem(month.name());
        }
        flex.setWidget(1, Column.Month.getNumber(), monthListBox);
        dayTextBox.setWidth("50px");
        flex.setWidget(1, Column.Day.getNumber(), dayTextBox);

        hoursTextBox.setWidth("50px");
        flex.setWidget(1, Column.Hours.getNumber(), hoursTextBox);
        minutesTextBox.setWidth("50px");
        flex.setWidget(1, Column.Minutes.getNumber(), minutesTextBox);
        secondsTextBox.setWidth("50px");
        flex.setWidget(1, Column.Seconds.getNumber(), secondsTextBox);
        for (final TimeZone zone : TimeZone.values()) {
            timeZoneListBox.addItem(zone.getName());
        }
        flex.setWidget(1, Column.Timezone.getNumber(), timeZoneListBox);
        updateButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                updateDate();
            }
        });
        flex.setWidget(1, Column.Action.getNumber(), updateButton);

        flex.setWidget(2, 0, statusLabel);
        flex.getFlexCellFormatter().setColSpan(2, 0, Column.values().length + 1);

        updateValues();
        updateDate();

        initWidget(flex);
        setStyleName("time-entry");
    }

    private void updateValues() {
        yearTextBox.setText(String.valueOf(dateTimeUtil.getYear(localDate)));
        int monthListIndex = 0;
        for (final Month month : Month.values()) {
            if (dateTimeUtil.getMonth(localDate) == month.getNumber()) {
                monthListBox.setSelectedIndex(monthListIndex);
            }
            monthListIndex++;
        }
        dayTextBox.setText(String.valueOf(dateTimeUtil.getDay(localDate)));
        hoursTextBox.setText(String.valueOf(dateTimeUtil.getHours(localDate)));
        minutesTextBox.setText(String.valueOf(dateTimeUtil.getMinutes(localDate)));
        secondsTextBox.setText(String.valueOf(dateTimeUtil.getSeconds(localDate)));
        double timeZoneOffset = DOM.getElementById("timeZone").getPropertyDouble("value");
        int zoneListIndex = 0;
        for (final TimeZone zone : TimeZone.values()) {
            if (Double.valueOf(timeZoneOffset * Constants.SECONDS_PER_MINUTE).intValue() == zone.getUtcOffsetMinutes()) {
                clientOffset = Double.valueOf(timeZoneOffset * Constants.SECONDS_PER_MINUTE).intValue();
                timeZoneListBox.setSelectedIndex(zoneListIndex);
            }
            zoneListIndex++;
        }
    }

    private void updateDate() {
        // "yyyy.MM.dd HH:mm:ss"
        statusLabel.setText(String.valueOf('\u00A0'));
        try {
            final StringBuilder dateString = new StringBuilder();
            validateYear();
            dateString.append(yearTextBox.getText());
            dateString.append(".");
            final Month month = Month.valueOf(monthListBox.getValue(monthListBox.getSelectedIndex()));
            dateString.append(formatToTwoDigits(String.valueOf(month.getNumber())));
            dateString.append(".");
            validateDay();
            dateString.append(formatToTwoDigits(dayTextBox.getText()));
            dateString.append(" ");
            validateHours();
            dateString.append(formatToTwoDigits(hoursTextBox.getText()));
            dateString.append(":");
            validateMinutes();
            dateString.append(formatToTwoDigits(minutesTextBox.getText()));
            dateString.append(":");
            validateSeconds();
            dateString.append(formatToTwoDigits(secondsTextBox.getText()));
            final Date tempDate = dateTimeFormat.parse(dateString.toString());
            final TimeZone timeZone = TimeZone.getTimeZoneForName(timeZoneListBox.getValue(timeZoneListBox.getSelectedIndex()));
            // final com.google.gwt.i18n.client.TimeZone tzUtc =
            // com.google.gwt.i18n.client.TimeZone.createTimeZone(0);
            // final com.google.gwt.i18n.client.TimeZone tz =
            // com.google.gwt.i18n.client.TimeZone.createTimeZone(timeZone.getUtcOffsetMinutes());
            final Date boxDate = new Date(tempDate.getTime() - (timeZone.getUtcOffsetMinutes() * Constants.MILLISECONDS_PER_MINUTE));
            localDate = new Date(boxDate.getTime() + (clientOffset * Constants.MILLISECONDS_PER_MINUTE));
            final StringBuilder status = new StringBuilder();
            status.append(dateTimeFormat.format(tempDate));
            status.append(" ");
            status.append(timeZone.getName());
            if (timeZone.getUtcOffsetMinutes() != clientOffset) {
                status.append(" --> ");
                status.append(dateTimeFormat.format(localDate));
                status.append(" UTC+");
                status.append(clientOffset / Constants.SECONDS_PER_MINUTE);
            }
            if (timeZone.getUtcOffsetMinutes() != 0) {
                status.append(" --> ");
                status.append(dateTimeFormat.format(boxDate));
                status.append(" UTC ");
            }
            statusLabel.setText(status.toString());
            eventBus.fireEvent(new DateUpdatedEvent(localDate));
        } catch (IllegalArgumentException iae) {
            statusLabel.setText(iae.getMessage());
        }
    }

    private void validateSeconds() {
        int minutes = Integer.valueOf(secondsTextBox.getText());
        if (minutes < 0 || minutes > Constants.MINUTES_PER_HOUR - 1) {
            secondsTextBox.setFocus(true);
            throw new IllegalArgumentException("Seconds are out of range.");
        }
    }

    private void validateMinutes() {
        int minutes = Integer.valueOf(minutesTextBox.getText());
        if (minutes < 0 || minutes > Constants.MINUTES_PER_HOUR - 1) {
            minutesTextBox.setFocus(true);
            throw new IllegalArgumentException("Minutes are out of range.");
        }
    }

    private void validateHours() {
        int hours = Integer.valueOf(hoursTextBox.getText());
        if (hours < 0 || hours > Constants.HOURS_PER_DAY - 1) {
            hoursTextBox.setFocus(true);
            throw new IllegalArgumentException("Hour is out of range.");
        }
    }

    private void validateYear() {
        int year = Integer.valueOf(yearTextBox.getText());
        if (year < LOWER_BOUNDARY || year > HIGHER_BOUNDARY) {
            yearTextBox.setFocus(true);
            throw new IllegalArgumentException("Year is out of range.");
        }
    }

    private void validateDay() {
        int day = Integer.valueOf(dayTextBox.getText());
        final Month month = Month.valueOf(monthListBox.getValue(monthListBox.getSelectedIndex()));
        if (day < 1 || day > month.getDays()) {
            int year = Integer.valueOf(yearTextBox.getText());
            if (day == month.getDays() + 1 && DateTimeUtil.isLeapYear(year) && month.equals(Month.February)) {
                dayTextBox.setFocus(true);
                throw new IllegalArgumentException("Day is out of range. (" + year + " is a leap year).");
            } else {
                dayTextBox.setFocus(true);
                throw new IllegalArgumentException("Day is out of range.");
            }
        }
    }

    private String formatToTwoDigits(final String in) {
        if (in.length() == 1) {
            return "0" + in;
        } else {
            return in;
        }
    }

    public final Date getLocalDate() {
        return localDate;
    }

    public final String getClientTimezone() {
        return " UTC+" + (clientOffset / Constants.MINUTES_PER_HOUR);
    }

    public final void updateDate(final Date newUtcDate) {
        localDate = newUtcDate;
        updateValues();
        int zoneListIndex = 0;
        for (final TimeZone zone : TimeZone.values()) {
            if (zone.getUtcOffsetMinutes() == 0) {
                timeZoneListBox.setSelectedIndex(zoneListIndex);
                break;
            }
            zoneListIndex++;
        }
        updateDate();
    }
}
