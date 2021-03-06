/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.cdise.weld;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

/**
 * Weld specific implementation of {@link org.apache.deltaspike.cdise.api.CdiContainer}.
 */
@SuppressWarnings("UnusedDeclaration")
public class WeldContainerControl implements CdiContainer
{
    private Weld weld;
    private WeldContainer weldContainer;

    private Bean<ContextControl> ctxCtrlBean = null;
    private CreationalContext<ContextControl> ctxCtrlCreationalContext = null;
    private ContextControl ctxCtrl = null;


    @Override
    public BeanManager getBeanManager()
    {
        if (weldContainer == null)
        {
            return null;
        }

        return weldContainer.getBeanManager();
    }


    @Override
    public synchronized void boot()
    {
        weld = new Weld();
        weldContainer = weld.initialize();
    }

    @Override
    public synchronized  void shutdown()
    {
        if (ctxCtrl != null)
        {
            ctxCtrlBean.destroy(ctxCtrl, ctxCtrlCreationalContext);
        }

        weld.shutdown();
        weld = null;
        ctxCtrl = null;
        ctxCtrlBean = null;
        ctxCtrlCreationalContext = null;

    }

    @Override
    public synchronized ContextControl getContextControl()
    {
        if (ctxCtrl == null)
        {
            Set<Bean<?>> beans = getBeanManager().getBeans(ContextControl.class);
            ctxCtrlBean = (Bean<ContextControl>) getBeanManager().resolve(beans);

            ctxCtrlCreationalContext = getBeanManager().createCreationalContext(ctxCtrlBean);

            ctxCtrl = (ContextControl)
                    getBeanManager().getReference(ctxCtrlBean, ContextControl.class, ctxCtrlCreationalContext);
        }
        return ctxCtrl;
    }
}
