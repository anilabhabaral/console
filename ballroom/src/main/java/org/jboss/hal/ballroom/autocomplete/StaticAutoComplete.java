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

import java.util.List;

public class StaticAutoComplete extends AutoComplete {

    List<String> values;

    public StaticAutoComplete(final List<String> values) {
        this.values = values;
        Options options = new OptionsBuilder<String>((query, response) -> {
            String[] matches = this.values.stream()
                    .filter(value -> SHOW_ALL_VALUE.equals(query) || value.toLowerCase().contains(query.toLowerCase()))
                    .toArray(String[]::new);
            response.response(matches);
        }).build();
        init(options);
    }

    public void update(List<String> values) {
        this.values = values;
    }
}
