/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Pattern Low Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PatternLowFreqIndicatorImpl extends LowFrequencyIndicatorImpl implements PatternLowFreqIndicator {

    private String charsToReplace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    // Mark replace parameter whether has been setting by the user.
    private boolean hasBeanCustomized = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected PatternLowFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.PATTERN_LOW_FREQ_INDICATOR;
    }

    @Override
    public boolean prepare() {
        // MOD xqliu 2009-06-29 bug 7068
        final TextParameters textParameter = this.getParameters() == null ? null : this.getParameters().getTextParameter();
        // ~
        if (textParameter != null) {

            // TDQ-10044: fix when the user didn't set the replace and charactersToReplace, use the default value(only
            // for jave engine)
            String replacementCharacters = textParameter.getReplacementCharacters();
            if (!StringUtils.isBlank(replacementCharacters)) {
                this.replacementChars = replacementCharacters;
                hasBeanCustomized = true;
            } else {
                replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";
                hasBeanCustomized = false;
            }

            String charactersToReplace = textParameter.getCharactersToReplace();
            if (!StringUtils.isBlank(charactersToReplace)) {
                this.charsToReplace = charactersToReplace;
            } else {
                charsToReplace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            }
            // TDQ-10044~

        }
        return super.prepare();
    }

    @Override
    public boolean handle(Object data) {
        if (data == null) {
            return super.handle(data);
        } else {
            String parsedData = convertCharacters(String.valueOf(data));
            return super.handle(parsedData);
        }
    }

    /**
     * add by zshen for feature 12919 to convertCharacter when save data into analysisDataSet with java engin
     */
    @Override
    public String convertCharacters(String data) {
        return replaceCharacters(String.valueOf(data), this.charsToReplace, this.replacementChars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getFrequencyLabel(java.lang.Object)
     */
    @Override
    protected String getFrequencyLabel(Object name) {
        return convertCharacters(name.toString());
    }

    /**
     * Getter for hasBeanCustomized.
     * 
     * @return the hasBeanCustomized
     */
    public boolean isBeanCustomized() {
        return hasBeanCustomized;
    }

} // PatternLowFreqIndicatorImpl
