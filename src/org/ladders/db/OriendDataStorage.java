package org.ladders.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class OriendDataStorage extends AbstractDataStorage
{

	public static void main(String[] a) throws Exception
	{

		final ODatabaseDocumentTx db = new ODatabaseDocumentTx("plocal:/temp/OriendDB1");
		if (!db.exists())
		{
			db.create();
			final OSchema schema = db.getMetadata().getSchema();
			final OClass clazz = schema.createClass("test");
			clazz.createProperty("numericAtt", OType.DOUBLE);
		}
		db.open("admin", "admin");

		
		final OSchema schema = db.getMetadata().getSchema();
		final OClass clazz = schema.createClass("test");
		clazz.createProperty("FirstName", OType.STRING);
		clazz.createProperty("LastName", OType.STRING);
		
		db.command(new OCommandSQL("INSERT INTO test(FirstName,LastName) VALUES ('Samir','Khobragade')")).execute();

		final List<ODocument> docs = db.query(new OSQLSynchQuery("SELECT FROM test"));
		for (ODocument doc : docs)
		{
			System.out.println(doc.field("numericAtt"));
		}

		//Assert.assertEquals(new Double(28.23), new Float(28.23).doubleValue());

		try
		{
			// YOUR CODE
		} finally
		{
			db.close();
		}

	}

	OriendDataStorage(String ladderName) throws Exception
	{
		super(ladderName);
	}

	@Override
	public ArrayList<MyRecord> getRows(HashMap<String, String> andPairs, String[] fieldsList) throws Exception
	{
		return null;
	}

	@Override
	public ArrayList<MyRecord> getRows(String keyname, HashSet<String> documentIds, String[] fieldsList) throws Exception
	{
		return null;
	}

	@Override
	public void delete(MyRecord row) throws Exception
	{
	}

	@Override
	void update(String id, MyRecord row) throws Exception
	{
	}

	@Override
	protected void indexBy(String key)
	{

	}

	@Override
	public void removeAllRows() throws Exception
	{
	}

}
