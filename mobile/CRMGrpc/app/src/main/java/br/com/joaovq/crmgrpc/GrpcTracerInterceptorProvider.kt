package br.com.joaovq.crmgrpc

import io.grpc.ClientInterceptor
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.sdk.trace.export.SpanExporter

object GrpcTracerInterceptorProvider {
    fun create(spanExporter: SpanExporter = LoggingSpanExporter.create()): ClientInterceptor {
        val tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .setResource(Resource.getDefault())
            .build()
        val openTelemetrySdk = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal()
        val grpcTelemetry = GrpcTelemetry.create(openTelemetrySdk)
        return grpcTelemetry.newClientInterceptor()
    }
}