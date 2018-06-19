package com.zl.weilu.saber.compiler.utils;


import java.lang.annotation.Annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by weilu on 2017/12/14.
 */

public class FieldEntity implements Entity {
    VariableElement element;
    String classSimpleName;
    String classQualifiedName;
    String fieldName;
    String typeName;
    private TypeMirror typeMirror;

    public FieldEntity(Elements elementUtil, VariableElement element) {
        this.element = element;
        this.classSimpleName = element.getEnclosingElement().getSimpleName().toString();
        this.classQualifiedName = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
        this.fieldName = element.getSimpleName().toString();
        this.typeMirror = element.asType();
        this.typeName = typeMirror.toString();
    }

    /**
     * get VariableElement
     *
     * @return
     */
    public VariableElement getElement() {
        return element;
    }

    /**
     * get the current class of the field ,such as MainActivity
     *
     * @return
     */
    @Override
    public String getClassSimpleName() {
        return classSimpleName;
    }

    /**
     * get the current class of the field ,such as com.vinctor.MainActivity
     *
     * @return
     */
    @Override
    public String getClassQualifiedName() {
        return classQualifiedName;
    }

    /**
     * get the field name ,
     * such as 'txtName' in 'TextView txtName'.
     *
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * get the field class,such as 'TextView' in 'TextView txtName'.
     *
     * @return
     */
    public String getTypeString() {
        return typeName;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }


    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return element.getAnnotation(clazz);
    }

    @Override
    public String toString() {
        return "FieldEntity{" +
                "element=" + element +
                ", classSimpleName='" + classSimpleName + '\'' +
                ", classQualifiedName='" + classQualifiedName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
