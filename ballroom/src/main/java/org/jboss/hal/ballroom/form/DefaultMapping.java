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

import org.jboss.hal.dmr.ModelNode;

public class DefaultMapping<T> implements DataMapping<T> {

    @Override
    public void newModel(T model, Form<T> form) {
        for (FormItem formItem : form.getBoundFormItems()) {
            formItem.clearError();
            formItem.clearValue();
            formItem.setUndefined(true);
            formItem.setModified(true);
        }
    }

    @Override
    public void populateFormItems(T model, Form<T> form) {
        // empty
    }

    @Override
    public void populateFormItem(String id, String name, ModelNode attributeDescription, ModelNode value,
            FormItem formItem) {
        // empty
    }

    @Override
    public void clearFormItems(Form<T> form) {
        for (FormItem formItem : form.getBoundFormItems()) {
            formItem.clearValue();
        }
    }

    @Override
    public void persistModel(T model, Form<T> form) {
        // empty
    }

    @Override
    public void persistModel(String id, T model, Iterable<FormItem> formItems) {
        // empty
    }
}
