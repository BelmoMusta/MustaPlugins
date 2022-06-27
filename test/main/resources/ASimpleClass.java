import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ASimpleClassImpl {

    public int getX() {
        return aX;
    }

    public void setX(int x) {
        aX = x;
    }

    public int getY() {
        return aY;
    }

    public void setY(int y) {
        aY = y;
    }

    public int getZ() {
        return aZ;
    }

    public void setZ(int z) {
        aZ = z;
    }

    public boolean isValid() {
        return aValid;
    }

}
