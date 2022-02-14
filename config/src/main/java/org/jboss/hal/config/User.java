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
package org.jboss.hal.config;

import java.util.HashSet;
import java.util.Set;

import org.jboss.hal.spi.EsReturn;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static org.jboss.hal.config.Role.ADMINISTRATOR;
import static org.jboss.hal.config.Role.SUPER_USER;

/** Holds information about an user. */
@JsType
public class User {

    private static final User current = new User("Unknown", new HashSet<>(), false); // NON-NLS

    /** @return the current user. */
    public static User current() {
        return current;
    }

    private final Set<Role> roles;
    private String name;
    private boolean authenticated;

    private User(String name, Set<Role> roles, boolean authenticated) {
        this.name = name;
        this.roles = roles;
        this.authenticated = authenticated;
    }

    /** @return the user name. */
    @JsProperty
    public String getName() {
        return name;
    }

    @JsIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsIgnore
    public Set<Role> getRoles() {
        return roles;
    }

    @JsIgnore
    public void refreshRoles(Set<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    @JsIgnore
    public void addRole(Role role) {
        roles.add(role);
    }

    /** @return true if this user belongs to the role SuperUser, false otherwise. */
    @JsProperty
    public boolean isSuperuser() {
        for (Role role : roles) {
            if (SUPER_USER.equals(role)) {
                return true;
            }
        }
        return false;
    }

    /** @return true if this user belongs to the role Administrator, false otherwise. */
    @JsProperty
    public boolean isAdministrator() {
        for (Role role : roles) {
            if (ADMINISTRATOR.equals(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    // ------------------------------------------------------ JS methods

    /** @return the roles of this user. */
    @JsProperty(name = "roles")
    @EsReturn("Role[]")
    public Role[] jsRoles() {
        return roles.toArray(new Role[roles.size()]);
    }
}
