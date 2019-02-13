package com.example.generator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: zyw
 * @Date: 2019/2/12
 */
public class LogUtil {
    private static final Map<String, List<String>> logMap = new ConcurrentHashMap();

    public static void push(String logTag, String message)
    {
        if (logMap.containsKey(logTag)) {
            synchronized ((List)logMap.get(logTag))
            {
                ((List)logMap.get(logTag)).add("[基础模板生成] ".concat(message));
            }
        } else {
            synchronized (logMap)
            {
                List<String> logList = new ArrayList();
                ((List)logList).add("[基础模板生成] ".concat(message));
                logMap.put(logTag, logList);
            }
        }
    }

    public static List<String> pop(String logTag)
    {
        List<String> result = new ArrayList();
        if ((null != logMap.get(logTag)) && (!((List)logMap.get(logTag)).isEmpty())) {
            synchronized ((List)logMap.get(logTag))
            {
                result.addAll((Collection)logMap.get(logTag));
                ((List)logMap.get(logTag)).clear();
            }
        }
        return result;
    }
}
