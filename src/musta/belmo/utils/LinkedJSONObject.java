package musta.belmo.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;

public class LinkedJSONObject extends JSONObject {

    public LinkedJSONObject(){
        super(new LinkedHashMap<>());
    }


    public LinkedJSONObject(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    public LinkedJSONObject(JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        } else {
            while(true) {
                char c = x.nextClean();
                switch(c) {
                    case '\u0000':
                        throw x.syntaxError("A JSONObject text must end with '}'");
                    case '}':
                        return;
                    default:
                        x.back();
                        String key = x.nextValue().toString();
                        c = x.nextClean();
                        if (c != ':') {
                            throw x.syntaxError("Expected a ':' after a key");
                        }

                        if (key != null) {
                            if (this.opt(key) != null) {
                                throw x.syntaxError("Duplicate key \"" + key + "\"");
                            }

                            Object value = x.nextValue();
                            if (value != null) {
                                this.put(key, value);
                            }
                        }

                        switch(x.nextClean()) {
                            case ',':
                            case ';':
                                if (x.nextClean() == '}') {
                                    return;
                                }

                                x.back();
                                break;
                            case '}':
                                return;
                            default:
                                throw x.syntaxError("Expected a ',' or '}'");
                        }
                }
            }
        }
    }


}
