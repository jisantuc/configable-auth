package com.jisantuc.configableauth

import com.jisantuc.configableauth.database.util.{
  CirceJsonbMeta,
  Filterables,
  GeotrellisWktMeta
}

package object database
    extends CirceJsonbMeta
    with GeotrellisWktMeta
    with Filterables
