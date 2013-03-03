package com.soulgalore.jdbcmetrics.configurator;

import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.JmxReporter;

public class JMXConfigurator implements MetricsConfigurator {

    @Override
    public void configure(MetricsRegistry registry) {
        new JmxReporter(registry);
    }

}
