let
  $seq :=
	for
		$l in collection("lineitem"),
		$o in collection("orders")
	where
		$l/l_receiptdate >= "1994-01-01"
		and $l/l_receiptdate < "1995-10-01"
		and $o/o_orderkey = $l/l_orderkey
		and ( $l/l_shipmode = "TRUCK" or $l/l_shipmode = "MAIL")
		and $l/l_commitdate < $l/l_receiptdate
		and $l/l_shipdate < $l/l_commitdate
	let
		$l_shipmode := $l/l_shipmode,
		$o_orderpriority := $o/o_orderpriority,
		$high_line_count := if ($o/o_orderpriority = "1-URGENT" or $o/o_orderpriority = "2-HIGH") then 1 else 0,
		$low_line_count  := if ($o/o_orderpriority != "1-URGENT" or $o/o_orderpriority != "2-HIGH") then 1 else 0
	group by
	 	$l_shipmode
	order by
		$l_shipmode
	return
	  element ships_mode_order_priority{
	    element l_shipmode { $l_shipmode },
	    element high_line_count { sum ($high_line_count) },
	    element low_line_count { sum ($low_line_count) }
	 }
return
	$seq