package musta.belmo.plugins.action.text;

import java.nio.charset.StandardCharsets;

public class UnUnicodifyAction extends AbstractTextAction {

    public String changeText(String string) {
        String regex = "\\\\u";
        string = string.replaceAll(regex,"\\u");
        return new String(string.getBytes(), StandardCharsets.ISO_8859_1);
    }
}
