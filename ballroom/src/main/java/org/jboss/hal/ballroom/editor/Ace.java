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
package org.jboss.hal.ballroom.editor;

import jsinterop.annotations.JsMethod;

class Ace {

    @JsMethod(namespace = "ace")
    static native Editor edit(String id);

    @JsMethod(namespace = "ace")
    static native ModeList require(String module);

    static native void init() /*-{
        $wnd.ace.config.set('basePath', 'js');
        $wnd.ace.config.setDefaultValue("session", "useWorker", false);
    }-*/;

    private Ace() {
    }
}
