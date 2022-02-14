/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.processor.mbui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class FormInfo extends MbuiElementInfo {

    private final TypeParameter typeParameter;
    private final MetadataInfo metadata;
    private final String title;
    private final boolean autoSave;
    private final String onSave;
    private final boolean reset;
    private String prepareReset;
    private final String nameResolver;
    private final String addHandler;
    private final boolean includeRuntime;
    private final boolean singleton;
    private final List<Attribute> attributes;
    private final List<Group> groups;

    FormInfo(final String name, final String selector, final TypeParameter typeParameter,
            final MetadataInfo metadata, final String title, final String addHandler, final boolean autoSave,
            final String onSave, final boolean reset, final String prepareReset, final String nameResolver,
            final boolean includeRuntime, final boolean singleton) {
        super(name, selector);
        this.typeParameter = typeParameter;
        this.metadata = metadata;
        this.title = ExpressionParser.templateSafeValue(title); // title can be a simple value or an expression
        this.autoSave = autoSave;
        this.onSave = ExpressionParser.stripExpression(onSave); // save handler has to be an expression
        this.reset = reset;
        this.prepareReset = ExpressionParser.stripExpression(prepareReset); // reset handler has to be an expression
        this.nameResolver = ExpressionParser.stripExpression(nameResolver); // name resolver has to be an expression
        this.addHandler = ExpressionParser.stripExpression(addHandler); // add handler has to be an expression
        this.includeRuntime = includeRuntime;
        this.singleton = singleton;
        this.attributes = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    public TypeParameter getTypeParameter() {
        return typeParameter;
    }

    public MetadataInfo getMetadata() {
        return metadata;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public String getOnSave() {
        return onSave;
    }

    public String getAddHandler() {
        return addHandler;
    }

    public boolean isReset() {
        return reset;
    }

    public String getPrepareReset() {
        return prepareReset;
    }

    public String getNameResolver() {
        return nameResolver;
    }

    public boolean isIncludeRuntime() {
        return includeRuntime;
    }

    public boolean isSingleton() {
        return singleton;
    }

    void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public boolean isHasAttributesWithProvider() {
        for (Attribute attribute : attributes) {
            if (attribute.getProvider() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isHasUnboundAttributes() {
        for (Attribute attribute : attributes) {
            if (attribute.getFormItem() != null) {
                return true;
            }
        }
        return false;
    }

    public List<Attribute> getValidationHandlerAttributes() {
        if (groups.isEmpty()) {
            return attributes.stream()
                    .filter(attribute -> attribute.getValidationHandler() != null)
                    .collect(toList());
        } else {
            List<Attribute> attributes = new ArrayList<>();
            groups.forEach(group -> group.getAttributes().stream()
                    .filter(attribute -> attribute.getValidationHandler() != null)
                    .forEach(attributes::add));
            return attributes;
        }
    }

    public List<Attribute> getSuggestHandlerAttributes() {
        if (groups.isEmpty()) {
            return attributes.stream()
                    .filter(attribute -> attribute.getSuggestHandler() != null
                            || !attribute.getSuggestHandlerTemplates().isEmpty())
                    .collect(toList());
        } else {
            List<Attribute> attributes = new ArrayList<>();
            groups.forEach(group -> group.getAttributes().stream()
                    .filter(attribute -> attribute.getSuggestHandler() != null
                            || !attribute.getSuggestHandlerTemplates().isEmpty())
                    .forEach(attributes::add));
            return attributes;
        }
    }

    public List<Group> getGroups() {
        return groups;
    }

    void addGroup(Group group) {
        groups.add(group);
    }

    public static class Group {

        private final String id;
        private final String name;
        private final String title;
        private final List<Attribute> attributes;
        private final Set<String> excludes;

        Group(final String id, final String name, final String title) {
            this.id = id;
            this.name = name;
            this.title = ExpressionParser.templateSafeValue(title);
            this.attributes = new ArrayList<>();
            this.excludes = new HashSet<>();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        void addAttribute(Attribute attribute) {
            attributes.add(attribute);
        }

        void exclude(final String name) {
            excludes.add(name);
        }

        void exclude(final Group group) {
            excludes.addAll(group.getAttributes().stream().map(Attribute::getName).collect(toList()));
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public Set<String> getExcludes() {
            return excludes;
        }

        public boolean isHasAttributesWithProvider() {
            for (Attribute attribute : attributes) {
                if (attribute.getProvider() != null) {
                    return true;
                }
            }
            return false;
        }

        public boolean isHasUnboundAttributes() {
            for (Attribute attribute : attributes) {
                if (attribute.getFormItem() != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
