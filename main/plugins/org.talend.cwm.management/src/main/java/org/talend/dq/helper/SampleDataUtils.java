// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.talend.dataprofiler.service.IAnalysisEditorService;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.sample.data.SampleDataStatement;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SampleDataUtils extends AbstractOSGIServiceUtils implements IAnalysisEditorService {

    private IAnalysisEditorService analysisSampleService;

    private static SampleDataUtils instance;

    private SampleDataUtils() {

    }

    /**
     * Getter for instance.
     * 
     * @return the instance
     */
    public static SampleDataUtils getInstance() {
        if (instance == null) {
            instance = new SampleDataUtils();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.AbstractOSGIServiceUtils#getPluginName()
     */
    @Override
    public String getPluginName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.AbstractOSGIServiceUtils#getServiceName()
     */
    @Override
    public String getServiceName() {
        return IAnalysisEditorService.class.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.AbstractOSGIServiceUtils#isServiceInstalled()
     */
    @Override
    public boolean isServiceInstalled() {
        initService(true);
        return this.analysisSampleService != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.AbstractOSGIServiceUtils#getMissingMessageName()
     */
    @Override
    protected String getMissingMessageName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.AbstractOSGIServiceUtils#getRestartMessageName()
     */
    @Override
    protected String getRestartMessageName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.AbstractOSGIServiceUtils#setService(org.osgi.framework.BundleContext,
     * org.osgi.framework.ServiceReference)
     */
    @Override
    protected void setService(BundleContext context, ServiceReference serviceReference) {
        if (serviceReference != null) {
            Object obj = context.getService(serviceReference);
            if (obj != null) {
                this.analysisSampleService = (IAnalysisEditorService) obj;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.service.IAnalysisEditorService#getSampleData()
     */
    public SampleDataStatement getSampleDataStatement(Analysis findAnalysis) {
        if (isServiceInstalled()) {
            return analysisSampleService.getSampleDataStatement(findAnalysis);
        }
        return null;
    }

}
