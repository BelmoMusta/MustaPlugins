package musta.belmo.plugins.action.text;

import musta.belmo.utils.TextUtils;

public class UnicodifyAction extends AbstractTextAction {

    public String changeText(String string) {
        return TextUtils.unicodify(string);
    }
}
