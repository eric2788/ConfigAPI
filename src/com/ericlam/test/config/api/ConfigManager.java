package com.ericlam.test.config.api;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class  ConfigManager {

    private final Map<String, Configuration> map = new ConcurrentHashMap<>();

    public ConfigManager(Map<String, Class<? extends Configuration>> ymls) {
        reloadConfig(ymls);
    }

    private void reloadConfig(Map<String, Class<? extends Configuration>> ymls){
        Yaml yaml = new Yaml();
       ymls.forEach((yml, cls)->{
           try {
               Configuration obj = cls.getConstructor().newInstance();
               Resource resource = cls.getAnnotation(Resource.class);
               File file = new File(yml);
               InputStream stream = cls.getResourceAsStream(resource.locate());
               if (!file.exists()) FileUtils.copyInputStreamToFile(stream, file);
               FileInputStream fileInputStream = new FileInputStream(file);
               Map<String, Object> map = yaml.load(fileInputStream);
               fileInputStream.close();
               for (Field field : Arrays.stream(cls.getDeclaredFields()).filter(f->f.isAnnotationPresent(Inject.class)).collect(Collectors.toSet())) {
                   field.setAccessible(true);
                   final String path = field.getAnnotation(Inject.class).path();
                   String name = path.isBlank() ? field.getName() : path;
                   Object value = map.get(name);
                   if (value == null) continue;
                   //Object strictedValue = field.getType().cast(value);
                   field.set(obj, value);
               }
               this.map.put(yml, obj);
           } catch (Exception e) {
               e.printStackTrace();
           }
       });
    }

    public <T extends Configuration> T getConfig(String yml){
        return (T)this.map.get(yml);
    }

    public <T extends Configuration> T getConfigAs(String yml, Class<T> type){
        return type.cast(this.map.get(yml));
    }
}
