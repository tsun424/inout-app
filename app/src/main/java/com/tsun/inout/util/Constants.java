package com.tsun.inout.util;

import com.microsoft.aad.adal.AuthenticationResult;

public class Constants {

    public static final String SDK_VERSION = "1.0";

    /**
     * UTF-8 encoding
     */
    public static final String UTF8_ENCODING = "UTF-8";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String HEADER_AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    // -------------------------------AAD
    // PARAMETERS----------------------------------
    // https://login.windows.net/tenantInfo
    // https://login.windows.net/<tenantID>
    public static String AUTHORITY_URL = "https://login.windows.net/c6c0f5f5-fff4-4667-ad16-ff34d43073a8";

    // Clientid is given from AAD page when you register your Android app
    public static String CLIENT_ID = "5d616890-ed6b-4706-a526-d619c8d22791";

    // URI for the resource. You need to setup this resource(WEB API) at AAD. APP ID URI configured in AAD App
    public static String RESOURCE_ID = "https://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php";

    // RedirectUri is where the token is redirected to in web flows. In native clients, this isn't used yet.
    // but it must be configured, otherwise, sign in error
    public static String REDIRECT_URL = "https://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php";

    // Endpoint we are targeting for the deployed WebAPI service
    public static String SERVICE_URL = "https://ec2-54-149-243-26.us-west-2.compute.amazonaws.com/inout/public/index.php";

    public static String CORRELATION_ID = "";
    public static String USER_HINT = "";
    public static String EXTRA_QP = "";
    public static boolean FULL_SCREEN = true;
    public static AuthenticationResult CURRENT_RESULT = null;
    public static String AUTH_TOKEN = "";

    // ------------------------------------------------------------------------------------------

    public static final String SHARED_PREFERENCE_NAME = "";

    public static final String KEY_NAME_ASK_BROKER_INSTALL = "";
    public static final String KEY_NAME_CHECK_BROKER = "";

}
