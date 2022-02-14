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
package org.jboss.hal.client.runtime.subsystem.jpa;

import org.jboss.hal.core.deployment.DeploymentResource;
import org.jboss.hal.dmr.ModelNode;
import org.jboss.hal.dmr.ResourceAddress;
import org.jboss.hal.resources.Strings;

import static org.jboss.hal.dmr.ModelDescriptionConstants.STATISTICS_ENABLED;

class JpaStatistic extends DeploymentResource {

    private final String persistenceUnit;

    JpaStatistic(ResourceAddress address, ModelNode node) {
        super(address, node);
        this.persistenceUnit = Strings.substringAfterLast(address.lastValue(), "#");
    }

    boolean isStatisticsEnabled() {
        return hasDefined(STATISTICS_ENABLED) && get(STATISTICS_ENABLED).asBoolean(false);
    }

    public String getPersistenceUnit() {
        return persistenceUnit;
    }
}
