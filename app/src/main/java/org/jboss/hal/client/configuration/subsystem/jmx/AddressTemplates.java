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
package org.jboss.hal.client.configuration.subsystem.jmx;

import org.jboss.hal.meta.AddressTemplate;

interface AddressTemplates {

    String JMX_ADDRESS = "/{selected.profile}/subsystem=jmx";
    String AUDIT_LOG_ADDRESS = JMX_ADDRESS + "/configuration=audit-log";
    String AUDIT_LOG_HANDLER_ADDRESS = AUDIT_LOG_ADDRESS + "/handler=*";
    String REMOTING_CONNECTOR_ADDRESS = JMX_ADDRESS + "/remoting-connector=jmx";

    AddressTemplate JMX_TEMPLATE = AddressTemplate.of(JMX_ADDRESS);
    AddressTemplate AUDIT_LOG_TEMPLATE = AddressTemplate.of(AUDIT_LOG_ADDRESS);
    AddressTemplate AUDIT_LOG_HANDLER_TEMPLATE = AddressTemplate.of(AUDIT_LOG_HANDLER_ADDRESS);
    AddressTemplate REMOTING_CONNECTOR_TEMPLATE = AddressTemplate.of(REMOTING_CONNECTOR_ADDRESS);
}
