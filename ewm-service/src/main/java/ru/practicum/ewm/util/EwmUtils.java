package ru.practicum.ewm.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EwmUtils {
    public static <T> void copyNotNullProperties(T source, T target) {
        BeanUtils.copyProperties(source, target, getNullValueProperties(source));
    }

    /**
     * Scans for getters and gets field names with null value
     **/
    private static <T> String[] getNullValueProperties(T source) {
        Method[] methods = source.getClass().getMethods();
        Map<String, String> privateFields = getGetterAndFieldMatch(source);

        List<String> nullPropertiesList = new ArrayList<>();
        for (Method method: methods) {
            if (method.getModifiers() == Modifier.PUBLIC // getters
                    && method.getName().startsWith("get")
                    && method.getParameters().length == 0) {

                try {
                    if (method.invoke(source) == null
                            && privateFields.containsKey(method.getName())) {
                        nullPropertiesList.add(privateFields.get(method.getName()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return nullPropertiesList.toArray(new String[0]);
    }

    private static <T> Map<String, String> getGetterAndFieldMatch(T source) {
        // example entry: "getField", "field"
        return Arrays.stream(source.getClass().getDeclaredFields())
                .filter(field -> field.getModifiers() == Modifier.PRIVATE)
                .map(field -> {
                    String capitalizeName = field.getName()
                            .substring(0, 1)
                            .toUpperCase()
                            .concat(field.getName().substring(1));
                    String getter = "get".concat(capitalizeName);
                    String[] result = new String[2];
                    result[0] = getter;
                    result[1] = field.getName();
                    return result;

                }).collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }
}
