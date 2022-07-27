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
package org.jboss.hal.client.runtime.subsystem.undertow;

import org.jboss.hal.core.finder.PreviewContent;
import org.jboss.hal.core.subsystem.SubsystemMetadata;
import org.jboss.hal.resources.Names;
import org.jboss.hal.resources.Previews;
import org.jboss.hal.resources.Resources;

import elemental2.dom.HTMLElement;

import static org.jboss.gwt.elemento.core.Elements.section;

public class UndertowPreview extends PreviewContent<SubsystemMetadata> {

    private final HTMLElement descriptionPreview;

    public UndertowPreview(Resources resources) {
        super(Names.WEB, Names.UNDERTOW);

        descriptionPreview = section().element();
        Previews.innerHtml(descriptionPreview, resources.previews().runtimeWeb());

        previewBuilder()
                .add(descriptionPreview);
    }
}
