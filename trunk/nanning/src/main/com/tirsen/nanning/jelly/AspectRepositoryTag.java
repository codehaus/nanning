/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import com.tirsen.nanning.definition.AspectRepository;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * TODO document AspectRepositoryTag
 *
 * <!-- $Id: AspectRepositoryTag.java,v 1.5 2003-03-21 17:11:11 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.5 $
 */
public class AspectRepositoryTag extends TagSupport {
    private String id = "default";
    private AspectRepository aspectRepository;

    public void setId(String id) {
        this.id = id;
    }

    public AspectRepository getAspectRepository() {
        return aspectRepository;
    }

    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        aspectRepository = new AspectRepository();
        invokeBody(xmlOutput);
        getContext().setVariable(id, "parent", aspectRepository);
    }
}
