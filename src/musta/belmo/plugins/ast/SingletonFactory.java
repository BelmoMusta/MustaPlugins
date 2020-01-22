package musta.belmo.plugins.ast;

import java.util.EnumMap;
import java.util.Map;

public class SingletonFactory {

    private static final Map<TransformerType, Transformer> map = new EnumMap<>(TransformerType.class);

    public static Transformer getTransformer(TransformerType type) {
        Transformer transformer = map.get(type);
        if (transformer == null) {
            transformer = getFromType(type);
            map.put(type, transformer);
        }
        return transformer;
    }

    private static Transformer getFromType(TransformerType type) {
        Transformer transformer = null;
        switch (type) {
            case LOMBOK:
                transformer = new LombokTransformer();
                break;
            case JPA:
                transformer = new JPAAnnotationsTransformer();
                break;
            case ON_DEMAND_HOLDER:
                transformer = new GenerateOnDemandHolderPattern();
                break;
        }
        return transformer;
    }
}
