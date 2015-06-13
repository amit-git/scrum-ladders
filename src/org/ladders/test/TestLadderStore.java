package org.ladders.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ladders.db.AbstractDataStorage;
import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.model.LaddersTopologyModel;
import org.ladders.util.Cols;
import org.ladders.util.U;

public class TestLadderStore
{

	private static int TEST_REC_SIZE = 20;
	private static String NAME = "_TEST";
	private static String SCHEMA = "["+
			" {"+
			"	Name: \"Project\","+
			"	Schema: ["+
			"		{"+
			"			Name:\"Description\","+
			"			Validation: \"regex('[a-z]+')\","+
			"			MaxLen: 100,"+
			"			MinLen: 2"+
			"		},"+
			"		"+
			"		{"+
			"			Name:\"Product_Owner\","+
			"			Args: [\"Product Manager 1\", \"Product Manager 2\", \"Product Manager 3\", \"Product Manager 4\", \"Product Manager 5\"]"+
			"		}	"+
			"		"+
			"	]"+
			"}]";
	
	@Before
	public void setUp() throws Exception
	{
		
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testFactory() throws Exception
	{
		ArrayList<MyRecord> list;
		AbstractDataStorage ds;
		LaddersTopologyModel ltm;
		//Create Ladder
		{
			Assert.assertEquals(LadderFactory.contains(NAME), false);
	
			ltm = new LaddersTopologyModel(SCHEMA);
			LadderFactory.add(NAME, ltm);
			
			Assert.assertEquals(LadderFactory.contains(NAME), true);
	
			ds = LadderFactory.getLadder(NAME);

			list = ds.getRows(null, null);
			Assert.assertEquals(list.size(), 0);
		}
		
		//Insert Rows
		for (int i=0; i<TEST_REC_SIZE; i++)
		{
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("Description", i+" ] XXXXXXXXXXX aikudhakidhasoidja kdnakjaoe20w8e0jcxaldj02e82qo2eu23131(*U(*$IHKHO(U)(U)UO");
			params.put("Product_Owner", i+" owner");
			params.put(Cols.PARENTID, Cols.ROOT_PARENT_ID);

			MyRecord record = ds.insertNew("Project", params);

			assertSize(ds, i+1);
		}
		
		//Remove Rows leave 10
		int size = TEST_REC_SIZE;
		while(size>TEST_REC_SIZE/2){
			MyRecord rec = ds.getRows(null, null).get(0);
			ds.delete(rec);

			size--;
			assertSize(ds, size);
		}
		
		//Now update the remaining records
		{
			ArrayList<MyRecord> arr = ds.getRows(null, null);
			for (MyRecord rec: arr){
				String rowId = rec.getString(Cols.ROWID);
				String newDesc = "Updated "+rowId;
				
				ds.update(rowId, "Description", newDesc);
				Assert.assertEquals(ds.getRow(rowId).getString("Description"), newDesc );

				assertSize(ds, TEST_REC_SIZE/2);
			}
		}

		//Drop the ladder 
		{
			LadderFactory.removeLadder(NAME);
			Assert.assertEquals(LadderFactory.contains(NAME), false);
		}
		
	}
	private static void assertSize(AbstractDataStorage ds, int expectedSize) throws Exception{
		int realSize = ds.getRows(null, null).size();
		U.log("expectedSize="+expectedSize +" realSize="+realSize );
		Assert.assertEquals(realSize, expectedSize);
		
	}
}
