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
package org.jboss.hal.js;

/** Represents a Json String value. */
public class JsonString extends JsonValue {

    public static JsonString create(String string) {
        return createProd(string);
    }

    /*
     * MAGIC: String cast to object interface.
     */
    private static native JsonString createProd(String string) /*-{
        // no need to box String for ProdMode and DevMode
        return string;
    }-*/;

    protected JsonString() {
    }

    public final String getString() {
        return valueProd();
    }

    private native String valueProd() /*-{
        return @org.jboss.hal.js.JsonValue::debox(Lorg/jboss/hal/js/JsonValue;)(this);
    }-*/;
}
