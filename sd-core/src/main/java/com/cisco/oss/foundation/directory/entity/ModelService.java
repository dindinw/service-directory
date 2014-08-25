/**
 * Copyright (c) 2013-2014 by Cisco Systems, Inc. 
 * All rights reserved. 
 */
package com.cisco.oss.foundation.directory.entity;

import java.util.Date;
import java.util.List;

/**
 * The Service Model Object in Service Directory.
 * 
 * It is the logic Service Object in Service Directory. The ServiceInstance of same service name belong to
 * one ModeService.
 * 
 * @author zuxiang
 *
 */
public class ModelService {

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
	private List<ModelServiceInstance> serviceInstances;
	
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
	public ModelService(){
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * 		the name.
	 * @param id
	 * 		the instanceId.
	 * @param modifiedTime
	 * 		the last modified time stamp.
	 * @param createTime
	 * 		the create time stamp.
	 */
	public ModelService(String name, String id, Date modifiedTime, Date createTime){
		this.name = name;
		this.id = id;
		this.modifiedTime = modifiedTime;
		this.createTime = createTime;
	}
	
	/**
	 * Get service name.
	 * 
	 * @return
	 * 		the service name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set service name.
	 * 
	 * @param name
	 * 		the service name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the id.
	 * 
	 * @return
	 * 		the id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the id.
	 * 
	 * @param id
	 * 		the id.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the ModelServiceInstance list.
	 * 
	 * @return
	 * 		the ModelServiceInstance list.
	 */
	public List<ModelServiceInstance> getServiceInstances() {
		return serviceInstances;
	}
	
	/**
	 * Set the ModelServiceInstance list.
	 * @param serviceInstances
	 * 		the ModelServiceInstance list.
	 */
	public void setServiceInstances(List<ModelServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
	
	/**
	 * Get the last modified time stamp.
	 * 
	 * @return
	 * 		the last modifed time stamp.
	 */
	public Date getModifiedTime() {
		return modifiedTime;
	}
	
	/**
	 * Set the last modified time stamp.
	 * 
	 * @param modifiedTime
	 * 		the last modified time stamp.
	 */
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	/**
	 * Get the create time stamp.
	 * 
	 * @return
	 * 		the create time stamp.
	 */
	public Date getCreateTime() {
		return createTime;
	}
	
	/**
	 * Set the create time stamp.
	 * 
	 * @param createTime
	 * 		the create time stamp.
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
