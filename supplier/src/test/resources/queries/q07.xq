for
  $s in collection("supplier"),
  $c in collection("customer"),
  $n1 in collection("nation"),
  $n2 in collection("nation"),
  $o in collection("orders"),
  $l in collection("lineitem")
where
  $l/l_shipdate ge "1995-01-01" and
  $l/l_shipdate le "1996-12-31" and
  $s/s_suppkey = $l/l_suppkey and
  $o/o_orderkey = $l/l_orderkey and
  $c/c_custkey = $o/o_custkey and
  $s/s_nationkey = $n1/n_nationkey and
  $c/c_nationkey = $n2/n_nationkey and
  (
    ($n1/n_name = "ARGENTINA" and $n2/n_name = "INDIA") or
    ($n1/n_name = "INDIA" and $n2/n_name = "ARGENTINA")
  )
let
  $supp_nation := $n1/n_name,
  $cust_nation := $n2/n_name,
  $l_year := substring($l/l_shipdate, 1, 5),
  $volume := $l/l_extendedprice * (1 - $l/l_discount)
group by
  $supp_nation,
  $cust_nation,
  $l_year
order by
  $supp_nation,
  $cust_nation,
  $l_year
return
  element shipping_volume {
    element supp_nation { $supp_nation },
    element cust_nation { $cust_nation },
    element l_year { $l_year },
    element revenue { sum($volume) }
  }
