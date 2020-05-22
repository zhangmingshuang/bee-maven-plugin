package bee.com.nascent.maven.plugin.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.http.HttpStatus;

/**
 * 网络工具类.
 *
 * @author carver.gu
 * @since 1.0, Sep 12, 2009
 */
@SuppressWarnings({"java:S1186", "java:S4830", "unused", "FieldCanBeLocal"})
public class WebUtils {

    public static final String DEFAULT_CONTENT_TYPE
        = "application/x-www-form-urlencoded;charset=UTF-8";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String HTTPS = "https";
    public static final String QUESTION = "?";
    public static final String AND = "&";


    private static SSLContext sslContext = null;

    private static HostnameVerifier hostnameVerifier;

    private static SSLSocketFactory sslSocketFactory = null;


    static {

        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()},
                new SecureRandom());

            sslContext.getClientSessionContext().setSessionTimeout(15);
            sslContext.getClientSessionContext().setSessionCacheSize(1000);

            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            //Ignore
        }

        //默认认证不通过，进行证书校验。
        hostnameVerifier = (hostname, session) -> false;
    }

    private WebUtils() {
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @SuppressWarnings("RedundantThrows")
        @Override
        public void checkClientTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {
            //ignore
        }

        @SuppressWarnings("RedundantThrows")
        @Override
        public void checkServerTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {
            //ignore
        }
    }

    /**
     * 执行HTTP POST请求.
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param connectTimeout 连接超时时间 毫秒
     * @param readTimeout    读取时超时时间 毫秒
     * @return 响应字符串
     * @throws IOException 请求流异常
     */
    public static String doPost(String url, Map<String, String> params, int connectTimeout,
        int readTimeout) throws IOException {
        return doPost(url, params, StandardCharsets.UTF_8, connectTimeout, readTimeout);
    }

    /**
     * 执行Post请求.
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param connectTimeout 连接超时时间，毫秒
     * @param readTimeout    读超时时间，毫秒
     * @param headers        请求头
     * @return 请求响应结果
     * @throws IOException 请求异常
     */
    public static String doPost(String url, Map<String, String> params, int connectTimeout,
        int readTimeout, Map<String, String> headers) throws IOException {
        String query = buildQuery(params, StandardCharsets.UTF_8);
        byte[] content = {};
        if (query != null) {
            content = query.getBytes(StandardCharsets.UTF_8);
        }
        return doPost(url, DEFAULT_CONTENT_TYPE, content, connectTimeout, readTimeout, headers);
    }

    /**
     * 执行HTTP POST请求.
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param charset        字符集，如UTF-8, GBK,
     * @param connectTimeout 连接超时时间 毫秒
     * @param readTimeout    读取时超时时间 毫秒
     * @return 响应字符串
     * @throws IOException 请求流异常
     */
    public static String doPost(String url, Map<String, String> params, Charset charset,
        int connectTimeout, int readTimeout) throws IOException {
        String contentType = "application/x-www-form-urlencoded;charset=" + charset;
        String query = buildQuery(params, charset);
        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }
        return doPost(url, contentType, content, connectTimeout, readTimeout);
    }

    /**
     * 执行HTTP POST请求.
     *
     * @param url            请求地址
     * @param contentType    请求类型
     * @param content        请求字节数组
     * @param connectTimeout 连接超时时间 毫秒
     * @param readTimeout    读取时超时时间 毫秒
     * @param headers        请求头
     * @return 响应字符串
     * @throws IOException 请求流异常
     */
    public static String doPost(String url, String contentType, byte[] content, int connectTimeout,
        int readTimeout,
        Map<String, String> headers) throws IOException {

        HttpURLConnection conn = getConnection(new URL(url), METHOD_POST, contentType, headers);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        try (OutputStream out = conn.getOutputStream()) {
            if (content != null && content.length > 0) {
                out.write(content);
            }
            return getResponseAsString(conn);
        }
    }


    public static String doGet(String url, String contentType, byte[] content, int connectTimeout,
        int readTimeout,
        Map<String, String> headers) throws IOException {

        HttpURLConnection conn = getConnection(new URL(url), METHOD_GET, contentType, headers);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        try (OutputStream out = conn.getOutputStream()) {
            if (content != null && content.length > 0) {
                out.write(content);
            }
            return getResponseAsString(conn);
        }
    }

    public static String doGet(String url, String contentType, byte[] content, int connectTimeout,
        int readTimeout) throws IOException {
        return doGet(url, contentType, content, connectTimeout, readTimeout, null);
    }

    public static String doPost(String url, String contentType, byte[] content, int connectTimeout,
        int readTimeout) throws IOException {
        return doPost(url, contentType, content, connectTimeout, readTimeout, null);
    }

    /**
     * 执行HTTP GET请求.
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws IOException 请求流异常
     */
    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, StandardCharsets.UTF_8);
    }

    /**
     * 执行Get请求.
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param charset        编码
     * @param connectTimeout 连接超时，毫秒
     * @param readTimeout    读取超时，毫秒
     * @return 请求响应结果
     * @throws IOException 请求异常
     */
    public static String doGet(String url, Map<String, String> params,
        Charset charset,
        int connectTimeout,
        int readTimeout) throws IOException {

        HttpURLConnection conn = null;
        try {
            String contentType = "application/x-www-form-urlencoded;charset=" + charset;
            String query = buildQuery(params, charset);
            conn = getConnection(buildGetUrl(url, query), METHOD_GET, contentType, null);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            return getResponseAsString(conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 执行HTTP GET请求.
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException 请求流异常
     */
    public static String doGet(String url, Map<String, String> params,
        Charset charset) throws IOException {
        return doGet(url, params, charset, 2000, 2000);
    }

    private static HttpURLConnection getConnection(URL url, String method,
        String contentType, Map<String, String> headers) throws IOException {
        HttpURLConnection conn;
        if (HTTPS.equals(url.getProtocol())) {
            HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
            connHttps.setSSLSocketFactory(sslSocketFactory);
            connHttps.setHostnameVerifier(hostnameVerifier);
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept",
            "text/xml,text/javascript,text/html,text/json,application/json");
        conn.setRequestProperty("User-Agent", "szm-sdk-java");
        conn.setRequestProperty("Content-Type", contentType);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                conn.setRequestProperty(key, value);
            }
        }
        return conn;
    }

    private static URL buildGetUrl(String strUrl, String query) throws IOException {
        URL url = new URL(strUrl);
        if (isEmpty(query)) {
            return url;
        }

        if (isEmpty(url.getQuery())) {
            if (strUrl.endsWith(QUESTION)) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + QUESTION + query;
            }
        } else {
            if (strUrl.endsWith(AND)) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + AND + query;
            }
        }

        return new URL(strUrl);
    }

    /**
     * 构建参数地址.
     *
     * @param params  请求参数
     * @param charset 编码
     * @return 携带参数的地址
     * @throws UnsupportedEncodingException 参数在处理UrlEncode时可能无法处理指定的编码
     */
    public static String buildQuery(Map<String, String> params, Charset charset)
        throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (!isAnyEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=").append(URLEncoder.encode(value, charset.name()));
            }
        }

        return query.toString();
    }

    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() != HttpStatus.OK.value()) {
            throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
        }
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (isEmpty(msg)
                || conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {
            StringWriter writer = new StringWriter();
            char[] chars = new char[256];
            int count;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            return writer.toString();
        }
    }

    private static String getResponseCharset(String contentType) {
        String charset = StandardCharsets.UTF_8.name();

        if (!isEmpty(contentType)) {
            String[] params = contentType.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2 && !isEmpty(pair[1])) {
                        charset = pair[1].trim();
                    }
                    break;
                }
            }
        }

        return charset;
    }

    /**
     * 使用默认的UTF-8字符集反编码请求参数值.
     *
     * @param value 参数值
     * @return 反编码后的参数值
     */
    public static String decode(String value) {
        return decode(value, StandardCharsets.UTF_8);
    }

    /**
     * 使用指定的字符集反编码请求参数值.
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值
     */
    public static String decode(String value, Charset charset) {
        String result = null;
        if (!isEmpty(value)) {
            try {
                result = URLDecoder.decode(value, charset.name());
            } catch (UnsupportedEncodingException e) {
                //ignore
            }
        }
        return result;
    }

    /**
     * 使用默认的UTF-8字符集编码请求参数值.
     *
     * @param value 参数值
     * @return 编码后的参数值
     */
    public static String encode(String value) {
        return encode(value, StandardCharsets.UTF_8);
    }


    /**
     * 使用指定的字符集编码请求参数值.
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值
     */
    public static String encode(String value, Charset charset) {
        String result = null;
        if (!isEmpty(value)) {
            try {
                result = URLEncoder.encode(value, charset.name());
            } catch (UnsupportedEncodingException e) {
                //ignore
            }
        }
        return result;
    }

    /**
     * 从URL中提取所有的参数.
     *
     * @param url Url地址
     * @return 参数列表
     */
    public static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> map = null;
        if (url != null && url.contains(QUESTION)) {
            map = splitUrlQuery(url.substring(url.indexOf(QUESTION) + 1));
        }
        if (map == null) {
            map = new HashMap<>(4);
        }
        return map;
    }

    /**
     * 从URL中提取所有的参数.
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<>(4);

        String[] pairs = query.split("&");
        if (pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }

    /**
     * 构建提交表单.
     *
     * @param baseUrl    提交地址
     * @param parameters 参数列表
     * @return 表单Html
     */
    @SuppressWarnings({"SpellCheckingInspection", "StringBufferReplaceableByString"})
    public static String buildForm(String baseUrl, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form name=\"punchout_form\" method=\"post\" action=\"");
        sb.append(baseUrl);
        sb.append("\">\n");
        sb.append(buildHiddenFields(parameters));

        sb.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n");
        sb.append("</form>\n");
        sb.append("<script>document.forms[0].submit();</script>");
        return sb.toString();
    }

    private static String buildHiddenFields(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 除去参数中的空值
            if (key == null || value == null) {
                continue;
            }
            sb.append(buildHiddenField(key, value));
        }
        return sb.toString();
    }

    private static String buildHiddenField(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<input type=\"hidden\" name=\"");
        sb.append(key);

        sb.append("\" value=\"");
        //转义双引号
        String a = value.replace("\"", "&quot;");
        sb.append(a).append("\">\n");
        return sb.toString();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private static boolean isAnyEmpty(String... strs) {
        for (String str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }
}
