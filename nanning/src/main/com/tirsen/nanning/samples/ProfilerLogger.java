package com.tirsen.nanning.samples;

import com.tirsen.nanning.Invocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProfilerLogger {
    private static final Log logger = LogFactory.getLog(ProfilerLogger.class);

    private static final ProfilerLogger instance = new ProfilerLogger();

    static String lastLog;

    private ProfilerLogger() {
    }

    public static ProfilerLogger getProfilerLogger() {
        return instance;
    }

    public void log(Invocation invocation, long duration) {
        StringBuffer sb = new StringBuffer();
        sb.append(invocation.getTarget().toString()).
                append(".").append(invocation.getMethod().getName()).
                append(": ").append(duration).append("ms");
        lastLog = sb.toString();
        logger.info(lastLog);
    }

}
