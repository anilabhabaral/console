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
package org.jboss.hal.ballroom.dataprovider;

/** Wrapper for a {@link Filter} and its value */
public class FilterValue<T> {

    public static final FilterValue EMPTY = new FilterValue<>((model, filter) -> false, "");

    private final Filter<T> filter;
    private String value;

    public FilterValue(Filter<T> filter, String value) {
        this.filter = filter;
        this.value = value;
    }

    public Filter<T> getFilter() {
        return filter;
    }

    public String getValue() {
        return value;
    }
}
