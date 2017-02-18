package com.fiap.lucene.service.processor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fiap.lucene.service.domain.Match;
import com.fiap.lucene.service.domain.Ticket;
import com.fiap.lucene.service.domain.TicketResult;
import com.fiap.lucene.service.repository.TicketRepository;
import com.fiap.lucene.service.setup.LuceneSetup;

@Service
public class LuceneProcessor {

	@Value("${lucene.max.result.size}")
	private int maxResultSize; 
	
	@Value("${lucene.index.dir}")
	private String indexDir;
	
	@Value("${base.url}")
	private String baseUrl;
	
	@Autowired
	private TicketRepository repository;
	
	@Async
	public void process(Ticket ticket) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
	    PhraseQuery query = new PhraseQuery(
	    		LuceneSetup.CONTENTS_FIELD, 
	    		ticket.getQuery().split(" "));
	    
	    TopDocs topDocs = searcher.search(query, maxResultSize);
	    ScoreDoc [] scoreDocs = topDocs.scoreDocs;
	    
	    TicketResult result = new TicketResult();
	    List<Match> matches = new ArrayList<>();
	    
	    for (ScoreDoc scoreDoc : scoreDocs) {
	    	Match match = new Match();
	    	
	    	Document document = searcher.doc(scoreDoc.doc);
	    	String file = document.getField(LuceneSetup.FILE_NAME_FIELD).stringValue();
	    	
	    	match.setFile(file);
	    	match.setUrl(baseUrl + file);
	    	match.setScore(scoreDoc.score);
	    	
	    	matches.add(match);
	    }
	    
	    result.setId(ticket.getTicket());
	    result.setQuery(ticket.getQuery());
	    result.setMatches(matches);
	    
	    repository.save(result);	
	    	    
	    reader.close();
	}
	
}
