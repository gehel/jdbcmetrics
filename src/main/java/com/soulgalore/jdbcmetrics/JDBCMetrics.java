/******************************************************
 * JDBCMetrics
 * 
 *
 * Copyright (C) 2013 by Magnus Lundberg (http://magnuslundberg.com) & Peter Hedenskog (http://peterhedenskog.com)
 *
 ******************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is 
 * distributed  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
 * See the License for the specific language governing permissions and limitations under the License.
 *
 *******************************************************
 */
package com.soulgalore.jdbcmetrics;

import java.util.concurrent.TimeUnit;

import com.soulgalore.jdbcmetrics.configurator.MetricsConfigurator;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;

/**
 * Class responsible for holding all the Yammer Metrics.
 *
 */
public class JDBCMetrics {
    
    private static final String METRICS_CONFIGURATORS = JDBCMetrics.class
            .getName() + ".metricsConfigurators";

	private static final String GROUP = "jdbc";
	private static final String TYPE_READ = "read";
	private static final String TYPE_WRITE = "write";
	
	private final MetricsRegistry registry = new MetricsRegistry();
	
	private final Counter totalNumberOfReads = registry.newCounter(new MetricName(
			GROUP, TYPE_READ, "total-of-reads"));

	private final Counter totalNumberOfWrites = registry.newCounter(new MetricName(
			GROUP, TYPE_WRITE, "total-of-writes"));

	private final Histogram readCountsPerRequest = registry.newHistogram(new MetricName(
			GROUP, TYPE_READ, "read-counts-per-request"), true);

	private final Histogram writeCountsPerRequest = registry.newHistogram(new MetricName(
			GROUP, TYPE_WRITE, "write-counts-per-request"), true);

	private final Meter readMeter = registry.newMeter(new MetricName(GROUP, TYPE_READ,
			"reads"), "jdbcread", TimeUnit.SECONDS);

	private final Meter writeMeter = registry.newMeter(new MetricName(GROUP,
			TYPE_WRITE, "writes"), "jdbcwrite", TimeUnit.SECONDS);
	
	private final Timer writeTimer = registry.newTimer(new MetricName(GROUP,
			TYPE_WRITE, "write-time"), TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
	
	private final Timer readTimer = registry.newTimer(new MetricName(GROUP,
			TYPE_WRITE, "read-time"), TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
	
	
	private static final JDBCMetrics INSTANCE = new JDBCMetrics();

	
	private JDBCMetrics() {
	    configureMetricsRegistry();
	}

    /**
     * Configure reporting for the {@link MetricsRegistry}.
     * 
     * An exception will be thrown if the {@link MetricsRegistry} cannot be
     * configured. Throwing exception from constructor might not be the best
     * practice, but I prefer an early and clear failure. We could just log the
     * problems and continue as the {@link JDBCMetricsDriver} is still
     * functional even without this configuration.
     * 
     * @param configurators
     *            comma separated list of name of classes implementing
     *            {@link MetricsConfigurator}
     * @throws IllegalArgumentException
     *             if the given classes do not implement
     *             {@link MetricsConfigurator} or cannot be found on the
     *             classpath
     * @throws RuntimeException
     *             if the given classes cannot be instantiated or cannot be
     *             accessed
     */
    private void configureMetricsRegistry() {
        String configurators = System.getProperty(METRICS_CONFIGURATORS);

        // if METRICS_CONFIGURATORS property is not set, then dont do any
        // configuration
        if (configurators == null || configurators.length() == 0) {
            return;
        }

        for (String configuratorName : configurators.split(",")) {
            try {
                Class<?> clazz = Class.forName(configuratorName);
                if (!clazz.isInstance(MetricsConfigurator.class)) {
                    throw new IllegalArgumentException("MetricsConfigurator ["
                            + configuratorName + "] is not of type ["
                            + MetricsConfigurator.class.getName() + "]");
                }
                @SuppressWarnings("unchecked")
                Class<MetricsConfigurator> configuratorClass = (Class<MetricsConfigurator>) clazz;
                MetricsConfigurator configurator = configuratorClass
                        .newInstance();
                configurator.configure(getRegistry());
            } catch (ClassNotFoundException classNotFoundEx) {
                throw new IllegalArgumentException("MetricsConfigurator ["
                        + configuratorName + "] not found.", classNotFoundEx);
            } catch (InstantiationException ie) {
                throw new RuntimeException("MetricsConfigurator ["
                        + configuratorName + "] cannot be instantiated.", ie);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("MetricsConfigurator ["
                        + configuratorName + "] cannot be accessed.", iae);
            }
        }
    }

	/**
	 * Get the instance.
	 * 
	 * @return the singleton instance.
	 */
	public static JDBCMetrics getInstance() {
		return INSTANCE;
	}

	public Counter getTotalNumberOfReads() {
		return totalNumberOfReads;
	}

	public Counter getTotalNumberOfWrites() {
		return totalNumberOfWrites;
	}

	public Histogram getReadCountsPerRequest() {
		return readCountsPerRequest;
	}

	public Histogram getWriteCountsPerRequest() {
		return writeCountsPerRequest;
	}

	public Meter getReadMeter() {
		return readMeter;
	}

	public Meter getWriteMeter() {
		return writeMeter;
	}
	
	public Timer getWriteTimer() {
		return writeTimer;
	}
	
	public Timer getReadTimer() {
		return readTimer;
	}
	
	public MetricsRegistry getRegistry() {
		return registry;
	}
	
}
