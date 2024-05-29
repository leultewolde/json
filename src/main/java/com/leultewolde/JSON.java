package com.leultewolde;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSON<T> {
    private Collection<T> objects;

    private JSON(Collection<T> objects) {
        this.objects = objects;
    }

    public static <T> JSON<T> from(T[] objects) {
        return new JSON<>(Arrays.asList(objects));
    }

    public static <T> JSON<T> from(Collection<T> objects) {
        return new JSON<>(objects);
    }

    public static <T> JSON<T> from(Stream<T> objects) {
        return new JSON<>(objects.toList());
    }

    public static <T> JSON<T> from(T object) {
        List<T> objects = new ArrayList<>();
        objects.add(object);
        return new JSON<>(objects);
    }

    public JSON<T> filter(Predicate<T> predicate) {
        this.objects = objects.stream().filter(predicate).collect(Collectors.toList());
        return this;
    }

    public JSON<T> sort(Comparator<T> comparator) {
        this.objects = objects.stream().sorted(comparator).collect(Collectors.toList());
        return this;
    }

    public JSON<T> map(Function<T, T> mapper) {
        this.objects = objects.stream().map(mapper).collect(Collectors.toList());
        return this;
    }

    public JSON<T> forEach(Consumer<T> consumer) {
        this.objects.forEach(consumer);
        return this;
    }

    public String convert() {
        return convertToJSON();
    }

    public void print() {
        System.out.println(convert());
    }

    private String convertToJSON() {
        return this.objects.stream()
                .map(JSON::parse)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    public static String parse(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldsStr = new ArrayList<>();
        for (Field f : fields) {
            f.setAccessible(true);
            Object value = null;
            try {
                value = f.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            fieldsStr.add("\"" + f.getName() + "\": " + value);
        }

        return fieldsStr.stream()
                .collect(Collectors.joining(", ", "\t{","}"));
    }
}
