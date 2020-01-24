package musta.belmo.plugins.action.text;

import musta.belmo.utils.TextUtils;

public class DecodeBase64Action extends AbstractTextAction {

    @Override
    public String changeText(String string) {
        return TextUtils.decode64(string);
    }
}
