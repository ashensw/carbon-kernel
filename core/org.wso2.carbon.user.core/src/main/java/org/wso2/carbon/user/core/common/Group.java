/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.user.core.common;

import java.util.List;

/**
 * Group class which can be used to represent a set of users.
 */
public class Group extends Entity {

    private static final long serialVersionUID = -6157030956831929121L;

    public Group(String groupID) {

        super(groupID);
    }

    public Group(String groupID, String groupName) {

        super(groupID, groupName);
    }

    public Group(String groupID, String groupName, String displayName, String tenantDomain, String userStoreDomain,
            List<Claim> claims) {

        super(groupID, groupName, displayName, tenantDomain, userStoreDomain, claims);
    }

    public String getGroupID() {

        return super.getId();
    }

    public void setGroupID(String groupID) {

        super.setId(groupID);
    }

    public String getGroupName() {

        return super.getName();
    }

    public void setGroupName(String groupName) {

        super.setName(groupName);
    }
}
