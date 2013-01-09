for
  $r in collection("region"),
  $s in collection("supplier"),
  $n in collection("nation"),
  $ps in collection("partsupp"),
  $p in collection("part")
where
  $p/p_size = 15
  and $r/r_name = 'EUROPE'
  and contains($p/p_type,"BRASS")
  and $p/p_partkey = $ps/ps_partkey
  and $s/s_suppkey = $ps/ps_suppkey
  and $s/s_nationkey = $n/n_nationkey
  and $n/n_regionkey = $r/r_regionkey
  and $ps/ps_supplycost = (
    let
    	$seq :=
		(    for
		      $r in collection("region"),
		      $s in collection("supplier"),
		      $n in collection("nation"),
			  $ps in collection("partsupp")		      
		    where
		      $p/p_partkey = $ps/ps_partkey
		      and $s/s_suppkey = $ps/ps_suppkey
		      and $s/s_nationkey = $n/n_nationkey
		      and $n/n_regionkey = $r/r_regionkey
		      and $r/r_name = 'EUROPE'
		    return
		      $ps/ps_supplycost
		)
	return
		min($seq)
   )
order by
  $s/s_acctbal descending,
  $n/n_name,
  $s/s_name,
  $p/p_partkey
return
  element minimum_cost_supplier {
    $s/s_acctbal,
    $s/s_name,
    $n/n_name,
    $p/p_partkey,
    $p/p_mfgr,
    $s/s_address,
    $s/s_phone,
    $s/s_comment
  }
