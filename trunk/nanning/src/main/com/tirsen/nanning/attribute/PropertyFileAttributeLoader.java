package com.tirsen.nanning.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyFileAttributeLoader implements AttributesLoader {
    private static Map propertiesCache = new HashMap();

    public void load(ClassAttributes classAttributes) {
        ClassPropertiesHelper classPropertiesHelper = new ClassPropertiesHelper(classAttributes);
        classPropertiesHelper.loadAttributes(getProperties(classAttributes.getAttributeClass()));
    }

    Properties getProperties(Class klass) {
        Properties properties = (Properties) propertiesCache.get(klass);
        if (properties == null) {
            InputStream inputStream = null;
            try {
                properties = (Properties) propertiesCache.get(klass);
                if (properties == null) {
                    properties = new Properties();
                    propertiesCache.put(klass, properties);
                }
                assert properties != null : "no properties created for " + klass;

                String className = klass.getName();

                // load the JavaDoc-tags
                inputStream = Attributes.findFile(klass, ClassPropertiesHelper.className(klass) + ".attributes");
                if (inputStream == null) {
                    inputStream = Attributes.findFile(klass, className.replace('.', '/') + ".attributes");
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
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return properties;
    }

}
