for
  $ps in collection("partsupp"),
  $s in collection("supplier"),
  $n in collection("nation")
where
  $ps/ps_suppkey = $s/s_suppkey
  and $s/s_nationkey = $n/n_nationkey
  and $n/n_name = "ARGENTINA"
let
  $ps_partkey := $ps/ps_partkey
group by
  $ps_partkey
let
  $value := sum($ps/ps_supplycost * $ps/ps_availqty)
where
  $value > 0.0001 * (
    let 
      $seq :=
	    for
	      $ps1 in collection("partsupp"),
	      $s1 in collection("supplier"),
	      $n1 in collection("nation")
	    where
	      $ps1/ps_suppkey = $s1/s_suppkey
	      and $s1/s_nationkey = $n1/n_nationkey
	      and $n1/n_name = "ARGENTINA"
	    return
	      $ps1/ps_supplycost * $ps1/ps_availqty
	return
		sum($seq)
  )
order by
  $value descending
return
  element important_stock {
    $ps_partkey,
    element value { $value }
  }
