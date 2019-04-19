package com.zl.weilu.saber.compiler;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import com.zl.weilu.saber.annotation.AndroidViewModel;
import com.zl.weilu.saber.annotation.LiveData;
import com.zl.weilu.saber.annotation.LiveDataClassType;
import com.zl.weilu.saber.annotation.LiveDataType;
import com.zl.weilu.saber.compiler.utils.BaseProcessor;
import com.zl.weilu.saber.compiler.utils.ClassEntity;
import com.zl.weilu.saber.compiler.utils.FieldEntity;
import com.zl.weilu.saber.compiler.utils.JavaPoetTypeUtils;
import com.zl.weilu.saber.compiler.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

import static com.zl.weilu.saber.annotation.LiveDataType.MEDIATOR;
import static java.util.stream.Collectors.toList;

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
        LiveData liveData = classEntity.getAnnotation(LiveData.class);
        /*类名*/
        String className = classEntity.getElement().getSimpleName().toString();
        
        if (!className.endsWith("Model")){
            className += "ViewModel";
        }
        
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

        // 优先执行类LiveData注解
        if (liveData != null){
            TypeName valueTypeName;
            if (liveData.classType() == LiveDataClassType.DEFAULT){
                valueTypeName = ClassName.get(classEntity.getElement());
            }else {
                valueTypeName = JavaPoetTypeUtils.getLiveDataClassType(liveData.classType(), ClassName.get(classEntity.getElement()));
            }
            Optional<AnnotationMirror> optional = MoreElements.getAnnotationMirror(classEntity.getElement(), LiveData.class);
            if (optional.isPresent()){
                AnnotationMirror annotation = optional.get();
                ImmutableSet<TypeMirror> typeMirrors = AnnotationMirrors.getAnnotationValue(annotation, "liveDataType")
                        .accept(new AllTypesVisitor(), null);
                if (typeMirrors.isEmpty()){
                    brewLiveData(liveData.type(), classEntity.getClassSimpleName(), valueTypeName, builder, null);
                }else {
                    for (TypeMirror module : typeMirrors) {
                        brewLiveData(liveData.type(), classEntity.getClassSimpleName(), valueTypeName, builder, (ClassName) TypeName.get(module));
                    }
                }
            }
            
        }else {
            Map<String, FieldEntity> fields = classEntity.getFields();

            for (FieldEntity fieldEntity : fields.values()){
                LiveData liveData1 = fieldEntity.getAnnotation(LiveData.class);
                LiveDataType type = liveData1.type();
                String fieldName = StringUtils.upperCase(fieldEntity.getFieldName());
                TypeName valueTypeName = ClassName.get(fieldEntity.getTypeMirror());
                Optional<AnnotationMirror> optional = MoreElements.getAnnotationMirror(fieldEntity.getElement(), LiveData.class);
                if (optional.isPresent()){
                    AnnotationMirror annotation = optional.get();
                    ImmutableSet<TypeMirror> typeMirrors = AnnotationMirrors.getAnnotationValue(annotation, "liveDataType")
                            .accept(new AllTypesVisitor(), null);
                    if (typeMirrors.isEmpty()){
                        brewLiveData(type, fieldName, valueTypeName, builder, null);
                    }else {
                        for (TypeMirror module : typeMirrors) {
                            brewLiveData(type, fieldName, valueTypeName, builder, (ClassName) TypeName.get(module));
                        }
                    }
                }
            }
        }

        TypeSpec typeSpec = builder.build();
        // 指定包名
        return JavaFile.builder("com.zl.weilu.saber.viewmodel", typeSpec).build();
    }

    private static final class AllTypesVisitor extends SimpleAnnotationValueVisitor7<ImmutableSet<TypeMirror>, Void> {
        @Override
        public ImmutableSet<TypeMirror> visitArray(List<? extends AnnotationValue> values, Void aVoid) {
            Collection collection = values.stream().flatMap(new Function<AnnotationValue, Stream<?>>() {
                @Override
                public Stream<?> apply(AnnotationValue annotationValue) {
                    return annotationValue.accept(AllTypesVisitor.this, null).stream();
                }
            }).collect(toList());
            return ImmutableSet.copyOf(collection);
        }

        @Override
        public ImmutableSet<TypeMirror> visitType(TypeMirror a, Void aVoid) {
            return ImmutableSet.of(a);
        }

        @Override
        protected ImmutableSet<TypeMirror> defaultAction(Object o, Void aVoid) {
            throw new AssertionError(o);
        }
    }
    
    private void brewLiveData(LiveDataType type, String fieldName, TypeName valueTypeName, TypeSpec.Builder builder, ClassName typeClassName){
        String liveDataType;
        ClassName liveDataTypeClassName;

        switch (type){
            case SINGLE:
                liveDataType = "m$L = new SingleLiveEvent<>()";
                liveDataTypeClassName = ClassName.get("com.zl.weilu.saber.api.event", "SingleLiveEvent");
                break;
            case MEDIATOR:
                liveDataType = "m$L = new MediatorLiveData<>()";
                liveDataTypeClassName = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "MediatorLiveData");
                break;
            case OTHER:
                liveDataType = "m$L = new " + typeClassName.simpleName() + "<>()";
                liveDataTypeClassName = typeClassName;
                break;
            default:
                liveDataType = "m$L = new MutableLiveData<>()";
                liveDataTypeClassName = ClassName.get(useAndroidX ? "androidx.lifecycle" : "android.arch.lifecycle", "MutableLiveData");
                break;
        }

        ParameterizedTypeName typeName = ParameterizedTypeName.get(liveDataTypeClassName, valueTypeName);

        FieldSpec field = FieldSpec.builder(typeName, "m" + fieldName, Modifier.PRIVATE)
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
                .returns(valueTypeName)
                .addStatement("return this.get$L().getValue()", fieldName)
                .build();

        MethodSpec setMethod = MethodSpec
                .methodBuilder("set" + fieldName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(valueTypeName, "mValue")
                .beginControlFlow("if (this.m$L == null)", fieldName)
                .addStatement("return")
                .endControlFlow()
                .addStatement("this.m$L.setValue(mValue)", fieldName)
                .build();

        MethodSpec postMethod = MethodSpec
                .methodBuilder("post" + fieldName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(valueTypeName, "mValue")
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

        if (type == MEDIATOR){
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
                    .methodBuilder("add" + fieldName + "Source")
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
                    .methodBuilder("remove" + fieldName + "Source")
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
}
