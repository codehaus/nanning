/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.jelly;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import com.tirsen.nanning.AspectRepository;

/**
 * TODO document AspectRepositoryTag
 *
 * <!-- $Id: AspectRepositoryTag.java,v 1.1 2002-11-03 19:05:33 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectRepositoryTag extends TagSupport
{
    private String id = "default";
    private AspectRepository aspectRepository;

    public void setId(String id)
    {
        this.id = id;
    }

    public AspectRepository getAspectRepository()
    {
        return aspectRepository;
    }

    public void doTag(XMLOutput xmlOutput) throws Exception
    {
        aspectRepository = new AspectRepository();
        invokeBody(xmlOutput);
        getContext().setVariable(id, "parent", aspectRepository);
    }
}
