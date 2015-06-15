package org.ladders.db;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NRTCachingDirectory;
import org.apache.lucene.util.Version;
import org.ladders.db.MyRecord.MyType;
import org.ladders.util.Cols;
import org.ladders.util.JsonUtil;
import org.ladders.util.U;

public class LuceneHelper
{
	private Analyzer	analyzer		= null;
	private Directory	index			= null;
	//private IndexWriter	writer			= null;
	private String		dbName			= null;
	private String		keyFieldName	= null;

	void close() throws IOException
	{
		analyzer.close();
		//writer.close();
		index.close();
	}

	public static void main(String[] args) throws Exception
	{
		LuceneHelper h = new LuceneHelper("KEY", "TEST_LUCENE1");
		// MyRecord record = new MyRecord();
		// record.put("22517", "Samir");
		// h.set("xxxx", record);

		ArrayList<MyRecord> rows = h.getAll();
		for (MyRecord row : rows)
		{
			U.log(row);
		}
	}

	LuceneHelper(String keyFieldName, String dirName) throws IOException
	{
		this.dbName = dirName;

		this.keyFieldName = keyFieldName;

		analyzer = new StandardAnalyzer(null);

		Path path = Paths.get("/temp/" + dirName);
		//Using Lucene as a transaction DB has its downsides.
		for (int i = 0; i < 5; i++)
		{
			Directory fsDir=null;
			try
			{
				fsDir = FSDirectory.open(path);
				index = new NRTCachingDirectory(fsDir, 5.0, 60.0);

				IndexWriterConfig config = new IndexWriterConfig(analyzer);

				IndexWriter writer = new IndexWriter(index, config);
				writer.commit();
				writer.close();
				break;
			} catch (LockObtainFailedException lfe)
			{
				lfe.printStackTrace();
				index.close();
				fsDir.close();

				if (i < 4)
				{
					U.sleep((i + 1) * 1000);
					continue;
				} else
				{
					throw lfe;
				}
			}

		}//for retry loop

	}

	public ArrayList<MyRecord> search(String keyname, HashSet<String> documentIds, String[] fieldsList) throws Exception
	{
		LuceneSearcher searcher = new LuceneSearcher(index, dbName);
		ArrayList<MyRecord> rows = searcher.searchIds(keyname, documentIds, fieldsList, analyzer);
		searcher.close();
		return rows;
	}

	ArrayList<MyRecord> search(HashMap<String, String> pairs, Occur occurType, String[] fieldsList) throws Exception
	{
		LuceneSearcher searcher = new LuceneSearcher(index, dbName);
		ArrayList<MyRecord> rows = searcher.search(pairs, occurType, fieldsList, analyzer);
		searcher.close();
		return rows;
	}

	ArrayList<MyRecord> getAll() throws Exception
	{
		LuceneSearcher searcher = new LuceneSearcher(index, dbName);
		ArrayList<MyRecord> rows = searcher.search(null, Occur.MUST, null, analyzer);
		searcher.close();
		return rows;
	}

	void delete(MyRecord row) throws Exception
	{
		String keyFieldVal = row.getString(this.keyFieldName);
		Term term = new Term(this.keyFieldName, keyFieldVal);

		IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));
		writer.deleteDocuments(term);
		writer.commit();
		writer.close();
	}

	void set(String keyFieldVal, MyRecord record) throws Exception
	{
		Document doc = new Document();

		for (String key : record.keySet())
		{

			Field field = null;

			if (key.equals(Cols.ROWID) || key.equals(Cols.PARENTID) || key.equals(Cols.GRANDPAID) || key.equals(Cols.ROWTYPE))
			{
				String strVal = record.getString(key);
				field = new StringField(key, strVal, Field.Store.YES);
			} else if (record.getType(key) == MyType.String)
			{
				String strVal = record.getString(key);
				//if (strVal.contains(" "))
				//{
				//field = new TextField(key, strVal, Field.Store.YES);
				//} else
				//{
				field = new StringField(key, strVal, Field.Store.YES);
				//}
			} else if (record.getType(key) == MyType.Number)
			{
				field = new DoubleField(key, record.getNumber(key), Field.Store.YES);

			} else if (record.getType(key) == MyType.Date)
			{
				Date date = (Date) record.getObject(key);
				field = new StringField(key, "__DATE:" + date.getTime(), Field.Store.YES);

			} else if (record.getType(key) == MyType.Map)
			{
				HashMap<String, String> map = record.getMap(key);
				field = new StringField(key, "__MAP:" + JsonUtil.map2Json(map), Field.Store.YES);

			} else
			{
				throw new Exception(record.getObject(key).getClass() + " NOT supported. Value:" + record.getObject(key));
			}
			doc.add(field);

		}// for

		//U.log("LuceneHelper.set {" + dbName + "}: " + record);

		IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));
		writer.updateDocument(new Term(this.keyFieldName, keyFieldVal), doc);
		writer.commit();
		writer.close();
	}// set

}
