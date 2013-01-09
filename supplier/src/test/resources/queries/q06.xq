let
 $revenueseq:=
 	(
		for
		  $l in collection("lineitem")
		where
		  $l/l_shipdate >= "1994-01-01" and
		  $l/l_shipdate < "1995-01-01" and
		  $l/l_discount >= 0.05 and
		  $l/l_discount <= 0.07 and
		  $l/l_quantity < 24
		let
		  $l_extendedprice := $l/l_extendedprice,
		  $l_discount := $l/l_discount,
		  $revenueitem := $l_extendedprice*$l_discount
		return
		  $revenueitem
	)
return
	sum($revenueseq)
