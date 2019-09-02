package com.zl.weilu.saber.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.LiveEventBus;
import com.zl.weilu.saber.annotation.ObserveType;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.compiler.utils.BaseProcessor;
import com.zl.weilu.saber.compiler.utils.ClassEntity;
import com.zl.weilu.saber.compiler.utils.FieldEntity;
import com.zl.weilu.saber.compiler.utils.JavaPoetTypeUtils;
import com.zl.weilu.saber.compiler.utils.MethodEntity;
import com.zl.weilu.saber.compiler.utils.StringUtils;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


@AutoService(Processor.class)
public class BindViewModelProcessor extends BaseProcessor {

    @Override
    protected Class[] getSupportedAnnotations() {
        return new Class[]{BindViewModel.class, OnChange.class, LiveEventBus.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ClassEntity> map = entityHandler.handlerElement(roundEnv, this);
        for (Map.Entry<String, ClassEntity> item : map.entrySet()) {
            entityHandler.generateCode(brewActivity(item));
        }
        return true;
    }

    private JavaFile brewActivity(Map.Entry<String, ClassEntity> item) {
        ClassEntity classEntity = item.getValue();
        /*获取包名*/
        String packageName = processingEnv.getElementUtils().getPackageOf(classEntity.getElement()).getQualifiedName().toString();
        /*类名*/
        String className = classEntity.getElement().getSimpleName().toString() + "_Providers";

        ClassName unBinderClazz = ClassName.get("com.zl.weilu.saber.api", "UnBinder");
        ClassName viewModelProvidersClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "ViewModelProviders");
        ClassName activityClazz = ClassName.bestGuess(classEntity.getClassQualifiedName());
        ClassName uiThreadClazz = ClassName.get(useAndroidX ? "androidx.annotation" : "android.support.annotation", "UiThread");
        ClassName callSuperClazz = ClassName.get(useAndroidX ? "androidx.annotation" : "android.support.annotation", "CallSuper");
//        ClassName exceptionClazz = ClassName.get("java.lang", "IllegalStateException");
        ClassName lifecycleObserverClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "LifecycleObserver");
        ClassName lifecycleEventClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "OnLifecycleEvent");
        ClassName lifecycleClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "Lifecycle");
        
        TypeSpec.Builder builder = TypeSpec
                .classBuilder(className)
                .addSuperinterface(unBinderClazz)
                .addSuperinterface(lifecycleObserverClazz)
                .addModifiers(Modifier.PUBLIC);

        FieldSpec field = FieldSpec
                .builder(activityClazz, "target", Modifier.PRIVATE)
                .build();

        MethodSpec constructorMethod = MethodSpec
                .constructorBuilder()
                .addAnnotation(uiThreadClazz)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(activityClazz, "target")
                .addStatement("this.target = target")
                .addStatement("init()")
                .build();

        AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(lifecycleEventClazz);
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder().add("$T.Event.ON_DESTROY", lifecycleClazz);
        annotationBuilder.addMember("value", codeBlockBuilder.build());
        
        MethodSpec.Builder unbindMethodBuilder = MethodSpec
                .methodBuilder("unbind")
                .addAnnotation(Override.class)
                .addAnnotation(callSuperClazz)
                .addAnnotation(uiThreadClazz)
                .addAnnotation(annotationBuilder.build())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("$T target = this.target", activityClazz)
                .beginControlFlow("if (target == null)")
//                .addStatement("throw new $T(\"Bindings already cleared.\")", exceptionClazz)
                .addStatement("return")
                .endControlFlow()
                .addStatement("this.target = null");

        builder.addField(field)
                .addMethod(constructorMethod);

        Map<String, FieldEntity> fields = classEntity.getFields();

        MethodSpec.Builder initBuilder = MethodSpec
                .methodBuilder("init")
                .addModifiers(Modifier.PRIVATE)
                .returns(void.class);

        initBuilder.addStatement("target.getLifecycle().addObserver(this)");
        
        for (FieldEntity fieldEntity : fields.values()){
            String fieldName = fieldEntity.getFieldName();
            String key = fieldEntity.getAnnotation(BindViewModel.class).key();
            boolean isShare = fieldEntity.getAnnotation(BindViewModel.class).isShare();
            ClassName mClazz;
            
            if (!fieldEntity.getTypeString().contains(".")){
                //无包名
                mClazz = ClassName.get("com.zl.weilu.saber.viewmodel", fieldEntity.getTypeString());
            }else {
                mClazz = ClassName.bestGuess(fieldEntity.getTypeString());
            }
            
            if (StringUtils.isEmpty(key)){
                if (isShare){
                    initBuilder.addStatement("target.$L = $T.of(target.getActivity()).get($T.class)", fieldName, viewModelProvidersClazz, mClazz);

                }else {
                    initBuilder.addStatement("target.$L = $T.of(target).get($T.class)", fieldName, viewModelProvidersClazz, mClazz);

                }

            }else {
                if (isShare){
                    initBuilder.addStatement("target.$L = $T.of(target.getActivity()).get($S, $T.class)", fieldName, viewModelProvidersClazz, key, mClazz);

                }else {
                    initBuilder.addStatement("target.$L = $T.of(target).get($S, $T.class)", fieldName, viewModelProvidersClazz, key, mClazz);

                }
            }
        }

        Map<String, MethodEntity> methods = classEntity.getMethods();

        ClassName observerClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "Observer");
        
        for (MethodEntity methodEntity : methods.values()){
            String field1 = methodEntity.getParameterElements().get(0).toString();
            String model;
            boolean isBus;
            boolean isSticky = false;
            ObserveType type;
            if (methodEntity.getAnnotation(OnChange.class) == null){
                model = methodEntity.getAnnotation(LiveEventBus.class).key();
                isSticky = methodEntity.getAnnotation(LiveEventBus.class).isSticky();
                type = methodEntity.getAnnotation(LiveEventBus.class).type();
                isBus = true;
            }else {
                model = methodEntity.getAnnotation(OnChange.class).model();
                type = methodEntity.getAnnotation(OnChange.class).type();
                isBus = false;
            }
            if (StringUtils.isEmpty(model)) {
                throw new IllegalArgumentException(
                        String.format("%s 中的 %s方法 model为空!", classEntity.getClassSimpleName(), methodEntity.getMethodName()));
            }

            TypeName generic = ClassName.get(methodEntity.getMethodElement().getParameters().get(0).asType());
            generic = JavaPoetTypeUtils.adapterKotlinBaseType(generic);
            
            TypeSpec comparator = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(observerClazz, generic))
                    .addMethod(MethodSpec.methodBuilder("onChanged")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(generic, "value")
                            .returns(void.class)
                            .addStatement("target.$L(value)", methodEntity.getMethodName())
                            .build())
                    .build();

            ClassName liveDataBusClazz = ClassName.get("com.jeremyliao.liveeventbus", "LiveEventBus");
            
            switch (type){
                case DEFAULT:
                    if (isBus){
                        if (isSticky){
                            initBuilder.addStatement("$T.get().with($S, $T.class).observeSticky(target, $L)",
                                    liveDataBusClazz, model, generic, comparator);
                        }else {
                            initBuilder.addStatement("$T.get().with($S, $T.class).observe(target, $L)",
                                    liveDataBusClazz, model, generic, comparator);
                        }
                        
                    }else {
                        initBuilder.addStatement("target.$L.get" + StringUtils.upperCase(field1) + "().observe(target, $L)",
                                model, comparator);
                    }

                    break;
                case FOREVER:

                    /**
                     * FOREVER模式需要手动取消订阅
                     */
                    FieldSpec observerField = FieldSpec
                            .builder(observerClazz, model + "Observer", Modifier.PRIVATE)
                            .build();
                    
                    builder.addField(observerField);

                    initBuilder.addStatement(model + "Observer = $L", comparator);
                    
                    if (isBus){

                        if (isSticky){
                            initBuilder.addStatement("$T.get().with($S, $T.class).observeStickyForever("+ model + "Observer)",
                                    liveDataBusClazz, model, generic);
                        }else {
                            initBuilder.addStatement("$T.get().with($S, $T.class).observeForever("+ model + "Observer)",
                                    liveDataBusClazz, model, generic);
                        }
                        
                        unbindMethodBuilder.addStatement("$T.get().with($S, $T.class).removeObserver("+ model + "Observer)",
                                liveDataBusClazz, model, generic);
                    }else {
                        initBuilder.addStatement("target.$L.get" + StringUtils.upperCase(field1) + 
                                        "().observeForever("+ model + "Observer)", model);

                        unbindMethodBuilder.addStatement("target.$L.get" + StringUtils.upperCase(field1) + 
                                        "().removeObserver("+ model + "Observer)", model);
                    }

                    break;
                default:
                    break;
            }

        }

        builder.addMethod(initBuilder.build())
                .addMethod(unbindMethodBuilder.build());

        TypeSpec typeSpec = builder.build();

        return JavaFile.builder(packageName, typeSpec).build();

    }
   
}
