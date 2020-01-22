import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bean {

    private static class BeanHolder {

        public static final Bean INSTANCE = new Bean();

        private BeanHolder() {
        }
    }

    int x;

    int y;

    int z;

    boolean valid;

    public static Bean getInstance() {
        return BeanHolder.INSTANCE;
    }
}
