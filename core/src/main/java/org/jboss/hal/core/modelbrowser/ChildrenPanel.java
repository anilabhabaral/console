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
package org.jboss.hal.core.modelbrowser;

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import elemental2.dom.HTMLElement;
import org.jboss.gwt.elemento.core.Elements;
import org.jboss.gwt.elemento.core.HasElements;
import org.jboss.gwt.elemento.core.builder.ElementsBuilder;
import org.jboss.hal.ballroom.Attachable;
import org.jboss.hal.ballroom.table.DataTable;
import org.jboss.hal.ballroom.table.Options;
import org.jboss.hal.ballroom.table.OptionsBuilder;
import org.jboss.hal.ballroom.table.Scope;
import org.jboss.hal.ballroom.table.Table;
import org.jboss.hal.ballroom.tree.Node;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.Operation;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.dmr.dispatch.Dispatcher;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Resources;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.jboss.gwt.elemento.core.Elements.h;
import static org.jboss.hal.core.modelbrowser.ReadChildren.uniqueId;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION;

/** Panel which holds the children of the selected resource. */
class ChildrenPanel implements HasElements, Attachable {

    @NonNls private static final Logger logger = LoggerFactory.getLogger(ChildrenPanel.class);

    private final Dispatcher dispatcher;
    private final ElementsBuilder builder;
    private final HTMLElement header;
    private final Table<String> table;
    private Node<Context> parent;

    ChildrenPanel(final ModelBrowser modelBrowser, final Dispatcher dispatcher, final Resources resources) {
        this.dispatcher = dispatcher;

        //noinspection HardCodedStringLiteral
        Options<String> options = new OptionsBuilder<String>()
                .column("resource", Names.RESOURCE, (cell, type, row, meta) -> row)
                .column(resources.constants().view(), row -> modelBrowser.tree.openNode(parent.id,
                        () -> modelBrowser.select(uniqueId(parent, row), false)))
                .button(resources.constants().add(), table -> modelBrowser.add(parent, table.getRows()))

                .button(resources.constants().remove(), table -> {
                            ResourceAddress fq = parent.data.getAddress()
                                    .getParent()
                                    .add(parent.text, table.selectedRow());
                            modelBrowser.remove(fq);
                        }, Scope.SELECTED
                )
                .paging(false)
                .options();

        table = new DataTable<>(Ids.build(Ids.MODEL_BROWSER, "children", Ids.TABLE_SUFFIX), options);
        builder = Elements.elements()
                .add(header = h(1).asElement())
                .add(table);
    }

    @Override
    public Iterable<HTMLElement> asElements() {
        return builder.asElements();
    }

    @Override
    public void attach() {
        table.attach();
    }

    @SuppressWarnings("HardCodedStringLiteral")
    void update(final Node<Context> node, final ResourceAddress address) {
        this.parent = node;

        SafeHtmlBuilder safeHtml = new SafeHtmlBuilder();
        if (node.data.hasSingletons()) {
            safeHtml.appendEscaped("Singleton ");
        }
        safeHtml.appendEscaped("Child Resources of ")
                .appendHtmlConstant("<code>")
                .appendEscaped(node.text)
                .appendHtmlConstant("</code>");
        header.innerHTML = safeHtml.toSafeHtml().asString();

        Operation operation = new Operation.Builder(address.getParent(), READ_CHILDREN_NAMES_OPERATION)
                .param(CHILD_TYPE, node.text)
                .build();
        dispatcher.execute(operation, result -> {
            List<String> names = result.asList().stream().map(ModelNode::asString).collect(toList());
            table.update(names);
            if (node.data.hasSingletons()) {
                logger.debug("Read {} / {} singletons", names.size(), node.data.getSingletons().size());
            }
        });
    }

    void show() {
        for (HTMLElement element : asElements()) {
            Elements.setVisible(element, true);
        }
        table.show();
    }

    void hide() {
        for (HTMLElement element : asElements()) {
            Elements.setVisible(element, false);
        }
        table.hide();
    }
}
