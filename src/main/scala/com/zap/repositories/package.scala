package com.zap

import zio.query.DataSource

package object repositories:
  type UDataSource[A] = DataSource[Any, A]
