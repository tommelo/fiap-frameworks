package com.fiap.lucene.service.setup;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.POITextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LuceneSetup {

	public static final String PATH_FIELD = "path";
	public static final String FILE_NAME_FIELD = "filename";
	public static final String CONTENTS_FIELD = "contents";
	
	private final String DOCS_PATH = "/var/www/html/docs";
	private final String INDEX_PATH = "/var/www/lucene";
	
	public LuceneSetup() {
		setup();
	}
	
	private void setup() {
		try {
						
			File docs = new File(DOCS_PATH);			
			Directory dir = FSDirectory.open(Paths.get(INDEX_PATH));
			
			StandardAnalyzer analyzer = new StandardAnalyzer()	;
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(dir, config);
			
			File [] files = docs.listFiles();
			for (File file : files) {				
				Document doc = new Document();
				
				Field path = new StringField(PATH_FIELD, file.getAbsolutePath(), Field.Store.YES);
				Field name = new TextField(FILE_NAME_FIELD, file.getName(), Field.Store.YES);				
				Field content =	 new TextField(CONTENTS_FIELD, getReader(file)); 
				
				doc.add(path);
				doc.add(name);				
				doc.add(content);
				
				writer.addDocument(doc);
			}
			
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}					
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private BufferedReader getReader(File file) throws IOException {
		String name = file.getName();
		
		if (name.endsWith(".pdf"))
			return new BufferedReader(new StringReader(readPDF(file)));
		else if (name.endsWith(".doc"))
			return new BufferedReader(new StringReader(readDoc(file)));
		else 
			return new BufferedReader(new FileReader(file));
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private String readPDF(File file) {
		String content = "";
		
		try {		
		    PDDocument document = null;
		    document = PDDocument.load(file);		    		   
	        
		    PDFTextStripperByArea stripper = new PDFTextStripperByArea();		        
	        stripper.setSortByPosition(true);
	        
	        PDFTextStripper textStripper = new PDFTextStripper();
	        content = textStripper.getText(document);			        
		    		    
	        document.close();	        
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return content;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String readDoc(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		POITextExtractor extractor = new WordExtractor(fis);
		String text = extractor.getText();
		extractor.close();
		
		return text;
	}
}
