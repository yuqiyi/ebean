package io.ebeaninternal.server.cache;

import io.ebean.cache.ServerCache;
import io.ebean.cache.ServerCacheRegion;
import io.ebean.cache.ServerCacheType;
import io.ebeaninternal.api.SpiCacheRegion;
import io.ebeaninternal.server.cluster.ClusterManager;
import io.ebeaninternal.server.deploy.DCacheRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the bean and query caches.
 */
public class DefaultServerCacheManager implements SpiCacheManager {

  private static final Logger log = LoggerFactory.getLogger("io.ebean.cache.REGION");

  private final Map<String, SpiCacheRegion> regionMap = new HashMap<>();

  private final ClusterManager clusterManager;

  private final DefaultCacheHolder cacheHolder;

  private final boolean localL2Caching;

  private final String serverName;

  /**
   * Create with a cache factory and default cache options.
   */
  public DefaultServerCacheManager(CacheManagerOptions builder) {
    this.clusterManager = builder.getClusterManager();
    this.serverName = builder.getServerName();
    this.localL2Caching = builder.isLocalL2Caching();
    this.cacheHolder = new DefaultCacheHolder(builder);
  }

  /**
   * Construct when l2 cache is disabled.
   */
  public DefaultServerCacheManager() {
    this(new CacheManagerOptions());
  }

  @Override
  public boolean isLocalL2Caching() {
    return localL2Caching;
  }

  @Override
  public List<ServerCacheRegion> allRegions() {
    return new ArrayList<>(regionMap.values());
  }

  @Override
  public void setEnabledRegions(String regions) {

    List<String> enabledRegions = Arrays.asList(regions.split(","));
    List<String> disabledRegions = new ArrayList<>();

    for (SpiCacheRegion region : regionMap.values()) {
      if (enabledRegions.contains(region.getName())) {
        if (!region.isEnabled()) {
          region.setEnabled(true);
          log.debug("Cache region[{}] enabled", region.getName());
        }
      } else {
        disabledRegions.add(region.getName());
        if (region.isEnabled()) {
          region.setEnabled(false);
          log.debug("Cache region[{}] disabled", region.getName());
        }
      }
    }
    log.info("Cache regions enabled:{} disabled:{}", enabledRegions, disabledRegions);
  }

  @Override
  public void setAllRegionsEnabled(boolean enabled) {
    log.debug("All cache regions enabled[{}]", enabled);
    for (SpiCacheRegion region : regionMap.values()) {
      region.setEnabled(enabled);
    }
  }

  @Override
  public SpiCacheRegion getRegion(String region) {
    return regionMap.computeIfAbsent(region, DCacheRegion::new);
  }

  /**
   * Clear all caches.
   */
  @Override
  public void clearAll() {
    cacheHolder.clearAll();
    if (clusterManager != null) {
      clusterManager.cacheClearAll(serverName);
    }
  }

  @Override
  public void clearAllLocal() {
    cacheHolder.clearAll();
  }

  @Override
  public void clear(Class<?> beanType) {
    cacheHolder.clear(name(beanType));
    if (clusterManager != null) {
      clusterManager.cacheClear(serverName, beanType);
    }
  }

  @Override
  public void clearLocal(Class<?> beanType) {
    cacheHolder.clear(name(beanType));
  }

  @Override
  public ServerCache getCollectionIdsCache(Class<?> beanType, String propertyName) {
    return cacheHolder.getCache(beanType, name(beanType) + "." + propertyName, ServerCacheType.COLLECTION_IDS);
  }

  @Override
  public ServerCache getNaturalKeyCache(Class<?> beanType) {
    return cacheHolder.getCache(beanType, name(beanType), ServerCacheType.NATURAL_KEY);
  }

  /**
   * Return the query cache for a given bean type.
   */
  @Override
  public ServerCache getQueryCache(Class<?> beanType) {
    return cacheHolder.getCache(beanType, name(beanType), ServerCacheType.QUERY);
  }

  /**
   * Return the bean cache for a given bean type.
   */
  @Override
  public ServerCache getBeanCache(Class<?> beanType) {
    return cacheHolder.getCache(beanType, name(beanType), ServerCacheType.BEAN);
  }

  private String name(Class<?> beanType) {
    return beanType.getName();
  }

}
