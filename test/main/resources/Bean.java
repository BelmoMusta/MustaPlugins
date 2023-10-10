package dummy;


public class Bean {
    public Bean() {
        // nari nari
    }
    /**
     * x field
     */
    int x;

    int y;

    int z;

    boolean valid;

    public int getX() {
        System.out.println();
        return this.x;
    }

    public int getXXXXX() {
        System.out.println();
        // this is not a true getter
        return this.x;
    }
    public void setX(int x) {
        // this is a comment
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int yyyyy) {
        y = yyyyy;
        System.out.println();
    }
    public int getZ() {
        int z1 = z;
        return z1;
    }
    public void setZ(int z) {
        this.z = z;
    }
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
