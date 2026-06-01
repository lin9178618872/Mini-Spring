package com.interview.minispring.beans.factory.xml;

import com.interview.minispring.beans.factory.config.BeanDefinition;
import com.interview.minispring.beans.factory.config.BeanReference;
import com.interview.minispring.beans.factory.support.DefaultBeanFactory;
import com.interview.minispring.core.io.ResourceLoader;
import com.interview.minispring.core.TinySpringException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlBeanDefinitionReader {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

    private final DefaultBeanFactory beanFactory;
    private final ResourceLoader resourceLoader = new ResourceLoader();
    private final Properties properties = new Properties();

    public XmlBeanDefinitionReader(DefaultBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadBeanDefinitions(String location) {
        try (InputStream stream = resourceLoader.open(location)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().parse(stream);
            Element root = document.getDocumentElement();
            loadExternalProperties(root);
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element element && "bean".equals(element.getTagName())) {
                    parseBean(element);
                }
            }
        } catch (Exception ex) {
            throw new TinySpringException("Could not load bean definitions from " + location, ex);
        }
    }

    private void loadExternalProperties(Element root) {
        String propertyLocation = root.getAttribute("properties");
        if (propertyLocation == null || propertyLocation.isBlank()) {
            return;
        }
        try (InputStream stream = resourceLoader.open(propertyLocation)) {
            properties.load(stream);
        } catch (Exception ex) {
            throw new TinySpringException("Could not load properties: " + propertyLocation, ex);
        }
    }

    private void parseBean(Element element) throws ClassNotFoundException {
        String className = element.getAttribute("class");
        Class<?> beanClass = Class.forName(className);
        String beanName = firstNonBlank(element.getAttribute("id"), element.getAttribute("name"), decapitalize(beanClass.getSimpleName()));
        BeanDefinition definition = new BeanDefinition(beanClass);
        definition.setScope(element.getAttribute("scope"));
        definition.setLazyInit(Boolean.parseBoolean(element.getAttribute("lazy")));
        definition.setInitMethod(blankToNull(element.getAttribute("init-method")));
        definition.setDestroyMethod(blankToNull(element.getAttribute("destroy-method")));

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element property && "property".equals(property.getTagName())) {
                parseProperty(definition, property);
            }
        }
        beanFactory.registerBeanDefinition(beanName, definition);
    }

    private void parseProperty(BeanDefinition definition, Element property) {
        String name = property.getAttribute("name");
        String ref = property.getAttribute("ref");
        String value = property.getAttribute("value");
        if (!ref.isBlank()) {
            definition.addPropertyValue(name, new BeanReference(ref));
        } else {
            definition.addPropertyValue(name, resolvePlaceholders(value));
        }
    }

    private String resolvePlaceholders(String text) {
        Matcher matcher = PLACEHOLDER.matcher(text);
        StringBuffer resolved = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = properties.getProperty(key, System.getProperty(key, ""));
            matcher.appendReplacement(resolved, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(resolved);
        return resolved.toString();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        throw new TinySpringException("No usable bean name");
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String decapitalize(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}
