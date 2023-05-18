package Shared;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonSerializer implements Serializer {
    public String serialize(Object requestInfo) {
        return (new Gson()).toJson(requestInfo);
    }

    public <T> T deserialize(String value, Type returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

}
