[
 {
	Name: "Project",
	Schema: [
		{
			Name:"Description",
			Validation: "regex('[a-z]+')",
			MaxLen: 100,
			MinLen: 2
		},
		
		{
			Name:"Product_Owner",
			Args: ["Product Manager 1", "Product Manager 2", "Product Manager 3", "Product Manager 4", "Product Manager 5"]
		}	
		
	]
},

{
	Name: "Epic",
	Schema: [
		{
			Name:"Description",
			Validation: "regex('[a-z]+')"
		},

		{
			Name:"Epic_Owner",
			Args: ["TPM 1", "TPM 2", "TPM 3", "TPM 4", "TPM 5"],
			RollsUp: "group"
		},
		{
			Name:"Low_Estimate",
			Args: [ 250, 400, 650, 1000],
			RollsUp: "sum"
		},
		
		{
			Name:"High_Estimate",
			Args: [250, 400, 650, 1000],
			RollsUp: "sum"
		}		
	]
},
{
	Name: "Story",
	Schema: [
		{
			Name:"Description",
			Validation: "regex('[a-z]+')",
			MaxLen: 100,
			MinLen: 2
		},
		{
			Name:"Story_Owner",
			Args: ["Dev 1", "Dev 2", "Dev 3", "Dev 4", "Dev 5"],
			RollsUp: "group"
		},
		{
			Name:"Team",
			Args: ["Unassigned", "UI Team", "Backend Team"]
		},
		
		{
			Name:"Sprint",
			Args: ["Backlog", "Jan 2013", "Feb 2013", "Mar 2013", "Apr 2013", "May 2013"]
		},
		 
		{
			Name:"Story_Points",
			Args: [3, 5, 8, 10, 15, 20, 25],
			RollsUp: "sum"
		}
	]
},

{
	Name: "Task",
	Schema: [
		{
			Name:"Description",
			Validation: "regex('[a-z]+')",
			MaxLen: 100,
			MinLen: 2
		},

		{
			Name:"Task_Owner",
			Args: ["Dev 1", "Dev 2", "Dev 3", "Dev 4", "Dev 5"],
			RollsUp: "group"
		},
 
		{
			Name:"Task_Points",
			Args: [1, 2, 3, 5, 8, 13],
			RollsUp: "sum"
		},
		{
			Name:"Status",
			Args: ["Open", "Closed", "Blocked" ],
			Colors: ["#FFFFFF", "#88FF88", "#FF8888" ],
			RollsUp: "group"
		} 
	]
}

]

