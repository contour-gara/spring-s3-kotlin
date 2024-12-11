package org.contourgara

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration(proxyBeanMethods = false)
class S3ClientConfig(private val awsConfig: AwsConfig) {
    @Bean
    @Profile("default")
    fun createS3Client(): S3Client {
        return S3Client.builder()
            .credentialsProvider {
                AwsBasicCredentials.create(awsConfig.accessKeyId, awsConfig.secretKey)
            }
            .region(Region.of(awsConfig.region))
            .build()
    }

    @Bean
    @Profile("localstack")
    fun createLocalStackClient(): S3Client {
        return S3Client.builder()
            .credentialsProvider {
                AwsBasicCredentials.create(awsConfig.accessKeyId, awsConfig.secretKey)
            }
            .region(Region.of(awsConfig.region))
            .forcePathStyle(true)
            .endpointOverride(URI.create(awsConfig.s3.endpoint))
            .build()
    }
}
