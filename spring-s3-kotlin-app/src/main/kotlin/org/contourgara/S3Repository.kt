package org.contourgara

import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class S3Repository(private val s3Client: S3Client) {
    fun save(bucket: String, key: String) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromString("Hello S3!!!")
        )
    }
}
