# 異變與邊界

## 參考連結
- [scala-协变、逆变、上界、下界](http://www.cnblogs.com/jacksu-tencent/p/4979666.html)
- [Scala中的协变，逆变，上界，下界等](http://www.tuicool.com/articles/uYvyAbB)
- [Scala的协变(+)，逆变(-)，上界(<:)，下界(>:)](http://my.oschina.net/xinxingegeya/blog/486671)

Scala 是一種靜態類型語言，它的型別系統可能是所有程式語言中最複雜的。

Scala 類型系統企圖透過型別推斷讓程式碼更精簡與更有表達力，並防止程序在執行時期處於無效狀態。在編譯時期強加限制，使得運行時不會發生失敗，這需要編寫程式時額外遵守一些規則，而這些規則使得一般人覺得 Scala 很複雜。
