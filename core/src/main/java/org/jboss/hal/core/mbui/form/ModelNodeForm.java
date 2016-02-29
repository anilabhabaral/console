/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.hal.core.mbui.form;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import elemental.dom.Element;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.hal.ballroom.HelpTextBuilder;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.ballroom.form.AddOnlyStateMachine;
import org.jboss.hal.ballroom.form.DataMapping;
import org.jboss.hal.ballroom.form.DefaultForm;
import org.jboss.hal.ballroom.form.ExistingModelStateMachine;
import org.jboss.hal.ballroom.form.FormItem;
import org.jboss.hal.ballroom.form.FormItemProvider;
import org.jboss.hal.ballroom.form.StateMachine;
import org.jboss.hal.ballroom.form.ViewOnlyStateMachine;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.ModelNodeHelper;
import org.jboss.hal.dmr.Property;
import org.jboss.hal.meta.capabilitiy.Capabilities;
import org.jboss.hal.meta.description.ResourceDescription;
import org.jboss.hal.meta.security.SecurityContext;
import org.jboss.hal.resources.Messages;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.jboss.hal.resources.CSS.*;

/**
 * @author Harald Pehl
 */
public class ModelNodeForm<T extends ModelNode> extends DefaultForm<T> {

    private static class UnboundFormItem {

        final FormItem formItem;
        final int position;

        private UnboundFormItem(final FormItem formItem, final int position) {
            this.formItem = formItem;
            this.position = position;
        }
    }


    public static class Builder<T extends ModelNode> {

        private static final String ILLEGAL_COMBINATION = "Illegal combination in ";

        final String id;
        final SecurityContext securityContext;
        final ResourceDescription resourceDescription;
        final Capabilities capabilities;
        final Set<String> includes;
        final Set<String> excludes;
        final Map<String, FormItemProvider> providers;
        final List<UnboundFormItem> unboundFormItems;
        boolean createResource;
        boolean viewOnly;
        boolean addOnly;
        boolean unsorted;
        boolean includeRuntime;
        SaveCallback<T> saveCallback;
        CancelCallback<T> cancelCallback;
        ResetCallback<T> resetCallback;
        DataMapping<T> dataMapping;


        // ------------------------------------------------------ configure required and optional settings

        public Builder(@NonNls final String id, final SecurityContext securityContext,
                final ResourceDescription resourceDescription, final Capabilities capabilities) {
            this.id = id;
            this.securityContext = securityContext;
            this.resourceDescription = resourceDescription;
            this.capabilities = capabilities;
            this.includes = new HashSet<>();
            this.excludes = new HashSet<>();
            this.providers = new HashMap<>();
            this.unboundFormItems = new ArrayList<>();
            this.createResource = false;
            this.viewOnly = false;
            this.addOnly = false;
            this.unsorted = false;
            this.includeRuntime = false;
            this.dataMapping = new ModelNodeMapping<>(resourceDescription);
        }

        public Builder<T> include(final String[] attributes) {
            includes.addAll(Arrays.asList(attributes));
            return this;
        }

        public Builder<T> include(final Iterable<String> attributes) {
            Iterables.addAll(includes, attributes);
            return this;
        }

        public Builder<T> include(@NonNls final String first, @NonNls final String... rest) {
            includes.addAll(Lists.asList(first, rest));
            return this;
        }

        public Builder<T> exclude(@NonNls final String first, @NonNls final String... rest) {
            excludes.addAll(Lists.asList(first, rest));
            return this;
        }

        public Builder<T> createResource() {
            this.createResource = true;
            return this;
        }

        public Builder<T> viewOnly() {
            this.viewOnly = true;
            return this;
        }

        public Builder<T> addOnly() {
            this.addOnly = true;
            return this;
        }

        public Builder<T> unsorted() {
            this.unsorted = true;
            return this;
        }

        public Builder<T> includeRuntime() {
            this.includeRuntime = true;
            return this;
        }

        public Builder<T> customFormItem(@NonNls final String attribute, final FormItemProvider provider) {
            providers.put(attribute, provider);
            return this;
        }

        public Builder<T> unboundFormItem(final FormItem formItem) {
            return unboundFormItem(formItem, -1);
        }

        public Builder<T> unboundFormItem(final FormItem formItem, final int position) {
            this.unboundFormItems.add(new UnboundFormItem(formItem, position));
            return this;
        }

        public Builder<T> onSave(final SaveCallback<T> saveCallback) {
            this.saveCallback = saveCallback;
            return this;
        }

        public Builder<T> onCancel(final CancelCallback<T> cancelCallback) {
            this.cancelCallback = cancelCallback;
            return this;
        }

        public Builder<T> onReset(final ResetCallback<T> resetCallback) {
            this.resetCallback = resetCallback;
            return this;
        }

        public Builder<T> dataMapping(DataMapping<T> dataMapping) {
            this.dataMapping = dataMapping;
            return this;
        }


        // ------------------------------------------------------ build

        public ModelNodeForm<T> build() {
            validate();
            return new ModelNodeForm<>(this);
        }

        void validate() {
            if (viewOnly && addOnly) {
                throw new IllegalStateException(ILLEGAL_COMBINATION + formId() + ": viewOnly && addOnly");
            }
            if (createResource) {
                if (viewOnly) {
                    throw new IllegalStateException(
                            ILLEGAL_COMBINATION + formId() + ": createResource && viewOnly");
                }
                String path = OPERATIONS + "." + ADD + "." + REQUEST_PROPERTIES;
                if (!ModelNodeHelper.failSafeGet(resourceDescription, path).isDefined()) {
                    throw new IllegalStateException("No request properties found for " + formId() +
                            " / operation add in resource description " + resourceDescription);
                }
                if (!excludes.isEmpty()) {
                    List<Property> requiredRequestProperties = resourceDescription.getRequiredRequestProperties();
                    for (Property property : requiredRequestProperties) {
                        if (excludes.contains(property.getName())) {
                            throw new IllegalStateException("Required request property " + property.getName() +
                                    " must not be excluded from " + formId() + " when using createMode == true");
                        }
                    }
                }
            } else {
                if (!resourceDescription.hasDefined(ATTRIBUTES)) {
                    throw new IllegalStateException("No attributes found for " + formId() +
                            " in resource description " + resourceDescription);
                }
            }
        }

        StateMachine stateMachine() {
            return createResource || addOnly ?
                    new AddOnlyStateMachine() :
                    (viewOnly ? new ViewOnlyStateMachine() : new ExistingModelStateMachine());
        }

        private String formId() {
            return "form(" + id + ")"; //NON-NLS
        }
    }


    private static final Messages MESSAGES = GWT.create(Messages.class);
    private static final Logger logger = LoggerFactory.getLogger(ModelNodeForm.class);

    private final FormItemProvider formItemProvider;

    private ModelNodeForm(final Builder<T> builder) {
        super(builder.id, builder.stateMachine(), builder.dataMapping, builder.securityContext);

        this.formItemProvider = new DefaultFormItemProvider(builder.capabilities);
        this.saveCallback = builder.saveCallback;
        this.cancelCallback = builder.cancelCallback;
        this.resetCallback = builder.resetCallback;

        String path = builder.createResource ? Joiner.on('.').join(OPERATIONS, ADD, REQUEST_PROPERTIES) : ATTRIBUTES;
        Iterable<Property> allProperties = ModelNodeHelper.failSafeGet(builder.resourceDescription, path)
                .asPropertyList();
        //noinspection Guava
        FluentIterable<Property> fi = FluentIterable.from(allProperties).filter(new PropertyFilter(builder));
        Iterable<Property> filtered = builder.unsorted ? fi.toList() :
                fi.toSortedList((p1, p2) -> p1.getName().compareTo(p2.getName()));

        int index = 0;
        LabelBuilder labelBuilder = new LabelBuilder();
        HelpTextBuilder helpTextBuilder = new HelpTextBuilder();
        for (Property property : filtered) {

            // any unbound form items for the current index?
            for (Iterator<UnboundFormItem> iterator = builder.unboundFormItems.iterator(); iterator.hasNext(); ) {
                UnboundFormItem unboundFormItem = iterator.next();
                if (unboundFormItem.position == index) {
                    addFormItem(unboundFormItem.formItem);
                    markAsUnbound(unboundFormItem.formItem.getName());
                    iterator.remove();
                }
            }

            String name = property.getName();
            ModelNode attribute = property.getValue();

            FormItem formItem;
            if (builder.providers.containsKey(name)) {
                formItem = builder.providers.get(name).createFrom(property);
            } else {
                formItem = formItemProvider.createFrom(property);
            }
            if (formItem != null) {
                addFormItem(formItem);
                if (attribute.hasDefined(DESCRIPTION)) {
                    SafeHtml helpText = helpTextBuilder.helpText(property);
                    addHelp(labelBuilder.label(property), helpText);
                }
                index++;
            } else {
                logger.warn("Unable to create form item for '{}' in form '{}'", name, builder.id); //NON-NLS
            }
        }

        // add remaining unbound form items
        for (UnboundFormItem unboundFormItem : builder.unboundFormItems) {
            addFormItem(unboundFormItem.formItem);
            markAsUnbound(unboundFormItem.formItem.getName());
        }
    }

    @Override
    public void attach() {
        super.attach();
        if (Iterables.isEmpty(getFormItems())) {
            // if there's really nothing at all show an info
            Element empty = new Elements.Builder()
                    .div().css(alert, alertInfo)
                    .span().css(pfIcon("info")).end()
                    .span().innerHtml(MESSAGES.emptyModelNodeForm()).end()
                    .end()
                    .build();
            Elements.removeChildrenFrom(asElement());
            asElement().appendChild(empty);
        }
    }
}
