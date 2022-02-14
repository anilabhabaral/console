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
package org.jboss.hal.processor.mbui;

public class MetadataInfo {

    static int counter = 0;

    private final String name;
    private final String template;
    private final boolean singleton;

    MetadataInfo(final String template) {
        this.name = "metadata" + counter; // NON-NLS
        this.template = template;
        this.singleton = !template.endsWith("*");
        counter++;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public boolean isSingleton() {
        return singleton;
    }
}
