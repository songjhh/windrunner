package top.songjhh.windrunner.core.ognl;

import ognl.Ognl;
import ognl.OgnlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.task.model.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * @author songjhh
 */
class OgnlTest {

    // @Test
    // void test() throws OgnlException {
    //     OgnlUser user = new OgnlUser("songjhh");
    //     Object name = Ognl.getValue(Ognl.parseExpression("name"), user);
    //     Assertions.assertEquals("songjhh", name);
    //
    //     Map<String, Object> map = new HashMap<>();
    //     map.put("age", 20);
    //     map.put("introduction", "My name is ");
    //
    //     Object age = Ognl.getValue(Ognl.parseExpression("age > 25"), map);
    //     System.out.println(age);
    //
    //     Object contextValue = Ognl.getValue(Ognl.parseExpression("#introduction"), map, user);
    //     System.out.println(contextValue);
    //
    //     Object hello = Ognl.getValue(Ognl.parseExpression("#introduction + name"), map, user);
    //     System.out.println(hello);
    // }
    //
    //
    // record OgnlUser(String name) {
    // }


}
