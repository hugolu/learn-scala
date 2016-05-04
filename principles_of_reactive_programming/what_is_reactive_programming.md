# What is Reactive Programming

## 改變中的需求

|   | 十年前 | 現在 |
|---|--------|------|
| 伺服器節點 | 數十個 | 數千個 |
| 反應時間 | 數秒 | 數毫秒 |
| 停機維護時間| 小時 | 無 |
| 資料量 | GBs | TBs → PBs |

## 需要新的架構

之前：使用像 Java Enterprise 架構管理伺服器與應用程式容器 (猜測類似 [Tomcat](https://zh.wikipedia.org/wiki/Apache_Tomcat) 的容器)

現在：響應式應用程式
- 事件驅動 (event-driven)
- 可擴充性 (scalable)
- 有彈性的 (resilient)
- 反應敏捷 (responsive)

### 響應式 (Reactive)

Reactive: 對刺激快速做出反應

- 對**事件**做出反應 ⇒ 事件驅動
- 對**覆載**做出反應 ⇒ 可擴充性
- 對**失敗**做出反應 ⇒ 有彈性的
- 對**用戶**做出反應 ⇒ 反應敏捷

Event-driven enables scalable, resilient, responsive. Scalable enables responsive. Resilient enables reponsive.

### 事件驅動

傳統上：系統由多執行緒構成，這些執行緒透過分享、同步的狀態進行溝通
- 強耦合、難以編寫

現在：系統由寬鬆耦合的事件處理器構成
- 事件可以非同步處理，不需 blocking (效率自然較佳)

### 可擴充性

唯有能夠根據使用狀況進行擴充，這樣的應用程式才算有**可擴充性**。
- 垂直擴充：利用多核心系統平行運算
- 橫向擴充：利用多伺服器節點

對可擴充性來說，共享的資料改變越小越好
對橫向擴充來說，要做到地點透明、有彈性的
- 地點透明(location transparency)：資料放在何處(本機或遠端)對運算來說都沒差
- 有彈性的(resilience)：失敗發生可以快速恢復

### 有彈性的

唯有能快速從失敗中恢復，這樣的應用程式才算是**有彈性的**。

失敗可以是
- 軟體失敗
- 硬體失敗
- 連線失敗

通常，彈性無法事後補救，它需要事前的設計
- 鬆耦合
- 對於狀態的封裝
- 普遍的監督架構

### 反應敏捷

唯有在高負載與可能發生失敗的情況下，依然提供用戶豐富、即時的反應，這樣的應用程式才算是**反應敏捷**。

響應式應用建立在事件驅動、可擴充性、有彈性的架構上。但還是要注意演算法、系統設計、後端壓力，及其他細節。

### Call-backs
處理事件通常使用 call-backs，例如 java observers
```scala
class Counter implements ActionListener {
  private var count = 0
  button.addActionListener(this)
  def actionPerformed(e: ActionEvent): Unit = {
    count += 1
  } 
}
```

問題：
- 需要共享可變更的狀態 (`var count`)
- 沒辦法組合簡單的 lisenter
- 導致 call-backs 地獄 (構成網狀 callback 結構，問題難以追蹤)

### 怎麼做更好

使用函式編程的基本結構得到**可組合**的事件抽象概念。
- 事件是一級類別
- 事件通常以訊息方式呈現
- 事件處理器也是一級類別
- 複雜的處理器也可以由基本元件構成

## 課程內容

- 複習函數編程
- 一個函數模式重要的課題：monads
- 有狀態世界中的函數編程
- 事件的抽象概念：futures
- 事件流的抽象概念：observables
- 訊息傳遞架構: actors
- 處理失敗：supervisors
- 橫向擴充：distributed sctors
