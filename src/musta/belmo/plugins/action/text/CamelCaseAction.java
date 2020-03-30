package musta.belmo.plugins.action.text;

import musta.belmo.utils.TextUtils;

public class CamelCaseAction extends AbstractTextAction {

    public String changeText(String string) {
        return TextUtils.camelCase(string);
    }
}
