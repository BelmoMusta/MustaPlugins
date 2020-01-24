package musta.belmo.plugins.action.text;

import musta.belmo.utils.TextUtils;

public class EncodeBase64Action extends AbstractTextAction {

    @Override
    public String changeText(String string) {
        return TextUtils.encode64(string);
    }
}
