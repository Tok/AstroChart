package astrochart.client.widgets;

import java.util.Date;
import astrochart.client.event.DateUpdatedEvent;
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
	@SuppressWarnings("unused")
    private final HandlerManager eventBus;
	private final FlexTable flex = new FlexTable();
    private final TextBox yearTextBox = new TextBox();
    private final ListBox monthListBox = new ListBox();
    private final TextBox dayTextBox = new TextBox();
    private final TextBox hoursTextBox = new TextBox();
    private final TextBox minutesTextBox = new TextBox();
    private final TextBox secondsTextBox = new TextBox();
    private final ListBox timeZoneListBox = new ListBox();
    private final Button updateButton = new Button("Submit");
    private final Label statusLabel = new Label(String.valueOf('\u00A0')); //NBSP
    private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
    private int clientOffset;
    private Date localDate; //local date of the client, not the ListBox
    
	public TimeEntry(final HandlerManager eventBus) {
		this.eventBus = eventBus;
		localDate = new Date(); 
		
		flex.setText(0, 0, "Year");
		flex.setText(0, 1, "Month");
		flex.setText(0, 2, "Day");
		flex.setText(0, 3, "Hours");
		flex.setText(0, 4, "Minutes");
		flex.setText(0, 5, "Seconds");
		flex.setText(0, 6, "Timezone");
		flex.setText(0, 7, "Action");

		yearTextBox.setText(String.valueOf(getYear()));
		yearTextBox.setWidth("50px");
		flex.setWidget(1, 0, yearTextBox);
		int monthListIndex = 0;
		for (final Month month : Month.values()) {
			monthListBox.addItem(month.name());
			if (getMonth() == month.getNumber()) {
				monthListBox.setSelectedIndex(monthListIndex);
			}
			monthListIndex++;
		}
		flex.setWidget(1, 1, monthListBox);
		dayTextBox.setWidth("50px");
		dayTextBox.setText(String.valueOf(getDay()));
		flex.setWidget(1, 2, dayTextBox);
		
		hoursTextBox.setWidth("50px");
		hoursTextBox.setText(String.valueOf(getHours()));
		flex.setWidget(1, 3, hoursTextBox);
		minutesTextBox.setWidth("50px");
		minutesTextBox.setText(String.valueOf(getMinutes()));
		flex.setWidget(1, 4, minutesTextBox);
		secondsTextBox.setWidth("50px");
		secondsTextBox.setText(String.valueOf(getSeconds()));
		flex.setWidget(1, 5, secondsTextBox);
		double timeZoneOffset = DOM.getElementById("timeZone").getPropertyDouble("value");
		int zoneListIndex = 0;
		for (final TimeZone zone : TimeZone.values()) {
			timeZoneListBox.addItem(zone.getName());
			if (Double.valueOf(timeZoneOffset * 60).intValue() == zone.getUtcOffsetMinutes()) {
				clientOffset = Double.valueOf(timeZoneOffset * 60).intValue();
				timeZoneListBox.setSelectedIndex(zoneListIndex);
			}
			zoneListIndex++;
		}
		flex.setWidget(1, 6, timeZoneListBox);
		updateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateDate();
				eventBus.fireEvent(new DateUpdatedEvent());
			}
		});
		flex.setWidget(1, 7, updateButton);

		final TimeZone timeZone = TimeZone.getTimeZoneForName(timeZoneListBox.getValue(timeZoneListBox.getSelectedIndex()));
		statusLabel.setText(dateTimeFormat.format(localDate) + " " + timeZone.getName());
		flex.setWidget(2, 0, statusLabel);
		flex.getFlexCellFormatter().setColSpan(2, 0, 8);
		
		
		initWidget(flex);
		setStyleName("time-entry");
	}

	private final int getYear() {
		return Integer.valueOf(dateTimeFormat.format(localDate).substring(0,4));
	}
	
	private final int getMonth() {
		return Integer.valueOf(dateTimeFormat.format(localDate).substring(5,7));
	}

	private final int getDay() {
		return Integer.valueOf(dateTimeFormat.format(localDate).substring(8,10));
	}

	private final int getHours() {
		return Integer.valueOf(dateTimeFormat.format(localDate).substring(11,13));
	}
	
	private final int getMinutes() {
		return Integer.valueOf(dateTimeFormat.format(localDate).substring(14,16));
	}

	private final int getSeconds() {
		return Integer.valueOf(dateTimeFormat.format(localDate).substring(17,19));
	}

	private final void updateDate() {
		//"yyyy.MM.dd HH:mm:ss"
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
//			final com.google.gwt.i18n.client.TimeZone tzUtc = com.google.gwt.i18n.client.TimeZone.createTimeZone(0);
//			final com.google.gwt.i18n.client.TimeZone tz = com.google.gwt.i18n.client.TimeZone.createTimeZone(timeZone.getUtcOffsetMinutes());
			final Date boxDate = new Date(tempDate.getTime() - (timeZone.getUtcOffsetMinutes() * 60000L));
			localDate = new Date(boxDate.getTime() + (clientOffset * 60000L));
			final StringBuilder status = new StringBuilder();
			status.append(dateTimeFormat.format(tempDate));
			status.append(" ");
			status.append(timeZone.getName());
			if (timeZone.getUtcOffsetMinutes() != clientOffset) {
				status.append(" --> ");
				status.append(dateTimeFormat.format(localDate));
				status.append(" UTC+");
				status.append(clientOffset / 60);
			}
			if (timeZone.getUtcOffsetMinutes() != 0) {
				status.append(" --> ");
				status.append(dateTimeFormat.format(boxDate));
				status.append(" UTC ");
			}
			statusLabel.setText(status.toString());
		} catch (IllegalArgumentException iae) {
			statusLabel.setText(iae.getMessage());
		}
	}
	
	private final void validateSeconds() throws IllegalArgumentException {
		int minutes = Integer.valueOf(secondsTextBox.getText());
		if (minutes < 0 || minutes > 59) {
			secondsTextBox.setFocus(true);
			throw new IllegalArgumentException("Seconds are out of range.");
		}
	}
	
	private final void validateMinutes() throws IllegalArgumentException {
		int minutes = Integer.valueOf(minutesTextBox.getText());
		if (minutes < 0 || minutes > 59) {
			minutesTextBox.setFocus(true);
			throw new IllegalArgumentException("Minutes are out of range.");
		}
	}
	
	private final void validateHours() throws IllegalArgumentException {
		int hours = Integer.valueOf(hoursTextBox.getText());
		if (hours < 0 || hours > 23) {
			hoursTextBox.setFocus(true);
			throw new IllegalArgumentException("Hour is out of range.");
		}
	}
	
	private final void validateYear() throws IllegalArgumentException {
		int year = Integer.valueOf(yearTextBox.getText());
		if (year < 1900 || year > 2100) {
			yearTextBox.setFocus(true);
			throw new IllegalArgumentException("Year is out of range.");
		}
	}

	private final void validateDay() throws IllegalArgumentException {
		int day = Integer.valueOf(dayTextBox.getText());
		final Month month = Month.valueOf(monthListBox.getValue(monthListBox.getSelectedIndex()));
		if (day < 1 || day > month.getDays()) {
			int year = Integer.valueOf(yearTextBox.getText());
			if (day == month.getDays() +1 && DateTimeUtil.isLeapYear(year) && month.equals(Month.February)) {
				dayTextBox.setFocus(true);
				throw new IllegalArgumentException("Day is out of range. (" + year + " is a leap year).");
			} else {
				dayTextBox.setFocus(true);
				throw new IllegalArgumentException("Day is out of range.");
			}
		}
	}
	
	private final String formatToTwoDigits(final String in) {
		if (in.length() == 1) {
			return "0" + in;
		} else {
			return in;
		}
	}

	public Date getLocalDate() {
	    return localDate;
    }
	
	public String getClientTimezone() {
	    return " UTC+" + (clientOffset / 60);
    }
}
