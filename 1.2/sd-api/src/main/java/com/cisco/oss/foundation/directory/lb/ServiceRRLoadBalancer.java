package com.cisco.oss.foundation.directory.lb;

import java.util.List;

import com.cisco.oss.foundation.directory.entity.ModelServiceInstance;
import com.cisco.oss.foundation.directory.impl.DirectoryLookupService;

public class ServiceRRLoadBalancer extends RoundRobinLoadBalancer {

	private final String serviceName ;
	public ServiceRRLoadBalancer(DirectoryLookupService lookupService, String serviceName) {
		super(lookupService);
		this.serviceName = serviceName;
	}
	
	public String getServiceName(){
		return serviceName;
	}

	@Override
	public List<ModelServiceInstance> getServiceInstanceList() {
		return getLookupService().getUPModelInstances(serviceName);
	}

}
