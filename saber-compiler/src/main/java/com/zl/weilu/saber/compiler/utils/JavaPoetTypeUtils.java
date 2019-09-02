package com.zl.weilu.saber.compiler.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.zl.weilu.saber.annotation.LiveDataClassType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaPoetTypeUtils {

    private static final ClassName STRING_TYPE = ClassName.get(String.class);
    private static final ClassName LIST_TYPE = ClassName.get(List.class);
    private static final ClassName ARRAY_LIST_TYPE = ClassName.get(ArrayList.class);
    private static final ClassName SET_TYPE = ClassName.get(Set.class);
    private static final ClassName HASH_SET_TYPE = ClassName.get(HashSet.class);
    private static final ClassName MAP_TYPE = ClassName.get(Map.class);
    private static final ClassName HASH_MAP_TYPE = ClassName.get(HashMap.class);

    private static final ClassName INT = ClassName.get(Integer.class);
    private static final ClassName BOOLEAN = ClassName.get(Boolean.class);
    private static final ClassName BYTE = ClassName.get(Byte.class);
    private static final ClassName SHORT = ClassName.get(Short.class);
    private static final ClassName LONG = ClassName.get(Long.class);
    private static final ClassName CHAR = ClassName.get(Character.class);
    private static final ClassName FLOAT = ClassName.get(Float.class);
    private static final ClassName DOUBLE = ClassName.get(Double.class);
    
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
    
    public static TypeName adapterKotlinBaseType (TypeName valueTypeName){
        String name = valueTypeName.toString();
        /// 适配kotlin基础类型
        if ("boolean".equals(name)){
            valueTypeName = BOOLEAN;
        } else if ("byte".equals(name)){
            valueTypeName = BYTE;
        } else if ("short".equals(name)){
            valueTypeName = SHORT;
        } else if ("int".equals(name)){
            valueTypeName = INT;
        } else if ("long".equals(name)){
            valueTypeName = LONG;
        } else if ("char".equals(name)){
            valueTypeName = CHAR;
        } else if ("float".equals(name)){
            valueTypeName = FLOAT;
        } else if ("double".equals(name)){
            valueTypeName = DOUBLE;
        }
        
        return valueTypeName;
    }
}