/*
 * LoadTestRegion.java -- 
 * Copyright (C) 2010-2015  Neustar Inc.  All Rights Reserved.
 */

package org.kfm.loadtesting.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kfm.loadtesting.cloud.CloudProvider;

public enum LoadTestRegion {

    US_EAST ("us-east-1", CloudProvider.AWS, "N. Virginia"),
    US_WEST ("us-west-1", CloudProvider.AWS, "San Francisco"),
    EU_WEST ("eu-west-1", CloudProvider.AWS, "Dublin, Ireland"),
    EU_CENTRAL ("eu-central-1", CloudProvider.AWS, "Frankfurt, Germany"),
    AP_SOUTHEAST ("ap-southeast-1", CloudProvider.AWS, "Singapore"),
    AP_NORTHEAST ("ap-northeast-1", CloudProvider.AWS, "Tokyo, Japan"),
    SAO_PAULO ("sa-east-1", CloudProvider.AWS, "Sao Paulo, Brasil"),
    OREGON ("us-west-2", CloudProvider.AWS, "Oregon"),
    SYDNEY ("ap-southeast-2", CloudProvider.AWS, "Sydney, Australia"),
    ORD ("ORD", CloudProvider.RACKSPACE, "Chicago, IL"), 
    DFW ("DFW", CloudProvider.RACKSPACE, ""),
    IAD ("IAD", CloudProvider.RACKSPACE, ""), 
    SYD ("SYD", CloudProvider.RACKSPACE, ""), 
    HKG ("HKG", CloudProvider.RACKSPACE, ""), 
    LON ("LON", CloudProvider.RACKSPACE, ""),
    GCE_ASIA_GENERAL ("asia-general", CloudProvider.GOOGLE, ""),
    GCE_ASIA_EAST_1A ("asia-east1-a", CloudProvider.GOOGLE, ""),
    GCE_ASIA_EAST_1B ("asia-east1-b", CloudProvider.GOOGLE, ""),
    GCE_ASIA_EAST_1C ("asia-east1-c", CloudProvider.GOOGLE, ""),
    GCE_EU_GENERAL ("europe-general", CloudProvider.GOOGLE, ""),
    GCE_EU_WEST_1B ("europe-west1-b", CloudProvider.GOOGLE, ""),
    GCE_EU_WEST_1C ("europe-west1-c", CloudProvider.GOOGLE, ""),
    GCE_EU_WEST_1D ("europe-west1-d", CloudProvider.GOOGLE, ""),
    GCE_US_GENERAL ("us-general", CloudProvider.GOOGLE, ""),
    GCE_US_CENTRAL_1B ("us-central1-b", CloudProvider.GOOGLE, ""),
    GCE_US_CENTRAL_1C ("us-central1-c", CloudProvider.GOOGLE, ""),
    GCE_US_CENTRAL_1F ("us-central1-f", CloudProvider.GOOGLE, ""),
    
    MULTI_REGION (null, CloudProvider.MULTI_REGION, "");

    private String regionName;
    private CloudProvider cloudProvider;
    private String location;
    
    static Map<CloudProvider, Integer> countMap = countRegions();
    
    private LoadTestRegion(String regionName, CloudProvider cloudProvider, String location) {
        this.regionName = regionName;
        this.cloudProvider = cloudProvider;
        this.location = location;
    }

    public CloudProvider getProvider(){
        return cloudProvider;
    }
    
    public String getProviderRegionName(){
        return regionName;
    }
    
    public static List<LoadTestRegion> getRackspaceRegions() {
    	
    	ArrayList<LoadTestRegion> list = new ArrayList<LoadTestRegion>();
    	list.add(LoadTestRegion.IAD);
    	list.add(LoadTestRegion.ORD);
    	list.add(LoadTestRegion.DFW);
    	list.add(LoadTestRegion.HKG);
    	list.add(LoadTestRegion.SYD);
    	list.add(LoadTestRegion.LON);
    	
    	return list; 
    }
    
    public static List<LoadTestRegion> getImplementationsForInterfaceRegion(LoadTestRegion region) {
    	
    	List<LoadTestRegion> implementations = new ArrayList<LoadTestRegion>();
       switch(region) {
            case GCE_ASIA_GENERAL:
            	implementations.add(LoadTestRegion.GCE_ASIA_EAST_1A);
            	implementations.add(LoadTestRegion.GCE_ASIA_EAST_1B);
            	implementations.add(LoadTestRegion.GCE_ASIA_EAST_1C);
    		    return implementations;
    		    
            case GCE_EU_GENERAL:
            	implementations.add(LoadTestRegion.GCE_EU_WEST_1B);
            	implementations.add(LoadTestRegion.GCE_EU_WEST_1C);
            	implementations.add(LoadTestRegion.GCE_EU_WEST_1D);
            	return implementations;
            	
            case GCE_US_GENERAL:
            	implementations.add(LoadTestRegion.GCE_US_CENTRAL_1B);
            	implementations.add(LoadTestRegion.GCE_US_CENTRAL_1C);
            	implementations.add(LoadTestRegion.GCE_US_CENTRAL_1F);
            	return implementations;
            	
            default:
    			return null;
       }
    }
    
    public static boolean isAvailabilityZone(LoadTestRegion region) {
    	
    	if (region.getProvider().equals(CloudProvider.GOOGLE) && !isInterfaceRegion(region)) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isInterfaceRegion(LoadTestRegion region) {
    	
    	if (region.equals(GCE_EU_GENERAL) || region.equals(GCE_ASIA_GENERAL) || region.equals(GCE_US_GENERAL)) {
    		return true;
    	}
    	return false;
    }
    
    public static LoadTestRegion[] getGCEInterfaceRegions() {
    	return new LoadTestRegion[] {LoadTestRegion.GCE_US_GENERAL, LoadTestRegion.GCE_ASIA_GENERAL, LoadTestRegion.GCE_EU_GENERAL};
    }
    
    public static LoadTestRegion getInterfaceForImplementationRegion(LoadTestRegion implementationRegion) {
    	
    	if (LoadTestRegion.isInterfaceRegion(implementationRegion)) {
    		return implementationRegion;
    	}
    	for (LoadTestRegion interfaceRegion : getGCEInterfaceRegions()) {
     		for (LoadTestRegion each : getImplementationsForInterfaceRegion(interfaceRegion)) {
    			if (implementationRegion.equals(each)) {
        			return interfaceRegion;
        		}
    		}
    	}
    	return null;
    }
    
    public static LoadTestRegion getSiblingImplementationRegion(LoadTestRegion implementationRegion) {
    	LoadTestRegion interfaceRegion = getInterfaceForImplementationRegion(implementationRegion);
    	for (LoadTestRegion potentialSiblingRegion : getImplementationsForInterfaceRegion(interfaceRegion)) {
    		if (!potentialSiblingRegion.equals(implementationRegion)) {
    			return potentialSiblingRegion;
    		}
    	}
    	return null;
    }
    
    public String getLocation() {
    	
        switch (this) {
            case AP_NORTHEAST:
                return "TOKYO";
            case AP_SOUTHEAST:
                return "SINGAPORE";
            case EU_WEST:
                return "DUBLIN";
            case EU_CENTRAL:
            	return "EU_CENTRAL";
            case US_EAST:
                return "DC";
            case US_WEST:
                return "SF";
            case SAO_PAULO:
                return "SAO_PAULO";
            case OREGON :
                return "OREGON";
            case SYDNEY:
                return "SYDNEY";
            case DFW:
                return "DFW";
            case IAD:
                return "IAD"; 
            case ORD:
                return "ORD";
            case HKG:
                return "HKG";
            case SYD:
                return "SYD"; 
            case LON:
            	return "LON";
            case GCE_US_GENERAL:
            	return "us-general";
            case GCE_US_CENTRAL_1B:
            	return "us-central1-b";
            case GCE_US_CENTRAL_1C:
            	return "us-central1-c";
            case GCE_US_CENTRAL_1F:
            	return "us-central1-f";
            case GCE_EU_GENERAL:
            	return "europe-general";
            case GCE_EU_WEST_1B:
            	return "europe-west1-b";
            case GCE_EU_WEST_1C:
            	return "europe-west1-c";
            case GCE_EU_WEST_1D:
            	return "europe-west1-d";
            case GCE_ASIA_GENERAL:
            	return "asia-general";
            case GCE_ASIA_EAST_1A:
            	return "asia-east1-a";
            case GCE_ASIA_EAST_1B:
            	return "asia-east1-b";
            case GCE_ASIA_EAST_1C:
            	return "asia-east1-c";
            case MULTI_REGION:
            	return "MULTI_REGION";
            default:
                return "UNKNOWN";
        }
    }
    
    public static LoadTestRegion getLocationByString(String location) {
    	for (LoadTestRegion region : LoadTestRegion.values()) {
    		if (region.getLocation().equalsIgnoreCase(location)) {
    			return region;
    		}
    	}
    	return null;
    }
    
    public static List<LoadTestRegion> getImplementationRegionMissingFromList(List<LoadTestRegion> usedRegions) {
    	LoadTestRegion interfaceRegion = getInterfaceForImplementationRegion(usedRegions.get(0));
    	List<LoadTestRegion> implementationRegions = getImplementationsForInterfaceRegion(interfaceRegion);
    	for (LoadTestRegion usedRegion : usedRegions) {
    		if (implementationRegions.contains(usedRegion)) {
    			implementationRegions.remove(usedRegion);
    		}
    	}
    	return implementationRegions;
    }
    
    public static int totalNumRegions(CloudProvider cloudProvider){
    	Integer count = countMap.get(cloudProvider);
    	if(count == null)
    	{
    		return 0; 
    	}
    	return count; 
    }

    private static Map<CloudProvider, Integer> countRegions() {
    	
    	int amazonEC2Count = 0; 
    	int rackspaceUSCount = 0; 
    	int rackspaceEUCount = 0;
    	int googleCECount = 0;
    	for (LoadTestRegion ltr: LoadTestRegion.values()) {  
    		if (CloudProvider.AWS.equals(ltr.cloudProvider)) {
    			amazonEC2Count++;
    		}
    		if (CloudProvider.RACKSPACE.equals(ltr.cloudProvider)) {
    			rackspaceUSCount++;
    		}
    		if (CloudProvider.RACKSPACE.equals(ltr.cloudProvider)) {
    			rackspaceEUCount++;
    		}
    		if(CloudProvider.GOOGLE.equals(ltr.cloudProvider)) {
    			googleCECount++;
    		}
    	}
    	
    	Map<CloudProvider, Integer> result = new HashMap<CloudProvider, Integer>();
    	result.put(CloudProvider.AWS, amazonEC2Count);
    	result.put(CloudProvider.RACKSPACE, rackspaceUSCount);
    	result.put(CloudProvider.RACKSPACE, rackspaceEUCount);
    	result.put(CloudProvider.GOOGLE, googleCECount);
    	
    	return result;
	}

    /**
     * MRLT: //Review for MRLT
     * @return
     */
	public boolean isHighCapacityRegion(){
        return this == US_EAST || this == US_WEST || this == OREGON || this == EU_WEST;
    }
	
	public static LoadTestRegion getLoadTestRegionByStringName(String region) {
		LoadTestRegion[] regions = LoadTestRegion.values();
		for (LoadTestRegion each : regions) {
			if (each.getLocation().equals(region)) {
				return each;
			}
		}
		return null;
	}

}
