package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.__BeeEnvironment.Elapsed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

  public static final String METHOD_POST = "POST";
  public static final String METHOD_GET = "GET";
  public static final String CONTENT_TYPE_JSON = "application/json";
  public static final String HTTPS = "https";
  public static final String QUESTION = "?";
  public static final String AND = "&";
  public static final String EQ = "=";
  private static final Charset CHARSET = StandardCharsets.UTF_8;
  public static final String DEFAULT_CONTENT_TYPE =
      "application/x-www-form-urlencoded;charset=" + CHARSET.name();
  private static SSLContext sslContext = null;

  private static HostnameVerifier hostnameVerifier;

  private static SSLSocketFactory sslSocketFactory = null;

  static {
    try {
      sslContext = SSLContext.getInstance("TLSv1.2");
      sslContext.init(
          new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());

      sslContext.getClientSessionContext().setSessionTimeout(15);
      sslContext.getClientSessionContext().setSessionCacheSize(1000);

      sslSocketFactory = sslContext.getSocketFactory();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      // Ignore
    }

    // 默认认证不通过，进行证书校验。
    hostnameVerifier = (hostname, session) -> false;
  }

  private WebUtils() {}

  public static String formPost(
      String url,
      Map<String, String> params,
      int connectTimeout,
      int readTimeout,
      Map<String, String> headers)
      throws IOException {
    String query = buildQuery(params);
    byte[] content = {};
    if (query != null) {
      content = query.getBytes(CHARSET);
    }
    return doPost(url, DEFAULT_CONTENT_TYPE, content, connectTimeout, readTimeout, headers);
  }

  public static String jsonBodyPost(
      String url, byte[] content, int connectTimeout, int readTimeout, Map<String, String> headers)
      throws IOException {
    return doPost(url, CONTENT_TYPE_JSON, content, connectTimeout, readTimeout, headers);
  }

  public static String doPost(
      String url,
      String contentType,
      byte[] content,
      int connectTimeout,
      int readTimeout,
      Map<String, String> headers)
      throws IOException {
    Elapsed elapsed = Elapsed.get();
    elapsed.start(Elapsed.HTTP_REQUEST);
    elapsed.start(Elapsed.HTTP_CONNECTION);
    HttpURLConnection conn =
        getConnection(new URL(url), METHOD_POST, contentType, headers, connectTimeout, readTimeout);
    elapsed.end(Elapsed.HTTP_CONNECTION);

    try (OutputStream out = conn.getOutputStream()) {
      if (content != null && content.length > 0) {
        out.write(content);
      }
      elapsed.start(Elapsed.HTTP_RESPOSE);
      return getResponseAsString(conn);
    } finally {
      elapsed.end(Elapsed.HTTP_RESPOSE);
      elapsed.end(Elapsed.HTTP_REQUEST);
    }
  }

  public static String doGet(
      String url,
      Map<String, String> params,
      int connectionTimeOut,
      int readTimeOut,
      Map<String, String> headers)
      throws IOException {
    URL query = buildGetUrl(url, buildQuery(params));
    return doGet(query, DEFAULT_CONTENT_TYPE, null, connectionTimeOut, readTimeOut, headers);
  }

  public static String jsonBodyGet(
      String url, byte[] content, int connectTimeout, int readTimeout, Map<String, String> headers)
      throws IOException {
    return doGet(
        new URL(url), WebUtils.CONTENT_TYPE_JSON, content, connectTimeout, readTimeout, headers);
  }

  public static String doGet(
      URL url,
      String contentType,
      byte[] content,
      int connectTimeout,
      int readTimeout,
      Map<String, String> headers)
      throws IOException {
    Elapsed elapsed = Elapsed.get();
    elapsed.start(Elapsed.HTTP_REQUEST);
    elapsed.start(Elapsed.HTTP_CONNECTION);
    HttpURLConnection conn =
        getConnection(url, METHOD_GET, contentType, headers, connectTimeout, readTimeout);
    elapsed.end(Elapsed.HTTP_CONNECTION);

    try (OutputStream out = conn.getOutputStream()) {
      if (content != null && content.length > 0) {
        out.write(content);
      }
      elapsed.start(Elapsed.HTTP_RESPOSE);
      return getResponseAsString(conn);
    } finally {
      elapsed.end(Elapsed.HTTP_RESPOSE);
      elapsed.end(Elapsed.HTTP_REQUEST);
    }
  }

  private static HttpURLConnection getConnection(
      URL url,
      String method,
      String contentType,
      Map<String, String> headers,
      int connectTimeout,
      int readTimeout)
      throws IOException {
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
    conn.setRequestProperty(
        "Accept", "text/xml,text/javascript,text/html,text/json,application/json");
    conn.setRequestProperty("User-Agent", "bee-plugin-java");
    conn.setRequestProperty("Content-Type", contentType);
    if (headers != null) {
      for (Entry<String, String> entry : headers.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        conn.setRequestProperty(key, value);
      }
    }
    conn.setConnectTimeout(connectTimeout);
    conn.setReadTimeout(readTimeout);
    return conn;
  }

  private static URL buildGetUrl(String strUrl, String query) throws MalformedURLException {
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

  public static String buildQuery(Map<String, String> params) {
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
          query.append(AND);
        } else {
          hasParam = true;
        }
        try {
          query.append(name).append(EQ).append(URLEncoder.encode(value, CHARSET.name()));
        } catch (UnsupportedEncodingException e) {
          // Ignore
        }
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
      if (isEmpty(msg) || conn.getResponseCode() != 200) {
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
   * @param value 参数值
   * @param charset 字符集
   * @return 反编码后的参数值
   */
  public static String decode(String value, Charset charset) {
    String result = null;
    if (!isEmpty(value)) {
      try {
        result = URLDecoder.decode(value, charset.name());
      } catch (UnsupportedEncodingException e) {
        // ignore
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
   * @param value 参数值
   * @param charset 字符集
   * @return 编码后的参数值
   */
  public static String encode(String value, Charset charset) {
    String result = null;
    if (!isEmpty(value)) {
      try {
        result = URLEncoder.encode(value, charset.name());
      } catch (UnsupportedEncodingException e) {
        // ignore
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
    // 转义双引号
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

  private static class DefaultTrustManager implements X509TrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
      // ignore
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
      // ignore
    }
  }
}
