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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.oss.foundation.directory.Configurations;
import com.cisco.oss.foundation.directory.DirectoryServiceClientManager;
import com.cisco.oss.foundation.directory.ServiceInstanceHealth;
import com.cisco.oss.foundation.directory.entity.ACL;
import com.cisco.oss.foundation.directory.entity.AuthScheme;
import com.cisco.oss.foundation.directory.entity.OperationalStatus;
import com.cisco.oss.foundation.directory.entity.Permission;
import com.cisco.oss.foundation.directory.entity.ProvidedServiceInstance;
import com.cisco.oss.foundation.directory.entity.ServiceInstanceToken;
import com.cisco.oss.foundation.directory.entity.User;
import com.cisco.oss.foundation.directory.event.ServiceDirectoryEvent;
import com.cisco.oss.foundation.directory.event.ServiceDirectoryEvent.ClientSessionEvent;
import com.cisco.oss.foundation.directory.event.ServiceDirectoryEvent.ClientSessionEvent.SessionEvent;
import com.cisco.oss.foundation.directory.event.ServiceDirectoryListener;
import com.cisco.oss.foundation.directory.utils.PermissionUtil;

/**
 * It is the Directory Registration Service to perform the ServiceInstance registration.
 *
 * It registers ServiceInstance to DirectoryServer.
 *
 *
 */
public class DirectoryRegistrationService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DirectoryRegistrationService.class);

    /**
     * The RegistrationManager health check executor kick off delay time
     * property name in seconds.
     */
    public static final String SD_API_REGISTRY_HEALTH_CHECK_DELAY_PROPERTY = "registry.health.check.delay";

    /**
     * The default delay time of health check executor kick off.
     */
    public static final int SD_API_REGISTRY_HEALTH_CHECK_DELAY_DEFAULT = 1;

    /**
     * The RegistrationManager health check interval property name in seconds.
     */
    public static final String SD_API_REGISTRY_HEALTH_CHECK_INTERVAL_PROPERTY = "registry.health.check.interval";

    /**
     * The default health check interval value of RegistrationManager.
     */
    public static final int SD_API_REGISTRY_HEALTH_CHECK_INTERVAL_DEFAULT = 5;

    /**
     * The remote ServiceDirectory node client.
     */
    private final DirectoryServiceClientManager directoryServiceClientManager;

    /**
     * All ServiceInstanceHealth set collection.
     */
    private Map<ServiceInstanceToken, InstanceHealthPair> instanceCache = null;

    /**
     * ServiceInstanceHealth check ExecutorService.
     */
    private ScheduledExecutorService healthJob;

    private SessionListener sessionListener;

    /**
     * Constructor.
     *
     * @param directoryServiceClientManager
     *         DirectoryServiceClientManager to get DirectoryServiceClient.
     */
    public DirectoryRegistrationService(
            DirectoryServiceClientManager directoryServiceClientManager) {
        this.directoryServiceClientManager = directoryServiceClientManager;
        sessionListener = new SessionListener();
        directoryServiceClientManager.getDirectoryServiceClient().registerClientChangeListener(sessionListener);
    }

    /**
     * Register a ProvidedServiceInstance.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     */
    public void registerService(ProvidedServiceInstance serviceInstance) {
        getServiceDirectoryClient().registerServiceInstance(serviceInstance);
        getCacheServiceInstances().put(new ServiceInstanceToken(serviceInstance.getServiceName(), serviceInstance.getProviderId()), new InstanceHealthPair(null));
    }

    /**
     * Register a ProvidedServiceInstance with the OperationalStatus and the ServiceInstanceHealth callback.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     * @param registryHealth
     *         the ServiceInstanceHealth callback.
     */
    public void registerService(ProvidedServiceInstance serviceInstance, ServiceInstanceHealth registryHealth) {
        registerService(serviceInstance);
        getCacheServiceInstances().put(new ServiceInstanceToken(serviceInstance.getServiceName(), serviceInstance.getProviderId()),
                new InstanceHealthPair(registryHealth));
    }

    /**
     * Update the uri of the ProvidedServiceInstance by serviceName and providerId.
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
        getServiceDirectoryClient().updateServiceInstanceUri(serviceName, providerId, uri);
    }

    /**
     * Update the OperationalStatus of the ProvidedServiceInstance by serviceName and providerId.
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
        getServiceDirectoryClient().updateServiceInstanceStatus(serviceName, providerId, status);

    }

    /**
     * Update the ProvidedServiceInstance.
     *
     * @param serviceInstance
     *         the ProvidedServiceInstance.
     */
    public void updateService(ProvidedServiceInstance serviceInstance) {
        getServiceDirectoryClient().updateServiceInstance(serviceInstance);

    }

    /**
     * Unregister a ProvidedServiceInstance by serviceName and providerId.
     *
     * @param serviceName
     *         the serviceName of ProvidedServiceInstance.
     * @param providerId
     *         the provierId of ProvidedServiceInstance.
     */
    public void unregisterService(String serviceName, String providerId) {
        getServiceDirectoryClient().unregisterServiceInstance(serviceName, providerId);
        getCacheServiceInstances().remove(new ServiceInstanceToken(serviceName, providerId));
    }

    /**
     * Create a User.
     *
     * @param user
     *         the User.
     * @param password
     *         the user password.
     */
    public void createUser(User user, String password){
        getServiceDirectoryClient().createUser(user, password);
    }

    /**
     * Get the User by name.
     *
     * @param name
     *         the user name.
     * @return
     *         the User.
     */
    public User getUser(String name) {
        return getServiceDirectoryClient().getUser(name);
    }

    /**
     * Update the User.
     *
     * @param user
     *         the User.
     */
    public void updateUser(User user) {
        getServiceDirectoryClient().updateUser(user);
    }

    /**
     * Delete the User by name.
     *
     * @param name
     *         the user name.
     */
    public void deleteUser(String name) {
        getServiceDirectoryClient().deleteUser(name);
    }

    /**
     * Set the user permission.
     *
     * @param userName
     *         the user name.
     * @param permissions
     *         the user permission.
     */
    public void setUserPermission(String userName, List<Permission> permissions){
        getServiceDirectoryClient().setACL(new ACL(AuthScheme.DIRECTORY, userName, PermissionUtil.permissionList2Id(permissions)));
    }

    /**
     * Set the user password.
     *
     * @param userName
     *         the user name.
     * @param password
     *         the user password.
     */
    public void setUserPassword(String userName, String password) {
        getServiceDirectoryClient().setUserPassword(userName, password);
    }

    /**
     * Set the user permission.
     *
     * @param userName
     *         the user name.
     * @param permission
     *          the user permission.
     */
    public void setUserPermission(String userName, Permission permission){
        if(permission == null){
            permission = Permission.NONE;
        }
        getServiceDirectoryClient().setACL(new ACL(AuthScheme.DIRECTORY, userName, permission.getId()));
    }

    /**
     * Get the user permission.
     *
     * @param userName
     *         user name.
     * @return
     *         the permission list.
     */
    public List<Permission> getUserPermission(String userName) {
        ACL acl = getServiceDirectoryClient().getACL(AuthScheme.DIRECTORY, userName);

        int permissionId = 0;
        if(acl != null){
            permissionId = acl.getPermission();
        }
        return PermissionUtil.id2Permissions(permissionId);
    }

    /**
     * Get all users.
     *
     * @return
     *         the all User list.
     */
    public List<User> getAllUser(){
        return getServiceDirectoryClient().getAllUser();
    }

    /**
     * Get the DirectoryServiceClient.
     *
     * @return the DirectoryServiceClient to access remote directory server.
     */
    protected DirectoryServiceClient getServiceDirectoryClient() {

        return directoryServiceClientManager.getDirectoryServiceClient();
    }

    /**
     * Get the CachedProviderServiceInstance Set.
     *
     * It is lazy initialized and thread safe.
     *
     * @return
     *         the CachedProviderServiceInstance Set.
     */
    private Map<ServiceInstanceToken, InstanceHealthPair> getCacheServiceInstances() {
        if (instanceCache == null) {
            synchronized (this) {
                if (instanceCache == null) {
                    instanceCache = new ConcurrentHashMap<ServiceInstanceToken, InstanceHealthPair>();
                    initJobTasks();
                }
            }
        }
        return instanceCache;
    }

    /**
     * Attache the ServiceInstance to current session.
     */
    private void attachSession(){
        if(instanceCache != null){
            getServiceDirectoryClient().attachSession(new ArrayList<ServiceInstanceToken>(instanceCache.keySet()));
        }
    }

    /**
     * initialize the Heartbeat task and Health Check task. It invoked in the
     * getCacheServiceInstances method which is thread safe, no synchronized
     * needed.
     */
    private void initJobTasks() {
        healthJob = Executors
                .newSingleThreadScheduledExecutor(new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("SD API RegistryHealth Check");
                        t.setDaemon(true);
                        return t;
                    }
                });
        int rhDelay = Configurations.getInt(
                SD_API_REGISTRY_HEALTH_CHECK_DELAY_PROPERTY,
                SD_API_REGISTRY_HEALTH_CHECK_DELAY_DEFAULT);
        int rhInterval = Configurations.getInt(
                SD_API_REGISTRY_HEALTH_CHECK_INTERVAL_PROPERTY,
                SD_API_REGISTRY_HEALTH_CHECK_INTERVAL_DEFAULT);
        LOGGER.info("Start the SD API RegistryHealth Task scheduler, delay="
                + rhDelay + ", interval=" + rhInterval);
        healthJob.scheduleAtFixedRate(new HealthCheckTask(), rhDelay,
                rhInterval, TimeUnit.SECONDS);
    }

    /**
     * The ServiceInstanceHealth checking task.
     *
     *
     */
    private class HealthCheckTask implements Runnable {

        @Override
        public void run() {
            try {
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("Kickoff the HealthCheckTask thread");
                }

                for (ServiceInstanceToken ist : new ArrayList<ServiceInstanceToken>(getCacheServiceInstances().keySet())) {
                    InstanceHealthPair pair = getCacheServiceInstances().get(ist);
                    if (pair == null || pair.healthCallback == null) {
                        continue;
                    }
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("Check the Health for service=" + ist.getServiceName() + ", providerId=" + ist.getInstanceId());
                    }
                    try{
                        boolean isHealth = pair.healthCallback.isHealthy();
                        if(!pair.isInited || pair.lastResult != isHealth){
                            String serviceName= ist.getServiceName();
                            String instanceId = ist.getInstanceId();
                            OperationalStatus status = OperationalStatus.UP;
                            if(! isHealth){
                                status = OperationalStatus.DOWN;
                            }
                            if(LOGGER.isDebugEnabled()){
                                LOGGER.debug("Update the ServiceInstance internal status service=" + ist.getServiceName() + ", providerId=" + ist.getInstanceId()
                                        + ", status=" + status);
                            }
                            if(getServiceDirectoryClient().getStatus().isConnected()){
                                getServiceDirectoryClient().updateServiceInstanceInternalStatus(serviceName, instanceId, status);
                                pair.lastResult = isHealth;
                                if(!pair.isInited){
                                    pair.isInited = true;
                                }
                            }
                        }
                    } catch (Exception e){
                        LOGGER.error("Invoke ServiceInstanceHealth callback get exception.", e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ServiceInstanceHealth callback check failed.", e);
            }
        }

    }

    /**
     * The SessionListener for Session Created and REOPEN.
     *
     *
     */
    private class SessionListener implements ServiceDirectoryListener{

        @Override
        public void notify(ServiceDirectoryEvent event) {
            if(event instanceof ClientSessionEvent){
                ClientSessionEvent e = (ClientSessionEvent) event;
                if(LOGGER.isTraceEnabled()){
                    LOGGER.trace("Got the ClientSessionEvent - " + e.toString());
                }

                if(SessionEvent.CREATED.equals(e.getSessionEvent()) || SessionEvent.REOPEN.equals(e.getSessionEvent())){
                    attachSession();
                }
            }
        }

    }

    /**
     * The ServiceInstance to ServiceInstanceHealth paire.
     *
     *
     */
    private static class InstanceHealthPair{
        /**
         * The ServiceInstanceHealth.
         */
        ServiceInstanceHealth healthCallback;

        /**
         * last health check result.
         */
        boolean lastResult;

        /**
         * Is initied.
         */
        boolean isInited = false;

        /**
         * Constructor.
         *
         * @param healthCallback
         *         the ServiceInstanceHealth.
         */
        public InstanceHealthPair(ServiceInstanceHealth healthCallback){
            this.healthCallback = healthCallback;
        }
    }
}
