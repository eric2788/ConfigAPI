package com.ericlam.test.config.api;

import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ConfigManager {

    private final Map<String, Configuration> map = new ConcurrentHashMap<>();

    public ConfigManager(Map<String, Class<? extends Configuration>> ymls) {
        reloadConfig(ymls);
    }

    private void reloadConfig(Map<String, Class<? extends Configuration>> ymls) {
        Yaml yaml = new Yaml();
        ymls.forEach((yml, cls) -> {
            try {
                Configuration obj = cls.getConstructor().newInstance();
                Resource resource = cls.getAnnotation(Resource.class);
                File file = new File(yml);
                InputStream stream = cls.getResourceAsStream(resource.locate());
                if (!file.exists()) FileUtils.copyInputStreamToFile(stream, file);
                FileInputStream fileInputStream = new FileInputStream(file);
                Map<String, Object> map = yaml.load(fileInputStream);
                fileInputStream.close();
                setField(map, cls, obj);
                this.map.put(yml, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private Object parseComponent(@NonNull Map map, Field f) throws Exception {
        Class<?> type;
        if (Map.class.isAssignableFrom(f.getType())) {
            Type genericType = f.getGenericType();
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            type = (Class) parameterizedType.getActualTypeArguments()[1];
            if (!type.isAnnotationPresent(Component.class)) return map;
            for (Object key : map.keySet()) {
                Object newValue = type.getConstructor().newInstance();
                for (Field field : Arrays.stream(type.getDeclaredFields()).filter(ff -> ff.isAnnotationPresent(Inject.class)).collect(Collectors.toSet())) {
                    Object value = parse((Map) map.get(key), field);
                    if (value == null) continue;
                    field.set(newValue, value);
                }
                System.out.println(key + "=" + newValue);
                map.put(key, newValue);

            }
            return map;
        } else {
            type = f.getType();
            if (!type.isAnnotationPresent(Component.class)) return map;

            Object newValue = type.getConstructor().newInstance();
            setField(map, type, newValue);

            return newValue;
        }


    }

    private void setField(@NonNull Map map, Class<?> type, Object newValue) throws Exception {
        for (Field field : Arrays.stream(type.getDeclaredFields()).filter(ff -> ff.isAnnotationPresent(Inject.class)).collect(Collectors.toSet())) {
            Object value = parse(map, field);
            if (value == null) continue;
            field.set(newValue, value);
        }
    }

    private Object parse(@NonNull Map map, Field field) throws Exception {
        Object value = handleField(map, field);

        if (value instanceof Map) {
            return parseComponent((Map) value, field);
        }
        return value;
    }


    private Object handleField(@NonNull Map map, Field field) {
        field.setAccessible(true);
        final String path = field.getAnnotation(Inject.class).path();
        String name = path.isBlank() ? field.getName() : path;
        return map.get(name);
    }

    public <T extends Configuration> T getConfig(String yml) {
        return (T) this.map.get(yml);
    }

    public <T extends Configuration> T getConfigAs(String yml, Class<T> type) {
        return type.cast(this.map.get(yml));
    }
}
