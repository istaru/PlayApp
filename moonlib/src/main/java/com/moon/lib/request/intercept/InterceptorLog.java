package com.moon.lib.request.intercept;

import com.moon.lib.tools.LogUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class InterceptorLog implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * 日志请求和响应基本信息
         * <p>
         * 请求信息[方法 URL 协议]
         * <p>
         * 响应信息[code message tookTime responseBody]
         */
        BASIC,
        /**
         * 包括所有信息{@link #BASIC}
         * <p>日志请求头和响应头
         */
        HEADERS
    }

    private final Level mLevel;

    public InterceptorLog() {
        this(Level.BASIC);
    }

    /**
     * @param level {@link Level}
     */
    public InterceptorLog(Level level) {
        mLevel = level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        StringBuilder requestBuilder = new StringBuilder();

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        requestBuilder.append(request.method())
                .append(" ")
                .append(request.url())
                .append(" ")
                .append(protocol)
                .append("\n");

        if (mLevel == Level.HEADERS) {
            Headers requestHeaders = request.headers();
            for (int i = 0, count = requestHeaders.size(); i < count; i++) {
                String name = requestHeaders.name(i);
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    requestBuilder.append(name)
                            .append(": ")
                            .append(requestHeaders.value(i))
                            .append("\n");
                }
            }
        }

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            if (requestBody.contentType() != null) {
                requestBuilder.append("Content-Type: ")
                        .append(requestBody.contentType())
                        .append("\n");
            }
            if (requestBody.contentLength() != -1) {
                requestBuilder.append("Content-Length: ")
                        .append(requestBody.contentLength())
                        .append("\n");
            }
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            requestBuilder.append("Request-Body: ")
                    .append(buffer.readUtf8())
                    .append("\n");
        }

        LogUtils.t(LogUtils.TAG_OKHTTP).d(requestBuilder.toString());

        StringBuilder responseBuilder = new StringBuilder();
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            LogUtils.t(LogUtils.TAG_OKHTTP).e(e, "HTTP FAILED");
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        responseBuilder.append(response.code())
                .append(" ")
                .append(response.message())
                .append(" ")
                .append(response.request().url())
                .append(" (took: ")
                .append(tookMs)
                .append("ms")
                .append(")\n");

        if (mLevel == Level.HEADERS) {
            Headers responseHeaders = response.headers();
            for (int i = 0, count = responseHeaders.size(); i < count; i++) {
                responseBuilder.append(responseHeaders.name(i))
                        .append(": ")
                        .append(responseHeaders.value(i))
                        .append("\n");
            }
        }

        if (bodyEncoded(response.headers())) {
            responseBuilder.append("END HTTP (encoded body omitted)");
            LogUtils.t(LogUtils.TAG_OKHTTP).d(responseBuilder.toString());
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    responseBuilder.append("Couldn't decode the response body; charset is likely malformed.")
                            .append("END HTTP");
                    LogUtils.t(LogUtils.TAG_OKHTTP).d(responseBuilder.toString());
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                responseBuilder.append("END HTTP (binary ")
                        .append(buffer.size())
                        .append("-byte body omitted)");
                LogUtils.t(LogUtils.TAG_OKHTTP).d(responseBuilder.toString());
                return response;
            }

            responseBuilder.append("END HTTP (")
                    .append(buffer.size())
                    .append("-byte body)");

            LogUtils.t(LogUtils.TAG_OKHTTP).d(responseBuilder.toString());
            if (contentLength != 0) {
                LogUtils.t(LogUtils.TAG_OKHTTP).json(buffer.clone().readString(charset));
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}
