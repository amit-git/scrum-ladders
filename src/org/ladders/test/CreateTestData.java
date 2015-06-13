package org.ladders.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.ladders.db.AbstractDataStorage;
import org.ladders.db.LadderFactory;
import org.ladders.db.MyRecord;
import org.ladders.util.Cols;
import org.ladders.util.U;

public class CreateTestData
{

	static int rand(int Min, int Max)
	{
		return Min + (int) (Math.random() * ((Max - Min) + 1));
	}

	static ArrayList<String>	printableChars	= new ArrayList<String>();
	static
	{
		for (char i = 'A'; i <= 'Z'; i++)
			printableChars.add(Character.toString(i));
		for (char i = 'a'; i <= 'z'; i++)
			printableChars.add(Character.toString(i));
		for (char i = '0'; i <= '9'; i++)
			printableChars.add(Character.toString(i));

		for (char i : "[!@#$%^&*()_+=-|'\"><".toCharArray())
			printableChars.add(Character.toString(i));

		for (char i = 'A'; i <= 'Z'; i++)
			printableChars.add(Character.toString(i));
		for (char i = 'a'; i <= 'z'; i++)
			printableChars.add(Character.toString(i));
		for (char i = 'A'; i <= 'Z'; i++)
			printableChars.add(Character.toString(i));
		for (char i = 'a'; i <= 'z'; i++)
			printableChars.add(Character.toString(i));

	}

	static StringBuffer generateLongString(int len)
	{
		StringBuffer buf = new StringBuffer();
		while (buf.length() < len)
		{
			buf.append(printableChars.get(rand(0, printableChars.size() - 1)));
			if (rand(1, 7) == 1)
				buf.append(" ");
			if (rand(1, 200) == 1)
				buf.append("\n");
			if (rand(1, 20) == 1)
				buf.append("\t");
		}
		return buf;
	}

	/**
	 * @param args
	 */
	static String	LADDER_NAME	= "Ladders";

	public static void main(String[] args) throws Exception
	{
		AbstractDataStorage.VALIDATE = false;

		// delete all first
		ArrayList<MyRecord> rows = null;
		do
		{
			HashSet<String> types = new HashSet<String>();
			types.add("Project");
			types.add("Epic");
			types.add("Story");
			types.add("Task");
			rows = LadderFactory.getLadder(LADDER_NAME).getRows(Cols.ROWTYPE, types, null);
			for (MyRecord row : rows)
			{
				LadderFactory.getLadder(LADDER_NAME).delete(row);
				U.log("deleted");
			}
		} while (rows.size() > 0);

		insertProjects("Project", Cols.ROOT_PARENT_ID);
	}

	private static void insertProjects(String rowType, Object parentId) throws Exception
	{

		for (int i = 1; i < 5; i++)
		{

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("Description", rowType + ": " + i + " For " + parentId + " " + generateLongString(rand(50, 400)));
			params.put("Product Owner", "Product Manager " + ((i % 5) + 1));
			params.put(Cols.PARENTID, parentId.toString());

			U.log(i + ": " + params.get("Product Owner") + " - " + params.get("Description"));
			MyRecord row = LadderFactory.getLadder(LADDER_NAME).insertNew(rowType, params);

			insertEpics(rowType, "Epic", row.getString(Cols.ROWID));
		}

	}

	private static void insertEpics(String parentType, String rowType, String parentId) throws Exception
	{

		for (int i = 1; i < 5; i++)
		{

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("Description", rowType + ": " + i + " For " + parentId + " " + generateLongString(rand(50, 400)));
			params.put("Epic Owner", "TPM " + ((i % 5) + 1));
			params.put(Cols.PARENTID, parentId.toString());

			params.put("Low Estimate", rand("250,400,650,1000"));
			params.put("High Estimate", rand("250,400,650,1000"));

			U.log(i + ": " + params.get("Epic Owner") + " - " + params.get("Low Estimate"));
			MyRecord row = LadderFactory.getLadder(LADDER_NAME).insertNew(rowType, params);

			insertStories(rowType, "Story", row.getString(Cols.ROWID));
		}

	}

	private static void insertStories(String parentType, String rowType, String parentId) throws Exception
	{

		for (int i = 1; i < 5; i++)
		{

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("Description", rowType + ": " + i + " For " + parentId + " " + generateLongString(rand(50, 400)));
			params.put("Story Owner", "Dev " + ((i % 5) + 1));
			params.put(Cols.PARENTID, parentId.toString());

			params.put("Team", rand("Unassigned, UI Team, Backend Team"));
			params.put("Sprint", rand("Backlog, Jan 2013, Feb 2013, Mar 2013, Apr 2013, May 2013"));
			params.put("Story Points", rand("3, 5, 8, 10, 15, 20, 25"));

			U.log(i + ": " + params.get("Story Owner") + " - " + params.get("Description"));
			MyRecord row = LadderFactory.getLadder(LADDER_NAME).insertNew(rowType, params);

			insertTasks(rowType, "Task", row.getString(Cols.ROWID));
		}

	}

	private static void insertTasks(String parentType, String rowType, String parentId) throws Exception
	{

		for (int i = 1; i < 5; i++)
		{

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("Description", rowType + ": " + i + " For " + parentId + " " + generateLongString(rand(50, 400)));
			params.put("Task Owner", "Dev " + ((i % 5) + 1));
			params.put(Cols.PARENTID, parentId.toString());

			params.put("Task Points", rand("1, 2, 3, 5, 8, 13"));
			params.put("Status", rand("Open, Closed, Blocked"));

			U.log(i + ": " + params.get("Task Owner") + " - " + params.get("Description"));
			LadderFactory.getLadder(LADDER_NAME).insertNew(rowType, params);
		}

	}

	static String rand(String commaSeparatedString)
	{
		return rand(commaSeparatedString.split(","));
	}

	static String rand(Object[] arr)
	{
		Object s = arr[rand(0, arr.length - 1)];
		return s.toString().trim();
	}
}
