package com.redpigmall.api.sec;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

/**
 * 存储在线用户名 
 * 这里需要是单例的
 * @author redpig
 */
@Component
public class SessionRegistry  {
	
    protected final Log logger = LogFactory.getLog(SessionRegistry.class);
    //在线用户名集合
    private final List<String> principals = Lists.newArrayList();
    
    public List<String> getAllPrincipals() {
        return principals;
    }
    
    public void removeSessionRegistry(String userName){
    	principals.remove(userName);
    }
    
    public void registerNewSessionRegistry(String userName) {
    	
    	principals.add(userName);
    	
    }
}
