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

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class Editor {

    @JsProperty public Object $blockScrolling;

    public native void focus();

    public native void resize();

    public native void gotoLine(int line, int column, boolean animate);

    public native void selectAll();

    public native Session getSession();

    public native void setOptions(Options options);

    public native void setReadOnly(boolean readOnly);

    public native void setTheme(String theme);

    public native void find(String query);

    public native void findNext();

    public native void findPrevious();
}
