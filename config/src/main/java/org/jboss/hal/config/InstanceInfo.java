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
package org.jboss.hal.config;

import org.jboss.hal.resources.Names;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Contains information about the server instance the console is connected to or loaded from (taken from the root resource).
 */
@JsType
public class InstanceInfo {

    private String productName;
    private String productVersion;
    private String releaseName;
    private String releaseVersion;

    @JsIgnore
    InstanceInfo() {
        this.productName = Names.NOT_AVAILABLE;
        this.productVersion = Names.NOT_AVAILABLE;
        this.releaseName = Names.NOT_AVAILABLE;
        this.releaseVersion = Names.NOT_AVAILABLE;
    }

    /** @return the product name. */
    @JsProperty(name = "productName")
    public String productName() {
        return productName;
    }

    /** @return the product version. */
    @JsProperty(name = "productVersion")
    public String productVersion() {
        return productVersion;
    }

    /** @return the release name. */
    @JsProperty(name = "releaseName")
    public String releaseName() {
        return releaseName;
    }

    /** @return the release version. */
    @JsProperty(name = "releaseVersion")
    public String releaseVersion() {
        return releaseVersion;
    }

    @JsIgnore
    public void update(String productName, String productVersion,
            String releaseName, String releaseVersion) {
        this.productName = productName;
        this.productVersion = productVersion;
        this.releaseName = releaseName;
        this.releaseVersion = releaseVersion;
    }
}
