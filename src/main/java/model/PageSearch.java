package model;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// 3.30:edit
public class PageSearch {
    private StandardAnalyzer analyzer;
    private Directory index;
    private SharedContext sharedContext;

    public PageSearch(SharedContext sharedContext, View view) throws IOException {
        this.analyzer = new StandardAnalyzer();
        this.index = new ByteBuffersDirectory();
        this.sharedContext = sharedContext;

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter w = new IndexWriter(index, config)) {
            for (Page p : sharedContext.getPages()) {
                addDoc(w, p.getTitle(), p.getContent());
            }
        }
    }

    public Collection<PageSearchResult> search(String queryStr, boolean isUserAuthenticated) throws IOException, ParseException {
        Query q = new QueryParser("title", analyzer).parse(queryStr);

        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        List<PageSearchResult> results = new ArrayList<>();
        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document d = searcher.doc(docId);
            Page correspondingPage = sharedContext.getPageByTitle(d.get("title"));
            if (correspondingPage != null && (isUserAuthenticated || !correspondingPage.isPrivate())) {
                String title = d.get("title");
                String content = d.get("content");
                String formattedContent = "title:" + title + "\n" + "content:" + content;
                results.add(new PageSearchResult(formattedContent));
            }
        }
        reader.close();
        if (results.size() > 4) {
            return new ArrayList<>(results).subList(0, 4);
        }
        return results;
    }

    private static void addDoc(IndexWriter w, String title, String content) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        w.addDocument(doc);
    }
}