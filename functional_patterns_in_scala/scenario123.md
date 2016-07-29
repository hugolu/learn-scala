# Scenario 1, 2, 3

```scala
case class Warehouse(id: String, name: String)
case class Supplier(id: String, name: String)
case class Product(id: String, name: String)
```

```scala
trait InternalApi {
  def getWarehouse(id: String): Future[Warehouse]
  def getSupplier(warehouse: Warehouse, id: String): Future[Supplier]
  def getProduct(supplier: Supplier, id: String): Future[Product]
}
```

- scenario1: functions returning `Option`s
- scenario2: functions returning `Future`s
- scenario3: functions returning `Function1`s
