package com.zap

import zio.query.DataSource

package object zquery:
  type UDataSource[A] = DataSource[Any, A]
