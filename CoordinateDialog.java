//Julia urbanska juur8340
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CoordinateDialog extends Alert {
    private TextField fieldX = new TextField();
    private TextField fieldY = new TextField();


    public CoordinateDialog(){
        super(AlertType.CONFIRMATION);
        GridPane gridPane = new GridPane();
        setTitle("Input Coordinates:");
        gridPane.addRow(0, new Label("x: "), fieldX);
        gridPane.addRow(1, new Label("y: "), fieldY);
        getDialogPane().setContent(gridPane);
        setHeaderText(null);
    }

    public double getXCoordinate() {
        double coordX;

        coordX = Double.parseDouble(fieldX.getText()); //parse texten till en double
        return coordX;

    }

    public double getYCoordinate() {
        double coordY;
        coordY = Double.parseDouble(fieldY.getText());
        return coordY;
    }

}
