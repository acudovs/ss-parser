package ss.parser.ad;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.net.URL;
import java.time.Duration;
import java.util.regex.Matcher;

public interface AdConfig {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Duration getRate();

    void setRate(Duration rate);

    URL getUrl();

    Duration getTimeout();

    String getRegex();

    String getReplace();

    Expression getExpression();

    void setExpression(Expression expression);

    Ad newAd(Element element, Matcher matcher);

    @Component
    @ConfigurationPropertiesBinding
    class ExpressionConverter implements Converter<Object, Expression> {
        @Override
        public Expression convert(Object source) {
            try {
                Expression expr = new SpelExpressionParser().parseExpression(source.toString());
                validateNode(((SpelExpression) expr).getAST());
                return expr;
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }

        private static void validateNode(SpelNode node) {
            if (node instanceof TypeReference || node instanceof BeanReference || node instanceof ConstructorReference) {
                throw new IllegalArgumentException("Expression uses unsupported construct: " + node.toStringAST());
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                validateNode(node.getChild(i));
            }
        }
    }
}
