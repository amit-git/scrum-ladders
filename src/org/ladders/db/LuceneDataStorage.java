package org.ladders.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.search.BooleanClause.Occur;
import org.ladders.util.Cols;
import org.ladders.util.U;

public class LuceneDataStorage extends AbstractDataStorage
{
	private LuceneHelper	tasks;
	//private LuceneHelper	settings;

	LuceneDataStorage(String ladderName) throws Exception
	{
		super(ladderName);

		tasks = new LuceneHelper(Cols.ROWID, ladderName + "-tasks");
		//settings = new LuceneHelper("KEY", ladderName + "-settings");
	}

	@Override
	public ArrayList<MyRecord> getRows(HashMap<String, String> andPairs, String[] fieldsList) throws Exception
	{
		ArrayList<MyRecord> arr = tasks.search(andPairs, Occur.MUST, fieldsList);

		// Looks like sort isn't working. I will figure it out later.
		// For now sort it again.
		Collections.sort(arr, new Comparator<MyRecord>() {
			@Override
			public int compare(MyRecord row1, MyRecord row2)
			{
				try
				{
					double d1 = U.tryParse(row1.getString(Cols.PRIORITY));
					double d2 = U.tryParse(row2.getString(Cols.PRIORITY));
					return (int) (d1 - d2);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return 0;
			}
		});
		
		
		
		

		return arr;
	}

	@Override
	public ArrayList<MyRecord> getRows(String keyname, HashSet<String> documentIds, String[] fieldsList) throws Exception
	{
		return tasks.search(keyname, documentIds, fieldsList);
	}


	@Override
	public void delete(MyRecord row) throws Exception
	{
		tasks.delete(row);
	}

	@Override
	void update(String id, MyRecord row) throws Exception
	{
		U.log("A LuceneDS.update id:" + id + " row:" + row);
		tasks.set(id, row);


	}

	
	@Override
	protected void indexBy(String key)
	{

	}
	
	
	@Override
	public void removeAllRows() throws Exception
	{
		//Remove all records
		ArrayList<MyRecord> arr = tasks.search(null, Occur.SHOULD, null);
		for (MyRecord rec: arr){
			tasks.delete(rec);
		}
	}

}
