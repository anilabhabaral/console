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
import java.util.List;

import org.jboss.hal.ballroom.table.Scope;

import static java.util.stream.Collectors.toList;

public class DataTableInfo extends MbuiElementInfo {

    private final TypeParameter typeParameter;
    private final MetadataInfo metadata;
    private final String title;
    private FormInfo formRef;
    private final List<Column> columns;
    private final List<Action> actions;

    DataTableInfo(final String name, final String selector, final TypeParameter typeParameter,
            final MetadataInfo metadata, final String title) {
        super(name, selector);
        this.typeParameter = typeParameter;
        this.metadata = metadata;
        this.title = ExpressionParser.templateSafeValue(title); // title can be a simple value or an expression
        this.columns = new ArrayList<>();
        this.actions = new ArrayList<>();
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

    public FormInfo getFormRef() {
        return formRef;
    }

    void setFormRef(final FormInfo formRef) {
        this.formRef = formRef;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean isOnlySimpleColumns() {
        for (Column column : columns) {
            if (column.getValue() != null) {
                return false;
            }
        }
        return true;
    }

    void addColumn(Column column) {
        columns.add(column);
    }

    public List<Action> getActions() {
        return actions;
    }

    void addAction(Action action) {
        actions.add(action);
    }

    public static class Action {

        private final String handler;
        private final HandlerRef handlerRef;
        private final String title;
        private final Scope scope;
        private final String constraint;
        private final String nameResolver;
        private final List<Attribute> attributes;

        public Action(final String handler, final String title, final String scope, final String constraint,
                final String nameResolver) {
            this.handler = ExpressionParser.stripExpression(handler);
            this.handlerRef = HandlerRef.referenceFor(handler);
            this.title = ExpressionParser.templateSafeValue(title); // title can be a simple value or an expression
            this.scope = scope != null ? Scope.fromSelector(scope) : null;
            this.constraint = constraint;
            this.nameResolver = ExpressionParser.stripExpression(nameResolver); // name resolver has to be an expression
            this.attributes = new ArrayList<>();
        }

        public String getHandler() {
            return handler;
        }

        public boolean isKnownHandler() {
            return handlerRef != null;
        }

        public HandlerRef getHandlerRef() {
            return handlerRef;
        }

        public String getTitle() {
            return title;
        }

        public Scope getScope() {
            return scope;
        }

        public String getConstraint() {
            return constraint;
        }

        public String getNameResolver() {
            return nameResolver;
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

        public boolean isHasAttributesWithValidationsHandler() {
            return !getSuggestHandlerAttributes().isEmpty();
        }

        public List<Attribute> getValidationHandlerAttributes() {
            return attributes.stream()
                    .filter(attribute -> attribute.getValidationHandler() != null)
                    .collect(toList());
        }

        public boolean isHasAttributesWithSuggestionHandler() {
            return !getSuggestHandlerAttributes().isEmpty();
        }

        public List<Attribute> getSuggestHandlerAttributes() {
            return attributes.stream()
                    .filter(attribute -> attribute.getSuggestHandler() != null
                            || !attribute.getSuggestHandlerTemplates().isEmpty())
                    .collect(toList());
        }

        void addAttribute(Attribute attribute) {
            attributes.add(attribute);
        }
    }

    public static class Column {

        private final String name;
        private final String value;

        Column(final String name, final String value) {
            this.name = name;
            this.value = ExpressionParser.stripExpression(value); // value has to be an expression
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public enum HandlerRef {
        ADD_RESOURCE("add-resource", "add()"), REMOVE_RESOURCE("remove-resource", "remove()");

        static HandlerRef referenceFor(String value) {
            for (HandlerRef ref : HandlerRef.values()) {
                if (ref.getRef().equals(value)) {
                    return ref;
                }
            }
            return null;
        }

        private final String ref;
        private final String i18n;

        HandlerRef(final String ref, final String i18n) {
            this.ref = ref;
            this.i18n = i18n;
        }

        public String getRef() {
            return ref;
        }

        public String getI18n() {
            return i18n;
        }
    }
}
