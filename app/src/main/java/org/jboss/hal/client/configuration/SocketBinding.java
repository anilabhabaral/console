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
package org.jboss.hal.client.configuration;

import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;

enum SocketBinding {

    INBOUND(Ids.SOCKET_BINDING_GROUP_INBOUND, Names.INBOUND, ModelDescriptionConstants.SOCKET_BINDING), OUTBOUND_LOCAL(
            Ids.SOCKET_BINDING_GROUP_OUTBOUND_LOCAL, Names.OUTBOUND_LOCAL,
            ModelDescriptionConstants.LOCAL_DESTINATION_OUTBOUND_SOCKET_BINDING), OUTBOUND_REMOTE(
                    Ids.SOCKET_BINDING_GROUP_OUTBOUND_REMOTE, Names.OUTBOUND_REMOTE,
                    ModelDescriptionConstants.REMOTE_DESTINATION_OUTBOUND_SOCKET_BINDING);

    final String baseId;
    final String type;
    final String resource;

    SocketBinding(final String baseId, final String type, final String resource) {
        this.baseId = baseId;
        this.type = type;
        this.resource = resource;
    }

    String templateSuffix() {
        return resource + "=*";
    }
}
