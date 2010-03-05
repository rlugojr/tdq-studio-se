// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis.explore;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * @author scorreia
 * 
 * This class explores the data that matched or did not match a pattern indicator.
 */
public class PatternExplorer extends DataExplorer {

    /**
     * DOC scorreia PatternExplorer constructor comment.
     * 
     * @param dbmsLang
     */
    public PatternExplorer() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getInvalidRowsStatement()
     */
    public String getInvalidRowsStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexNotLike(columnName, regexPatternString);
        // add null as invalid rows
        String nullClause = dbmsLanguage.or() + columnName + dbmsLanguage.isNull();
        return getRowsStatement(regexCmp + nullClause);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getValidRowsStatement()
     */
    public String getValidRowsStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        final String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexLike(columnName, regexPatternString);
        return getRowsStatement(regexCmp);
    }

    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        // MOD zshen 10448 Add menus "view invalid values" and "view valid values" on pattern matching indicator
        map.put("view invalid values", getInvalidValuesStatement()); //$NON-NLS-1$
        map.put("view valid values", getValidValuesStatement()); //$NON-NLS-1$
        // ~10448
        map.put("view invalid rows", getInvalidRowsStatement()); //$NON-NLS-1$
        map.put("view valid rows", getValidRowsStatement()); //$NON-NLS-1$

        return map;
    }

    /**
     * 
     * DOC zshen Comment method "getValidValuesStatement".
     * 
     * @return SELECT statement for the invalid Value of select column
     */
    public String getInvalidValuesStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexNotLike(columnName, regexPatternString);
        // add null as invalid rows
        String nullClause = dbmsLanguage.or() + columnName + dbmsLanguage.isNull();
        return getValuesStatement(columnName, regexCmp + nullClause);
    }

    /**
     * 
     * DOC zshen Comment method "getValidValuesStatement".
     * 
     * @return SELECT statement for the valid Value of select column
     */
    public String getValidValuesStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        final String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexLike(columnName, regexPatternString);
        return getValuesStatement(columnName, regexCmp);
    }

}
