insert node element nation{
	element n_nationkey { 26 },
	element n_name { "TESTNation" },
	element n_regionkey { "2" },
	element n_comment { "yes3" } 
} into collection( "nation" )
break
insert node element nation{
	element n_nationkey { 27 },
	element n_name { "TESTNation1" },
	element n_regionkey { "3" },
	element n_comment { "yes1" } 
} into collection( "nation" )
break
insert node element nation{
	element n_nationkey { 28 },
	element n_name { "TESTNation2" },
	element n_regionkey { "4" },
	element n_comment { "yes2" } 
} into collection( "nation" )
break
delete nodes collection( "nation" )[n_nationkey = 28]
