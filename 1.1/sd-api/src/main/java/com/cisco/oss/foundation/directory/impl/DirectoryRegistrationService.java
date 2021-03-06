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




package com.cisco.oss.foundation.directory.impl;

import com.cisco.oss.foundation.directory.ServiceInstanceHealth;
import com.cisco.oss.foundation.directory.entity.OperationalStatus;
import com.cisco.oss.foundation.directory.entity.ProvidedServiceInstance;
import com.cisco.oss.foundation.directory.exception.ErrorCode;
import com.cisco.oss.foundation.directory.exception.ServiceException;

import static com.cisco.oss.foundation.directory.ServiceDirectory.getServiceDirectoryConfig;

/**
 * It is the Directory Registration Service to perform the ServiceInstance registration.
 *
 * It registers ServiceInstance to Directory Server.
 *
 *
 */
public class DirectoryRegistrationService {

    /**
     * The property to disable the ILLEGAL_SERVICE_INSTANCE_OWNER_ERROR error in the Directory tool.
     */
    public static final String SD_API_REGISTRY_DISABLE_OWNER_ERROR_PROPERTY_NAME = "com.cisco.oss.foundation.directory.registry.disable.owner.error";

    /**
     * Default to disable ILLEGAL_SERVICE_INSTANCE_OWNER_ERROR.
     */
    public static final boolean SD_API_REGISTRY_DISABLE_OWNER_ERROR_DEFAULT = false;

    /**
     * The remote ServiceDirectory client.
     */
    private final DirectoryServiceClient directoryServiceClient;

    private boolean disableOwnerError = false;

    /**
     * Constructor.
     *
     * @param directoryServiceClient
     *         DirectoryServiceClientManager to get DirectoryServiceClient.
     */
    public DirectoryRegistrationService(
            DirectoryServiceClient directoryServiceClient) {
        this.directoryServiceClient = directoryServiceClient;
        disableOwnerError = getServiceDirectoryConfig().getBoolean(SD_API_REGISTRY_DISABLE_OWNER_ERROR_PROPERTY_NAME,
                SD_API_REGISTRY_DISABLE_OWNER_ERROR_DEFAULT);
    }

    /**
     * Register a ProvidedServiceInstance.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     */
    public void registerService(ProvidedServiceInstance serviceInstance) {
        getServiceDirectoryClient().registerInstance(serviceInstance);
    }

    /**
     * Register a ProvidedServiceInstance with the OperationalStatus.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     * @param status
     *         the OperationalStatus of the ProvidedServiceInstance.
     */
    public void registerService(ProvidedServiceInstance serviceInstance,
            OperationalStatus status) {

        serviceInstance.setStatus(status);
        registerService(serviceInstance);
    }

    /**
     * Register a ProvidedServiceInstance with the ServiceInstanceHealth callback.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     * @param registryHealth
     *         the ServiceInstanceHealth callback.
     */
    public void registerService(ProvidedServiceInstance serviceInstance, ServiceInstanceHealth registryHealth) {
        // Monitor disabled ProvidedServiceInstance should not have the ServiceInstanceHealth.
        if(serviceInstance.isMonitorEnabled()== false){
            throw new ServiceException(ErrorCode.SERVICE_INSTANCE_HEALTH_ERROR);
        }
        registerService(serviceInstance);
    }

    /**
     * Update the uri attribute of the ProvidedServiceInstance
     * The ProvidedServiceInstance is uniquely identified by serviceName and providerId
     *
     * @param serviceName
     *         the serviceName of the ProvidedServiceInstance.
     * @param providerId
     *         the providerId of the ProvidedServiceInstance.
     * @param uri
     *         the new uri.
     */
    public void updateServiceUri(String serviceName, String providerId,
            String uri) {
        getServiceDirectoryClient().updateInstanceUri(serviceName, providerId,
                uri, disableOwnerError);
    }

    /**
     * Update the OperationalStatus of the ProvidedServiceInstance
     * The ProvidedServiceInstance is uniquely identified by serviceName and providerId
     *
     * @param serviceName
     *         the serviceName of the ProvidedServiceInstance.
     * @param providerId
     *         the providerId of the ProvidedServiceInstance.
     * @param status
     *         the new OperationalStatus of the ProvidedServiceInstance.
     */
    public void updateServiceOperationalStatus(String serviceName,
            String providerId, OperationalStatus status) {
        getServiceDirectoryClient().updateInstanceStatus(serviceName,
                providerId, status, disableOwnerError);

    }

    /**
     * Update the ProvidedServiceInstance.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     */
    public void updateService(ProvidedServiceInstance serviceInstance) {
        getServiceDirectoryClient().updateInstance(serviceInstance);

    }

    /**
     * Unregister a ProvidedServiceInstance
     * The ProvidedServiceInstance is uniquely identified by serviceName and providerId
     *
     * @param serviceName
     *         the serviceName of ProvidedServiceInstance.
     * @param providerId
     *         the provierId of ProvidedServiceInstance.
     */
    public void unregisterService(String serviceName, String providerId) {
        getServiceDirectoryClient().unregisterInstance(serviceName, providerId, disableOwnerError);
    }

    /**
     * Get the DirectoryServiceClient.
     *
     * @return the DirectoryServiceClient to access remote directory server.
     */
    protected DirectoryServiceClient getServiceDirectoryClient() {
        return this.directoryServiceClient;
    }
}
