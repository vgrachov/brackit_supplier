let
  $avg_yearly_seq :=
	for
		$l in collection("lineitem"),
		$p in collection("part")
	let
		$l_extendedprice := $l/l_extendedprice
	where
		$p/p_brand = "Brand#23"
		and $p/p_container = "MED DRUM"
		and $p/p_partkey = $l/l_partkey
		and $l/l_quantity < (
			let
				$seq :=
					(for 
						$l in collection("lineitem")
					where 
						$p/p_partkey = $l/l_partkey
					return
						$l/l_quantity
					)
			return
				0.2 * avg($seq)
		)
return 
	$l_extendedprice
return
	element avg_yearly { sum($avg_yearly_seq) }