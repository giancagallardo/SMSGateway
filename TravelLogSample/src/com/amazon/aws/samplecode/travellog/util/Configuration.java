/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazon.aws.samplecode.travellog.util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple class to manage loading the property file containing needed configuration data
 * from the package. Once loaded the configuration is held in memory as a singleton.  Since
 * we already require the simplejpa.properties file to support SimpleJPA, we use that
 * to store additional configuration values.
 */
public class Configuration {

    private static Configuration configuration = new Configuration();    

    public static final String S3_ENDPOINT_KEY = "S3";
    public static final String SIMPLE_DB_ENDPOINT_KEY = "SimpleDB";
    public static final String SNS_ENDPOINT_KEY = "SNS";
    
    public static final String BUNDLE_BUCKET_KEY = "bundleBucket";
    public static final String BUNDLE_PATH_KEY = "bundlePath";
    
    private Properties simpleJpaProperties = new Properties();
    private Properties endpoints = new Properties();
    private Properties bundleProperties = new Properties();

    private static final String AWS_CREDENTIALS_PROPERTIES = "/AwsCredentials.properties";
    private static final String SIMPLE_JPA_PROPERTY_PATH = "/simplejpa.properties";
    private static final String ENDPOINTS_PROPERTY_PATH = "/endpoints.properties";
    private static final String BUNDLE_PROPERTY_PATH = "/content-bundle.properties";

    private Logger logger = Logger.getLogger(Configuration.class.getName());

    private Configuration() {
        try {
            simpleJpaProperties.load(this.getClass().getResourceAsStream(SIMPLE_JPA_PROPERTY_PATH));
            simpleJpaProperties.load(this.getClass().getResourceAsStream(AWS_CREDENTIALS_PROPERTIES));
            endpoints.load(this.getClass().getResourceAsStream(ENDPOINTS_PROPERTY_PATH));
            bundleProperties.load(this.getClass().getResourceAsStream(BUNDLE_PROPERTY_PATH));           
        } catch ( Exception e ) {
            logger.log(Level.SEVERE, "Unable to load configuration: " + e.getMessage(), e);
        }
    }

    public static final Configuration getInstance () {
        return configuration;
    }

    public String getProperty (String propertyName) {
        return simpleJpaProperties.getProperty(propertyName);
    }
    
    /**
     * Returns the service endpoint for the service name given.
     * 
     * @see Configuration#S3_ENDPOINT_KEY
     * @see Configuration#SIMPLE_DB_ENDPOINT_KEY
     * @see Configuration#SNS_ENDPOINT_KEY
     */
    public String getServiceEndpoint(String service) {
        return endpoints.getProperty(service);
    }
    
    /**
     * Returns the bundle property requested. Used for pre-loading a journal
     * with predefined entries.
     * 
     * @see Configuration#BUNDLE_BUCKET_KEY
     * @see Configuration#BUNDLE_PATH_KEY
     */
    public String getBundleProperty(String bundlePropertyName) {
        return bundleProperties.getProperty(bundlePropertyName);
    }
}
