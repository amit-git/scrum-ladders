[
{
	Name: "Epic",
	Schema: [
		{
			Name:"Description",
			ColumnType:"Text",
			MaxLen: 100,
			MinLen: 2
		},
		{
	        Name: "Comments",
	        ColumnType: "Notes"
	    },
		{
			Name:"Epic_Owner",
			ColumnType:"Select",
			Args: ["PM 1", "PM 2", "PM 3"]
		},
		{
			Name:"Milestones",
			ColumnType:"Select",
			Args: ["Q1 2015", "Q2 2015", "Q3 2015", "Q4 2015"]
		},
		{
			Name:"T_Shirt_Estimates",
			ColumnType:"Select",
			Args: ["Extra Large", "Medium", "Small"]
		},

		{
			Name:"Status_rollup",
			ColumnType:"Rollup",
			RollupTarget: "Story",
			RollupColumn: "Status",
			RollupType: "group",
			RollupSumColumn:"Story_Points"
		}

	]
},

{
	Name: "Story",
	Schema: [
		{
			Name:"Description",
			ColumnType:"Text",
			MaxLen: 100,
			MinLen: 2
		},
		{
	        Name: "Comments",
	        ColumnType: "Notes"
	    },
		{
			Name:"Story_Owner",
			ColumnType:"Select",
			Args: ["Dev 1", "Dev 2", "Dev 3", "Dev 4", "Dev 5"]
		},
		{
			Name:"Team",
			ColumnType:"Select",
			Args: ["Unassigned", "UI Team", "Backend Team"]
		},
		
		{
			Name:"Sprint",
			ColumnType:"Select",
			Args: ["Backlog", "Jan 2013", "Feb 2013", "Mar 2013", "Apr 2013", "May 2013"]
		},

		{
			Name:"Story_Points",
			ColumnType:"Select",
			Args: [3, 5, 8, 10, 15, 20, 25]
		},
		{
			Name:"Status",
			ColumnType:"Select",
			Args: ["Open", "Closed", "Blocked" ]
		},

		{
			Name:"Total_Task_Points",
			ColumnType:"Rollup",
			RollupTarget: "Task",
			RollupColumn: "Task_Points",
			RollupType: "sum"
		},

		{
			Name:"Status_rollup",
			ColumnType:"Rollup",
			RollupTarget: "Task",
			RollupColumn: "Status",
			RollupType: "group",
			RollupSumColumn:"Task_Points"
		}

	]
},

{
	Name: "Task",
	Schema: [
		{
			Name:"Description",
			ColumnType:"Text",
			MaxLen: 100,
			MinLen: 2
		},

		{
			Name:"Task_Owner",
			ColumnType:"Select",
			Args: ["Dev 1", "Dev 2", "Dev 3", "Dev 4", "Dev 5"]
		},
 
		{
			Name:"Task_Points",
			ColumnType:"Select",
			Args: [1, 2, 3, 5, 8, 13]
		},
		{
			Name:"Status",
			ColumnType:"Select",
			Args: ["Open", "Closed", "Blocked" ],
			Colors: ["#FFFFFF", "#88FF88", "#FF8888" ]
		} 
	]
}

]

