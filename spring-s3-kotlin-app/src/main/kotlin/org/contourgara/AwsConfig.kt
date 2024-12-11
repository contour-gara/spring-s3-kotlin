package org.contourgara

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aws")
class AwsConfig {
    var accessKeyId: String = ""
    var secretKey: String = ""
    var region: String = ""
    var s3: S3 = S3()

    inner class S3 {
        var endpoint: String = ""
    }
}
