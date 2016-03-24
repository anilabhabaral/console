/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.dmr.macro;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to start / stop recording;
 *
 * @author Harald Pehl
 */
public class RecordingEvent extends GwtEvent<RecordingEvent.RecordingHandler> {

    public interface RecordingHandler extends EventHandler {

        void onRecording(RecordingEvent event);
    }


    public enum Action {START, STOP}


    private static final Type<RecordingHandler> TYPE = new Type<>();

    public static Type<RecordingHandler> getType() {
        return TYPE;
    }

    public static RecordingEvent start(MacroOptions options) {
        return new RecordingEvent(Action.START, options);
    }

    public static RecordingEvent stop() {
        return new RecordingEvent(Action.STOP, null);
    }

    private final Action action;
    private final MacroOptions options;

    private RecordingEvent(final Action action, final MacroOptions options) {
        this.action = action;
        this.options = options;
    }

    public Action getAction() {
        return action;
    }

    public MacroOptions getOptions() {
        return options;
    }

    @Override
    protected void dispatch(RecordingHandler handler) {
        handler.onRecording(this);
    }

    @Override
    public Type<RecordingHandler> getAssociatedType() {
        return TYPE;
    }
}
