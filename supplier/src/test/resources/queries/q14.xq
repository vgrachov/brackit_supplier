let
	$seq :=
	(
	for
		$l in collection("lineitem"),
		$p in collection("part")
	where
		$l/l_shipdate >= "1995-09-01"
		and $l/l_shipdate < "1995-10-01"	
		and $l/l_partkey = $p/p_partkey
	let
		$l_revenue_full := $l/l_extendedprice * (1 - $l/l_discount),
		$l_revenue_promo := if (starts-with($p/p_type,"PROMO")) then ($l/l_extendedprice * (1 - $l/l_discount)) else 0
	return
		element revenue{ 
			element revenue_promo { $l_revenue_promo } ,
			element revenue_full { $l_revenue_full }
		}
	)
return
	element res { 
		element revenue_promo { 100 * sum ( $seq//revenue_promo ) div sum ( $seq//revenue_full )}
	}