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
package org.jboss.hal.ballroom.form;

import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

public class SwitchBridge {

    private static final String STATE = "state";
    private static final String DESTROY = "destroy";
    private static final String DISABLED = "disabled";
    private static final String CHANGE_EVENT = "switchChange.bootstrapSwitch";

    @JsFunction
    @FunctionalInterface
    public interface ChangeListener {

        void onChange(Event event, boolean state);
    }

    @JsType(isNative = true)
    public static class Api {

        @JsMethod(namespace = GLOBAL, name = "$")
        public static native Api element(HTMLInputElement element);

        public native boolean bootstrapSwitch(String method);

        public native void bootstrapSwitch(String method, boolean param);

        public native void on(String event, ChangeListener listener);

        @JsOverlay
        public final void destroy() {
            bootstrapSwitch(DESTROY);
        }

        @JsOverlay
        public final boolean getValue() {
            return bootstrapSwitch(STATE);
        }

        @JsOverlay
        public final void setValue(boolean value) {
            bootstrapSwitch(STATE, value);
        }

        @JsOverlay
        public final void setEnable(boolean enabled) {
            bootstrapSwitch(DISABLED, !enabled);
        }

        @JsOverlay
        public final boolean isEnable() {
            return !bootstrapSwitch(DISABLED);
        }

        @JsOverlay
        public final void onChange(ChangeListener listener) {
            on(CHANGE_EVENT, listener);
        }
    }
}
