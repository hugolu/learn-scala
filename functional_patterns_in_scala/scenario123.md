# Scenario 1, 2, 3

```scala
case class Warehouse(id: String, name: String)
case class Supplier(id: String, name: String)
case class Product(id: String, name: String)
```

## scenario1: functions returning `Option`s
```scala
trait InternalApi {
  def getWarehouse(id: String): Option[Warehouse]
  def getSupplier(warehouse: Warehouse, id: String): Option[Supplier]
  def getProduct(supplier: Supplier, id: String): Option[Product]
}

def getProductName(warehouseId: String, supplierId: String, productId: String): Option[String]
```

## scenario2: functions returning `Future`s
```scala
trait InternalApi {
  def getWarehouse(id: String): Future[Warehouse]
  def getSupplier(warehouse: Warehouse, id: String): Future[Supplier]
  def getProduct(supplier: Supplier, id: String): Future[Product]
}

def getProductName(warehouseId: String, supplierId: String, productId: String): Future[String]
```

## scenario3: functions returning `Function1`s
```scala
trait InternalApi {
  case class Config()
  def getWarehouse(id: String): Config => Warehouse
  def getSupplier(warehouse: Warehouse, id: String): Config => Supplier
  def getProduct(supplier: Supplier, id: String): Config => Product
}

def getProductName(warehouseId: String, supplierId: String, productId: String): Config => String
```

## NaivePublicApi
```scala
try {
  val warehouse = getWarehouse(warehouseId).get
  val supplier = getSupplier(warehouse, supplierId).get
  val product = getProduct(supplier, productId).get
  Some(product.name)
}
catch {
  case NonFatal(e) => None
}
```
- 呼叫 `getWarehouse(warehouseId).get` 得到 warehouse
- 呼叫 `getSupplier(warehouse, supplierId).get` 得到 supplier
- 呼叫 `getProduct(supplier, productId).get` 得到 product
- 回傳 `Some(product.name)`
- 以上任何步驟錯誤，回傳 `None`

## FlatMapPublicApi
```scala
getWarehouse(warehouseId).flatMap(warehouse =>
  getSupplier(warehouse, supplierId).flatMap(supplier =>
    getProduct(supplier, productId).map(product =>
      product.name
    )
  )
)
```

## ForComprehensionPublicApi
```scala
for {
  warehouse <- getWarehouse(warehouseId)
  supplier <- getSupplier(warehouse, supplierId)
  product <- getProduct(supplier, productId)
} yield product.name
```
- 等同於 FlatMapPublicApi 的做法
