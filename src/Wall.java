import java.awt.*;
class Wall
{
    private Location loc;
    private int size;
    Wall(int r, int c, int size)
    {
        loc=new Location(r,c);
        this.size=size;
    }
    Rectangle getRect()
    {
        return new Rectangle(loc.getCol()*size,loc.getRow()*size,size,size);
    }
}