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
package org.jboss.hal.client.configuration.subsystem.elytron;

import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.RegExp;
import elemental2.dom.HTMLElement;
import org.jboss.hal.ballroom.LabelBuilder;
import org.jboss.hal.ballroom.form.ModelNodeItem;
import org.jboss.hal.ballroom.form.TagsItem;
import org.jboss.hal.ballroom.form.TagsManager.Validator;
import org.jboss.hal.ballroom.form.TagsMapping;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.resources.Messages;

import static elemental2.dom.DomGlobal.document;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.jboss.hal.ballroom.form.Decoration.*;
import static org.jboss.hal.ballroom.form.Form.State.READONLY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;

/**
 * Form item which is used for some attributes in the Elytron subsystem which are defined as
 * <pre>
 * "attribute-name" => {
 *     "type" => LIST,
 *     "value-type" => {
 *         "name" => {
 *             "type" => STRING,
 *             "required" => true
 *         },
 *         "value" => {
 *             "type" => LIST,
 *             "required" => true,
 *             "value-type" => STRING
 *         }
 *     }
 * }
 * </pre>
 */
class MultiValueListItem extends TagsItem<ModelNode> implements ModelNodeItem {

    private static class MapMapping implements TagsMapping<ModelNode> {

        private static final String VALUE_SEPARATOR = ":";
        private static final RegExp REGEX = RegExp.compile(
                "^([\\w\\-\\.\\/]+)=([\\w\\-\\.\\/" + VALUE_SEPARATOR + "]+)$"); //NON-NLS

        @Override
        public Validator validator() {
            return REGEX::test;
        }

        @Override
        public ModelNode parse(final String cst) {
            ModelNode result = new ModelNode();
            if (cst != null) {
                Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .withKeyValueSeparator('=')
                        .split(cst)
                        .forEach((key, value) -> {
                            ModelNode kv = new ModelNode();
                            kv.get(NAME).set(key);
                            for (String v : value.split(VALUE_SEPARATOR)) {
                                kv.get(VALUE).add(v);
                            }
                            result.add(kv);
                        });
                return result;
            }
            return result;
        }

        @Override
        public List<String> tags(final ModelNode value) {
            if (!value.isDefined()) {
                return emptyList();
            }
            return value.asList().stream()
                    .map(kv -> kv.get(NAME).asString() + "=" + kv.get(VALUE).asList().stream()
                            .map(ModelNode::asString)
                            .collect(joining(VALUE_SEPARATOR)))
                    .collect(toList());
        }

        @Override
        public String asString(final ModelNode value) {
            if (!value.isDefined()) {
                return "";
            }
            return value.asList().stream()
                    .map(kv -> kv.get(NAME).asString() + " \u21D2 " + kv.get(VALUE).asList().stream()
                            .map(ModelNode::asString)
                            .collect(joining(VALUE_SEPARATOR)))
                    .collect(joining("\n"));
        }
    }


    private static final Messages MESSAGES = GWT.create(Messages.class);

    MultiValueListItem(String attribute) {
        super(attribute, new LabelBuilder().label(attribute), MESSAGES.multiValueListHint(),
                EnumSet.of(DEFAULT, DEPRECATED, ENABLED, INVALID, REQUIRED, RESTRICTED), new MapMapping());
    }

    @Override
    public void attach() {
        super.attach();
        HTMLElement element = (HTMLElement) document.getElementById(getId(READONLY));
        if (element != null) {
            element.style.whiteSpace = "pre";
        }
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null || !getValue().isDefined();
    }
}
