package top.songjhh.windrunner.core.engine.identity;

import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;

import java.util.List;

/**
 * @author songjhh
 */
public interface IdentityService {

    /**
     * 通过用户 id 获取姓名
     *
     * @param userId 用户 id
     * @return 姓名
     */
    String getNameByUserId(String userId);

    /**
     * 查询用户所属的平台列表
     *
     * @param userId 用户 id
     * @return 平台列表
     */
    List<String> listPlatformsByUserId(String userId);

    /**
     * 查询用户所属的组织列表
     *
     * @param userId 用户 id
     * @return 平台列表
     */
    List<String> getDepartmentByUserId(String userId);

    /**
     * 获取 User Entity
     *
     * @param userId 用户 id
     * @return User Entity
     */
    default UserEntity getEntityByUserId(String userId) {
        return UserEntity.builder().id(userId).name(getNameByUserId(userId))
                .platforms(listPlatformsByUserId(userId)).departments(getDepartmentByUserId(userId)).build();
    }

}
