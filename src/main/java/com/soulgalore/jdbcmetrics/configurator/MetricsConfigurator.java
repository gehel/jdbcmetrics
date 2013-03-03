package com.soulgalore.jdbcmetrics.configurator;

import com.yammer.metrics.core.MetricProcessor;
import com.yammer.metrics.core.MetricsRegistry;

public interface MetricsConfigurator {

    /**
     * Configure reporting for the given {@link MetricsRegistry}.
     * 
     * A class implementing this method could configure a
     * {@link MetricProcessor}, for example a GraphiteProcessor to publish
     * metrics to a Graphite server.
     * 
     * @param registry
     */
    void configure(MetricsRegistry registry);

}
