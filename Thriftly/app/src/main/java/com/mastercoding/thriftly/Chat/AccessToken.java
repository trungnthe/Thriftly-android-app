package com.mastercoding.thriftly.Chat;

import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public static String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"thriftly-5747f\",\n" +
                    "  \"private_key_id\": \"c0ec2dbc6b0cb9120b971e6fe9a791067658973c\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLNIu8tKqqr7sJ\\n+c+jo7iPCwGjx5RT1GwP5f/F2KGuWPkDfpNzaOSwCTa4CdCEbZg+b8Rd+GUMC5L3\\nIQYQn+4LP1FsLVeiExgC9K9OepDfAY3wy4/ickUD6aLCmo5RXXnyPAigpJqxF6/i\\n6RZOkZ0i2+5RIrMs4LNqKOUsfV/jBLroUKedl4/Mcf8uXFzPIvwvapGMxB24VEUQ\\nF0JiJRAFQnbIAIEwHkTZuHH0iK9un6PlHj1E8SFoGMuACxG9F8ennyaI8nk3PXnA\\nVkR5Lhymo5xcpQT6Sp2OvtJsR8JZaGwP9SU4H3BuI8i/BU5oTiVGH25aotrM4jvQ\\n0/3WaL7bAgMBAAECggEADhVAMf3bbiTrs49Koq6wFcnPfUhRsVWhf6CTD2UqH2kv\\nKNzW30K/xqt8zVhwyRzHVlxsoY6bgjAzKUlyoChVWRY0UGj8D9F+8oV+uNOpwqra\\n6XtSJ4F/4vuIOjB3+I+f6ffbKTb/OZLIr9MzppJU75uKcrkS7eNMy+i1qVWvFY6Y\\nqrhN4w8h4RrGDz1r9CFUscmC2sHbLFy6/hU+evZitzITx8/1tTr4sYGBpaem+prr\\nnnC1g7GM/rrIeKQP0Y+6OdDmd2seVdNV9ZpCt0B4ZYbtT+HJ9O7vDgxi8OhAZAdI\\nQeVzvUefjz+xYp3R4Kylk4eHcOgQl05rfdIf/8wa8QKBgQD1V8lAFmjx404zKah5\\ndf7E8nFifiQ36iEf3sPCwX0AQFCXsvvSt1YG9yB0uatbwWlIfYoBgw0dZG3TdAaL\\niDWXUYHyUCpMVm5qz3rWCGT2v+/RUM1CTx79H/3n8+Xax478i2o8VmOA33y35oqb\\nuZRpaD445vgBYWkxZbyTz1+5rQKBgQDUCDBYkkjUOyrVQtgBeKHbwTBiG1KSYEw0\\n7a1sAGysXm63aKzlMHyba6goHOtE5SFDxKwFZqK1bcFVapCA7gEzSwDk2E/pMIY/\\n7HLN1X7hs8nXD/Oc3WVXyC0wgz/mpvKOrSPmIpZZzvLIHsCPXxyUg/mdlfa980AR\\nu1+udNL7pwKBgAwzSFu6gY99yTa8HJelqXyrvb2zHiP1rZ4DGuMFcizgBeH1UZaB\\n7XI9Ocz0KrROYujq7UDz/mP2yYtZWrX3mQqkT/z3xZK0PZtsI+Se2D54/vj+tqZU\\nfusBe0PcmhKOF+sxF6BWf246Lt6KRVxHFz1CpM15lt2itXNyZrVG+7ClAoGAK5YD\\nWjG4BuiixXQYMzU5EJOF/qTfNHfXMoopPCpaLJS87bb206ZD9y8KQjSvqFnxnJyx\\nP+r5R5JoS8a8LcS/OmwQovpUcvsmnb1dXDKnAn6S30LIMVbRnlnn8DEK0b8/3VR6\\nLLSvc4a3IawwqUhD98OpbFuHfUSjxwFdid85/8ECgYEAihwd/m3nCmpR3CrVzjG9\\nkDFI+oamM3vPrTNoN4qzj06MBQ7pnXMO8U2dNLPGRo668alxCGHfqlszc0kX8hTo\\njKAXzLtfrYNcrlsXhKBrPj/NbUXJUxP+ypmQcaUJzbcyuCu9zBBTSOBF+ZLnZVV9\\nY8mkwCerV3ycAPe+9XzPJ7s=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-oikuf@thriftly-5747f.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"112258231642274621937\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-oikuf%40thriftly-5747f.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}
