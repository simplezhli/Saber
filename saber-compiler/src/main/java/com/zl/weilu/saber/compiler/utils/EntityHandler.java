package com.zl.weilu.saber.compiler.utils;

import com.squareup.javapoet.JavaFile;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by weilu on 2017/12/14.
 */

public class EntityHandler {
    //    ProcessingEnvironment env;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;
    private Map<String, ClassEntity> classEntityMap;

    private OnRegulaListener<VariableElement> onFieldRegulaListener;
    private OnRegulaListener<ExecutableElement> onMethodRegulaListener;
    private RoundEnvironment env;

    public EntityHandler(ProcessingEnvironment processingEnv) {
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        classEntityMap = new HashMap<>();
    }

    public void setOnFieldRegulaListener(OnRegulaListener<VariableElement> onFieldRegulaListener) {
        this.onFieldRegulaListener = onFieldRegulaListener;
    }

    public void setOnMethodRegulaListener(OnRegulaListener<ExecutableElement> onMethodRegulaListener) {
        this.onMethodRegulaListener = onMethodRegulaListener;
    }

    public Map<String, ClassEntity> handlerElement(RoundEnvironment env, BaseProcessor processor) {
        this.env = env;
        for (Class<? extends Annotation> support : processor.getSupportedAnnotations()) {
            for (Element element : env.getElementsAnnotatedWith(support)) {
                if (element.getKind() == ElementKind.FIELD){
                    handlerField((VariableElement) element);
                }
                if (element.getKind() == ElementKind.METHOD){
                    handlerMethod((ExecutableElement) element);
                }
                if (element.getKind() == ElementKind.CLASS) {
                    handlerClass((TypeElement) element);
                }
            }
        }
        return classEntityMap;
    }

    private void handlerClass(TypeElement element) {
        ClassEntity classEntity = new ClassEntity(elementUtils, typeUtils, element);
        String className = classEntity.getClassSimpleName();

        if (classEntityMap.get(className) == null) {
            classEntityMap.put(className, classEntity);
        }
    }

    /**
     * handler method annotations
     *
     * @param element
     */
    private void handlerMethod(ExecutableElement element) {
        if (onMethodRegulaListener != null) {
            String msg = onMethodRegulaListener.onRegula(element);
            if (!("".equals(msg) || msg == null)) {
                throwExceptionWithMsg(msg, element);
                return;
            }
        }
        MethodEntity methodEntity = new MethodEntity(element, typeUtils, elementUtils);

        printNormalMsg(methodEntity.toString());

        String className = methodEntity.getClassSimpleName();
        if (classEntityMap.get(className) == null){
            classEntityMap.put(className,
                    new ClassEntity(elementUtils, typeUtils,
                            (TypeElement) element.getEnclosingElement()));
        }

        ClassEntity classEntity = classEntityMap.get(className);
        classEntity.addMethodEntity(methodEntity);
    }

    /**
     * handler field annotations
     *
     * @param element
     */
    private void handlerField(VariableElement element) {
        if (onFieldRegulaListener != null) {
            String msg = onFieldRegulaListener.onRegula(element);
            if (!("".equals(msg) || msg == null)) {
                throwExceptionWithMsg(msg, element);
                return;
            }
        }
        //builder bean
        FieldEntity fieldEntity = new FieldEntity(elementUtils, element);
        printNormalMsg(fieldEntity.toString());
        //add to map
        //get the class name
        String className = fieldEntity.getClassSimpleName();
        //builder class entity according the classname
        if (classEntityMap.get(className) == null) {
            classEntityMap.put(className,
                    new ClassEntity(elementUtils, typeUtils,
                            (TypeElement) element.getEnclosingElement()));
        }
        //set the field
        ClassEntity classEntity = classEntityMap.get(className);
        classEntity.addFieldEntity(fieldEntity);
    }

    public void generateCode(JavaFile code) {
        try {
            code.writeTo(filer);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void printNormalMsg(String msg, Element element) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg, element);
    }

    public void printNormalMsg(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public void log(String tag, String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, tag + "--->" + msg);
    }

    public void printErrorMsg(String msg, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
    }

    public void throwExceptionWithMsg(String msg, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
    }

    public void throwExceptionWithMsg(String msg) {
        throw new RuntimeException(msg);
    }

    public void printWranningMsg(String msg, Element element) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg, element);
    }
}
