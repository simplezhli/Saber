package com.zl.weilu.saber.compiler.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;

public abstract class BaseProcessor extends AbstractProcessor {

    protected EntityHandler entityHandler;

    /**
     * 被注解处理工具调用
     * @param processingEnv 提供了Element，Filer，Messager等工具
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        entityHandler = new EntityHandler(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Class[] typeStrings = getSupportedAnnotations();
        for (Class type : typeStrings) {
            types.add(type.getCanonicalName());
        }
        return types;
    }

    protected abstract Class[] getSupportedAnnotations();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
