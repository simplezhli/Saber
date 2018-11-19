package com.zl.weilu.saber.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import com.zl.weilu.saber.annotation.AndroidViewModel;
import com.zl.weilu.saber.annotation.LiveData;
import com.zl.weilu.saber.annotation.LiveDataType;
import com.zl.weilu.saber.compiler.utils.BaseProcessor;
import com.zl.weilu.saber.compiler.utils.ClassEntity;
import com.zl.weilu.saber.compiler.utils.FieldEntity;
import com.zl.weilu.saber.compiler.utils.StringUtils;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class LiveDataProcessor extends BaseProcessor {

    @Override
    protected Class[] getSupportedAnnotations() {
        return new Class[]{LiveData.class, AndroidViewModel.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ClassEntity> map = entityHandler.handlerElement(roundEnv, this);
        for (Map.Entry<String, ClassEntity> item : map.entrySet()) {
            entityHandler.generateCode(brewViewModel(item));
        }
        return true;
    }

    private JavaFile brewViewModel(Map.Entry<String, ClassEntity> item) {

        ClassEntity classEntity = item.getValue();
        AndroidViewModel viewModel = classEntity.getAnnotation(AndroidViewModel.class);
        /*类名*/
        String className = classEntity.getElement().getSimpleName().toString() + "ViewModel";

        ClassName mutableLiveDataClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "MutableLiveData");
        ClassName mediatorLiveDataClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "MediatorLiveData");
        ClassName singleLiveDataClazz = ClassName.get("com.zl.weilu.saber.api.event", "SingleLiveEvent");
        
        ClassName viewModelClazz;

        if (viewModel == null){
            viewModelClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "ViewModel");
        }else {
            viewModelClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "AndroidViewModel");
        }


        TypeSpec.Builder builder = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .superclass(viewModelClazz);

        if (viewModel != null){
            ClassName applicationClazz = ClassName.get("android.app", "Application");

            MethodSpec constructorMethod = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(applicationClazz, "application")
                    .addStatement("super(application)")
                    .build();

            builder.addMethod(constructorMethod);
        }

        Map<String, FieldEntity> fields = classEntity.getFields();

        for (FieldEntity fieldEntity : fields.values()){

            FieldSpec field;
            String fieldName = StringUtils.upperCase(fieldEntity.getFieldName());

            String l = fieldEntity.getTypeString();
            ParameterizedTypeName liveDataTypeName = null;
            
            ClassName mClazz;

            int i = l.indexOf("<");
            if (i < 0){
                mClazz = ClassName.bestGuess(l);

            }else {
                mClazz = null;

                String type = l.substring(0, i);

                int e = l.lastIndexOf(">");

                String[] types = l.substring(i + 1, e).split(",");
                
                if (types.length == 1){
                    liveDataTypeName = ParameterizedTypeName.get(ClassName.bestGuess(type), ClassName.bestGuess(types[0]));
                }

                if (types.length == 2){
                    liveDataTypeName = ParameterizedTypeName.get(ClassName.bestGuess(type), ClassName.bestGuess(types[0]), ClassName.bestGuess(types[1]));
                }
                
            }

            LiveDataType type = fieldEntity.getAnnotation(LiveData.class).type();

            String liveDataType;
            ClassName liveDataTypeClassName;

            switch (type){
                case SINGLE:
                    liveDataType = "m$L = new SingleLiveEvent<>()";
                    liveDataTypeClassName = singleLiveDataClazz;
                    break;
                case MEDIATOR:
                    liveDataType = "m$L = new MediatorLiveData<>()";
                    liveDataTypeClassName = mediatorLiveDataClazz;
                    break;
                default:
                    liveDataType = "m$L = new MutableLiveData<>()";
                    liveDataTypeClassName = mutableLiveDataClazz;
                    break;
            }

            ParameterizedTypeName typeName = ParameterizedTypeName.get(liveDataTypeClassName, mClazz == null ? liveDataTypeName : mClazz);

            field = FieldSpec.builder(typeName, "m" + fieldName, Modifier.PRIVATE)
                    .build();

            MethodSpec getMethod = MethodSpec
                    .methodBuilder("get" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(field.type)
                    .beginControlFlow("if (m$L == null)", fieldName)
                    .addStatement(liveDataType, fieldName)
                    .endControlFlow()
                    .addStatement("return m$L", fieldName)
                    .build();

            MethodSpec getValue = MethodSpec
                    .methodBuilder("get" + fieldName + "Value")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(mClazz == null ? liveDataTypeName : mClazz)
                    .addStatement("return this.get$L().getValue()", fieldName)
                    .build();

            MethodSpec setMethod = MethodSpec
                    .methodBuilder("set" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(mClazz == null ? liveDataTypeName : mClazz, "mValue")
                    .beginControlFlow("if (this.m$L == null)", fieldName)
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement("this.m$L.setValue(mValue)", fieldName)
                    .build();

            MethodSpec postMethod = MethodSpec
                    .methodBuilder("post" + fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(mClazz == null ? liveDataTypeName : mClazz, "mValue")
                    .beginControlFlow("if (this.m$L == null)", fieldName)
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement("this.m$L.postValue(mValue)", fieldName)
                    .build();

            builder.addField(field)
                    .addMethod(getMethod)
                    .addMethod(getValue)
                    .addMethod(setMethod)
                    .addMethod(postMethod);

            if (type == LiveDataType.MEDIATOR){
                ClassName liveDataClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "LiveData");
                ClassName observerClazz = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "Observer");
                ClassName mainThreadClazz = ClassName.get(useAndroidX ? "androidx.annotation" : "android.support.annotation", "MainThread");
                ClassName nonNullClazz = ClassName.get(useAndroidX ? "androidx.annotation" : "android.support.annotation", "NonNull");


                // S
                TypeVariableName mTypeVariable = TypeVariableName.get("S");
                // LiveData<S>
                ParameterizedTypeName mLiveDataName = ParameterizedTypeName.get(liveDataClazz, mTypeVariable);
                // @NonNull LiveData<S> source
                ParameterSpec liveDataParameterSpec = ParameterSpec.builder(mLiveDataName, "source")
                        .addAnnotation(nonNullClazz)
                        .build();

                ParameterizedTypeName observerName;
                if (useAndroidX){
                    // Observer<S>
                    observerName = ParameterizedTypeName.get(observerClazz, WildcardTypeName.supertypeOf(mTypeVariable));
                }else {
                    // Observer<? super S>
                    observerName = ParameterizedTypeName.get(observerClazz, mTypeVariable);
                }

                // @NonNull Observer<S> onChanged
                ParameterSpec observerParameterSpec = ParameterSpec.builder(observerName, "onChanged")
                        .addAnnotation(nonNullClazz)
                        .build();


                MethodSpec addSource = MethodSpec
                        .methodBuilder("addSource")
                        .addAnnotation(mainThreadClazz)
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(TypeVariableName.get("S"))
                        .returns(void.class)
                        .addParameter(liveDataParameterSpec)
                        .addParameter(observerParameterSpec)
                        .beginControlFlow("if (this.m$L == null)", fieldName)
                        .addStatement("return")
                        .endControlFlow()
                        .addStatement("this.m$L.addSource(source, onChanged)", fieldName)
                        .build();


                MethodSpec removeSource = MethodSpec
                        .methodBuilder("removeSource")
                        .addAnnotation(mainThreadClazz)
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(TypeVariableName.get("S"))
                        .returns(void.class)
                        .addParameter(liveDataParameterSpec)
                        .beginControlFlow("if (this.m$L == null)", fieldName)
                        .addStatement("return")
                        .endControlFlow()
                        .addStatement("this.m$L.removeSource(source)", fieldName)
                        .build();

                builder.addMethod(addSource)
                        .addMethod(removeSource);
            }
        }


        TypeSpec typeSpec = builder.build();
        // 指定包名
        return JavaFile.builder("com.zl.weilu.saber.viewmodel", typeSpec).build();
    }

}
