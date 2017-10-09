/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jboss.auto.AbstractProcessor;
import org.jboss.hal.core.Strings;
import org.jboss.hal.spi.EsParam;
import org.jboss.hal.spi.EsReturn;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.jboss.hal.processor.TemplateNames.TEMPLATES;

// Do not export this processor using @AutoService(Processor.class)
// It's executed explicitly in hal-app to process all exported js types in all maven modules.
@SupportedAnnotationTypes("jsinterop.annotations.JsType")
@SuppressWarnings({"HardCodedStringLiteral", "Guava", "ResultOfMethodCallIgnored", "SpellCheckingInspection",
        "DuplicateStringLiteralInspection"})
public class EsDocProcessor extends AbstractProcessor {

    private static final String AUTO = "<auto>";
    private static final String PACKAGE = "esdoc";
    private static final String PARAM_TAG = "@param";
    private static final String RETURN_TAG = "@return";
    private static final String TEMPLATE = "EsDoc.ftl";
    private static final String TYPES = "types";

    private final Multimap<String, Type> types;

    public EsDocProcessor() {
        super(EsDocProcessor.class, TEMPLATES);
        types = HashMultimap.create();
    }

    @Override
    protected boolean onProcess(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(JsType.class)) {
            JsType jsType = element.getAnnotation(JsType.class);
            if (jsType.isNative()) {
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            PackageElement packageElement = elementUtils.getPackageOf(typeElement);
            if (!packageElement.getQualifiedName().toString().startsWith("org.jboss.hal")) {
                continue;
            }

            Type type = new Type(namespace(packageElement, typeElement), typeName(typeElement),
                    comment(typeElement, ""));
            types.put(type.getNamespace(), type);
            debug("Discovered JsType [%s]", type);

            final List<? extends Element> elements = typeElement.getEnclosedElements();
            Predicate<Element> jsRelevant = e -> e != null &&
                    e.getAnnotation(JsIgnore.class) == null &&
                    e.getModifiers().contains(Modifier.PUBLIC);

            // Constructor
            ElementFilter.constructorsIn(elements)
                    .stream()
                    .filter(jsRelevant.and(e -> e.getAnnotation(JsConstructor.class) != null))
                    .findFirst()
                    .ifPresent(e -> type.setConstructor(
                            new Constructor(parameters(e), comment(e, "    "))));

            // Properties - Fields
            ElementFilter.fieldsIn(elements)
                    .stream()
                    .filter(jsRelevant)
                    .forEach(e -> {
                        boolean setter = !e.getModifiers().contains(Modifier.FINAL);
                        type.addProperty(
                                new Property(propertyName(e), comment(e, "    "), true, setter, _static(e)));
                    });

            // Properties - Methods (only getters are supported)
            ElementFilter.methodsIn(elements)
                    .stream()
                    .filter(jsRelevant.and(e -> e.getAnnotation(JsProperty.class) != null))
                    .forEach(e -> type.addProperty(
                            new Property(propertyName(e), comment(e, "    "), true, false, _static(e))));

            // Methods
            ElementFilter.methodsIn(elements)
                    .stream()
                    .filter(jsRelevant.and(e -> e.getAnnotation(JsProperty.class) == null))
                    .forEach(e -> type.addMethod(
                            new Method(methodName(e), parameters(e), comment(e, "    "), _static(e))));
        }

        if (!types.isEmpty()) {
            types.asMap().forEach((namespace, nsTypes) ->
                    resource(TEMPLATE, PACKAGE + "." + namespace, namespace + ".es6",
                            () -> {
                                Map<String, Object> context = new HashMap<>();
                                context.put(TYPES, nsTypes);
                                return context;
                            }));
            types.clear();
        }
        return false;
    }

    private String namespace(PackageElement packageElement, TypeElement typeElement) {
        JsPackage jsPackage = packageElement.getAnnotation(JsPackage.class);
        JsType jsType = typeElement.getAnnotation(JsType.class);

        String namespace;
        if (jsPackage != null) {
            namespace = jsPackage.namespace();
        } else {
            namespace = AUTO.equals(jsType.namespace())
                    ? packageElement.getQualifiedName().toString()
                    : jsType.namespace();
        }
        return namespace;
    }

    private String comment(Element element, String padding) {
        List<String> parameters = new ArrayList<>();
        String comment = elementUtils.getDocComment(element);
        if (comment != null) {

            // process comment line by line
            List<String> lines = stream(Splitter.on('\n').trimResults().split(comment).spliterator(), false)

                    // not supported by ESDoc
                    .filter(line -> !(line.contains("@author") || line.contains("@version")))

                    // process @param and @return in methods
                    .map(line -> {
                        String result = line;
                        if (element instanceof ExecutableElement) {
                            ExecutableElement method = (ExecutableElement) element;

                            if (line.startsWith(PARAM_TAG)) {
                                String paramType;
                                String lineWithoutParam = line.substring(PARAM_TAG.length());
                                VariableElement parameter = getParameter(method, parameters.size());
                                if (parameter != null) {
                                    EsParam esParam = parameter.getAnnotation(EsParam.class);
                                    if (esParam != null) {
                                        paramType = esParam.value();
                                    } else {
                                        paramType = simpleName(parameter.asType().toString());
                                    }
                                    result = PARAM_TAG + " {" + paramType + "}" + lineWithoutParam;
                                }
                                parameters.add(line); // parameters++

                            } else if (line.startsWith(RETURN_TAG)) {
                                String returnType;
                                EsReturn esReturn = method.getAnnotation(EsReturn.class);
                                if (esReturn != null) {
                                    returnType = esReturn.value();
                                } else {
                                    returnType = simpleName(method.getReturnType().toString());
                                }
                                result = RETURN_TAG + " {" + returnType + "}" + line.substring(RETURN_TAG.length());
                            }
                        }
                        return result;
                    })

                    // format comment and collect into list
                    .map(line -> padding + " * " + line)
                    .collect(toList());

            // remove trailing empty lines
            List<String> reversed = Lists.reverse(lines);
            for (Iterator<String> iterator = reversed.iterator(); iterator.hasNext(); ) {
                String line = iterator.next();
                if (line.equals(padding + " * ")) {
                    iterator.remove();
                } else {
                    break;
                }
            }

            if (reversed.isEmpty()) {
                comment = null;
            } else {
                // add first and last lines
                comment = Lists.reverse(reversed).stream().collect(joining("\n"));
                comment = "/**\n" + comment + "\n" + padding + " */";
            }
        }
        return comment;
    }

    private VariableElement getParameter(final ExecutableElement method, int index) {
        List<? extends VariableElement> parameters = method.getParameters();
        return index < parameters.size() ? parameters.get(index) : null;
    }

    private String simpleName(String type) {
        String simple = type.contains(".") ? Strings.substringAfterLast(type, ".") : type;
        switch (simple) {
            case "double":
            case "Double":
            case "float":
            case "Float":
            case "int":
            case "Integer":
            case "long":
            case "Long":
            case "Number":
                simple = "number";
                break;

            case "String":
                simple = "string";
                break;
        }
        return simple;
    }

    private String typeName(Element element) {
        JsType annotation = element.getAnnotation(JsType.class);
        if (annotation != null) {
            return AUTO.equals(annotation.name()) ? element.getSimpleName().toString() : annotation.name();
        } else {
            return element.getSimpleName().toString();
        }
    }

    private String propertyName(Element element) {
        JsProperty annotation = element.getAnnotation(JsProperty.class);
        if (annotation != null) {
            return AUTO.equals(annotation.name()) ? asProperty(element) : annotation.name();
        } else {
            return asProperty(element);
        }
    }

    private String asProperty(Element method) {
        String simpleName = method.getSimpleName().toString();
        if (simpleName.startsWith("get") || simpleName.startsWith("set")) {
            simpleName = UPPER_CAMEL.to(LOWER_CAMEL, simpleName.substring(3));
        } else if (simpleName.startsWith("is")) {
            simpleName = UPPER_CAMEL.to(LOWER_CAMEL, simpleName.substring(2));
        }
        return simpleName;
    }

    private String methodName(Element element) {
        JsMethod annotation = element.getAnnotation(JsMethod.class);
        if (annotation != null) {
            return AUTO.equals(annotation.name()) ? element.getSimpleName().toString() : annotation.name();
        } else {
            return element.getSimpleName().toString();
        }
    }

    private String parameters(ExecutableElement element) {
        return element.getParameters()
                .stream()
                .map(variable -> variable.getSimpleName().toString())
                .collect(joining(", "));

    }

    private boolean _static(Element element) {
        return element.getModifiers().contains(Modifier.STATIC);
    }


    public static class Type {

        private final String namespace;
        private final String name;
        private final String comment;
        private Constructor constructor;
        private final List<Property> properties;
        private final List<Method> methods;

        Type(final String namespace, final String name, final String comment) {
            this.namespace = namespace;
            this.name = name;
            this.comment = comment;
            this.properties = new ArrayList<>();
            this.methods = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("%s.%s", namespace, name);
        }

        void addProperty(Property property) {
            properties.add(property);
        }

        void addMethod(Method method) {
            methods.add(method);
        }

        public String getNamespace() {
            return namespace;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public Constructor getConstructor() {
            return constructor;
        }

        public void setConstructor(final Constructor constructor) {
            this.constructor = constructor;
        }

        public List<Property> getProperties() {
            return properties;
        }

        public List<Method> getMethods() {
            return methods;
        }
    }


    public static class Constructor {

        private final String parameters;
        private final String comment;

        Constructor(final String parameters, final String comment) {
            this.parameters = parameters;
            this.comment = comment;
        }

        @Override
        public String toString() {
            return String.format("(%s)", parameters);
        }

        public String getParameters() {
            return parameters;
        }

        public String getComment() {
            return comment;
        }
    }


    public static class Property {

        private final String name;
        private final String comment;
        private final boolean getter;
        private final boolean setter;
        private final boolean _static;

        Property(final String name, final String comment, final boolean getter, final boolean setter,
                final boolean _static) {
            this.name = name;
            this.comment = comment;
            this.getter = getter;
            this.setter = setter;
            this._static = _static;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public boolean isGetter() {
            return getter;
        }

        public boolean isSetter() {
            return setter;
        }

        public boolean isStatic() {
            return _static;
        }
    }


    public static class Method {

        private final String name;
        private final String parameters;
        private final String comment;
        private final boolean _static;

        Method(final String name, final String parameters, final String comment, final boolean _static) {
            this.name = name;
            this.parameters = parameters;
            this.comment = comment;
            this._static = _static;
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", name, parameters);
        }

        public String getName() {
            return name;
        }

        public String getParameters() {
            return parameters;
        }

        public String getComment() {
            return comment;
        }

        public boolean isStatic() {
            return _static;
        }
    }
}

