package astrochart.client.presenter;

import java.util.Date;
import astrochart.client.event.DateUpdatedEvent;
import astrochart.client.event.DateUpdatedEventHandler;
import astrochart.client.event.ResetAspectsEvent;
import astrochart.client.event.ResetAspectsEventHandler;
import astrochart.client.event.SetStatusEvent;
import astrochart.client.event.SetStatusEventHandler;
import astrochart.client.service.EpochService;
import astrochart.client.service.EpochServiceAsync;
import astrochart.client.service.GeocodeService;
import astrochart.client.service.GeocodeServiceAsync;
import astrochart.client.util.AstrologyUtil;
import astrochart.client.util.DateTimeUtil;
import astrochart.client.widgets.Chart;
import astrochart.client.widgets.TimeEntry;
import astrochart.shared.data.Epoch;
import astrochart.shared.data.GeocodeData;
import astrochart.shared.enums.AspectType;
import astrochart.shared.enums.HouseType;
import astrochart.shared.enums.Planet;
import astrochart.shared.exceptions.EpochNotFoundException;
import astrochart.shared.wrappers.AscendentAndOffset;
import astrochart.shared.wrappers.BodyPosition;
import astrochart.shared.wrappers.RiseAndSet;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChartPresenter extends AbstractTabPresenter implements Presenter {
    private final EpochServiceAsync epochService = GWT.create(EpochService.class);
    private final GeocodeServiceAsync geocodeService = GWT.create(GeocodeService.class);
    private final AstrologyUtil astroUtil = new AstrologyUtil();
    private final DateTimeUtil dateTimeUtil;
    private final Display display;
    private Date providedUtcDate;
    private Epoch epoch;
    private Date localDate;
    private Date utcDate;
    private boolean disableUpdate = false;

    public interface Display {
        Widget asWidget();
        Button getUpdatePositionsButton();
        Button getRegenerateChartButton();
        Chart getChart();
        Label getUtcLabel();
        Label getUtcJdLabel();
        Label getUtcSidLabel();
        CheckBox getPlanetCheckBox(Planet planet);
        Label getPlanetLabel(Planet planet);
        Button getSelectAllPlanetsButton();
        Button getUnselectAllPlanetsButton();
        ListBox getHousesListBox();
        CheckBox getAspectCheckBox(AspectType type);
        ListBox getAspectListBox(AspectType type);
        Label getAspectLabel(AspectType type);
        void resetAspects();
        Button resetOrbsButton();
        void resetOrbs();
        Button getSelectAllAspectsButton();
        Button getUnselectAllAspectsButton();
        TextBox getLocationTextBox();
        Button getSubmitCityButton();
        TextBox getLatitudeTextBox();
        Button getSubmitLatitudeButton();
        TextBox getLongitudeTextBox();
        Button getSubmitLongitudeButton();
        Label getSunriseLabel();
        Label getSunsetLabel();
        Label getAscendentLabel();
        Label getStatusLabel();
        Label getSunPositionLabel();
        TimeEntry getTimeEntry();
    }

    public ChartPresenter(final HandlerManager eventBus, final DateTimeUtil dateTimeUtil,
            final TabPanel tabPanel, final Display view, final Date providedUtcDate) {
        super(eventBus, tabPanel);
        this.dateTimeUtil = dateTimeUtil;
        this.providedUtcDate = providedUtcDate;
        this.display = view;
    }

    public final void bind() {
        bindBus();
        this.display.getUpdatePositionsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                updateEpoch();
            }
        });
        this.display.getRegenerateChartButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                regenerateChart();
            }
        });
        for (final Planet planet : Planet.values()) {
            this.display.getPlanetCheckBox(planet).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(final ValueChangeEvent<Boolean> event) {
                    regenerateChart();
                }
            });
        }
        this.display.getSelectAllPlanetsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                disableUpdate = true;
                for (final Planet planet : Planet.values()) {
                    display.getPlanetCheckBox(planet).setValue(true);
                }
                disableUpdate = false;
                regenerateChart();
            }
        });
        this.display.getUnselectAllPlanetsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                disableUpdate = true;
                for (final Planet planet : Planet.values()) {
                    display.getPlanetCheckBox(planet).setValue(false);
                }
                disableUpdate = false;
                regenerateChart();
            }
        });
        this.display.getHousesListBox().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                final String houseSystemName = display.getHousesListBox().getItemText(display.getHousesListBox().getSelectedIndex());
                final HouseType type = HouseType.getTypeForName(houseSystemName);
                display.getChart().changeHouseSystem(type);
            }
        });
        for (final AspectType type : AspectType.values()) {
            this.display.getAspectCheckBox(type).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(final ValueChangeEvent<Boolean> event) {
                    regenerateChart();
                }
            });
            this.display.getAspectListBox(type).addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(final ChangeEvent event) {
                    regenerateChart();
                }
            });
        }
        this.display.resetOrbsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                disableUpdate = true;
                display.resetOrbs();
                disableUpdate = false;
                regenerateChart();

            }
        });
        this.display.getSelectAllAspectsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                disableUpdate = true;
                for (final AspectType aspectType : AspectType.values()) {
                    display.getAspectCheckBox(aspectType).setValue(true);
                }
                disableUpdate = false;
                regenerateChart();
            }
        });
        this.display.getUnselectAllAspectsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                disableUpdate = true;
                for (final AspectType aspectType : AspectType.values()) {
                    display.getAspectCheckBox(aspectType).setValue(false);
                }
                disableUpdate = false;
                regenerateChart();
            }
        });
        this.display.getLocationTextBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(final KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    updateGeodataByCityName();
                }
            }
        });
        this.display.getSubmitCityButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                updateGeodataByCityName();
            }
        });
        this.display.getLatitudeTextBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(final KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    processCustomGeocode(true);
                }
            }
        });
        this.display.getSubmitLatitudeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                processCustomGeocode(true);
            }
        });
        this.display.getLongitudeTextBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(final KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    processCustomGeocode(true);
                }
            }
        });
        this.display.getSubmitLongitudeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                processCustomGeocode(true);
            }
        });
    }

    public final void bindBus() {
        getEventBus().addHandler(DateUpdatedEvent.TYPE, new DateUpdatedEventHandler() {
            @Override
            public void onDateUpdated(final DateUpdatedEvent event) {
                updateEpoch(event.getLocalDate());
            }
        });
        getEventBus().addHandler(ResetAspectsEvent.TYPE, new ResetAspectsEventHandler() {
            @Override
            public void onResetAspects(final ResetAspectsEvent event) {
                display.resetAspects();
            }
        });
        getEventBus().addHandler(SetStatusEvent.TYPE, new SetStatusEventHandler() {
            @Override
            public void onSetStatus(final SetStatusEvent event) {
                display.getStatusLabel().setText(event.getStatusMessage());
            }
        });
    }

    @Override
    public final void go(final HasWidgets container) {
        bind();
        container.clear();
        container.add(super.getTabPanel());
        if (providedUtcDate != null) {
            // fill time entry with provided date (calls updateEpoch)
            display.getTimeEntry().updateDate(providedUtcDate);
        } else {
            updateEpoch();
        }

        // XXX Experimental HTML5 Geolocation
        // tryToGetGeolocationFromBrowser();

        if (display.getLocationTextBox().getText().equals("unknown")) {
            updateGeodataByIp();
        }
        this.display.getLocationTextBox().setFocus(true);
    }

    private void updateEpoch() {
        updateEpoch(null);
    }

    private void updateEpoch(final Date inputLocalDate) {
        display.getStatusLabel().setText("Updating positions.");

        if (inputLocalDate != null) {
            localDate = inputLocalDate;
        } else {
            localDate = display.getTimeEntry().getLocalDate();
        }
        display.getUtcLabel().setTitle(
                "As Time in your local timezone: \n" + dateTimeUtil.formatLocalDate(localDate) + display.getTimeEntry().getClientTimezone());
        display.getUtcLabel().setText(dateTimeUtil.formatDateAsUtc(localDate));

        utcDate = dateTimeUtil.getUtcDate(localDate);
        display.getUtcJdLabel().setTitle(
                "As JD Time in your local timezone: \n" + dateTimeUtil.getFormattedJdTimeDate(localDate) + display.getTimeEntry().getClientTimezone());
        display.getUtcJdLabel().setText(dateTimeUtil.getFormattedJdTimeDate(utcDate));

        final Date sidDate = dateTimeUtil.getLocalSidTimeDate(localDate); // also known as LST
        final Date utcSidDate = dateTimeUtil.getLocalSidTimeDate(localDate); // also known as GMST
        display.getUtcSidLabel().setTitle(
                "As Sidereal Time in your local timezone: \n" + dateTimeUtil.formatLocalDate(sidDate) + display.getTimeEntry().getClientTimezone());
        display.getUtcSidLabel().setText(dateTimeUtil.formatDateAsUtc(utcSidDate));

        epochService.readEpoch(utcSidDate, new AsyncCallback<Epoch>() {
            @Override
            public void onSuccess(final Epoch result) {
                epoch = result;
                for (Planet planet : Planet.values()) {
                    final Label label = display.getPlanetLabel(planet);
                    label.setText(result.getPositionDegreeString(planet));
                    label.setTitle(result.getPositionString(planet));
                }
                display.getStatusLabel().setText("Positions updated.");
                processCustomGeocode(false);
            }
            @Override
            public void onFailure(final Throwable caught) {
                if (caught instanceof EpochNotFoundException) {
                    display.getStatusLabel().setText("Epoch not found. Please try another date.");
                } else {
                    display.getStatusLabel().setText("Fail reading epoch: " + caught.getMessage());
                }
            }
        });
    }

    /**
     * Experimental HTML5 feature
     */
    @SuppressWarnings("unused")
    private void tryToGetGeolocationFromBrowser() {
        final Geolocation geolocation = Geolocation.getIfSupported(); //experimental
        geolocation.getCurrentPosition(new Callback<Position, PositionError>() {
            @Override
            public void onSuccess(final Position result) {
                final Coordinates coords = result.getCoordinates();
                display.getLatitudeTextBox().setText(String.valueOf(coords.getLatitude()));
                //display.getLatitudeTextBox().setTitle(); // TODO convert to deg + mins + seconds
                display.getLongitudeTextBox().setText(String.valueOf(coords.getLongitude()));
                //display.getLongitudeTextBox().setTitle(); // TODO convert to deg + mins + seconds
                processCustomGeocode(true);
            }
            @Override
            public void onFailure(final PositionError reason) {
                display.getStatusLabel().setText("Fail: Not able to get geolocation data from browser.");
            }
        });
    }

    private void updateGeodataByIp() {
        display.getStatusLabel().setText("Updating geodata by IP.");
        geocodeService.getGeocodeDataForIp(new AsyncCallback<GeocodeData>() {
            @Override
            public void onSuccess(final GeocodeData result) {
                processGeocodeData(result);
            }
            @Override
            public void onFailure(final Throwable caught) {
                display.getStatusLabel().setText("Fail: Getting geocode data from IP. Please enter a city name or provide the latitude and longitude manually.");
            }
        });
    }

    private void updateGeodataByCityName() {
        display.getStatusLabel().setText("Updating geodata by location name.");
        final String cityName = display.getLocationTextBox().getText().trim();
        geocodeService.getGeocodeData(cityName, new AsyncCallback<GeocodeData>() {
            @Override
            public void onSuccess(final GeocodeData result) {
                processGeocodeData(result);
            }
            @Override
            public void onFailure(final Throwable caught) {
                display.getStatusLabel().setText("Fail: Getting geocode data.");
            }
        });
    }

    private void processCustomGeocode(final boolean resetCityName) {
        display.getStatusLabel().setText("Processing custom geocode data.");
        final GeocodeData geocode = new GeocodeData();
        if (resetCityName) {
            geocode.setCityName("user input");
            this.display.getLocationTextBox().setText("user input");
        } else {
            geocode.setCityName(display.getLocationTextBox().getText());
        }
        this.display.getStatusLabel().setText("");
        try {
            geocode.setLatitude(Double.valueOf(display.getLatitudeTextBox().getText()));
            try {
                geocode.setLongitude(Double.valueOf(display.getLongitudeTextBox().getText()));
                processGeocodeData(geocode);
            } catch (NumberFormatException nfe) {
                final String message = "Fail: Longitude is not numeric.";
                this.display.getStatusLabel().setText(message);
                this.display.getLongitudeTextBox().setFocus(true);
            }
        } catch (NumberFormatException nfe) {
            final String message = "Fail: Latitide is not numeric.";
            this.display.getStatusLabel().setText(message);
            this.display.getLatitudeTextBox().setFocus(true);
        }
    }

    private void processGeocodeData(final GeocodeData geocode) {
        if (geocode == null || (geocode.getCityName().equals("") && geocode.getLatitude() == 0.0D && geocode.getLongitude() == 0.0D)) {
            assert true; //ignore
        } else {
            display.getLocationTextBox().setText(geocode.getCityName());

            final NumberFormat nf = NumberFormat.getFormat("0.0000000");
            display.getLatitudeTextBox().setText(nf.format(geocode.getLatitude()));
            display.getLongitudeTextBox().setText(nf.format(geocode.getLongitude()));

            final RiseAndSet ras = astroUtil.calculateSunRiseAndSet(localDate, geocode.getLatitude(), geocode.getLongitude());
            display.getSunriseLabel().setText(dateTimeUtil.formatLocalDate(ras.getRise()));
            display.getSunsetLabel().setText(dateTimeUtil.formatLocalDate(ras.getSet()));
            final BodyPosition position = astroUtil.calculateSunPosition(utcDate, geocode.getLatitude(), geocode.getLongitude());
            display.getSunPositionLabel().setText(position.toString());

            final AscendentAndOffset ascendent = astroUtil.determineAscendent(utcDate, geocode.getLongitude(), geocode.getLatitude());
            display.getAscendentLabel().setText(ascendent.toString());

            display.getStatusLabel().setText("Generating chart...");
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    final String houseSystemName = display.getHousesListBox().getItemText(display.getHousesListBox().getSelectedIndex());
                    final HouseType type = HouseType.getTypeForName(houseSystemName);
                    display.getChart().generateChart(ascendent, epoch, type);
                }
            });
        }
    }

    public final void regenerateChart() {
        if (disableUpdate) {
            return;
        }
        display.getStatusLabel().setText("Generating chart...");
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                processCustomGeocode(false);
            }
        });
    }

}
