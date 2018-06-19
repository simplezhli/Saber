package com.zl.weilu.saber.compiler.utils;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by weilu on 2017/12/14.
 */

public class MethodEntity implements Entity {
    private final String packageName;
    private ExecutableElement methodElement;
    private String returnType;
    private List<? extends VariableElement> parameterElements;
    private List<? extends TypeParameterElement> typeParameterElements;
    private boolean isVarArgs;
    private String methodName;
    private List<? extends TypeMirror> exceptionTypes;
    private String classSimpleName;
    private final String classQualifiedName;

    public MethodEntity(ExecutableElement methodElement, Types typeMirror, Elements elementUtils) {
        this.methodElement = methodElement;
        this.returnType = methodElement.getReturnType().toString();
        this.parameterElements = methodElement.getParameters();
        this.isVarArgs = methodElement.isVarArgs();
        this.methodName = methodElement.getSimpleName().toString();
        this.exceptionTypes = methodElement.getThrownTypes();
        this.typeParameterElements = methodElement.getTypeParameters();
        this.classSimpleName = methodElement.getEnclosingElement().getSimpleName().toString();
        this.classQualifiedName = ((TypeElement) methodElement.getEnclosingElement()).getQualifiedName().toString();
        this.packageName = elementUtils.getPackageOf(methodElement).getQualifiedName().toString();
    }

    @Override
    public String getClassSimpleName() {
        return classSimpleName;
    }

    @Override
    public String getClassQualifiedName() {
        return classQualifiedName;
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<? extends VariableElement> getParameterElements() {
        return parameterElements;
    }

    private List<? extends TypeParameterElement> getTypeParameterElements() {
        return typeParameterElements;
    }

    public boolean isVarArgs() {
        return isVarArgs;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<? extends TypeMirror> getExceptionTypes() {
        return exceptionTypes;
    }

    public String getPackageName() {
        return packageName;
    }

    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return methodElement.getAnnotation(clazz);
    }

    @Override
    public String toString() {
        return "MethodEntity{" +
                "packageName='" + packageName + '\'' +
                ", methodElement=" + methodElement +
                ", returnType='" + returnType + '\'' +
                ", parameterElements=" + parameterElements +
                ", typeParameterElements=" + typeParameterElements.size() +
                ", isVarArgs=" + isVarArgs +
                ", methodName='" + methodName + '\'' +
                ", exceptionTypes=" + exceptionTypes +
                ", classSimpleName='" + classSimpleName + '\'' +
                ", classQualifiedName='" + classQualifiedName + '\'' +
                '}';
    }
}
