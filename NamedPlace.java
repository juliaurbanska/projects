import javafx.scene.control.Alert;

//Julia Urbanska juur8340
public class NamedPlace extends Place {

public NamedPlace(String name, Position position, String category){

    super(name, position, category);
}

 public String toString(){
    return getName();
 }

 @Override public void showInfo(){
     Alert alert = new Alert(Alert.AlertType.INFORMATION);
     alert.setTitle("Info");
     alert.setContentText( "Name: " + getName() + "[" + getPosition().getX() + ", " + getPosition().getY() + "]");
     alert.showAndWait();

 }

    @Override
    public String showList() {
        return "Named" + "," + getCategory() + "," + getPosition().getX() + "," + getPosition().getY() + "," + getName();
    }

}
