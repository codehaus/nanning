package org.codehaus.nanning.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import org.codehaus.nanning.util.WrappedException;

public class PropertyFileAttributeLoader implements AttributesLoader {
    public static final String ATTRIBUTE_FILE_SUFFIX = ".attributes";

    public void load(ClassAttributes classAttributes) {
        ClassPropertiesHelper classPropertiesHelper = new ClassPropertiesHelper(classAttributes);
        classPropertiesHelper.loadAttributes(getProperties(classAttributes.getAttributeClass()));
    }

    Properties getProperties(Class klass) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            String className = klass.getName();

            // load the JavaDoc-tags
            inputStream = Attributes.findFile(klass, ClassPropertiesHelper.className(klass) + ATTRIBUTE_FILE_SUFFIX);
            if (inputStream == null) {
                inputStream = Attributes.findFile(klass, className.replace('.', '/') + ATTRIBUTE_FILE_SUFFIX);
            }

            if (inputStream != null) {
                properties.load(inputStream);
            }

        } catch (MalformedURLException e) {
            throw new AttributeException("Error fetching properties for " + klass, e);
        } catch (IOException e) {
            throw new AttributeException("Error fetching properties for " + klass, e);
        } catch (AttributeException e) {
            throw e;
        } catch (Exception e) {
            throw new AttributeException("Error fetching properties for " + klass, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new WrappedException(e);
                }
            }
        }
        return properties;
    }

}
