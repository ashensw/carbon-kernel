/*
 *   Copyright (c) $today.year.WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT     WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.carbon.user.core.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.xml.StringUtils;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

/**
 * Handle Cache in Group ID resolving flow.
 */
public class GroupIdResolverCache {

    private static Log log = LogFactory.getLog(GroupIdResolverCache.class);
    private static GroupIdResolverCache groupIdResolverCache = new GroupIdResolverCache();
    private static final String GROUP_ID_RESOLVER_CACHE_MANAGER = "GROUP_ID_RESOLVER_CACHE_MANAGER";

    private GroupIdResolverCache() {
    }

    /**
     * Gets a new instance of GroupIdResolverCache.
     *
     * @return A new instance of GroupIdResolverCache.
     */
    public synchronized static GroupIdResolverCache getInstance() {

        return groupIdResolverCache;
    }

    private Cache<String, Group> GroupIdResolverCache(String cacheName) {

        Cache<String, Group> cache;
        CacheManager cacheManager = Caching.getCacheManagerFactory().getCacheManager(GROUP_ID_RESOLVER_CACHE_MANAGER);
        cache = cacheManager.getCache(cacheName);
        return cache;
    }

    /**
     * Add a cache entry.
     *  @param key       Key which cache entry is indexed.
     * @param entry     Actual object where cache entry is placed.
     * @param cacheName Name of the cache.
     * @param tenantId  Tenant ID.
     */
    public void addToCache(String key, Group entry, String cacheName, int tenantId) {

        if (validateAddToCacheRequest(key, entry, cacheName)) return;
        try {
            startTenantFlow(tenantId);
            Cache<String, Group> cache = GroupIdResolverCache(cacheName);
            if (cache != null && !cache.containsKey(key)) {
                cache.put(key, entry);
                if (log.isDebugEnabled()) {
                    log.debug("Cache: " + cacheName + " which is under " + GROUP_ID_RESOLVER_CACHE_MANAGER + "," +
                            "added the entry: " + entry + " for the key: " + key + " successfully");
                }
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Retrieves a cache entry.
     *
     * @param key       CacheKey.
     * @param cacheName Name of the cache.
     * @param tenantId  Tenant ID.
     * @return Cached entry if the key presents, else returns null.
     */
    public Group getValueFromCache(String key, String cacheName, int tenantId) {

        if (validateGetValueFromCacheRequest(key, cacheName)) return null;
        try {
            startTenantFlow(tenantId);
            Cache<String, Group> cache = GroupIdResolverCache(cacheName);
            if (cache != null) {
                if (cache.containsKey(key)) {
                    Group entry = cache.get(key);
                    if (log.isDebugEnabled()) {
                        log.debug("Cache: " + cacheName + " which is under " + GROUP_ID_RESOLVER_CACHE_MANAGER +
                                ", found the entry: " + entry + " for the key: " + key + " successfully.");
                    }
                    return entry;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Cache: " + cacheName + " which is under " + GROUP_ID_RESOLVER_CACHE_MANAGER +
                            ", doesn't contain the key: " + key);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache: " + cacheName + " which is under "
                            + GROUP_ID_RESOLVER_CACHE_MANAGER);
                }
            }
            return null;
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Clears a cache entry.
     *
     * @param key       Key to clear cache.
     * @param cacheName Name of the cache.
     * @param tenantId  Tenant ID.
     */
    public void clearCacheEntry(String key, String cacheName, int tenantId) {

        if (validateClearCacheEntryRequest(key, cacheName)) return;
        try {
            startTenantFlow(tenantId);
            Cache<String, Group> cache = GroupIdResolverCache(cacheName);
            if (cache != null) {
                cache.remove(key);
                if (log.isDebugEnabled()) {
                    log.debug("Cache: " + cacheName + " which is under " + GROUP_ID_RESOLVER_CACHE_MANAGER +
                            ", is removed entry for the key: " + key + " successfully.");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache: " + cacheName + " which is under "
                            + GROUP_ID_RESOLVER_CACHE_MANAGER);
                }
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Remove everything in the cache.
     *
     * @param cacheName Name of the cache.
     * @param tenantId  Tenant ID.
     */
    public void clear(String cacheName, int tenantId) {

        if (validateClearCacheRequest(cacheName)) return;
        try {
            startTenantFlow(tenantId);
            Cache<String, Group> cache = GroupIdResolverCache(cacheName);
            if (cache != null) {
                cache.removeAll();
                if (log.isDebugEnabled()) {
                    log.debug("Cache: " + cacheName + " which is under " + GROUP_ID_RESOLVER_CACHE_MANAGER +
                            ", is cleared successfully");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Error while getting the cache: " + cacheName + " which is under " +
                            GROUP_ID_RESOLVER_CACHE_MANAGER);
                }
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private void startTenantFlow(int tenantId) {

        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantId(tenantId, true);
    }

    private boolean validateAddToCacheRequest(String key, Group entry, String cacheName) {

        if (StringUtils.isEmpty(key) || entry == null || StringUtils.isEmpty(cacheName)) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid input parameters in add to cache request. Cache key: " + key + " ,Cache entry: " +
                        entry + " ,Cache: " + cacheName);
            }
            return true;
        }
        return false;
    }

    private boolean validateGetValueFromCacheRequest(String key, String cacheName) {

        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(cacheName)) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid input parameters in get value from cache request. Cache key: " + key +
                        " ,Cache: " + cacheName);
            }
            return true;
        }
        return false;
    }

    private boolean validateClearCacheEntryRequest(String key, String cacheName) {

        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(cacheName)) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid input parameters in clear from cache request. Cache key: " + key +
                        " ,Cache: " + cacheName);
            }
            return true;
        }
        return false;
    }

    private boolean validateClearCacheRequest(String cacheName) {

        if (StringUtils.isEmpty(cacheName)) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid input parameters in clear all cache request. Cache: " + cacheName);
            }
            return true;
        }
        return false;
    }

}
