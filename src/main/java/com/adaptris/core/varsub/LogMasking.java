package com.adaptris.core.varsub;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class LogMasking {

    public static final String LOG_MASKING_CONFIG_KEY = Constants.VARSUB_LOG_MASKED_VARIABLES_KEY;
    public static final String DEFAULT_LOG_MASK = "***";
    public static final String DEFAULT_LOG_MASKING_CONFIG_DELIMITER = ",";

    public static List<String> getLogMaskingConfigKeys(Properties cfg) {
        return getLogMaskingConfigKeys(cfg, DEFAULT_LOG_MASKING_CONFIG_DELIMITER);

    }
    public static List<String> getLogMaskingConfigKeys(Properties cfg, String delimiter) {
        String untokenizedKeys = cfg.getProperty(LOG_MASKING_CONFIG_KEY);
        if (untokenizedKeys != null) {
            return Arrays.asList(untokenizedKeys.split(delimiter));
        }
        return Collections.emptyList();
    }

    public static String getLogMaskedValue(List<String> logMaskedKeys, String key, String value) {
        return getLogMaskedValue(logMaskedKeys, key, value,  DEFAULT_LOG_MASK);
    }

    public static String getLogMaskedValue(List<String> logMaskedKeys, String key, String value, String mask) {
        if (logMaskedKeys != null && logMaskedKeys.contains(key)) {
            return mask;
        } else return value;
    }
}
