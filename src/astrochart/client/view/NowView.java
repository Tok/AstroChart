package astrochart.client.view;

import java.util.HashMap;
import java.util.Map;
import astrochart.client.presenter.NowPresenter;
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
    private final FlexTable contentTable;
    private final Label nowLabel = new Label();
    private final Label utcLabel = new Label();
    private final Label localJdLabel = new Label();
    private final Label utcJdLabel = new Label();
    private final Label sidLabel = new Label();
    private final Label utcSidLabel = new Label();

    private final TextBox locationTextBox = new TextBox();
    private final Button submitCityButton = new Button("Get");
    private final TextBox latitudeTextBox = new TextBox();
    private final Button submitLatitudeButton = new Button("Set");
    private final TextBox longitudeTextBox = new TextBox();
    private final Button submitLongitudeButton = new Button("Set");
    private final Label sunriseLabel = new Label();
    private final Label sunsetLabel = new Label();
    private final Label sunPositionLabel = new Label();
    private final Label ascendentLabel = new Label();

    private final Canvas chart = Canvas.createIfSupported();
    private final Label statusLabel = new Label();
    
    private final Map<Planet, CheckBox> planetCheckBoxes = new HashMap<Planet, CheckBox>();
    private final Map<Planet, Label> planetLabels = new HashMap<Planet, Label>();
    
    private int row;
    
    public NowView() {
        final DecoratorPanel contentTableDecorator = new DecoratorPanel();
        contentTableDecorator.setWidth("1010px");
        initWidget(contentTableDecorator);

        contentTable = new FlexTable();
        row = 0;

        if (chart != null) {
       	  	chart.setCoordinateSpaceHeight(600);
       	  	chart.setCoordinateSpaceWidth(600);
          	contentTable.setWidget(row, 0, chart);
       	} else {
       		contentTable.setText(row, 0, "Fail: Your browser doesn't support HTML5 Canvas.");
       	}
        
        contentTable.setText(row, 1, "Local Time: ");
        contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
        contentTable.setWidget(row, 2, nowLabel);
        contentTable.getCellFormatter().setVerticalAlignment(row, 2, HasVerticalAlignment.ALIGN_MIDDLE);
        row++;
            
        addRow("UTC Time: ", utcLabel);
        addRow("Local JD Time: ", localJdLabel);
        addRow("UTC JD Time: ", utcJdLabel);
        addRow("Local Sidereal Time: ", sidLabel);
        addRow("UTC Sidereal Time: ", utcSidLabel);
        
        contentTable.setWidget(row, 0, new HTML("&nbsp;"));
        row++;
        
        for (Planet planet : Planet.values()) {
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

        locationTextBox.setText("unknown");
        addRow("Location: ", locationTextBox, submitCityButton);
        latitudeTextBox.setText("0.0000000");        
        addRow("Latitude: ", latitudeTextBox, submitLatitudeButton);
        longitudeTextBox.setText("0.0000000");
        addRow("Longitude: ", longitudeTextBox, submitLongitudeButton);

        addRow("Sunrise Time: ", sunriseLabel);
        addRow("Sunset Time: ", sunsetLabel);
        addRow("Sun Position: ", sunPositionLabel);
        addRow("Tropical Ascendent: ", ascendentLabel);
        
        contentTable.setWidget(row, 0, statusLabel);
        contentTable.getFlexCellFormatter().setColSpan(row, 0, 3);

        contentTable.getFlexCellFormatter().setRowSpan(0, 0, row);
        
        contentTableDecorator.add(contentTable);
    }

    private final void addRow(final String label, final Widget widget) {
    	contentTable.setText(row, 0, label);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
    	contentTable.setWidget(row, 1, widget);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
    	row++;
    }
    
    private final void addWidgetRow(final Widget first, final Widget second) {
    	contentTable.setWidget(row, 0, first);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
    	contentTable.setWidget(row, 1, second);
    	contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
    	row++;
    }
    
    private final void addRow(final String label, final Widget firstWidget, final Widget secondWidget) {
        contentTable.setText(row, 0, label);
        contentTable.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        contentTable.setWidget(row, 1, firstWidget);
        contentTable.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
        contentTable.setWidget(row, 2, secondWidget);
        contentTable.getCellFormatter().setVerticalAlignment(row, 2, HasVerticalAlignment.ALIGN_MIDDLE);
        row++;
    }
    
    @Override
    public final Widget asWidget() {
        return this;
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
