package com.zznode.opentnms.isearch.otnRouteService.util;

import java.util.UUID;


public class UuidUtil {
    /**
     * 获取Uuid 字符串
     * @return
     */
    public static String randomUUID(){
        UUID uuid = UUID.randomUUID();
        if(uuid != null){
            return uuid.toString();
        }
        return new com.eaio.uuid.UUID().toString();
    }
}
