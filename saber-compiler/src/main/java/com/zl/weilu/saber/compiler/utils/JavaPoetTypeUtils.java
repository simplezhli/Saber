package com.zl.weilu.saber.compiler.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaPoetTypeUtils {

    public static final ClassName STRING_TYPE = ClassName.get(String.class);
    public static final ClassName LIST_TYPE = ClassName.get(List.class);
    public static final ClassName SET_TYPE = ClassName.get(Set.class);
    public static final ClassName MAP_TYPE = ClassName.get(Map.class);
    public static final ClassName ARRAY_LIST_TYPE = ClassName.get(ArrayList.class);
    public static final ClassName HASH_MAP_TYPE = ClassName.get(HashMap.class);
    public static final ClassName HASH_SET_TYPE = ClassName.get(HashSet.class);

    public static ParameterizedTypeName ListType(ClassName genericType) {
        return ParameterizedTypeName.get(LIST_TYPE, genericType);
    }

    public static ParameterizedTypeName ArraylistType(ClassName genericType) {
        return ParameterizedTypeName.get(ARRAY_LIST_TYPE, genericType);
    }

    public static ParameterizedTypeName SetType(ClassName genericType) {
        return ParameterizedTypeName.get(SET_TYPE, genericType);
    }

    public static ParameterizedTypeName HashSetlistType(ClassName genericType) {
        return ParameterizedTypeName.get(HASH_SET_TYPE, genericType);
    }

    public static ParameterizedTypeName MapType(ClassName K, ClassName V) {
        return ParameterizedTypeName.get(MAP_TYPE, K, V);
    }

    public static ParameterizedTypeName HashMapType(ClassName K, ClassName V) {
        return ParameterizedTypeName.get(HASH_MAP_TYPE, K, V);
    }
}