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

import java.util.EnumSet;

import static org.jboss.hal.ballroom.form.Form.Operation.CLEAR;
import static org.jboss.hal.ballroom.form.Form.Operation.VIEW;
import static org.jboss.hal.ballroom.form.Form.State.READONLY;

/**
 * A read-only state machine. Supports only the {@link Form.State#READONLY} state and the {@link Form.Operation#VIEW} operation.
 */
public class ReadOnlyStateMachine extends AbstractStateMachine implements StateMachine {

    public ReadOnlyStateMachine() {
        super(EnumSet.of(READONLY), EnumSet.of(VIEW, CLEAR));
        this.current = initial();
    }

    @Override
    protected Form.State initial() {
        return READONLY;
    }

    @Override
    protected <C> void safeExecute(final Form.Operation operation, final C context) {
        switch (operation) {

            case VIEW:
                assertState(READONLY);
                transitionTo(READONLY);
                break;

            case CLEAR:
                assertState(READONLY);
                transitionTo(READONLY);
                break;

            default:
                break;
        }
    }

    @Override
    protected String name() {
        return "ReadOnlyStateMachine";
    }
}
