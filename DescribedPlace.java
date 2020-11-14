import javafx.scene.control.Alert;

//Julia Urbanska juur8340
public class DescribedPlace extends Place {

   private String describedText;

    public DescribedPlace(String name, Position position, String category, String describedText){
        super(name, position, category);
        this.describedText = describedText;
    }


    public String getDescribedText() {
        return describedText;
    }

    public String toString(){
        return getName() + ", " + getDescribedText();
    }

    @Override public void showInfo(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Name: " + getName() + "[" + getPosition().getX() + ", " + getPosition().getY() + "]");
        alert.setContentText("Description: " + getDescribedText());
        alert.showAndWait();
    }

    @Override
    public String showList() {
        return "Described" + "," + getCategory() + "," + getPosition().getX() + "," + getPosition().getY() + "," + getName() + "," + getDescribedText();
    }
}
