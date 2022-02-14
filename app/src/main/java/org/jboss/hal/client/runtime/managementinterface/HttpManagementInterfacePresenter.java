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
package org.jboss.hal.client.runtime.managementinterface;

import java.util.Map;

import org.jboss.hal.ballroom.form.Form;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.meta.AddressTemplate;
import org.jboss.hal.meta.Metadata;

/** Not a real presenter, but common methods for {@code HostPresenter} and {@code StandaloneServerPresenter} */
public interface HttpManagementInterfacePresenter {

    void saveManagementInterface(AddressTemplate template, Map<String, Object> changedValues);

    void resetManagementInterface(AddressTemplate template, Form<ModelNode> form, Metadata metadata);

    void enableSslForManagementInterface();

    void disableSslForManagementInterface();
}
