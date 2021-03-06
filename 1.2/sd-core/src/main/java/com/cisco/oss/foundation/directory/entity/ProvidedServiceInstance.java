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
package com.cisco.oss.foundation.directory.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ProvidedServiceInstance used by Service Provider to register/update a service
 * instance.
 *
 * In the ProvidedServiceInstance, the address is required attribute to identify 
 * the multiple instances of the same service.
 *
 * The address is the IP address or hostname of the running instance, 
 * the port is the port which the instance binds to. The port is optional.
 *
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvidedServiceInstance {

    /**
     * The instance service name.
     */
    private String serviceName;

    /**
     * The instance uri.
     */
    private String uri;

    /**
     * The address of the instance, it can be real IP or host name
     */
    private String address;

    /**
     * The instance OperationalStatus.
     */
    private OperationalStatus status;

    /**
     * Whether the instance is monitored or not in Service Directory.
     */
    private boolean monitorEnabled = true;

    /**
     * The instance metadata info. Optional.
     */
    private Map<String, String> metadata;
    
    /**
     * The port of the instance. Optional.
     */
    private int port = 0;

    /**
     *  The TLS port of the instance. Optional.
     */
    private int tls_port = 0;
    
    /**
     * The protocol. Optional.
     */
    private String protocol;
    

    /**
     * Constructor.
     */
    public ProvidedServiceInstance() {

    }

    /**
     * Constructor.
     *
     * @param serviceName
     *            the service name.
     * @param address
     *            the address that the instance is running on
     */
    public ProvidedServiceInstance(String serviceName, String address) {
        this(serviceName, address, null, null,null);
    }

    /**
     * Constructor.
     *
     * @param serviceName
     *            the service name.
     * @param address
     *            the server address, it can be real IP or host name.
     * @param uri
     *            the instance uri.
     * @param status
     *            the OperationalStatus.
     * @param metadata
     *            the metadata Map.
     */
    public ProvidedServiceInstance(String serviceName, String address,
            String uri, OperationalStatus status,
            Map<String, String> metadata) {
        this.serviceName = serviceName;
        this.address = address;
        this.uri = uri;
        this.status = status;
        this.metadata = metadata;
    }

    /**
     * Get the URI string.
     *
     * @return the URI String.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the URI String.
     *
     * @param uri
     *            the URI String.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Get the real address, it can be real IP or host name.
     *
     * @return the real address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the real address, it can be real IP or host name.
     *
     * @param address
     *            the real address.
     */
    public void setAddress(String address) {
        this.address = address;
    }


    /**
     * Get the OperationalStatus.
     *
     * @return the OperationalStatus.
     */
    public OperationalStatus getStatus() {
        return status;
    }

    /**
     * Set the OperationalStatus.
     *
     * @param status
     *            the OperationalStatus.
     */
    public void setStatus(OperationalStatus status) {
        this.status = status;
    }

    /**
     * check if it is monitored in Service Directory.
     *
     * @return true if monitor enabled.
     */
    public boolean isMonitorEnabled() {
        return monitorEnabled;
    }

    /**
     * Set the service to be monitored or not.
     *
     * @param monitor
     *            true if monitor enabled, false if monitor disabled.
     */
    public void setMonitorEnabled(boolean monitor) {
        this.monitorEnabled = monitor;
    }

    /**
     * Get the metadata Map.
     *
     * @return the metadata Map.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Set the Metadata Map.
     *
     * @param metadata
     *            the metadata Map.
     */
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the service name.
     *
     * @return the service name.
     */
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * Get the port
     * 
     * @return port
     *           the port on which instance runs
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port
     * 
     * @param port
     *           the port on which instance runs
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the TLS port
     * 
     * @return tls_port
     *           the TLS port on which instance runs
     */
    public int getTls_port() {
        return tls_port;
    }

    /**
     * Set the port
     * 
     * @param tls_port
     *           the TLS port on which instance runs
     */
    public void setTls_port(int tls_port) {
        this.tls_port = tls_port;
    }

    /**
     * Get the protocol e.g. http
     * @return protocol
     *             the transport protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol
     * @param protocol
     *            the transport protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * set auto removed to false, which will set a metadata to let the instance will not be removed automatically if max living time is exceeed
     * on the server side. The default setting always true to allow auto-removing..
     */
    public void setAutoRemoved(boolean autoRemove){
        if (!autoRemove) {
            if (getMetadata() == null) {
                setMetadata(new HashMap<String, String>());
            }
            getMetadata().put("autoRemoved", "false");
        }
    }

    /**
     * get the autoRemoved property of the service instance.
     * @return true by default
     */
    public boolean getAutoRemoved(){
        Map<String, String> meta = getMetadata();
        if (meta!=null&&meta.containsKey("autoRemoved")){
            return !"false".equals(meta.get("autoRemoved"));
        }
        return true;
    }



}
