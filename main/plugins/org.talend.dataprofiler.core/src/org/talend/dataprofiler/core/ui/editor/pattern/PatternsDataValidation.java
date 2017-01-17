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
package org.talend.dataprofiler.core.ui.editor.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.dataprofiler.core.ui.utils.FixOrderList;
import org.talend.dataprofiler.core.ui.wizard.patterns.DataFilterType;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.validation.DataValidationImpl;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class PatternsDataValidation extends DataValidationImpl {

    private List<Map<Integer, RegexpMatchingIndicator>> tableFilterResult = null;

    private DataFilterType filterType = null;

    private FixOrderList orderResult = new FixOrderList();

    /**
     * DOC zshen PatternsDataValidation constructor comment.
     * 
     * @param tableFilterResult
     */
    public PatternsDataValidation(List<Map<Integer, RegexpMatchingIndicator>> tableFilterResult, DataFilterType filterType) {
        this.tableFilterResult = tableFilterResult;
        this.filterType = filterType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.validation.DataValidationImpl#initParameter()
     */
    @Override
    protected void initParameter() {
        if (resultList == null) {
            orderResult = new FixOrderList();
            resultList = orderResult;
        } else {
            orderResult.reset();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.indicator.DataValidation#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(Object inputData) {
        if (tableFilterResult == null) {
            return true;
        }
        for (Map<Integer, RegexpMatchingIndicator> tableItem : tableFilterResult) {
            if (inputData instanceof ArrayList) {
                for (int index = 0; index < ((ArrayList<?>) inputData).size(); index++) {
                    RegexpMatchingIndicator regMatIndicator = tableItem.get(index);
                    if (regMatIndicator == null) {
                        continue;
                    }
                    String regex = regMatIndicator.getRegex();
                    Pattern p = java.util.regex.Pattern.compile(regex);

                    Object theElement = ((ArrayList<?>) inputData).get(index);
                    if (theElement == null) {
                        theElement = "null";//$NON-NLS-1$
                    }
                    Matcher m = p.matcher(String.valueOf(theElement));
                    if (!m.find()) {
                        if (DataFilterType.MATCHES.equals(filterType)) {
                            return false;
                        } else if (DataFilterType.non_matches.equals(filterType)) {
                            return true;
                        }
                    }
                }
            }
        }
        return DataFilterType.MATCHES.equals(filterType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.indicator.DataValidation#isCheckKey()
     */
    @Override
    public boolean isCheckKey() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.validation.DataValidationImpl#add(java.lang.Object[])
     */
    @Override
    public boolean add(Object[] element) {
        return orderResult.add(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.validation.DataValidationImpl#isWork()
     */
    @Override
    public boolean isWork() {
        return orderResult.isWork();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.validation.DataValidationImpl#getResult()
     */
    @Override
    public List<Object[]> getResult() {
        return orderResult;
    }

}
