/**
 * Copyright (c) 2013-2014 by Cisco Systems, Inc. 
 * All rights reserved. 
 */
package com.cisco.vss.foundation.directory.impl;

import com.cisco.vss.foundation.directory.DirectoryServiceClientManager;
import com.cisco.vss.foundation.directory.LookupManager;
import com.cisco.vss.foundation.directory.RegistrationManager;
import com.cisco.vss.foundation.directory.ServiceDirectoryManagerFactory;
import com.cisco.vss.foundation.directory.config.ServiceDirectoryConfig;
import com.cisco.vss.foundation.directory.lifecycle.Closable;

/**
 * It is the default ServiceDirectoryManagerFactory to access remote ServiceDirectory server node.
 * 
 * When there is no other ServiceDirectoryManagerFactory provider assigned, SD API will instantialize 
 * this class to provide ServiceDirectory services.
 * 
 * @author zuxiang
 *
 */
public class DefaultServiceDirectoryManagerFactory implements
		ServiceDirectoryManagerFactory, Closable {
	
	/**
	 * RegistrationManager, it is lazy initialized.
	 */
	private RegistrationManagerImpl registrationManager;
	
	/**
	 * The LookupManager, it is lazy initialized.
	 */
	private LookupManagerImpl lookupManager;
	
	/**
	 * The DirectoryServiceClientManager.
	 */
	private DirectoryServiceClientManager dirSvcClientMgr;
	
	/**
	 * Default constructor.
	 */
	public DefaultServiceDirectoryManagerFactory(){
	}
	
	/**
	 * Get RegistrationManager.
	 * 
	 * It is thread safe in lazy initializing.
	 * 
	 * @return
	 * 		the RegistrationManager implementation instance.
	 */
	@Override
	public RegistrationManager getRegistrationManager(){
		if(registrationManager == null){
			synchronized(this){
				if(registrationManager == null){
					RegistrationManagerImpl registration = new RegistrationManagerImpl(getDirectoryServiceClientManager());
					registration.start();
					registrationManager = registration;
				}
			}
		}
		return registrationManager;
	}
	
	/**
	 * get LookupManager
	 * 
	 * It is thread safe in lazy initializing.
	 * 
	 * @return
	 * 		the LookupManager implementation instance.
	 */
	@Override
	public LookupManager getLookupManager(){
		if(lookupManager == null){
			synchronized(this){
				if(lookupManager == null){
					LookupManagerImpl lookup = new LookupManagerImpl(getDirectoryServiceClientManager());
					lookup.start();
					lookupManager = lookup;
				}
			}
		}
		return lookupManager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(DirectoryServiceClientManager manager) {
		this.dirSvcClientMgr = manager;
	}
	
	/**
	 * get the DirectoryServiceClientManager to access remote directory server.
	 * 
	 * @return
	 * 		the DirectoryServiceClientManager.
	 */
	public DirectoryServiceClientManager getDirectoryServiceClientManager(){
		return dirSvcClientMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setServiceDirectoryConfig(ServiceDirectoryConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// do nothing now.
	}

	@Override
	public void stop() {
		if(registrationManager != null){
			((RegistrationManagerImpl) registrationManager).stop();
		}
		
		if(lookupManager != null){
			((LookupManagerImpl) lookupManager).stop();
		}
	}

}
