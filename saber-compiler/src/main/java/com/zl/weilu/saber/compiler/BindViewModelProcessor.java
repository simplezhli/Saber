package com.zl.weilu.saber.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.ObserveType;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.compiler.utils.BaseProcessor;
import com.zl.weilu.saber.compiler.utils.ClassEntity;
import com.zl.weilu.saber.compiler.utils.FieldEntity;
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
        return new Class[]{BindViewModel.class, OnChange.class};
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
        ClassName exceptionClazz = ClassName.get("java.lang", "IllegalStateException");

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(className)
                .addSuperinterface(unBinderClazz)
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

        MethodSpec.Builder unbindMethodBuilder = MethodSpec
                .methodBuilder("unbind")
                .addAnnotation(callSuperClazz)
                .addAnnotation(uiThreadClazz)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("$T target = this.target", activityClazz)
                .beginControlFlow("if (target == null)")
                .addStatement("throw new $T(\"Bindings already cleared.\")", exceptionClazz)
                .endControlFlow()
                .addStatement("this.target = null");

        builder.addField(field)
                .addMethod(constructorMethod);

        Map<String, FieldEntity> fields = classEntity.getFields();

        MethodSpec.Builder initBuilder = MethodSpec
                .methodBuilder("init")
                .addModifiers(Modifier.PRIVATE)
                .returns(void.class);

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
            String field_ = methodEntity.getParameterElements().get(0).toString();
            String model = methodEntity.getAnnotation(OnChange.class).model();
            boolean isBus = methodEntity.getAnnotation(OnChange.class).isBus();
            
            if (StringUtils.isEmpty(model)) {
                throw new IllegalArgumentException(
                        String.format("%s 中的 %s方法 model为空!", classEntity.getClassSimpleName(), methodEntity.getMethodName()));
            }

            ObserveType type = methodEntity.getAnnotation(OnChange.class).type();

            String l = methodEntity.getMethodElement().getParameters().get(0).asType().toString();
            ParameterizedTypeName liveDataTypeName = null;

            ClassName mClazz = null;

            int i = l.indexOf("<");
            if (i < 0){
                mClazz = ClassName.bestGuess(l);

            }else {
                String classType = l.substring(0, i);

                int e = l.lastIndexOf(">");

                String[] types = l.substring(i + 1, e).split(",");

                if (types.length == 1){
                    liveDataTypeName = ParameterizedTypeName.get(ClassName.bestGuess(classType), ClassName.bestGuess(types[0]));
                }

                if (types.length == 2){
                    liveDataTypeName = ParameterizedTypeName.get(ClassName.bestGuess(classType), ClassName.bestGuess(types[0]), ClassName.bestGuess(types[1]));
                }
            }

            TypeSpec comparator = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(observerClazz, mClazz == null ? liveDataTypeName : mClazz))
                    .addMethod(MethodSpec.methodBuilder("onChanged")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(mClazz == null ? liveDataTypeName : mClazz, "value")
                            .returns(void.class)
                            .addStatement("target.$L(value)", methodEntity.getMethodName())
                            .build())
                    .build();

            ClassName liveDataBusClazz = ClassName.get("com.zl.weilu.saber.api", "LiveDataBus");
            
            switch (type){
                case DEFAULT:
                    if (isBus){
                        initBuilder.addStatement("$T.get().with($S, $T.class).observe(target, $L)",
                                liveDataBusClazz, model, mClazz == null ? liveDataTypeName : mClazz, comparator);
                    }else {
                        initBuilder.addStatement("target.$L.get" + StringUtils.upperCase(field_) + "().observe(target, $L)",
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
                        initBuilder.addStatement("$T.get().with($S, $T.class).observeForever("+ model + "Observer)",
                                liveDataBusClazz, model, mClazz == null ? liveDataTypeName : mClazz);
                        
                        unbindMethodBuilder.addStatement("$T.get().with($S, $T.class).removeObserver("+ model + "Observer)",
                                liveDataBusClazz, model, mClazz == null ? liveDataTypeName : mClazz);
                    }else {
                        initBuilder.addStatement("target.$L.get" + StringUtils.upperCase(field_) + 
                                        "().observeForever("+ model + "Observer)", model);

                        unbindMethodBuilder.addStatement("target.$L.get" + StringUtils.upperCase(field_) + 
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
