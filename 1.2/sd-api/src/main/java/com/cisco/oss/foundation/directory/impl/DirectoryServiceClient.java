package com.cisco.oss.foundation.directory.impl;

import com.cisco.oss.foundation.directory.client.DirectoryServiceRestfulClient;

/**
 * The class is only for old compatible
 * @deprecated
 */
public class DirectoryServiceClient {

    /**
     * use {@link DirectoryServiceRestfulClient#SD_API_SD_SERVER_FQDN_PROPERTY}
     */
    @Deprecated
    String SD_API_SD_SERVER_FQDN_PROPERTY = DirectoryServiceRestfulClient.SD_API_SD_SERVER_FQDN_PROPERTY;

    /**
     * use {@link DirectoryServiceRestfulClient#SD_API_SD_SERVER_FQDN_DEFAULT}
     */
    @Deprecated
    String SD_API_SD_SERVER_FQDN_DEFAULT = DirectoryServiceRestfulClient.SD_API_SD_SERVER_FQDN_DEFAULT;

    /**
     * use {@link DirectoryServiceRestfulClient#SD_API_SD_SERVER_PORT_PROPERTY}
     */
    @Deprecated
    String SD_API_SD_SERVER_PORT_PROPERTY = DirectoryServiceRestfulClient.SD_API_SD_SERVER_PORT_PROPERTY;

    /**
     * use {@link DirectoryServiceRestfulClient#SD_API_SD_SERVER_PORT_DEFAULT}
     */
    @Deprecated
    int SD_API_SD_SERVER_PORT_DEFAULT = DirectoryServiceRestfulClient.SD_API_SD_SERVER_PORT_DEFAULT;

}
