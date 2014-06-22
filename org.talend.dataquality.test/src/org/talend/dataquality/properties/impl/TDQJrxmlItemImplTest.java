// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.properties.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.emf.EMFUtil;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.helper.ByteArrayResource;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.TDQJrxmlItem;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class TDQJrxmlItemImplTest {

    TDQJrxmlItem jrxmlItem;

    ByteArrayResource byteArrayResource;

    File file;

    String modifiedContent = "modified jrxml content";
    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        jrxmlItem = PropertiesFactory.eINSTANCE.createTDQJrxmlItem();

        EMFUtil emfUtil = new EMFUtil();
        file = new File("./data/test_0.1.jrxml");
        System.out.println("Loading file " + file.getAbsolutePath());
        writeToFile("original jrxml content");

        ResourceSet rs = emfUtil.getResourceSet();
        // init the bytearray resource
        ByteArray byteArray = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createByteArray();
        byteArray.setInnerContentFromFile(file);

        byteArrayResource = new ByteArrayResource(URI.createFileURI(file.getAbsolutePath()));
        byteArrayResource.getContents().add(byteArray);
        rs.getResources().add(byteArrayResource);
        
        org.talend.core.model.properties.Property fileProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE
                .createProperty();

        fileProperty.setId(EcoreUtil.generateUUID());
        fileProperty.setItem(jrxmlItem);
        fileProperty.setLabel("test1");
        jrxmlItem.setName("test1");

        jrxmlItem.setProperty(fileProperty);
        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setPath("data");
        jrxmlItem.setState(itemState);

        // get the eresource of the item without creating the real project
        File property = new File("data/test_0.1.property");
        Resource propertyResource = emfUtil.getResourceSet().createResource(URI.createFileURI(property.getAbsolutePath()));
        propertyResource.getContents().add(jrxmlItem.getProperty());
        propertyResource.getContents().add(jrxmlItem.getState());
        propertyResource.getContents().add(jrxmlItem);
        rs.getResources().add(propertyResource);

        byteArrayResource.load(null);

        jrxmlItem.setContent(byteArray);
        emfUtil.save();
    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * TDQ-7259
     * 
     * 1) Create a actual JRXML resource via EMF. 2) Edit the content out of EMF resource. 3) Move the EMF resource to
     * other folders.-- make it unload to become proxy type $) Assert the content of EMF resource is fresh.
     * 
     * Test method for {@link org.talend.dataquality.properties.impl.TDQFileItemImpl#getContent()}.
     * 
     * @throws UnsupportedEncodingException
     * @throws CoreException
     * @throws FileNotFoundException
     */
    @Test
    public void testGetContent() throws UnsupportedEncodingException, CoreException, FileNotFoundException {

        // modify the related jrxml file outside
        writeToFile(this.modifiedContent);

        // unload the item
        byteArrayResource.unload();

        // get the content again, check if it is the new content of the modified one.
        ByteArray newContent = jrxmlItem.getContent();
        Assert.assertNotNull(newContent);

        byte[] innerContent = newContent.getInnerContent();
        Assert.assertNotNull(innerContent);

        Assert.assertEquals(modifiedContent, new String(innerContent));
    }

    private void writeToFile(String content) throws FileNotFoundException {
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file.getAbsoluteFile()));
        try {
            stream.write(content.getBytes());
        } catch (IOException e) {
            Assert.fail("modify jrxml file exception");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Assert.fail("modify jrxml file exception");
                }
            }
        }
    }

}
