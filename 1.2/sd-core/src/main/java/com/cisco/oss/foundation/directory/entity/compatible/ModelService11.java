package com.cisco.oss.foundation.directory.entity.compatible;

/**
 * Copyright 2014 Cisco Systems, Inc.
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


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * NOTE: It's the 1.1 version of ModelService, for backward compatible
 * <p>
 *
 * @since 1.2
 *
 *
 * The Service Model Object in Service Directory.
 *
 * It is the logic Service Object in Service Directory. ModelService consists of Service Instances with
 * same service name.
 *
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelService11 {

    /**
     * The name.
     */
    private String name;

    /**
     * The id.
     */
    private String id;

    /**
     * The ServiceInstances of the Service.
     */
    private List<ModelServiceInstance11> serviceInstances;

    /**
     * The last modified time.
     */
    private Date modifiedTime;

    /**
     * The creating time.
     */
    private Date createTime;

    /**
     * Constructor.
     */
    public ModelService11() {

    }

    /**
     * Constructor.
     *
     * @param name
     *            the name.
     * @param id
     *            the instanceId.
     * @param createTime
     *            the create time stamp.
     */
    public ModelService11(String name, String id, Date createTime) {
        this.name = name;
        this.id = id;
        this.createTime = createTime;
    }

    /**
     * Get service name.
     *
     * @return the service name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set service name.
     *
     * @param name
     *            the service name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id
     *            the id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the ModelServiceInstance list.
     *
     * @return the ModelServiceInstance list.
     */
    public List<ModelServiceInstance11> getServiceInstances() {
        return serviceInstances;
    }

    /**
     * Set the ModelServiceInstance list.
     *
     * @param serviceInstances
     *            the ModelServiceInstance list.
     */
    public void setServiceInstances(List<ModelServiceInstance11> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    /**
     * Get the last modified time stamp.
     *
     * @return the last modifed time stamp.
     */
    public Date getModifiedTime() {
        return modifiedTime;
    }

    /**
     * Set the last modified time stamp.
     *
     * @param modifiedTime
     *            the last modified time stamp.
     */
    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    /**
     * Get the create time stamp.
     *
     * @return the create time stamp.
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Set the create time stamp.
     *
     * @param createTime
     *            the create time stamp.
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}

