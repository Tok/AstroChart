package astrochart.client.view;

import java.util.HashMap;
import java.util.Map;
import astrochart.client.presenter.NowPresenter;
import astrochart.shared.AspectType;
import astrochart.shared.Planet;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NowView extends Composite implements NowPresenter.Display {
    private final FlexTable contentTable = new FlexTable();
    private final Button updatePositionsButton = new Button("Update Positions");
    private final Button regenerateChartButton = new Button("Regenerate Chart");
    private final TextBox locationTextBox = new TextBox();
    private final Button submitCityButton = new Button("Get");
    private final TextBox latitudeTextBox = new TextBox();
    private final Button submitLatitudeButton = new Button("Set");
    private final TextBox longitudeTextBox = new TextBox();
    private final Button submitLongitudeButton = new Button("Set");
    private final Label nowLabel = new Label();
    private final Label utcLabel = new Label();
    private final Label localJdLabel = new Label();
    private final Label utcJdLabel = new Label();
    private final Label sidLabel = new Label();
    private final Label utcSidLabel = new Label();
    private final Label sunriseLabel = new Label();
    private final Label sunsetLabel = new Label();
    private final Label sunPositionLabel = new Label();
    private final Label ascendentLabel = new Label();

    private final Canvas chart = Canvas.createIfSupported();
    private final Label statusLabel = new Label();
    
    private final Map<Planet, CheckBox> planetCheckBoxes = new HashMap<Planet, CheckBox>();
    private final Map<Planet, Label> planetLabels = new HashMap<Planet, Label>();
    private final Map<AspectType, CheckBox> aspectCheckBoxes = new HashMap<AspectType, CheckBox>();
    private final Map<AspectType, Label> aspectLabels = new HashMap<AspectType, Label>();
    
    private int row;
    
    public NowView() {
        final DecoratorPanel contentTableDecorator = new DecoratorPanel();
        contentTableDecorator.setWidth("1010px");
        initWidget(contentTableDecorator);
        
        row = 0;
        final HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setSpacing(5);
        buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        final HorizontalPanel locPanel = new HorizontalPanel();
        locPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        locationTextBox.setText("unknown");
        locationTextBox.setWidth("240px");
        locPanel.add(new Label("Location: "));
        locPanel.add(locationTextBox);
        locPanel.add(submitCityButton);
        buttonPanel.add(locPanel);
        
        final HorizontalPanel latPanel = new HorizontalPanel();
        latPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        latitudeTextBox.setText("0.0000000");
        latitudeTextBox.setWidth("80px");
        latPanel.add(new Label("Latitude: "));
        latPanel.add(latitudeTextBox);
        latPanel.add(submitLatitudeButton);
        buttonPanel.add(latPanel);

        final HorizontalPanel longPanel = new HorizontalPanel();
        longPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        longitudeTextBox.setText("0.0000000");
        longitudeTextBox.setWidth("80px");
        longPanel.add(new Label("Longitude: "));
        longPanel.add(longitudeTextBox);
        longPanel.add(submitLongitudeButton);
        buttonPanel.add(longPanel);
        
        buttonPanel.add(updatePositionsButton);
        buttonPanel.add(regenerateChartButton);
        contentTable.setWidget(row, 0, buttonPanel);
        contentTable.getFlexCellFormatter().setColSpan(row, 0, 3);
        row++;
        
        if (chart != null) {
       	  	chart.setCoordinateSpaceHeight(600);
       	  	chart.setCoordinateSpaceWidth(600);
          	contentTable.setWidget(row, 0, chart);
       	} else {
       		contentTable.setText(row, 0, "Fail: Your browser doesn't support HTML5 Canvas.");
       	}
        
    	contentTable.setText(row, 1, "Local Time: ");
    	contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
    	contentTable.setWidget(row, 2, nowLabel);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
    	row++;
    	
        addRow("UTC Time: ", utcLabel);
        addRow("Local JD Time: ", localJdLabel);
        addRow("UTC JD Time: ", utcJdLabel);
        addRow("Local Sidereal Time: ", sidLabel);
        addRow("UTC Sidereal Time: ", utcSidLabel);
        
        contentTable.setWidget(row, 0, new HTML("&nbsp;"));
        row++;
        
        for (final Planet planet : Planet.values()) {
        	final HorizontalPanel pan = new HorizontalPanel();
        	final CheckBox planetCheckBox = new CheckBox();
        	if (planet.isBody() && !planet.isOuter()) {
        		planetCheckBox.setValue(true);
        	}
        	planetCheckBoxes.put(planet, planetCheckBox);
        	final Label planetLabel = new Label();
        	planetLabels.put(planet, planetLabel);
        	pan.add(planetCheckBox);
        	pan.add(new Label(planet.getUnicode() + " " + planet.name() + ": "));
        	addWidgetRow(pan, planetLabel);
        }

        contentTable.setWidget(row, 0, new HTML("&nbsp;"));
        row++;
        
        for (final AspectType aspectType : AspectType.values()) {
        	final HorizontalPanel pan = new HorizontalPanel();
        	final CheckBox aspectCheckBox = new CheckBox();
        	aspectCheckBox.setValue(true);
        	aspectCheckBoxes.put(aspectType, aspectCheckBox);
        	final Label aspectLabel = new Label();
        	aspectLabels.put(aspectType, aspectLabel);
        	aspectLabel.setText("0");
        	pan.add(aspectCheckBox);
        	pan.add(new Label(aspectType.getUnicode() + " " + aspectType.name() + "s: "));
        	addWidgetRow(pan, aspectLabel);
        }
        
        contentTable.setWidget(row, 0, new HTML("&nbsp;"));
        row++;

        addRow("Sunrise Time: ", sunriseLabel);
        addRow("Sunset Time: ", sunsetLabel);
        addRow("Sun Position: ", sunPositionLabel);
        addRow("Tropical Ascendent: ", ascendentLabel);
        
        contentTable.setWidget(row, 0, statusLabel);
        contentTable.getFlexCellFormatter().setColSpan(row, 0, 3);

        contentTable.getFlexCellFormatter().setRowSpan(1, 0, row -1); //chart
        
        contentTableDecorator.add(contentTable);
    }

    private final void addRow(final String label, final Widget widget) {
    	contentTable.setText(row, 0, label);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
    	contentTable.setWidget(row, 1, widget);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
    	row++;
    }
    
    private final void addWidgetRow(final Widget first, final Widget second) {
    	contentTable.setWidget(row, 0, first);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
    	contentTable.setWidget(row, 1, second);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
    	row++;
    }
    
    @SuppressWarnings("unused")
    private final void addRow(final String label, final Widget firstWidget, final Widget secondWidget) {
        contentTable.setText(row, 0, label);
        contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
        contentTable.setWidget(row, 1, firstWidget);
        contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
        contentTable.setWidget(row, 2, secondWidget);
        contentTable.getCellFormatter().setVerticalAlignment(row, 2, HasVerticalAlignment.ALIGN_TOP);
        row++;
    }
    
    @Override
    public final Widget asWidget() {
        return this;
    }

    @Override
    public final Button getUpdatePositionsButton() {
        return updatePositionsButton;
    }
    
    @Override
    public final Button getRegenerateChartButton() {
        return regenerateChartButton;
    }
    
    @Override
    public final Label getNowLabel() {
        return nowLabel;
    }
    
    @Override
    public final Label getUtcLabel() {
        return utcLabel;
    }
    
    @Override
    public final Label getLocalJdLabel() {
        return localJdLabel;
    }

    @Override
    public final Label getUtcJdLabel() {
        return utcJdLabel;
    }

    @Override
    public final Label getUtcSidLabel() {
        return utcSidLabel;
    }
    
    @Override
    public final Label getLocalSidLabel() {
        return sidLabel;
    }
    
    @Override
    public final Canvas getChart() {
        return chart;
    }
    
    @Override
    public final CheckBox getPlanetCheckBox(final Planet planet) {
        return planetCheckBoxes.get(planet);
    }
    
    @Override
    public final Label getPlanetLabel(final Planet planet) {
        return planetLabels.get(planet);
    }

    @Override
    public final CheckBox getAspectCheckBox(final AspectType type) {
        return aspectCheckBoxes.get(type);
    }
    
    @Override
    public final Label getAspectLabel(final AspectType type) {
        return aspectLabels.get(type);
    }
    
    @Override
    public final void resetAspects() {
    	for (final AspectType aspectType : AspectType.values()) {
    		getAspectLabel(aspectType).setText("0");
    	}
    }
    
    @Override
    public final void addAspect(final AspectType aspectType) {
    	final Label label = getAspectLabel(aspectType);
    	int counter = Integer.parseInt(label.getText());
    	counter++;
    	label.setText(String.valueOf(counter));
    }
    
    @Override
    public final TextBox getLocationTextBox() {
        return locationTextBox;
    }
    
    @Override
    public final Button getSubmitCityButton() {
        return submitCityButton;
    }

    @Override
    public final TextBox getLatitudeTextBox() {
        return latitudeTextBox;
    }   
    
    @Override
    public final Button getSubmitLatitudeButton() {
        return submitLatitudeButton;
    }

    @Override
    public final TextBox getLongitudeTextBox() {
        return longitudeTextBox;
    }
    
    @Override
    public final Button getSubmitLongitudeButton() {
        return submitLongitudeButton;
    }
    
    @Override
    public final Label getSunriseLabel() {
        return sunriseLabel;
    }

    @Override
    public final Label getSunsetLabel() {
        return sunsetLabel;
    }

    @Override
    public final Label getSunPositionLabel() {
        return sunPositionLabel;
    }
    
    @Override
    public final Label getAscendentLabel() {
        return ascendentLabel;
    }
    
    @Override
    public final Label getStatusLabel() {
        return statusLabel;        
    }
}
