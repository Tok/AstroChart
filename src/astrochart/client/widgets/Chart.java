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
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 600;
    private static final int TEXT_SIZE_LARGER = 12;
    private static final int TEXT_SIZE_LARGE = 8;
    private static final double LINE_WIDTH_LIGHT = 0.8;
    private static final double LINE_WIDTH_LIGHTER = 0.75D;
    private static final double LINE_WIDTH_ALMOSTSUPERLIGHT = 0.40;
    private static final double LINE_WIDTH_SUPERLIGHT = 0.20;
    private static final double LINE_WIDTH_HYPERLIGHT = 0.10;
    private static final int Y_OFFSET = 18;
    private static final int X_OFFSET = 23;
    private static final double FULL_CIRCLE = 360D;
    private static final double RIGHT_ANGLE = 90D;
    private static final double DEGREES_PER_SIGN = 30D;
    private static final double DEGREES_PER_HALF_SIGN = DEGREES_PER_SIGN / 2;
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

    public Chart(final HandlerManager eventBus, final Map<Planet, CheckBox> planetCheckBoxes, final Map<AspectType, ListBox> aspectListboxes,
            final Map<AspectType, CheckBox> aspectCheckBoxes, final Map<AspectType, Label> aspectLabels) {
        this.eventBus = eventBus;
        this.planetCheckBoxes = planetCheckBoxes;
        this.aspectListboxes = aspectListboxes;
        this.aspectCheckBoxes = aspectCheckBoxes;
        this.aspectLabels = aspectLabels;
        if (canvas != null) {
            canvas.setCoordinateSpaceWidth(CANVAS_WIDTH);
            canvas.setCoordinateSpaceHeight(CANVAS_HEIGHT);
            chartPanel.add(canvas);
        } else {
            chartPanel.add(new Label("Fail: Your browser doesn't support HTML5 Canvas."));
        }
        initWidget(chartPanel);
        setStyleName("chart");
    }

    @SuppressWarnings("unused")
    private void generateEmptyChart() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                generateChart(new AscendentAndOffset(ZodiacSign.Aries, 0.0D, ((FULL_CIRCLE * 3) / 4)), null, HouseType.Equal);
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
                FULL_CIRCLE,
                false);

        //put zodiac
        ctx.setFont("36pt Arial");
        for (final ZodiacSign sign : ZodiacSign.values()) {
            final double angle = keepInRange(sign.getEclipticLongitude() + RIGHT_ANGLE - offset);

            //draw colored section
            ctx.beginPath();
            ctx.setFillStyle(CssColor.make(sign.getColor().getHex()));
            final double start = Math.toRadians((sign.getEclipticLongitude() * -1) + (FULL_CIRCLE / 2) - DEGREES_PER_SIGN + offset);
            final double end = Math.toRadians((sign.getEclipticLongitude() * -1) + (FULL_CIRCLE / 2) + offset);
            ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.OuterEclyptic), start, end, false);
            ctx.arc(getXCenter(), getYCenter(), ChartProportions.getRadius(getHcs(), ChartProportions.InnerEclyptic), end, start, true);
            ctx.fill();
            ctx.stroke();
            ctx.closePath();

            //draw Signs
            final double xSign = getHcs()
                    - (Math.sin(Math.toRadians(angle + DEGREES_PER_HALF_SIGN))
                            * ChartProportions.getRadius(getHcs(), ChartProportions.EclypticCenter)) - X_OFFSET;
            final double ySign = getHcs()
                    - (Math.cos(Math.toRadians(angle + DEGREES_PER_HALF_SIGN))
                            * ChartProportions.getRadius(getHcs(), ChartProportions.EclypticCenter)) + Y_OFFSET;
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
                double angle = keepInRange(sign.getEclipticLongitude() + RIGHT_ANGLE);
                if (houseType.equals(HouseType.WholeSigns)) {
                    angle = angle - (offset % DEGREES_PER_SIGN);
                }
                ctx.setLineWidth(LINE_WIDTH_HYPERLIGHT);
                drawExcentricLine(ctx, angle, ChartProportions.Inner, ChartProportions.InnerMark);
                drawExcentricLine(ctx, angle, ChartProportions.OuterEclyptic, ChartProportions.Outer);
                ctx.setLineWidth(LINE_WIDTH_LIGHT);
                writeExcentricInfo(ctx, TEXT_SIZE_LARGE, String.valueOf(house), angle + DEGREES_PER_HALF_SIGN, ChartProportions.HouseNumber);
                house++;
            }
        }

        if (epoch != null) {
            //place planet information
            ctx.setLineWidth(LINE_WIDTH_LIGHTER);
            for (final Planet planet : Planet.values()) {
                if (planetCheckBoxes.get(planet).getValue()) {
                    final ZodiacSign sign = ZodiacSign.valueOfAbbrevistion(epoch.getSign(planet));
                    final double degrees = epoch.getPreciseDegrees(planet) + sign.getEclipticLongitude();
                    final double angle = keepInRange(degrees + RIGHT_ANGLE - Double.valueOf(offset).intValue());
                    drawExcentricLine(ctx, angle, ChartProportions.PlanetMark, ChartProportions.InnerMark); //draw outer planet mark
                    writeExcentricInfo(ctx, TEXT_SIZE_LARGER, String.valueOf(planet.getUnicode()), angle, ChartProportions.PlanetSign);
                    writeExcentricInfo(ctx, TEXT_SIZE_LARGE, epoch.getDegrees(planet) + String.valueOf('\u00B0'), angle, ChartProportions.Degree);
                    writeExcentricInfo(ctx, TEXT_SIZE_LARGE, epoch.getMinutes(planet) + String.valueOf('\u2032'), angle, ChartProportions.Minute);
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
                    ctx.setLineWidth(LINE_WIDTH_ALMOSTSUPERLIGHT);
                    ctx.fillRect(tp.getX() - 1D, tp.getY() - DEGREES_PER_HALF_SIGN - 3, DEGREES_PER_HALF_SIGN - 1, DEGREES_PER_HALF_SIGN);
                    ctx.strokeRect(tp.getX() - 1D, tp.getY() - DEGREES_PER_HALF_SIGN - 3, DEGREES_PER_HALF_SIGN - 1, DEGREES_PER_HALF_SIGN);
                    ctx.setLineWidth(1D);
                    ctx.strokeText(tp.getText(), tp.getX(), tp.getY());
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        eventBus.fireEvent(new SetStatusEvent("Ready."));
    }

    private void drawAspectLine(final Context2d ctx, final Planet firstPlanet, final Planet secondPlanet, final double offset) {
        if (firstPlanet.equals(secondPlanet)
                || aspects.containsKey(secondPlanet.name() + ":" + firstPlanet.name())) {
            return; //aspect already placed
        }

        final ZodiacSign firstSign = ZodiacSign.valueOfAbbrevistion(epoch.getSign(firstPlanet));
        final double firstDegrees = epoch.getPreciseDegrees(firstPlanet) + firstSign.getEclipticLongitude();
        final double firstAngle = keepInRange(firstDegrees + RIGHT_ANGLE - Double.valueOf(offset).intValue());
        final ZodiacSign secondSign = ZodiacSign.valueOfAbbrevistion(epoch.getSign(secondPlanet));
        final double secondDegrees = epoch.getPreciseDegrees(secondPlanet) + secondSign.getEclipticLongitude();
        final double secondAngle = keepInRange(secondDegrees + RIGHT_ANGLE - Double.valueOf(offset).intValue());

        final double xFirst = getHcs()
                - (Math.sin(Math.toRadians(firstAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
        final double yFirst = getHcs()
                - (Math.cos(Math.toRadians(firstAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
        final double xSecond = getHcs()
                - (Math.sin(Math.toRadians(secondAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));
        final double ySecond = getHcs()
                - (Math.cos(Math.toRadians(secondAngle)) * ChartProportions.getRadius(getHcs(), ChartProportions.Inner));

        double difference = firstAngle - secondAngle;
        if (difference < 0D) {
            difference = difference * -1D;
        }
        AspectType isType = null;
        for (final AspectType type : AspectType.values()) {
            if (difference <= type.getAngle() + getAspectOrb(type)
                    && difference >= type.getAngle() - getAspectOrb(type)) {
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
                final double x = ((xFirst + xSecond) / 2D) - 6D;
                final double y = ((yFirst + ySecond) / 2D) + 6D;
                tp = new TextPosition(String.valueOf(isType.getUnicode()), x, y);
            } else {
                ctx.setLineWidth(1.0D); //Unlabeled Aspects with type
                drawLine(xFirst, yFirst, xSecond, ySecond);
            }
        } else {
            ctx.setLineWidth(LINE_WIDTH_SUPERLIGHT); //Apsects without type
            drawLine(xFirst, yFirst, xSecond, ySecond);
        }
        aspects.put(firstPlanet.name() + ":" + secondPlanet.name(), tp);
    }

    private void drawExcentricLine(final Context2d ctx, final double angle, final ChartProportions from, final ChartProportions to) {
        final double xFrom = getHcs() - (Math.sin(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), from));
        final double yFrom = getHcs() - (Math.cos(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), from));
        final double xTo = getHcs() - (Math.sin(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), to));
        final double yTo = getHcs() - (Math.cos(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), to));
        drawLine(xFrom, yFrom, xTo, yTo);
    }

    private void writeExcentricInfo(final Context2d ctx, final int textSize, final String text, final double angle, final ChartProportions prop) {
        ctx.setFont(textSize + "pt Arial");
        final double xMinute = getHcs() - (Math.sin(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), prop)) - (textSize / 2);
        final double yMinute = getHcs() - (Math.cos(Math.toRadians(angle)) * ChartProportions.getRadius(getHcs(), prop)) + (textSize / 2);
        ctx.strokeText(text, xMinute, yMinute);
    }

    private void markFiveDegrees(final Context2d ctx, final double offset) {
        for (int angle = 0; angle < FULL_CIRCLE; angle = angle + 5) {
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

    private void markDegrees(final Context2d ctx, final double offset) {
        for (int angle = 0; angle < FULL_CIRCLE; angle++) {
            ctx.beginPath();
            ctx.setFillStyle(CssColor.make(ChartColor.White.getHex()));
            ctx.setStrokeStyle(CssColor.make(ChartColor.Black.getHex()));
            final double start = Math.toRadians(angle + offset);
            final double end = Math.toRadians(angle + offset + 1D);
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
    private int getHcs() {
        if (getXCenter() >= getYCenter()) {
            return getXCenter();
        } else {
            return getYCenter();
        }
    }

    private int getXCenter() {
        return canvas.getOffsetWidth() / 2;
    }

    private int getYCenter() {
        return canvas.getOffsetHeight() / 2;
    }

    private double keepInRange(final double angle) {
        double result = angle;
        while (result < 0D) {
            result = result + FULL_CIRCLE;
        }
        result = result % FULL_CIRCLE;
        return result;
    }

    private void drawArc(final int x, final int y, final int r,
            final double startAngle, final double endAngle, final boolean antiClock) {
        canvas.getContext2d().beginPath();
        final double start = Math.toRadians(startAngle - RIGHT_ANGLE);
        final double end = Math.toRadians(endAngle - RIGHT_ANGLE);
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
        canvas.getContext2d().strokeText(text, xCenter - ChartProportions.getRadius(getHcs(), ChartProportions.InnerMark) + 5D, yCenter - 5D);
    }

    private void drawLine(final double startX, final double startY, final double endX, final double endY) {
        canvas.getContext2d().beginPath();
        canvas.getContext2d().moveTo(startX, startY);
        canvas.getContext2d().lineTo(endX, endY);
        canvas.getContext2d().closePath();
        canvas.getContext2d().stroke();
    }

    private double getAspectOrb(final AspectType type) {
        final ListBox box = aspectListboxes.get(type);
        final int selection =  box.getSelectedIndex();
        return Double.valueOf(box.getItemText(selection));
    }

    private void addAspect(final AspectType aspectType) {
        final Label label = aspectLabels.get(aspectType);
        int counter = Integer.parseInt(label.getText());
        counter++;
        label.setText(String.valueOf(counter));
    }

    public final void changeHouseSystem(final HouseType houseType) {
        this.houseType = houseType;
        generateChart(ascendent, epoch, houseType);
    }
}
