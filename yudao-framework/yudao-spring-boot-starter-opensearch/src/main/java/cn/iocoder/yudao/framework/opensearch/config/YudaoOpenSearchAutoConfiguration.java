package cn.iocoder.yudao.framework.opensearch.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.util.Collection;

@AutoConfiguration
@Conditional(YudaoOpenSearchAutoConfiguration.EndpointConfigured.class)
@ConditionalOnMissingBean(OpenSearchClient.class)
public class YudaoOpenSearchAutoConfiguration {

    @Bean(destroyMethod = "close")
    @Lazy
    public OpenSearchTransport openSearchTransport(
            @Value("${yudao.opensearch.url}") URI endpoint,
            @Value("${yudao.opensearch.username:}") String username,
            @Value("${yudao.opensearch.password:}") String password,
            @Value("${yudao.opensearch.ca-certificate:}") String caCertificate,
            @Value("${yudao.opensearch.connect-timeout:PT2S}") Duration connectTimeout,
            @Value("${yudao.opensearch.response-timeout:PT15S}") Duration responseTimeout)
            throws IOException, GeneralSecurityException {
        HttpHost host = HttpHost.create(endpoint);
        SSLContext sslContext = StringUtils.hasText(caCertificate) ? sslContext(Path.of(caCertificate)) : null;
        ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
        String path = endpoint.getRawPath();
        if (StringUtils.hasText(path) && !"/".equals(path)) {
            builder.setPathPrefix(path.replaceAll("/+$", ""));
        }
        builder.setHttpClientConfigCallback(client -> {
            var connections = PoolingAsyncClientConnectionManagerBuilder.create()
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                            .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout.toMillis()))
                            .build());
            if (sslContext != null) {
                connections.setTlsStrategy(ClientTlsStrategyBuilder.create().setSslContext(sslContext).build());
            }
            client.setConnectionManager(connections.build());
            if (StringUtils.hasText(username)) {
                BasicCredentialsProvider credentials = new BasicCredentialsProvider();
                credentials.setCredentials(new AuthScope(host),
                        new UsernamePasswordCredentials(username, password.toCharArray()));
                client.setDefaultCredentialsProvider(credentials);
            }
            return client;
        });
        builder.setRequestConfigCallback(request -> request
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeout.toMillis()))
                .setResponseTimeout(Timeout.ofMilliseconds(responseTimeout.toMillis())));
        return builder.build();
    }

    private SSLContext sslContext(Path caPath) throws IOException, GeneralSecurityException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        try (InputStream input = Files.newInputStream(caPath)) {
            Collection<? extends Certificate> certificates =
                    CertificateFactory.getInstance("X.509").generateCertificates(input);
            if (certificates.isEmpty()) {
                throw new IOException("OpenSearch CA certificate file is empty: " + caPath);
            }
            int index = 0;
            for (Certificate certificate : certificates) {
                trustStore.setCertificateEntry("opensearch-ca-" + index++, certificate);
            }
        }
        return SSLContexts.custom().loadTrustMaterial(trustStore, null).build();
    }

    @Bean
    @Lazy
    public OpenSearchClient openSearchClient(OpenSearchTransport transport) {
        return new OpenSearchClient(transport);
    }

    static class EndpointConfigured implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return StringUtils.hasText(context.getEnvironment().getProperty("yudao.opensearch.url"));
        }
    }
}
