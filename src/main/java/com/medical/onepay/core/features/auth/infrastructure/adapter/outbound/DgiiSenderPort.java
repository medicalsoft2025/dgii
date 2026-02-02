package com.medical.onepay.core.features.auth.infrastructure.adapter.outbound;

import java.io.File;

public interface DgiiSenderPort {
    String sendSignedXml(File xmlFile, String url, String token);
}
