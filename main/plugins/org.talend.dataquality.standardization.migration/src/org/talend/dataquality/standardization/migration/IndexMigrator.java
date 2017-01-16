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
package org.talend.dataquality.standardization.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * A tool to regenerate all "out of the box" indexes with specified analyzer. The regeneration simply reads and
 * re-writes all detected indexes in inputPath. This class is independent from SynonymIndexBuilder class.
 * 
 * @author sizhaoliu
 */
public class IndexMigrator {

    // Use standard analyzer without English stop words like "an", "was"
    private Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);

    // Default value points to an SVN working copy.
    // The provided indexes are located at "addons" folder of the studio.
    //private String inputPath = "/misc/repo-td/tdq-studio-ee/main/plugins/org.talend.dataquality.data.resources/data/synonym";//$NON-NLS-1$

    // private String inputPath =
    // "/Volumes/Macintosh/repo-td/tdq-studio-ee/main/plugins/org.talend.dataquality.data.index/TalendGivenNames_index";
    private String inputPath = "H:/TalendGivenNames_index";

    //private String inputPath = "/path/to/studio/addons/data/synonym";//$NON-NLS-1$

    private String outputPath = "";

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    public static final String F_WORDTERM = "wordterm";

    public static final String F_SYNTERM = "synterm";

    private static final boolean IS_MIGRATING_FIRSTNAME_INDEX = false;

    private Map<String, List<String[]>> nameMap = new HashMap<String, List<String[]>>();

    private int count = 0;

    /**
     * Sets the inputPath.
     * 
     * @param inputPath the inputPath to set
     */
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * Sets the outputPath.
     * 
     * @param outputPath the outputPath to set
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Deletes all files and sub-directories under a specified directory.
     * 
     * @param dir
     * @return true if all deletions were successful
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * prepare I/O folders and call regeneration process.
     * 
     * @throws java.io.IOException
     */
    public int run() throws IOException {
        File inputFolder = new File(inputPath);
        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            System.err.println("The input path <" + inputPath + "> does not exist or is not a folder.");
            System.err.println("Usage: java -jar IndexMigrator.jar <inputPath> <outputPath(optinal)>");
            return -1;
        }
        File outputFolder = new File(outputPath);
        if (inputFolder.equals(outputFolder)) {
            System.err.println("The I/O path should not be identical.");
            return -2;
        }
        System.out.println("Migrating all indexes in folder <" + inputPath + ">");
        if ("".equals(outputPath)) {
            System.out.println("No output folder specified. The new index(es) will be genenrated in <" + inputPath
                    + "_REGENERATED> folder");
            outputFolder = new File(inputPath + "_REGENERATED");
        } else {
            outputFolder = new File(outputPath);
        }
        if (outputFolder.exists() && outputFolder.isDirectory()) {
            System.out.println("The path <" + outputFolder + "> already exists.\nDeleting before migration...");
            deleteDir(outputFolder);
        }
        return regenerate(inputFolder, outputFolder);
    }

    /**
     * regenerate all indexes recursively.
     * 
     * @param inputFolder
     * @param outputFolder
     * @throws java.io.IOException
     */
    private int regenerate(File inputFolder, File outputFolder) throws IOException {
        FSDirectory indexDir = FSDirectory.open(inputFolder);
        CheckIndex check = new CheckIndex(indexDir);
        Status status = check.checkIndex();
        if (status.missingSegments) {
            for (File f : inputFolder.listFiles()) {
                if (f.isDirectory()) {
                    File out = new File(outputFolder.getAbsolutePath() + "/" + f.getName());
                    out.mkdir();
                    regenerate(f, out);
                }
            }
        } else {
            System.out.println("REGENERATE: " + inputFolder.getAbsoluteFile());
            FSDirectory outputDir = FSDirectory.open(outputFolder);

            IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
            IndexWriter writer = new IndexWriter(outputDir, config);
            // IndexWriter writer = new IndexWriter(outputDir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);

            IndexReader reader = DirectoryReader.open(indexDir);
            // IndexSearcher searcher = new IndexSearcher(reader);

            // IndexSearcher searcher = new IndexSearcher(indexDir);

            Document doc = null;
            // for any other indexes, regenerate with new Analyzer, but no
            // changes to document.
            Collection<String> fieldNames = new ArrayList<String>();
            Document newDoc = null;
            for (int i = 0; i < reader.maxDoc(); i++) {
                doc = reader.document(i);

                List<IndexableField> fields = doc.getFields();
                for (int k = 0; k < fields.size(); k++) {
                    fieldNames.add(fields.get(k).name());
                }

                if (IS_MIGRATING_FIRSTNAME_INDEX) {
                    newDoc = generateFirstNameDoc(doc);
                    if (newDoc != null) {
                        writer.addDocument(newDoc);
                    }
                } else {
                    if (fieldNames.contains(F_WORD) && fieldNames.contains(F_SYN)) {
                        // for "out of the box" indexes, regenerate the index with 2
                        // extra fields ("SYNTERM" and "WORDTERM") for
                        // better scoring.
                        // Document oldDoc = searcher.doc(i);
                        String word = doc.getValues(F_WORD)[0];
                        String[] synonyms = doc.getValues(F_SYN);
                        Set<String> synonymSet = new HashSet<String>();
                        for (String syn : synonyms) {
                            if (!syn.equals(word)) {
                                synonymSet.add(syn);
                            }
                        }
                        newDoc = generateDocument(word, synonymSet);
                        writer.addDocument(newDoc);
                    } else {
                        writer.addDocument(doc);
                    }
                    // writer.addDocument(doc);
                }
            }
            System.out.println("count: " + count);

            reader.close();
            writer.commit();
            writer.close();
            outputDir.close();

            // copy all other files such as "readMe.txt"
            for (File file : inputFolder.listFiles()) {
                if (file.isFile() && !isLuceneIndexFile(file)) {
                    // copy to destination folder
                    copyFile(file, outputFolder);
                }
            }
        }
        return 0;
    }

    private Document generateDocument(String word, Set<String> synonyms) {
        Document doc = new Document();
        word = word.trim();
        Field wordField = new Field(F_WORD, word, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, TermVector.NO);
        doc.add(wordField);
        Field wordTermField = new Field(F_WORDTERM, word.toLowerCase(), Field.Store.NO, Field.Index.NOT_ANALYZED, TermVector.NO);
        doc.add(wordTermField);
        for (String syn : synonyms) {
            if (syn != null) {
                syn = syn.trim();
                if (syn.length() > 0 && !syn.equals(word)) {
                    doc.add(new Field(F_SYN, syn, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, TermVector.NO));
                    doc.add(new Field(F_SYNTERM, syn.toLowerCase(), Field.Store.NO, Field.Index.NOT_ANALYZED, TermVector.NO));
                }
            }
        }
        return doc;
    }

    // private Document generateDocument(String word, Set<String> synonyms) {
    // String tempWord = word.trim();
    // Document doc = new Document();
    // FieldType ft = new FieldType();
    // ft.setStored(true);
    // ft.setIndexed(true);
    // ft.setOmitNorms(true);
    // ft.freeze();
    //
    // // Field wordField = new Field(SynonymIndexSearcher.F_WORD, tempWord, StringField.TYPE_STORED);
    // Field wordField = new Field(F_WORD, tempWord, ft);
    // doc.add(wordField);
    // Field wordTermField = new StringField(F_WORDTERM, tempWord.toLowerCase(), Field.Store.YES);
    // doc.add(wordTermField);
    // for (String syn : synonyms) {
    // if (syn != null) {
    // syn = syn.trim();
    // if (syn.length() > 0 && !syn.equals(tempWord)) {
    // // doc.add(new Field(SynonymIndexSearcher.F_SYN, syn, StringField.TYPE_STORED));
    // doc.add(new Field(F_SYN, syn, ft));
    // doc.add(new StringField(F_SYNTERM, syn.toLowerCase(), Field.Store.YES));
    // }
    // }
    // }
    // return doc;
    // }


    private Document generateFirstNameDoc(Document doc) {

        String name = doc.get("name");//$NON-NLS-1$
        String country = doc.get("country");//$NON-NLS-1$
        String gender = doc.get("gender");//$NON-NLS-1$

        List<String[]> variants = nameMap.get(name);
        if (variants != null) {
            // see if the current doc is duplicated
            for (String[] tuple : variants) {
                if ((country == null && tuple[0] == null || country != null && country.equals(tuple[0]))//
                        && (gender == null && tuple[1] == null || gender != null && gender.equals(tuple[1]))) {
                    return null;
                }
            }
            // return null;
        } else {
            variants = new ArrayList<String[]>();
        }
        variants.add(new String[] { country, gender });
        nameMap.put(name, variants);

        count++;
        // TODO Auto-generated method stub
        return generateDocument(name, country, gender);
    }

    /**
     * generate a document.
     *
     * @param word
     * @param synonyms
     * @return
     */
    private Document generateDocument(String name, String country, String gender) {
        name = name.trim();
        Document doc = new Document();
        FieldType ft = new FieldType();
        ft.setStored(true);
        ft.setIndexed(true);
        ft.setOmitNorms(true);
        ft.freeze();

        Field wordField = new Field("name", name, ft);
        doc.add(wordField);

        Field wordTermField = new StringField("nameterm", name.toLowerCase(), Field.Store.NO);
        doc.add(wordTermField);

        if (country != null) {
            Field countryField = new StringField("country", country, Field.Store.YES);
            doc.add(countryField);
        }

        if (gender != null) {
            Field genderField = new StringField("gender", gender, Field.Store.YES);
            doc.add(genderField);
        }
        return doc;
    }

    /**
     * check if a file is for Lucene index. A complete list of lucene index formats can be found here:
     * 
     * http://lucene.apache.org/core/old_versioned_docs/versions/3_0_1/ fileformats.html
     * 
     * @param file
     */
    private boolean isLuceneIndexFile(File file) {
        String fileName = file.getName();
        if (fileName.startsWith("segments") || "write.lock".equals(fileName) || fileName.endsWith(".cfs")
                || fileName.endsWith(".fnm") || fileName.endsWith(".fdx") || fileName.endsWith(".fdt")
                || fileName.endsWith(".tis") || fileName.endsWith(".tii") || fileName.endsWith(".frq")
                || fileName.endsWith(".prx") || fileName.endsWith(".nrm") || fileName.endsWith(".tvx")
                || fileName.endsWith(".tvd") || fileName.endsWith(".tvf") || fileName.endsWith(".del")) {
            return true;
        }
        return false;
    }

    private void copyFile(File source, File targetFolder) throws IOException {
        if (source.isDirectory()) {
            if (!".svn".equals(source.getName())) { // omit SVN metadata
                File dir = new File(targetFolder.getAbsolutePath() + "/" + source.getName());
                dir.mkdirs();
                for (File f : source.listFiles()) {
                    copyFile(f, dir);
                }
            }
        } else {
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = null;
            try {
                if (!targetFolder.exists()) {
                    targetFolder.mkdirs();
                }
                fos = new FileOutputStream(targetFolder + "/" + source.getName());
                byte[] buf = new byte[1024];
                int i = 0;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                }
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        IndexMigrator migration = new IndexMigrator();
        if (args.length > 0) {
            String inputPath = args[0];
            migration.setInputPath(inputPath);
            if (args.length > 1) {
                String outputPath = args[1];
                migration.setOutputPath(outputPath);
            }
        }
        int status = migration.run();
        System.exit(status);
    }

}
