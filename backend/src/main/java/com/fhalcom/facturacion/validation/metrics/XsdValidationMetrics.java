package com.fhalcom.facturacion.validation.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class XsdValidationMetrics {
  private final MeterRegistry registry;

  public XsdValidationMetrics(MeterRegistry registry) {
    this.registry = registry;
  }

  public void recordSuccess(String tenant, String docType, String version, long nanos) {
    Counter.builder("xsd_validation_success_total")
        .tag("tenant", tenant == null ? "default" : tenant)
        .tag("docType", docType == null ? "unknown" : docType)
        .tag("version", version == null ? "unknown" : version)
        .register(registry)
        .increment();

    Timer.builder("xsd_validation_duration")
        .publishPercentileHistogram()
        .tag("tenant", tenant == null ? "default" : tenant)
        .tag("docType", docType == null ? "unknown" : docType)
        .register(registry)
        .record(nanos, TimeUnit.NANOSECONDS);
  }

  public void recordFailure(String tenant, String docType, String version, String reason, long nanos) {
    Counter.builder("xsd_validation_failure_total")
        .tag("tenant", tenant == null ? "default" : tenant)
        .tag("docType", docType == null ? "unknown" : docType)
        .tag("version", version == null ? "unknown" : version)
        .tag("reason", reason == null ? "unknown" : sanitize(reason))
        .register(registry)
        .increment();

    Timer.builder("xsd_validation_duration")
        .publishPercentileHistogram()
        .tag("tenant", tenant == null ? "default" : tenant)
        .tag("docType", docType == null ? "unknown" : docType)
        .register(registry)
        .record(nanos, TimeUnit.NANOSECONDS);
  }

  private String sanitize(String s) {
    if (s == null) return "unknown";
    s = s.trim();
    // Limit tag length to avoid cardinality explosion
    if (s.length() > 64) return s.substring(0, 64);
    return s;
  }
}
