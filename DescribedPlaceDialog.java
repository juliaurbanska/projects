import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DescribedPlaceDialog extends Alert {
    private TextField nameField = new TextField();
    private TextField describeField = new TextField();

    public DescribedPlaceDialog(){
        super(AlertType.CONFIRMATION);
        GridPane gridPane = new GridPane();
        setTitle("Described Place");
        gridPane.addRow(0, new Label("Name:"), nameField);
        gridPane.addRow(1, new Label("Description:"), describeField);
        getDialogPane().setContent(gridPane);
        setHeaderText(null);
    }

    public String getName() {
        return nameField.getText();
    }

    public String getDescribe(){
        return describeField.getText();
    }
}
