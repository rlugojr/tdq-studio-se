// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.talend.commons.emf.FactoriesUtil;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DQFolderFliter extends ViewerFilter {

    private static final String SVN_FOLDER_NAME = ".svn";

    private boolean isShowFile;

    public DQFolderFliter() {
        this(false);
    }

    public DQFolderFliter(boolean isShowFile) {
        this.isShowFile = isShowFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            return !folder.getName().endsWith(SVN_FOLDER_NAME);
        }

        if (element instanceof IFile && isShowFile) {
            IFile file = (IFile) element;
            return !file.getFileExtension().equals(FactoriesUtil.PROPERTIES_EXTENSION);
        }

        return isShowFile;
    }

}
