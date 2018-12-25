package com.zl.weilu.saber.compiler.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.zl.weilu.saber.annotation.LiveDataClassType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaPoetTypeUtils {

    public static final ClassName STRING_TYPE = ClassName.get(String.class);
    public static final ClassName LIST_TYPE = ClassName.get(List.class);
    public static final ClassName ARRAY_LIST_TYPE = ClassName.get(ArrayList.class);
    public static final ClassName SET_TYPE = ClassName.get(Set.class);
    public static final ClassName HASH_SET_TYPE = ClassName.get(HashSet.class);
    public static final ClassName MAP_TYPE = ClassName.get(Map.class);
    public static final ClassName HASH_MAP_TYPE = ClassName.get(HashMap.class);
    
    public static ParameterizedTypeName getLiveDataClassType(LiveDataClassType classType, ClassName genericType){
        switch (classType){
            case LIST:
                return listType(genericType);
            case ARRAY_LIST:
                return arrayListType(genericType);
            case SET:
                return setType(genericType);
            case HASH_SET:
                return hashSetListType(genericType);
            case MAP:
                return mapType(STRING_TYPE, genericType);
            case HASH_MAP:
                return hashMapType(STRING_TYPE, genericType);
            case DEFAULT:
            default:
                return null;
        }
    }

    public static ParameterizedTypeName listType(ClassName genericType) {
        return ParameterizedTypeName.get(LIST_TYPE, genericType);
    }

    public static ParameterizedTypeName arrayListType(ClassName genericType) {
        return ParameterizedTypeName.get(ARRAY_LIST_TYPE, genericType);
    }

    public static ParameterizedTypeName setType(ClassName genericType) {
        return ParameterizedTypeName.get(SET_TYPE, genericType);
    }

    public static ParameterizedTypeName hashSetListType(ClassName genericType) {
        return ParameterizedTypeName.get(HASH_SET_TYPE, genericType);
    }

    public static ParameterizedTypeName mapType(ClassName key, ClassName value) {
        return ParameterizedTypeName.get(MAP_TYPE, key, value);
    }

    public static ParameterizedTypeName hashMapType(ClassName key, ClassName value) {
        return ParameterizedTypeName.get(HASH_MAP_TYPE, key, value);
    }
}