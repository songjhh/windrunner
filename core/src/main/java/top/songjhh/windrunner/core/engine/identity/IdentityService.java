package top.songjhh.windrunner.core.engine.identity;

import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;

import java.util.List;

/**
 * @author songjhh
 */
public interface IdentityService {

    String getNameByUserId(String userId);

    List<String> listPlatformsByUser(String user);

    default UserEntity getEntityByUserId(String userId) {
        return UserEntity.builder().id(userId).name(getNameByUserId(userId)).platforms(listPlatformsByUser(userId)).build();
    }

}
