package com.cisco.oss.foundation.directory.entity;


public enum Permission {
	/**
	 * No permission.
	 */
	NONE(0),
	/**
	 * Permission to lookup ServiceInstance.
	 */
	READ(1),
	/**
	 * Permission to update ServiceInstance.
	 */
	WRITE(2),
	
	/**
	 * Permission to register ServiceInstance.
	 */
	CREATE(4),
	
	/**
	 * Permission to unregister ServiceInstance.
	 */
	DELETE(8),
	
	/**
	 * Permission of createUser, setACL.
	 */
	ADMIN(16);
	
	private int id;
	
	Permission(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
