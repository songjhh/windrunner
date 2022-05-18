package top.songjhh.windrunner.core.util;

import ognl.Ognl;
import ognl.OgnlException;
import top.songjhh.windrunner.core.exception.FlowExpressionException;

import java.util.Map;

/**
 * @author songjhh
 */
public class ConditionExpressionUtils {

    private ConditionExpressionUtils() {
    }

    public static boolean validCondition(String conditionExpression, Map<String, Object> variables) {
        if (StringUtils.isNotEmpty(conditionExpression)) {
            try {
                Object expression = Ognl.parseExpression(conditionExpression);
                Object expressionValue = Ognl.getValue(expression, variables);
                return Boolean.TRUE.equals(expressionValue);
            } catch (OgnlException e) {
                throw new FlowExpressionException(e);
            }
        }
        return true;
    }
}
