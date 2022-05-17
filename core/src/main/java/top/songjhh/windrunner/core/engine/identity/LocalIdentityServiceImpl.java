package top.songjhh.windrunner.core.engine.identity;

import java.util.Collections;
import java.util.List;

/**
 * @author songjhh
 */
public class LocalIdentityServiceImpl implements IdentityService {
    @Override
    public String getNameByUserId(String userId) {
        return userId;
    }

    @Override
    public List<String> listPlatformsByUser(String user) {
        return Collections.singletonList("测试单位");
    }
}
