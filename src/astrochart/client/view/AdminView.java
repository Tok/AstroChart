package astrochart.client.view;

import astrochart.client.presenter.AdminPresenter;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class AdminView extends Composite implements AdminPresenter.Display {
    private final FlexTable contentTable;
    
    private final Button submitButton = new Button("Submit");
    private final TextArea inputTextArea = new TextArea();
    private final Label statusLabel = new Label();
    private final Label secondStatusLabel = new Label();

    public AdminView() {
        final DecoratorPanel contentTableDecorator = new DecoratorPanel();
        contentTableDecorator.setWidth("1010px");
        initWidget(contentTableDecorator);

        contentTable = new FlexTable();
        
        contentTable.setWidget(0, 0, submitButton);
        inputTextArea.setWidth("997px");
        inputTextArea.setHeight("500px");
        contentTable.setWidget(1, 0, inputTextArea);
        contentTable.setWidget(2, 0, statusLabel);
        contentTable.setWidget(3, 0, secondStatusLabel);
        
        contentTableDecorator.add(contentTable);
    }

    @Override
    public final Widget asWidget() {
        return this;
    }

    @Override
    public final Button getSubmitButton() {
        return submitButton;
    }
    
    @Override
    public final TextArea getInputTextArea() {
        return inputTextArea;
    }
    
    @Override
    public final Label getStatusLabel() {
        return statusLabel;
    }
    
    @Override
    public final Label getSecondStatusLabel() {
        return secondStatusLabel;
    }
}
