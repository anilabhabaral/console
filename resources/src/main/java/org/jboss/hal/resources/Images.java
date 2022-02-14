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
package org.jboss.hal.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

    @Source("homepage/access_control.png")
    ImageResource accessControl();

    @Source("homepage/configuration.png")
    ImageResource configuration();

    @Source("homepage/deployments.png")
    ImageResource deployments();

    @Source("homepage/help.png")
    ImageResource help();

    @Source("homepage/patching.png")
    ImageResource patching();

    @Source("homepage/runtime.png")
    ImageResource runtime();
}
