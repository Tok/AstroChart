package astrochart.client.presenter;

import java.util.Date;
import astrochart.client.service.EpochService;
import astrochart.client.service.EpochServiceAsync;
import astrochart.client.service.GeocodeService;
import astrochart.client.service.GeocodeServiceAsync;
import astrochart.client.util.AstrologyUtil;
import astrochart.client.util.DateTimeUtil;
import astrochart.shared.ChartColor;
import astrochart.shared.ChartProportions;
import astrochart.shared.Planet;
import astrochart.shared.ZodiacSign;
import astrochart.shared.data.Epoch;
import astrochart.shared.data.GeocodeData;
import astrochart.shared.wrappers.AscendentAndOffset;
import astrochart.shared.wrappers.BodyPosition;
import astrochart.shared.wrappers.RiseAndSet;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NowPresenter extends AbstractTabPresenter implements Presenter {
	private final EpochServiceAsync epochService = GWT.create(EpochService.class);
	private final GeocodeServiceAsync geocodeService = GWT.create(GeocodeService.class);
	private final DateTimeUtil dateTimeUtil = new DateTimeUtil();
    private final AstrologyUtil astroUtil = new AstrologyUtil();
	
    private final Display display;

	private Epoch epoch;
	private Date localNow;
	private Date utcNow;

    public interface Display {
        Widget asWidget();
		Button getUpdatePositionsButton();
		Button getRegenerateChartButton();
		Canvas getChart();
		Label getNowLabel();
		Label getUtcLabel();
		Label getLocalJdLabel();
		Label getUtcJdLabel();
		Label getLocalSidLabel();
		Label getUtcSidLabel();
		CheckBox getPlanetCheckBox(Planet planet);
		Label getPlanetLabel(Planet planet);
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
    }

    public NowPresenter(final HandlerManager eventBus, final TabPanel tabPanel, final Display view) {
        super(eventBus, tabPanel);
        this.display = view;
    }

    public final void bind() {
    	this.display.getUpdatePositionsButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateEpoch();
			}
		});
    	this.display.getRegenerateChartButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				regenerateChart();
			}
		});    	
    	for (Planet planet : Planet.values()) {
    		this.display.getPlanetCheckBox(planet).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					regenerateChart();
				}
			});
    	}    	
    	this.display.getLocationTextBox().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updateGeodataByCityName();
				}
			}
		});    	
    	this.display.getSubmitCityButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateGeodataByCityName();
			}
		});    	
    	this.display.getLatitudeTextBox().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					processCustomGeocode(true);
				}
			}
		});    	
    	this.display.getSubmitLatitudeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processCustomGeocode(true);
			}
		});    	
    	this.display.getLongitudeTextBox().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					processCustomGeocode(true);
				}
			}
		});    	
    	this.display.getSubmitLongitudeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processCustomGeocode(true);
			}
		});
    }
    
    @Override
    public final void go(final HasWidgets container) {
        bind();
        container.clear();
        container.add(super.getTabPanel());
        
        updateEpoch();
        
		//XXX Experimental HTML5 Geolocation
		//tryToGetGeolocationFromBrowser();
		
        updateGeodataByIp();
		
		this.display.getLocationTextBox().setFocus(true);
    }

    private final void updateEpoch() {
    	display.getStatusLabel().setText("Updating positions.");
    	
        localNow = new Date();
        
		display.getNowLabel().setText(dateTimeUtil.formatLocalDate(localNow));
		display.getUtcLabel().setText(dateTimeUtil.formatDateAsUtc(localNow));

		utcNow = dateTimeUtil.getUtcDate(localNow);
		display.getLocalJdLabel().setText(dateTimeUtil.getFormattedJdTimeDate(localNow));
		display.getUtcJdLabel().setText(dateTimeUtil.getFormattedJdTimeDate(utcNow));

		final Date sidDate = dateTimeUtil.getLocalSidTimeDate(localNow); //also known as LST
		display.getLocalSidLabel().setText(dateTimeUtil.formatLocalDate(sidDate));
		final Date utcSidDate = dateTimeUtil.getLocalSidTimeDate(localNow); //also known as GMST
		display.getUtcSidLabel().setText(dateTimeUtil.formatDateAsUtc(utcSidDate));
		
		epochService.readEpoch(utcSidDate, new AsyncCallback<Epoch>() {
			@Override
			public void onSuccess(Epoch result) {
				epoch = result;
				for (Planet planet : Planet.values()) {
					display.getPlanetLabel(planet).setText(result.getPositionString(planet));
				}
				display.getStatusLabel().setText("Positions updated."); 
				processCustomGeocode(false);
			}
			@Override
			public void onFailure(Throwable caught) {
				display.getStatusLabel().setText("Fail reading epoch: " + caught.getMessage()); 
			}
		});
    }
    
    /**
     * Experimental HTML5 feature
     */
    @SuppressWarnings("unused")
    private final void tryToGetGeolocationFromBrowser() {
		final Geolocation geolocation = Geolocation.getIfSupported(); //experimental
		geolocation.getCurrentPosition(new Callback<Position, PositionError>() {
			@Override
			public void onSuccess(Position result) {
				final Coordinates coords = result.getCoordinates();
				display.getLatitudeTextBox().setText(String.valueOf(coords.getLatitude()));
				display.getLongitudeTextBox().setText(String.valueOf(coords.getLongitude()));
				processCustomGeocode(true);
			}
			@Override
			public void onFailure(PositionError reason) {
				display.getStatusLabel().setText("Fail: Not able to get geolocation data from browser.");
			}
		});
    }

    private final void updateGeodataByIp() {
    	display.getStatusLabel().setText("Updating geodata by IP.");
		geocodeService.getGeocodeDataForIp(new AsyncCallback<GeocodeData>() {
			@Override
            public void onSuccess(GeocodeData result) {
				processGeocodeData(result);
            }
			@Override
            public void onFailure(Throwable caught) {
				display.getStatusLabel().setText("Fail: Getting geocode data from IP. Please enter a city name or provide the latitude and longitude manually.");
            }
		});
    }
    
    private final void updateGeodataByCityName() {
    	display.getStatusLabel().setText("Updating geodata by location name.");
		final String cityName = display.getLocationTextBox().getText().trim();
		geocodeService.getGeocodeData(cityName, new AsyncCallback<GeocodeData>() {
			@Override
            public void onSuccess(GeocodeData result) {
				processGeocodeData(result);
            }
			@Override
            public void onFailure(Throwable caught) {
				display.getStatusLabel().setText("Fail: Getting geocode data.");
            }
		});
    }
    
	private final void processCustomGeocode(final boolean resetCityName) {
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
	
    private final void processGeocodeData(final GeocodeData geocode) {
    	if (geocode == null || (geocode.getCityName().equals("") && geocode.getLatitude() == 0.0D && geocode.getLongitude() == 0.0D)) {
    		generateEmptyChart();    		
    	} else {
    		display.getLocationTextBox().setText(geocode.getCityName());
		
    		final NumberFormat nf = NumberFormat.getFormat("#.0000000");
    		display.getLatitudeTextBox().setText(nf.format(geocode.getLatitude()));
    		display.getLongitudeTextBox().setText(nf.format(geocode.getLongitude()));

    		final RiseAndSet ras = astroUtil.calculateSunRiseAndSet(localNow, geocode.getLatitude(), geocode.getLongitude());
    		display.getSunriseLabel().setText(dateTimeUtil.formatLocalDate(ras.getRise()));
    		display.getSunsetLabel().setText(dateTimeUtil.formatLocalDate(ras.getSet()));
    		final BodyPosition position = astroUtil.calculateSunPosition(utcNow, geocode.getLatitude(), geocode.getLongitude());
    		display.getSunPositionLabel().setText(position.toString());
		
    		final AscendentAndOffset ascendent = astroUtil.determineAscendent(utcNow, geocode.getLongitude(), geocode.getLatitude());
    		display.getAscendentLabel().setText(ascendent.toString());
    		
    		display.getStatusLabel().setText("Generating chart...");
    		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
    			@Override
    			public void execute() {
    	    		generateChart(ascendent);
    			}
    		});
    	}
    }

    private final void regenerateChart() {
		display.getStatusLabel().setText("Generating chart...");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
		    	processCustomGeocode(false);				
			}
		});
    }

    private final void generateEmptyChart() {
		display.getStatusLabel().setText("Generating empty chart...");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				generateChart(new AscendentAndOffset(ZodiacSign.Aries, 0.0D));
			}
		});		
    }
    
	private final void generateChart(final AscendentAndOffset ascendent) {
		final Context2d ctx = display.getChart().getContext2d();
		ctx.setStrokeStyle(CssColor.make(ChartColor.Black.getHex()));
		ctx.setFillStyle(CssColor.make(ChartColor.White.getHex()));
		ctx.setFont("10pt Arial");
		ctx.fillRect(0, 0, display.getChart().getOffsetWidth(), display.getChart().getOffsetHeight()); //clear ctx

		final double offset = ascendent.getOffset() + ascendent.getAscendent().getEclipticLongitude();

		ctx.setLineWidth(1D);
		markFiveDegrees(ctx, offset); 
		markDegrees(ctx, offset);
		drawAscendentLine(
				getXCenter(),  
				getYCenter(),
				ChartProportions.getRadius(getHcs(), ChartProportions.Inner),
				ascendent);
		
		ctx.setLineWidth(2D);
		drawArc(getXCenter(),  
				getYCenter(),
				ChartProportions.getRadius(getHcs(), ChartProportions.Inner),
				0D, 
				360D, 
				false);

		//put zodiac
		ctx.setFont("36pt Arial");
		for (final ZodiacSign sign : ZodiacSign.values()) {
			final double angle = keepInRange(sign.getEclipticLongitude() + 90D - offset);

			//draw colored section
			ctx.beginPath();
			ctx.setFillStyle(CssColor.make(sign.getColor().getHex()));
			final double start = Math.toRadians((sign.getEclipticLongitude() * -1) + 150D +offset);
			final double end = Math.toRadians((sign.getEclipticLongitude() * -1) + 180D +offset);
			ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.OuterEclyptic), start, end, false);
			ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.InnerEclyptic), end, start, true);
			ctx.fill();
			ctx.stroke();
			ctx.closePath();
			
			//draw Signs
			final double xSign = getHcs() - 
					(Math.sin(Math.toRadians(angle + 15D)) * ChartProportions.getRadius(getHcs(), ChartProportions.EclypticCenter)) -23;
			final double ySign = getHcs() - 
					(Math.cos(Math.toRadians(angle + 15D)) * ChartProportions.getRadius(getHcs(), ChartProportions.EclypticCenter)) +18;
			ctx.beginPath();
			ctx.setFillStyle(CssColor.make(ChartColor.White.getHex()));
			ctx.setStrokeStyle(CssColor.make(ChartColor.Black.getHex()));
			ctx.fillText(String.valueOf(sign.getUnicode()), xSign, ySign);
			ctx.strokeText(String.valueOf(sign.getUnicode()), xSign, ySign);
			ctx.closePath();
		}

		//draw houses
		int house = 1;
		for (final ZodiacSign sign : ZodiacSign.values()) {
			final double angle = keepInRange(sign.getEclipticLongitude() + 90D); 
			ctx.setLineWidth(0.10);
			drawExcentricLine(ctx, angle, ChartProportions.Inner, ChartProportions.InnerMark); 
			drawExcentricLine(ctx, angle, ChartProportions.OuterEclyptic, ChartProportions.Outer); 
			ctx.setLineWidth(0.8);
			writeExcentricInfo(ctx, 8, String.valueOf(house), angle + 15D, ChartProportions.HouseNumber);
			house++;
		}
		
		if (epoch != null) {
			//place planet information
			ctx.setLineWidth(0.75D);
			for (final Planet planet : Planet.values()) {
				if (display.getPlanetCheckBox(planet).getValue()) {
					final ZodiacSign sign = ZodiacSign.valueOfAbbrevistion(epoch.getSign(planet));
					final double degrees = epoch.getPreciseDegrees(planet) + sign.getEclipticLongitude();
					final double angle = keepInRange(degrees + 90D - Double.valueOf(offset).intValue());
					drawExcentricLine(ctx, angle, ChartProportions.PlanetMark, ChartProportions.InnerMark); //draw outer planet mark
					writeExcentricInfo(ctx, 12, String.valueOf(planet.getUnicode()), angle, ChartProportions.PlanetSign);
					writeExcentricInfo(ctx, 8, epoch.getDegrees(planet) + String.valueOf('\u00B0'), angle, ChartProportions.Degree);
					writeExcentricInfo(ctx, 8, epoch.getMinutes(planet) + String.valueOf('\u2032'), angle, ChartProportions.Minute);
					drawExcentricLine(ctx, angle, ChartProportions.Inner, ChartProportions.InnerLine); //draw inner planet mark
				}
			}
			
			//draw aspects
			ctx.setLineWidth(0.2D);
			for (final Planet firstPlanet : Planet.values()) {
				if (display.getPlanetCheckBox(firstPlanet).getValue()) {
					for (final Planet secondPlanet : Planet.values()) {
						if (display.getPlanetCheckBox(secondPlanet).getValue()) {
							final ZodiacSign firstSign = ZodiacSign.valueOfAbbrevistion(epoch.getSign(firstPlanet));
							final double firstDegrees = epoch.getPreciseDegrees(firstPlanet) + firstSign.getEclipticLongitude();
							final double firstAngle = keepInRange(firstDegrees + 90D - Double.valueOf(offset).intValue());
							final ZodiacSign secondSign = ZodiacSign.valueOfAbbrevistion(epoch.getSign(secondPlanet));
							final double secondDegrees = epoch.getPreciseDegrees(secondPlanet) + secondSign.getEclipticLongitude();
							final double secondAngle = keepInRange(secondDegrees + 90D - Double.valueOf(offset).intValue()); 
							final double xFirst = getHcs() - 
									(Math.sin(Math.toRadians(firstAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
							final double yFirst = getHcs() - 
									(Math.cos(Math.toRadians(firstAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
							final double xSecond = getHcs() - 
									(Math.sin(Math.toRadians(secondAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
							final double ySecond = getHcs() - 
									(Math.cos(Math.toRadians(secondAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
							drawLine(xFirst, yFirst, xSecond, ySecond);
						}
					}
				}
			}
		}

		display.getStatusLabel().setText("Ready.");
    }
		
	private final void drawExcentricLine(final Context2d ctx, final double angle, 
			final ChartProportions from, final ChartProportions to) {
		final double xFrom = getHcs() - 
				(Math.sin(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), from));
		final double yFrom = getHcs() - 
				(Math.cos(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), from));
		final double xTo = getHcs() - 
				(Math.sin(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), to));
		final double yTo = getHcs() - 
				(Math.cos(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), to));
		drawLine(xFrom, yFrom, xTo, yTo);
	}
	
	private final void writeExcentricInfo(final Context2d ctx, final int textSize, final String text, 
			final double angle, final ChartProportions prop) {
		ctx.setFont(textSize + "pt Arial");
		final double xMinute = getHcs() - 
				(Math.sin(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), prop)) -(textSize/2);
		final double yMinute = getHcs() - 
				(Math.cos(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), prop)) +(textSize/2);
		ctx.strokeText(text, xMinute, yMinute);
	}
	
	private final void markFiveDegrees(final Context2d ctx, final double offset) {
	    for (int angle = 0; angle < 360; angle = angle +5) {
			ctx.beginPath();
			ctx.setFillStyle(CssColor.make(ChartColor.White.getHex()));
			ctx.setStrokeStyle(CssColor.make(ChartColor.Black.getHex()));
			final double start = Math.toRadians(angle + offset);
			final double end = Math.toRadians(angle + offset + 5D);
			ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.InnerEclyptic), start, end, false);
			ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.OuterMark), end, start, true);
			ctx.fill();
			ctx.stroke();
			ctx.closePath();
	    }
    }
	
	private final void markDegrees(final Context2d ctx, final double offset) {
	    for (int angle = 0; angle < 360; angle++) {
			ctx.beginPath();
			ctx.setFillStyle(CssColor.make(ChartColor.White.getHex()));
			ctx.setStrokeStyle(CssColor.make(ChartColor.Black.getHex()));
			final double start = Math.toRadians(angle + offset);
			final double end = Math.toRadians(angle + offset+ 1D);
			ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.OuterMark), start, end, false);
			ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.InnerMark), end, start, true);
			ctx.fill();
			ctx.stroke();
			ctx.closePath();
	    }
    }

	/**
	 * returns the half size of the chart
	 * @return
	 */
	private final int getHcs() {
		if (getXCenter() >= getYCenter()) {
			return getXCenter();
		} else {
			return getYCenter();			
		}
	}
	
	private final int getXCenter() {
		return display.getChart().getOffsetWidth() / 2;
	}

	private final int getYCenter() {
		return display.getChart().getOffsetHeight() / 2;
	}
	
	private final double keepInRange(final double angle) {
		double result = angle;
		while (result < 0D) {
			result = result + 360D;
		}
		result = result % 360;
		return result;
	}
	
	private final void drawArc(final int x, final int y, final int r, 
			final double startAngle, final double endAngle, final boolean antiClock) {
		display.getChart().getContext2d().beginPath();
		final double start = Math.toRadians(startAngle -90D);
		final double end = Math.toRadians(endAngle -90D);		
		display.getChart().getContext2d().arc(x, y, r, start, end, antiClock);
		display.getChart().getContext2d().fill();
		display.getChart().getContext2d().stroke();
		display.getChart().getContext2d().closePath();
	}

	private void drawAscendentLine(final int xCenter, final int yCenter, final int innerRadius, 
			final AscendentAndOffset asc) {
		drawLine(xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.Inner), yCenter, 
				 xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.InnerMark), yCenter);
		final String text = "ASC " + asc.getAscendent().getUnicode() + " " + asc.getFormattedOffset();
		display.getChart().getContext2d().strokeText(text, xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.InnerMark) + 5D, yCenter -5D);
    }

    private final void drawLine(final double startX, final double startY, final double endX, final double endY) {
		display.getChart().getContext2d().beginPath();
		display.getChart().getContext2d().moveTo(startX, startY);
		display.getChart().getContext2d().lineTo(endX, endY);
		display.getChart().getContext2d().closePath();
		display.getChart().getContext2d().stroke();
	}
    
}
