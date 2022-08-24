package top.songjhh.windrunner.core.ognl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ognl.Ognl;
import ognl.OgnlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @author songjhh
 */
class OgnlTest {

    @Test
    void test() throws OgnlException {
        OgnlUser user = OgnlUser.builder().name("songjhh").build();
        Object name = Ognl.getValue(Ognl.parseExpression("name"), user);
        Assertions.assertEquals("songjhh", name);

        Map<String, Object> map = new HashMap<>();
        map.put("age", 20);
        map.put("introduction", "My name is ");

        Object age = Ognl.getValue(Ognl.parseExpression("age > 25"), map);
        System.out.println(age);

        Object contextValue = Ognl.getValue(Ognl.parseExpression("#introduction"), map, user);
        System.out.println(contextValue);

        Object hello = Ognl.getValue(Ognl.parseExpression("#introduction + name"), map, user);
        System.out.println(hello);
    }

    @Test
    void test2() throws OgnlException {
        Map<String, Object> variables = new HashMap<>();
        Map<String, List<LabelAndId>> bookVariable = new HashMap<>();
        bookVariable.put("read", Arrays.asList(
                LabelAndId.builder().id("a").build(),
                LabelAndId.builder().id("b").build()));
        bookVariable.put("want", Arrays.asList(
                LabelAndId.builder().id("c").build(),
                LabelAndId.builder().id("d").build()));
        variables.put("book", bookVariable);
        String expression = "#value=book.read.{id}, #value.addAll(book.want.{id}), #value";

        Object result = Ognl.getValue(Ognl.parseExpression(expression), variables);
        System.out.println(result);
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    static class OgnlUser {
        private String name;
        @Builder.Default
        private List<String> books = new ArrayList<>();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    static class LabelAndId {
        private String id;
        private String label;
    }

}
