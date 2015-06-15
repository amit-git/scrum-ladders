package org.ladders.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

class LuceneSearcher
{
	private IndexReader		reader		= null;
	private IndexSearcher	searcher	= null;

	//private String			dbName		= null;

	LuceneSearcher(Directory directory, String dbName) throws IOException
	{
		//this.dbName = dbName;
		reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);
	}

	Document get(int docId) throws IOException
	{
		return searcher.doc(docId);
	}

	private static MyRecord makeMyRecord(Document doc, HashSet<String> fieldsMap) throws Exception
	{

		//U.printCaller(doc.get(Cols.ROWID));

		MyRecord rec = new MyRecord();
		for (IndexableField field : doc.getFields())
		{
			String name = field.name();

			if (fieldsMap == null || fieldsMap.contains(name.toLowerCase().trim()))
			{
				String valu = field.stringValue();

				if (valu.startsWith("__MAP:"))
				{
					throw new Exception("makeMyRecord valu:"+valu);
					//HashMap<String, String> map = JsonUtil.json2Map(valu.replaceAll("__MAP:", ""));
					//rec.put(name, map);
				} else if (valu.startsWith("__DATE:"))
				{
					Date date = new Date(Long.parseLong(valu.replaceAll("__DATE:", "")));
					rec.put(name, date);
				} else
				{
					rec.put(name, valu);
				}

			} else
			{
				//U.log("makeMyRecord NOT Adding name:"+name + " = "+field.stringValue());
			}
		}
		return rec;
	}

	static Query createQuery(String key, String val)
	{
		return new TermQuery(new Term(key, val));
		//TODO: Uncomment following
		/*
		String[] arr = val.split(" ");
		if (arr.length == 1)
		{
			return new TermQuery(new Term(key, val));
		} else
		{
			PhraseQuery q2 = new PhraseQuery();
			for (String word : arr)
			{
				String w = word.toLowerCase().trim();
				if (U.isNullOrBlank(w))
					continue;
				q2.add(new Term(key, w));
			}
			return q2;
		}
		*/
	}

	ArrayList<MyRecord> searchIds(String keyname, HashSet<String> documentIds, String[] fieldsList, Analyzer analyzer) throws Exception
	{
		BooleanQuery booleanQuery = new BooleanQuery();

		for (String id: documentIds)
		{
			Query query1 = createQuery(keyname, id);
			booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
		}
		return search(booleanQuery, fieldsList, analyzer);
	}

	ArrayList<MyRecord> search(HashMap<String, String> pairs, Occur occurType, String[] fieldsList, Analyzer analyzer) throws Exception
	{
		//U.log("LuceneSearchHelper pairs:" + pairs);
		BooleanQuery booleanQuery = new BooleanQuery();

		if (pairs == null)
		{
			booleanQuery.add(new BooleanClause(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD));
		} else
		{

			for (Entry<String, String> pair : pairs.entrySet())
			{
				Query query1 = createQuery(pair.getKey(), pair.getValue());
				booleanQuery.add(query1, occurType);
			}
		}
		
		return search(booleanQuery, fieldsList, analyzer);
	}


	private ArrayList<MyRecord> search(BooleanQuery booleanQuery, String[] fieldsList, Analyzer analyzer) throws Exception
	{

		int hitsPerPage = 10000;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);

		//U.log("booleanQuery.clauses:" + booleanQuery.clauses());
		searcher.search(booleanQuery, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		//U.log("LuceneSearch.search hits.length:" + hits.length);

		//Make HashSet for easy searching
		HashSet<String> fieldsMap = null;
		if (fieldsList != null)
		{
			fieldsMap = new HashSet<String>();
			for (String s : fieldsList){
				if (s!=null) fieldsMap.add(s.toLowerCase().trim());
			}
		}

		ArrayList<MyRecord> returnDocs = new ArrayList<MyRecord>();

		for (int i = 0; i < hits.length; ++i)
		{
			Document doc = searcher.doc(hits[i].doc);
			MyRecord myrec = makeMyRecord(doc, fieldsMap);
			returnDocs.add(myrec);

			//U.log("LuceneSearcher myrec[" + i + "]:" + myrec);
		}

		return returnDocs;
	}

	void close() throws IOException
	{
		reader.close();
	}

}