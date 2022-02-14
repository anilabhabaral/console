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
package org.jboss.hal.theme.hal;

import org.jboss.hal.resources.Theme;
import org.jboss.hal.spi.GinModule;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

@GinModule
public class HalThemeModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(Theme.class).to(HalTheme.class).in(Singleton.class);
    }
}
