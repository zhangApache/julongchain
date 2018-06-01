/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.common.policies;

import org.bcia.javachain.protos.common.Policies;

/**
 * Manager is a read only subset of the policy ManagerImpl
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public interface IManager {

    /** GetPolicy returns a policy and true if it was the policy requested, or false if it is the default policy
     *
     * @param id
     * @return
     */
    Policies.Policy getPolicy(String id);

    /** Manager returns the sub-policy getPolicyManager for a given path and whether it exists
     *
     * @param path
     * @return
     */
    IManager Manager(String[] path);

    /** Basepath returns the basePath the getPolicyManager was instantiated with
     *
     * @return
     */
    String basePath();

    /** Policies returns all policy names defined in the getPolicyManager
     *
     * @return
     */
    String[] policyNames();

}
