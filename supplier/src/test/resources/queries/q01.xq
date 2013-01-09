for
  $l in collection("lineitem")
let
  $retflag := $l/l_returnflag,
  $linstat := $l/l_linestatus,
  $quantity := $l/l_quantity,
  $extendedprice := $l/l_extendedprice,
  $discount := $l/l_discount
where
  $l/l_shipdate le "1998-12-01"
let
  $disc_charge := $l/l_extendedprice * (1 - $l/l_discount),
  $charge := $disc_charge * (1 + $l/l_tax)
order by
  $retflag,
  $linstat
group by
  $retflag,
  $linstat
return
  element lineitem {
    $retflag,
    $linstat,
    element sum_qty { sum($quantity) },
    element sum_base_price { sum($extendedprice) },
    element sum_disc_charge { sum($disc_charge) },
    element sum_charge { sum($charge) },
    element avg_qty { avg($quantity) },
    element avg_price { avg($extendedprice) },
    element avg_disc { avg($discount) }
  }
