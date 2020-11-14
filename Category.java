import javafx.scene.paint.Color;
//Julia Urbanska juur8340
public enum Category {

    Bus("Bus", Color.RED), Underground("Underground", Color.BLUE), Train("Train", Color.GREEN), None("NoCategory", Color.BLACK);

    private final String name;
    private final Color color;

    Category(String name, Color color){
        this.name = name;
        this.color = color;
    }

    public String getName(){
        return name;
    }

    public Color getColor(){
        return color;
    }

    @Override
    public String toString(){
        return getName();
    }
}
