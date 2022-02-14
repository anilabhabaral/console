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
package org.jboss.hal.ballroom.autocomplete;

import elemental2.dom.Element;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;
import static org.jboss.hal.resources.UIConstants.OBJECT;

@JsType(isNative = true, namespace = GLOBAL, name = OBJECT)
public class Options {

    public Element selector;
    public SourceFunction source;
    public int minChars;
    public int delay;
    public int offsetLeft;
    public int offsetTop;
    public boolean cache;
    public String menuClass;
    public ItemRenderer renderItem;
    public SelectHandler onSelect;
}
