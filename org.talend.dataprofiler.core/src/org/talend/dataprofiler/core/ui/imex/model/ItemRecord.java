// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.emf.FactoriesUtil.EElementEName;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC bZhou class global comment. Detailled comment
 */
/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ItemRecord {

    private static Logger log = Logger.getLogger(ItemRecord.class);

    private static final String JARFILEEXTENSION = "jar";//$NON-NLS-1$

    private File file;

    private ModelElement element;

    private Property property;

    private IRepositoryNode conflictNode;

    private Map<File, ModelElement> dependencyMap = new HashMap<File, ModelElement>();

    private List<String> errors = new ArrayList<String>();

    private ItemRecord parent;

    private ItemRecord[] childern;

    private EElementEName elementEName;

    private static ResourceSet resourceSet;

    private static List<ItemRecord> allItemRecords;

    public ItemRecord(File file) {
        this.file = file;

        if (resourceSet == null) {
            resourceSet = new ResourceSetImpl();
        }

        if (allItemRecords == null) {
            allItemRecords = new ArrayList<ItemRecord>();
        }

        if (file != null && file.isFile()) {
            allItemRecords.add(this);
            initialize();
        }
    }

    /**
     * DOC bZhou Comment method "initialize".
     */
    private void initialize() {
        URI itemURI = URI.createFileURI(file.getAbsolutePath());
        URI propURI = itemURI.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        if (ItemRecord.JARFILEEXTENSION.equals(itemURI.fileExtension())) {
            return;
        }
        elementEName = EElementEName.findENameByExt(itemURI.fileExtension());

        try {
            if (property == null) {
                Resource resource = resourceSet.getResource(propURI, true);
                property = (Property) EcoreUtil
                        .getObjectByType(resource.getContents(), PropertiesPackage.eINSTANCE.getProperty());
            }

            if (element == null && !isJRXml()) {
                Resource resource = resourceSet.getResource(itemURI, true);
                EList<EObject> contents = resource.getContents();
                if (contents != null && !contents.isEmpty()) {
                    if (property.getItem() instanceof ConnectionItem) {
                        element = ((ConnectionItem) property.getItem()).getConnection();
                    } else {
                        EObject object = contents.get(0);
                        element = (ModelElement) object;
                    }
                }
            }
            computeDependencies();
        } catch (Exception e) {
            if (!isSQL()) {
                String errorMessage = "Can't initialize element [" + getName() + "] : " + e.getMessage();//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                log.error(errorMessage);
                this.errors.add(errorMessage);
            }
        }
    }

    /**
     * DOC bZhou Comment method "getElement".
     * 
     * @return
     */
    public ModelElement getElement() {
        return element;
    }

    /**
     * DOC bZhou Comment method "getFilePath".
     * 
     * @return
     */
    public IPath getFilePath() {
        return new Path(file.getAbsolutePath());
    }

    /**
     * DOC bZhou Comment method "getPropertyPath".
     * 
     * @return
     */
    public IPath getPropertyPath() {
        if (file != null) {
            IPath itemResPath = new Path(file.getAbsolutePath());
            return itemResPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        }
        return null;
    }

    /**
     * DOC bZhou Comment method "getFullPath".
     * 
     * @return
     */
    public IPath getFullPath() {
        IPath path = new Path(file.getAbsolutePath());
        path = path.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation());
        return path;
    }

    /**
     * Getter for dependencyMap.
     * 
     * @return the dependencyMap
     */
    public Map<File, ModelElement> getDependencyMap() {
        return this.dependencyMap;
    }

    /**
     * DOC bZhou Comment method "computeDependencies".
     */
    private void computeDependencies() {

        if (isJRXml()) {
            Collection<TdReport> allReports = (Collection<TdReport>) RepResourceFileHelper.getInstance().getAllElement();
            for (TdReport report : allReports) {
                for (AnalysisMap anaMap : report.getAnalysisMap()) {
                    if (StringUtils.equals(file.getAbsolutePath(), anaMap.getJrxmlSource())) {
                        dependencyMap.put(file, report);
                    }
                }
            }
        } else if (element != null) {
            List<ModelElement> dependencyElements = new ArrayList<ModelElement>();

            ModelElementHelper.iterateClientDependencies(element, dependencyElements);
            // MOD by zshen for bug 18724 2011.02.23
            TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.JAR_FILE_PATH, element.getTaggedValue());
            if (tv != null) {
                for (IFile udiJarFile : UDIUtils.getLibJarFileList()) {
                    if (Arrays.binarySearch(tv.getValue().split("\\|\\|"), udiJarFile.getName()) >= 0) {//$NON-NLS-1$
                        dependencyMap.put(udiJarFile.getLocation().toFile(), null);
                    }
                }
            }
            for (ModelElement dElement : dependencyElements) {

                EcoreUtil.resolveAll(dElement);

                if (!dElement.eIsProxy()) {
                    URI dURI = dElement.eResource().getURI();
                    Resource dResource = resourceSet.getResource(dURI, false);

                    if (dResource != null) {
                        File depFile = new File(dResource.getURI().toFileString());
                        dependencyMap.put(depFile, dElement);
                    }
                }
            }
        }
    }

    /**
     * clear the resource set.
     */
    public static void clear() {

        if (resourceSet != null) {
            for (Resource resource : resourceSet.getResources()) {
                resource.unload();
            }
            resourceSet.getResources().clear();
            resourceSet = null;
        }

        if (allItemRecords != null) {
            allItemRecords.clear();
            allItemRecords = null;
        }
    }

    /**
     * DOC bZhou Comment method "addError".
     * 
     * @param error
     */
    public void addError(String error) {
        if (elementEName != null) {
            error = "[" + elementEName.name() + "]" + error;//$NON-NLS-1$ //$NON-NLS-2$
        }
        this.errors.add(error);
    }

    /**
     * Getter for file.
     * 
     * @return the file
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Getter for errors.
     * 
     * @return the errors
     */
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * DOC bZhou Comment method "isValid".
     * 
     * @return
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Getter for property.
     * 
     * @return the property
     */
    public Property getProperty() {
        return this.property;
    }

    /**
     * Getter for resourceSet.
     * 
     * @return the resourceSet
     */
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    /**
     * Getter for parent.
     * 
     * @return the parent
     */
    public ItemRecord getParent() {
        if (parent == null && file != null) {
            parent = new ItemRecord(file.getParentFile());
        }

        return this.parent;
    }

    /**
     * Getter for childern.
     * 
     * @return the childern
     */
    public ItemRecord[] getChildern() {
        if (childern == null) {
            List<ItemRecord> recordList = new ArrayList<ItemRecord>();

            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File aFile : listFiles) {
                    if (isValid(aFile)) {
                        ItemRecord itemRecord = new ItemRecord(aFile);
                        if (itemRecord.isValid()) {
                            recordList.add(itemRecord);
                        }
                    }
                }
            }

            childern = recordList.toArray(new ItemRecord[recordList.size()]);
        }

        return this.childern;
    }

    /**
     * DOC bZhou Comment method "getName".
     * 
     * @return
     */
    public String getName() {
        if (property != null) {
            return property.getLabel();
        } else if (element != null) {
            return element.getName();
        } else {
            return file.getName();
        }
    }

    /**
     * DOC bZhou Comment method "isValid".
     * 
     * @param file
     * @return
     */
    private boolean isValid(File file) {
        if (file.isDirectory()) {
            return isValidDirectory(file);
        }

        return isValidFile(file);
    }

    /**
     * DOC bZhou Comment method "isValidFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidFile(File file) {
        IPath path = new Path(file.getAbsolutePath());
        IPath propPath = path.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        File propFile = propPath.toFile();

        return JARFILEEXTENSION.equals(path.getFileExtension()) || propFile.exists()
                && !StringUtils.equals(path.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION);//$NON-NLS-1$
    }

    /**
     * DOC bZhou Comment method "isTOPFile".
     * 
     * @param file
     * @return
     */
    private boolean isValidDirectory(File file) {
        if (!file.getName().startsWith(".")) {
            IPath filePath = new Path(file.getAbsolutePath());

            for (EResourceConstant constant : EResourceConstant.getTopConstants()) {
                if (filePath.toString().indexOf(constant.getPath()) > 0) {
                    String lastSeg = filePath.lastSegment();
                    if (constant == EResourceConstant.METADATA) {
                        // MOD klliu bug 19164 2011-03-03 surpport FILEDELIMITED connection
                        return lastSeg.equals(constant.getName()) || lastSeg.equals(EResourceConstant.DB_CONNECTIONS.getName())
                                || lastSeg.equals(EResourceConstant.MDM_CONNECTIONS.getName())
                                || lastSeg.equals(EResourceConstant.FILEDELIMITED.getName());
                    } else if (constant == EResourceConstant.LIBRARIES) {
                        return !lastSeg.equals(EResourceConstant.JRXML_TEMPLATE.getName());
                    }

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * DOC bZhou Comment method "isJRXml".
     * 
     * @return
     */
    private boolean isJRXml() {
        return file.getName().endsWith(FactoriesUtil.JRXML);
    }

    /**
     * DOC zshen Comment method "isSQL".
     * 
     * @return
     */
    private boolean isSQL() {
        return file.getName().endsWith(FactoriesUtil.SQL);
    }

    /**
     * DOC bZhou Comment method "findRecord".
     * 
     * @param file
     * @return
     */
    public static ItemRecord findRecord(File file) {
        for (ItemRecord record : allItemRecords) {
            if (file.getAbsolutePath().equals(record.getFile().getAbsolutePath())) {
                return record;
            }
        }

        return null;
    }

    /**
     * Getter for allItemRecords.
     * 
     * @return the allItemRecords
     */
    public static List<ItemRecord> getAllItemRecords() {
        return allItemRecords;
    }

    /**
     * Sets the conflictNode.
     * 
     * @param conflictNode the conflictNode to set
     */
    public void setConflictNode(IRepositoryNode conflictNode) {
        this.conflictNode = conflictNode;
    }

    /**
     * Getter for conflictNode.
     * 
     * @return the conflictNode
     */
    public IRepositoryNode getConflictNode() {
        return this.conflictNode;
    }

}
