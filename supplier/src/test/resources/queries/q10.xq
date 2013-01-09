for
  $o in collection("orders"),
  $l in collection("lineitem"),
  $c in collection("customer"),
  $n in collection("nation")
where
  $o/o_orderdate >= "1993-10-01"
  and $o/o_orderdate < "1994-01-01"
  and $c/c_custkey = $o/o_custkey
  and $l/l_orderkey = $o/o_orderkey
  and $l/l_returnflag = "R"
  and $c/c_nationkey = $n/n_nationkey
let
  $revenueitem := $l/l_extendedprice * (1- $l/l_discount),
  $c_custkey := $c/c_custkey,
  $c_name := $c/c_name,
  $c_acctbal := $c/c_acctbal,
  $c_phone := $c/c_phone,
  $n_name := $n/n_name,
  $c_address := $c/c_address,
  $c_comment := $c/c_comment
group by
  $c_custkey,
  $c_name,
  $c_acctbal,
  $c_phone,
  $n_name,
  $c_address,
  $c_comment
let
  $revenue := sum($revenueitem)
order by
  $revenue descending
return
  element returned_item {
    element cust_key { $c_custkey },
    element c_name { $c_name },
    element revenue { sum ($revenue) },
    element c_acctbal { $c_acctbal },
    element n_name { $n_name },
    element c_address { $c_address },
    element c_phone { $c_phone },
    element c_comment { $c_comment }
  }
