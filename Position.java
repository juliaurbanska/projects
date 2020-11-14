//Julia Urbanska juur8340
public class Position {

    private int coordX;
    private int coordY;

    Position(int x, int y){
        this.coordX = x;
        this.coordY = y;
    }

    public int getX(){
        return coordX;
    }

    public int getY(){
        return coordY;
    }

    @Override
    public int hashCode(){
        return Integer.parseInt(coordX+""+coordY);
    }



    @Override
    public boolean equals(Object otherObject){
        if(!(otherObject instanceof Position))
            return false;
        Position otherPosition = (Position) otherObject;
        if(this.coordX  == otherPosition.coordX && this.coordY == otherPosition.coordY){
            return true;
        } else {
            return false;
        }

    }

}
