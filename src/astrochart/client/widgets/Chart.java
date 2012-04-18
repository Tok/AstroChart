package astrochart.client.widgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import astrochart.client.event.ResetAspectsEvent;
import astrochart.client.event.SetStatusEvent;
import astrochart.shared.data.Epoch;
import astrochart.shared.enums.AspectType;
import astrochart.shared.enums.ChartColor;
import astrochart.shared.enums.ChartProportions;
import astrochart.shared.enums.HouseType;
import astrochart.shared.enums.Planet;
import astrochart.shared.enums.ZodiacSign;
import astrochart.shared.wrappers.AscendentAndOffset;
import astrochart.shared.wrappers.TextPosition;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Chart extends Composite {
	private final HandlerManager eventBus;
	private final Map<Planet, CheckBox> planetCheckBoxes;
	private final Map<AspectType, ListBox> aspectListboxes;
	private final Map<AspectType, CheckBox> aspectCheckBoxes;
	private final Map<AspectType, Label> aspectLabels;
	
	private final VerticalPanel chartPanel = new VerticalPanel();
	private final Canvas canvas = Canvas.createIfSupported();
	private AscendentAndOffset ascendent;
	private Epoch epoch;
	@SuppressWarnings("unused")
    private HouseType houseType;
	private final Map<String, TextPosition> aspects = new HashMap<String, TextPosition>();

	
	public Chart(final HandlerManager eventBus, final Map<Planet, CheckBox> planetCheckBoxes, 
			final Map<AspectType, ListBox> aspectListboxes, final Map<AspectType, CheckBox> aspectCheckBoxes,
			final Map<AspectType, Label> aspectLabels) {
		this.eventBus = eventBus;
		this.planetCheckBoxes = planetCheckBoxes;
		this.aspectListboxes = aspectListboxes;
		this.aspectCheckBoxes = aspectCheckBoxes;
		this.aspectLabels = aspectLabels;
        if (canvas != null) {
        	canvas.setCoordinateSpaceHeight(600);
        	canvas.setCoordinateSpaceWidth(600);
        	chartPanel.add(canvas);
       	} else {
       		chartPanel.add(new Label("Fail: Your browser doesn't support HTML5 Canvas."));
       	}
        
		initWidget(chartPanel);
		setStyleName("chart");
	}

    @SuppressWarnings("unused")
    private final void generateEmptyChart() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				generateChart(new AscendentAndOffset(ZodiacSign.Aries, 0.0D, 270D), null, HouseType.Equal);
			}
		});		
    }
    
    public final void generateChart(final AscendentAndOffset ascendent, final Epoch epoch, final HouseType houseType) {
    	this.ascendent = ascendent;
    	this.epoch = epoch;
    	this.houseType = houseType;
		final Context2d ctx = canvas.getContext2d();
		ctx.setStrokeStyle(CssColor.make(ChartColor.Black.getHex()));
		ctx.setFillStyle(CssColor.make(ChartColor.White.getHex()));
		ctx.setFont("10pt Arial");
		ctx.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight()); //clear ctx

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
		if (houseType.equals(HouseType.Equal) || houseType.equals(HouseType.WholeSigns)) {
			for (final ZodiacSign sign : ZodiacSign.values()) {
				double angle = keepInRange(sign.getEclipticLongitude() + 90D); 
				if (houseType.equals(HouseType.WholeSigns)) {
					angle = angle - (offset % 30D);
				}
				ctx.setLineWidth(0.10);
				drawExcentricLine(ctx, angle, ChartProportions.Inner, ChartProportions.InnerMark); 
				drawExcentricLine(ctx, angle, ChartProportions.OuterEclyptic, ChartProportions.Outer); 
				ctx.setLineWidth(0.8);
				writeExcentricInfo(ctx, 8, String.valueOf(house), angle + 15D, ChartProportions.HouseNumber);
				house++;
			}
		}
		
		if (epoch != null) {
			//place planet information
			ctx.setLineWidth(0.75D);
			for (final Planet planet : Planet.values()) {
				if (planetCheckBoxes.get(planet).getValue()) {
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
			

			/*
			//mark midheaven
			System.out.println("MC: " + ascendent.getMidheaven());
			final double angle = keepInRange(ascendent.getMidheaven());
			drawExcentricLine(ctx, angle, ChartProportions.PlanetMark, ChartProportions.InnerMark); //draw outer mark
			writeExcentricInfo(ctx, 12, "MC", angle, ChartProportions.PlanetSign);
			drawExcentricLine(ctx, angle, ChartProportions.Degree, ChartProportions.Inner); //draw inner mark
			*/
			
			//draw aspects
			eventBus.fireEvent(new ResetAspectsEvent());
			aspects.clear();
			for (final Planet firstPlanet : Planet.values()) {
				if (planetCheckBoxes.get(firstPlanet).getValue()) {
					for (final Planet secondPlanet : Planet.values()) {
						if (planetCheckBoxes.get(secondPlanet).getValue()) {
							drawAspectLine(ctx, firstPlanet, secondPlanet, offset);
						}
					}
				}
			}
			
			//write aspect labels
			ctx.setFont("12pt Arial");			
			final Iterator<Entry<String, TextPosition>> it = aspects.entrySet().iterator();
			while (it.hasNext()) {
				final Entry<String, TextPosition> entry = it.next();
				if (entry.getValue() != null) {
					final TextPosition tp = entry.getValue();
					ctx.setLineWidth(0.4D);
					ctx.fillRect(tp.getX() -1D, tp.getY() - 12D, 14D, 15D);
					ctx.strokeRect(tp.getX() -1D, tp.getY() - 12D, 14D, 15D);
					ctx.setLineWidth(1D);
					ctx.strokeText(tp.getText(), tp.getX(), tp.getY());
					it.remove(); // avoids a ConcurrentModificationException
				}
			}
		}

		eventBus.fireEvent(new SetStatusEvent("Ready."));
    }
	
	private final void drawAspectLine(final Context2d ctx, final Planet firstPlanet, final Planet secondPlanet, final double offset) {
		if (firstPlanet.equals(secondPlanet) ||
			aspects.containsKey(secondPlanet.name() + ":" + firstPlanet.name())) {
			return; //aspect already placed
		}
		
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
		
		double difference = firstAngle - secondAngle;
		if (difference < 0D) {
			difference = difference * -1D;
		}
		AspectType isType = null;
		for (final AspectType type : AspectType.values()) {
			if (difference <= type.getAngle() + getAspectOrb(type) && 
				difference >= type.getAngle() - getAspectOrb(type)) {
				isType = type;
				break;
			}
		}
		TextPosition tp = null;
		if (isType != null) {
			addAspect(isType);
			if (aspectCheckBoxes.get(isType).getValue()) {
				ctx.setLineWidth(2.0D); //Labeled aspects with type
				drawLine(xFirst, yFirst, xSecond, ySecond);
				final double x = ((xFirst + xSecond) / 2D) -6D;
				final double y = ((yFirst + ySecond) / 2D) +6D;
				tp = new TextPosition(String.valueOf(isType.getUnicode()), x, y);
			} else {
				ctx.setLineWidth(1.0D); //Unlabeled Aspects with type
				drawLine(xFirst, yFirst, xSecond, ySecond);				
			}
		} else {
			ctx.setLineWidth(0.2D); //Apsects without type
			drawLine(xFirst, yFirst, xSecond, ySecond);			
		}
		
		aspects.put(firstPlanet.name() + ":" + secondPlanet.name(), tp);
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
		return canvas.getOffsetWidth() / 2;
	}

	private final int getYCenter() {
		return canvas.getOffsetHeight() / 2;
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
		canvas.getContext2d().beginPath();
		final double start = Math.toRadians(startAngle -90D);
		final double end = Math.toRadians(endAngle -90D);		
		canvas.getContext2d().arc(x, y, r, start, end, antiClock);
		canvas.getContext2d().fill();
		canvas.getContext2d().stroke();
		canvas.getContext2d().closePath();
	}

	private void drawAscendentLine(final int xCenter, final int yCenter, final int innerRadius, 
			final AscendentAndOffset asc) {
		drawLine(xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.Inner), yCenter, 
				 xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.InnerMark), yCenter);
		final String text = "ASC " + asc.getAscendent().getUnicode() + " " + asc.getFormattedOffset();
		canvas.getContext2d().strokeText(text, xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.InnerMark) + 5D, yCenter -5D);
    }

    private final void drawLine(final double startX, final double startY, final double endX, final double endY) {
    	canvas.getContext2d().beginPath();
    	canvas.getContext2d().moveTo(startX, startY);
    	canvas.getContext2d().lineTo(endX, endY);
    	canvas.getContext2d().closePath();
    	canvas.getContext2d().stroke();
	}
    
    private final double getAspectOrb(final AspectType type) {
    	final ListBox box = aspectListboxes.get(type);
        final int selection =  box.getSelectedIndex();
        return Double.valueOf(box.getItemText(selection));
    }

    private final void addAspect(final AspectType aspectType) {
    	final Label label = aspectLabels.get(aspectType);
    	int counter = Integer.parseInt(label.getText());
    	counter++;
    	label.setText(String.valueOf(counter));
    }

	public void changeHouseSystem(final HouseType houseType) {
    	this.houseType = houseType;
    	generateChart(ascendent, epoch, houseType);
    }
}