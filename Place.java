import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
//Julia Urbanska juur8340

public abstract class Place extends Polygon {

    private String name;
    private Position position;
    private Category category;
    private boolean marked = false;

    public Place(String name, Position position, String category) {
        super(position.getX(), position.getY(), position.getX() -10.0, position.getY()-20.0, position.getX() +10.0, position.getY()-20.0);
        if (category == null) {
            this.category = Category.None;
        } else {
            this.category = Category.valueOf(category);
        }
        setFill(this.category.getColor());
        this.name = name;
        this.position = position;
    }

    public boolean isMarked(){
        return marked;
    }

    public void setMarked(){
        setFill(Color.YELLOW);
        marked = true;
    }

    public void setUnmarked(){
        setFill(category.getColor());
        marked = false;
    }



    public Position getPosition(){
        return position;
    }

    public Category getCategory(){
        return category;
    }

    public String getName() {
        return name;
    }

    public abstract String toString();

    public abstract void showInfo();

    public abstract String showList();

}
