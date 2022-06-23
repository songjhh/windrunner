package top.songjhh.windrunner.core.engine.identity;

import java.util.ArrayList;
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
        return new ArrayList<>();
    }

    @Override
    public List<String> getDepartmentByUserId(String userId) {
        return new ArrayList<>();
    }
}
