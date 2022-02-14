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

/** Theme interface is meant to be implemented by specific themes. */
public interface Theme {

    /** A name to be used in the UI. */
    String getName();

    /** A longer name to be used in the UI. */

    String getFullName();

    /** String used together with {@link #getLastName()} in the header. Defaults to {@link #getName()}. */

    default String getFirstName() {
        return getName();
    }

    /** If not {@code null} this string is used together with {@link #getFirstName()} in the header. */

    default String getLastName() {
        return null;
    }

    Logos logos();
}
