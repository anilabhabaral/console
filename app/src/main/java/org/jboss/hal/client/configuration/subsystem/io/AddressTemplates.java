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
package org.jboss.hal.client.configuration.subsystem.io;

import org.jboss.hal.meta.AddressTemplate;

interface AddressTemplates {

    String IO_SUBSYSTEM_ADDRESS = "/{selected.profile}/subsystem=io";
    String BUFFER_POOL_ADDRESS = IO_SUBSYSTEM_ADDRESS + "/buffer-pool=*";
    String WORKER_ADDRESS = IO_SUBSYSTEM_ADDRESS + "/worker=*";

    AddressTemplate IO_SUBSYSTEM_TEMPLATE = AddressTemplate.of(IO_SUBSYSTEM_ADDRESS);
    AddressTemplate BUFFER_POOL_TEMPLATE = AddressTemplate.of(BUFFER_POOL_ADDRESS);
    AddressTemplate WORKER_TEMPLATE = AddressTemplate.of(WORKER_ADDRESS);
}
