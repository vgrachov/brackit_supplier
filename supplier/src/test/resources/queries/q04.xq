for
  $o in collection("orders"),
  $l in collection("lineitem")
let
  $orderpriority := $o/o_orderpriority,
  $orderkey := $o/o_orderkey
where
  $o/o_orderdate >= "1993-07-01" and
  $o/o_orderdate < "1993-10-01" and
  $l/l_orderkey = $o/o_orderkey and
  $l/l_commitdate < $l/l_receiptdate
order by
  $orderpriority
group by
  $orderpriority
let
  $distinctorders := distinct-values( $orderkey )
return
  element order_priority {
    $orderpriority,
    element order_count { count($distinctorders) }
  }
