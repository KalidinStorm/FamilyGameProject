package Shared;

import java.lang.reflect.Type;

public interface Serializer {
    String serialize(Object requestInfo);

    <T> T deserialize(String value, Type returnType);

}
